package rainz;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.*;

import rainz.GeometryData;

import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

class JoglControlWindow extends JFrame {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private JoglWindow joglWin = null;
  
  class GeoButton extends JButton implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public int button_idx = 0;
    public GeoButton(int i)
    {
      if (i >= 0)
        button_idx = i;
      this.setText("Geometry "+(i+1));
      addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent e) {
        joglWin.toggleGeoVisible(button_idx);
    }
  }

  public JoglControlWindow(JoglWindow jwin)
  {
    setTitle("Control Window");
    setSize(500, 500);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    
    JPanel panel = new JPanel();
    getContentPane().add(panel);
    //panel.setLayout(null);


    joglWin = jwin;

    for (int i = 0; i < joglWin.geoArray.size(); ++i)
    {
      GeoButton geoButton = new GeoButton(i);
      //geoButton.setBounds(50, 60, 250, 30);
      panel.add(geoButton);
    }
  }
  
}

public class JoglWindow extends JFrame implements GLEventListener {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private int rotateY = 0;
  private int texture1;
  
  protected ArrayList<GeometryData> geoArray;
  protected boolean geoVisible[];
  
  protected JoglControlWindow joglCtrlWin;
  
  public JoglWindow()
  {
    super("Jogl Test");
    
    collada = new Collada();
    try {
      InputStream is = new BufferedInputStream(new FileInputStream("C:/Users/Yu/workspace/Playground/assets/tank.xml"));
      geoArray = collada.loadGeometryData(is, 0, 0, 1, 1, (float) 1);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    
    geoVisible = new boolean[geoArray.size()];
    for (int i = 0; i < geoVisible.length; ++i)
    {
      geoVisible[i] = true;
    }
    
    // setup OpenGL Version 2
    GLProfile profile = GLProfile.get(GLProfile.GL2);
    GLCapabilities capabilities = new GLCapabilities(profile);

    // The canvas is the widget that's drawn in the JFrame
    GLCanvas glcanvas = new GLCanvas(capabilities);
    glcanvas.setSize( 600, 600 );
    
    /* full screen
    setUndecorated(true); // no title
    GraphicsDevice gd = 
      GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    gd.setFullScreenWindow(this);
    */


    getContentPane().add(glcanvas);

    setDefaultCloseOperation(EXIT_ON_CLOSE);

    glcanvas.addGLEventListener(this);
    setSize(getContentPane().getPreferredSize());
    setVisible(true);
    
    Animator animator = new FPSAnimator(glcanvas, 60);
    animator.add(glcanvas);
    animator.start();
    
    joglCtrlWin = new JoglControlWindow(this);
    joglCtrlWin.setVisible(true);
  }

  private GLU glu = new GLU();

  private Collada collada;
  
  private void renderObject(GL2 gl)
  {
    if (geoArray == null)
      return;

    glu.gluLookAt(50.0f, 30.0f, -20.0f, 0.0f, 0.0f, 0.0f, 0, 1, 0);
    gl.glRotatef(rotateY, 0, 1, 0);
    
    // Enable Pointers
    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);  // Enable Vertex Arrays
    // Enable Texture Coord Arrays
    gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
    int geo_size = geoArray.size();
    for (int i = 0; i < geo_size; ++i)
    {
      if (!geoVisible[i])
        continue;
      GeometryData geo = geoArray.get(i);
      if (geo.texCoords.limit() < 1)
        continue;
      gl.glVertexPointer(3, GL.GL_FLOAT, 0, geo.positions);
      gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, geo.texCoords);
      gl.glDrawElements(GL.GL_TRIANGLES, geo.vertexIndices.limit(), GL.GL_UNSIGNED_SHORT, geo.vertexIndices);
    }
  
    
    // Disable Vertex Arrays
    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);  
    // Disable Texture Coord Arrays
    gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY); 
  }
  
  public void toggleGeoVisible(int i)
  {
    if (i < geoVisible.length && i >= 0)
    {
      geoVisible[i] = !geoVisible[i];
    }
  }
  
  
  public void setGeoVisible(int i, boolean v)
  {
    if (i < geoVisible.length && i >= 0)
    {
      geoVisible[i] = v;
    }
  }

  public boolean getGeoVisible(int i)
  {
    if (i < geoVisible.length && i >= 0)
    {
      return geoVisible[i];
    }
    return false;
  }
  
  public void display(GLAutoDrawable gLDrawable)
  {
    update();
    final GL2 gl = gLDrawable.getGL().getGL2();

    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    //gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);

    renderObject(gl);
  }

  public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged,
      boolean deviceChanged)
  {
    System.out.println("displayChanged called");
  }

  public void init(GLAutoDrawable gLDrawable)
  {
    System.out.println("init() called");
    GL2 gl = gLDrawable.getGL().getGL2();
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    gl.glClearDepth(1.0f);        // Depth Buffer Setup
    gl.glDepthFunc(GL.GL_LEQUAL);  // The Type Of Depth Testing (Less Or Equal)
    gl.glEnable(GL.GL_DEPTH_TEST);      // Enable Depth Testing
    gl.glShadeModel(GL2.GL_SMOOTH);      // or GL_FLAT
    // Set Perspective Calculations To Most Accurate
    gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);      
    gl.glEnable(GL.GL_TEXTURE_2D);      // Enable Textures

    gl.glDisable(GL.GL_DITHER);
    gl.glDisable(GL.GL_BLEND);

    gl.glFrontFace(GL.GL_CCW);
    // Enable face culling.
    gl.glEnable(GL.GL_CULL_FACE);
    // What faces to remove with the face culling.
    gl.glCullFace(GL.GL_BACK);

    try {      
      //BufferedImage im=ImageIO.read(new File("../texture0.jpg"));
      Texture t = TextureIO.newTexture(new File("C:/Users/Yu/workspace/Playground/assets/texture_tank.jpg"),true);
      texture1 = t.getTextureObject();
      gl.glBindTexture(GL.GL_TEXTURE_2D, texture1);
      gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
      gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
    } catch (GLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width,
      int height)
  {
    System.out.println("reshape() called: x = " + x + ", y = " + y
        + ", width = " + width + ", height = " + height);
    final GL2 gl = gLDrawable.getGL().getGL2();

    if (height <= 0)
    {
      height = 1;
    }

    final float h = (float) width / (float) height;

    gl.glViewport(0, 0, width, height);
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluPerspective(45.0f, h, 1.0, 300.0);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
  }

  public void dispose(GLAutoDrawable arg0)
  {
    System.out.println("dispose() called");
  }

  private void update()
  {
    rotateY += 1;
    rotateY %= 360;
  }
  
}
