package rainz;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import rainz.LevelLoader.*;


public class PlayWindow extends JFrame {

  private static final long serialVersionUID = 1L;
  PlayPanel playPanel;
  Dimension gameDimension = new Dimension(480, 640);

  public PlayWindow()
  {
    playPanel = new PlayPanel();
    add(playPanel);
    setTitle("PlayPanel");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(gameDimension.width, gameDimension.height);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  class TAdapter extends KeyAdapter {
    public void keyPressed(KeyEvent e)
    {
      int key = e.getKeyCode();
      
      if (key == KeyEvent.VK_LEFT)
      {
      }
      else if (key == KeyEvent.VK_RIGHT)
      {
      }
      else if (key == KeyEvent.VK_UP)
      {
      }
      else if (key == KeyEvent.VK_DOWN)
      {
      }
      else if (key == KeyEvent.VK_ESCAPE)
      {
      }
      else
      {
        GameObject player = playPanel.gameManager.thePlayer;
        if (key == 'a' || key == 'A')
        {
          player.move(DirectionEnum.LEFT);
        }
        else if (key == 's' || key == 'S')
        {
          player.move(DirectionEnum.DOWN);
        }
        else if (key == 'd' || key == 'D')
        {
          player.move(DirectionEnum.RIGHT);
        }
        else if (key == 'w' || key == 'W')
        {
          player.move(DirectionEnum.UP);
        }
        else if (key == ' ')
        {
        }
      }
    }

    public void keyReleased(KeyEvent e)
    {
      int key = e.getKeyCode();

      if (key == Event.LEFT || key == Event.RIGHT || key == Event.UP
          || key == Event.DOWN)
      {
      }
    }
  }

  class PlayPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    Timer timer;
    
    GameManager gameManager = GameManager.singleInstance();
    
    public PlayPanel()
    {
      setBackground(Color.black);
      setDoubleBuffered(true);
    
      gameManager.initGame();

      addKeyListener(new TAdapter());
      setFocusable(true);

      timer = new Timer();
      timer.scheduleAtFixedRate(new RepaintTask(), 100, 10);
    }
    
    public void addNotify()
    {
      super.addNotify();
      //GameInit();
    }

    @Override
    public void actionPerformed(ActionEvent arg0)
    {
    }

    public void paint(Graphics g)
    {
      super.paint(g);

      Graphics2D g2d = (Graphics2D) g;
      g2d.fillRect(0, 0, gameDimension.width, gameDimension.height);
      
      BufferedImage img_wall = gameManager.allImages.get(2);
      int img_w = img_wall.getWidth();
      int img_h = img_wall.getHeight();
      for (int i = 0; i < gameManager.levelLoader.map_rows; ++i)
      {
        for (int j = 0; j < gameManager.levelLoader.map_cols; ++j)
        {
          if (gameManager.levelLoader.map_data[i][j] == MapEnum.WALL)
          {
            g2d.drawImage(img_wall, j*img_w, i*img_h, null);
          }
        }
      }
      for (GameObject obj : gameManager.gameObjs)
      {
        g2d.drawImage(obj.image, obj.position_x, obj.position_y, null);
      }
      
      Toolkit.getDefaultToolkit().sync();
      g.dispose();
    }

    public void dumpSPMap(int [][] sp_map, int rows, int cols)
    {

      for (int i = 0; i < rows; ++i)
      {
        for (int j = 0; j < cols; ++j)
        {
          char c;
          switch (sp_map[i][j]) {
          case 4:
            c = '^'; break;
          case 1:
            c = 'v'; break;
          case 3:
            c = '<'; break;
          case 2:
            c = '>'; break;
          case 0:
            c = '*'; break;
          default:
            c = 'X'; break;
          }
          System.out.print(c);
          System.out.print(' ');
        }
        System.out.println();
      }
    }
  }
  
  class RepaintTask extends TimerTask {
    public void run()
    {
      /*
      for (GameObject obj : gameManager.gameObjs)
        obj.nextAction();
      */
      repaint();
    }
  }
}
