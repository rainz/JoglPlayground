package rainz;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import rainz.LevelLoader.DirectionEnum;

class GameObject {
  public int type = 0;
  
  public class StateEnums {
    public static final int ZOMBIE = -1;
    public static final int BIRTH = 0;
    public static final int NORMAL = 1;
  }
  
  public int state = StateEnums.BIRTH;
  
  public int position_x = 0;
  public int position_y = 0;
  public int angle = 0;
  public int speed = 5;
  public int width = 1;
  public int height = 1;

  public int m_sp_map[][] = null;
  public int m_curr_dir = DirectionEnum.HERE;
  
  public AI.AIStrategy ai = null;
  
  public int hit_points = 100;
  public int armor_level = 0;
  public int weapon_level = 0;
  
  public BufferedImage image = null;
  
  
  
  
  public GameObject()
  {
    
  }
  
  public void init(BufferedImage img, int sp_map[][])
  {
    image = img;
    m_sp_map = sp_map;
    width = image.getWidth();
    height = image.getHeight();    
  }
  
  public void setMapLocation(int row, int col)
  {
    position_x = col*width;
    position_y = row*height;
  }
  
  public void nextAction()
  {
    if (position_x % width == 0 && position_y % height == 0)
    {
      // Update direction only when the object "snaps into" this block
      int next_i = position_y / height;
      int next_j = position_x / width;
      m_curr_dir = m_sp_map[next_i][next_j];
    }
    move(m_curr_dir);
  }
  
  public void move(int dir)
  {
    int new_x = position_x;
    int new_y = position_y;
    if (dir == DirectionEnum.UP)
      new_y -= speed;
    else if (dir == DirectionEnum.DOWN)
      new_y += speed;
    else if (dir == DirectionEnum.LEFT)
      new_x -= speed;
    else if (dir == DirectionEnum.RIGHT)
      new_x += speed;
    // To do: collision detection
    
    position_x = new_x;
    position_y = new_y;
  }
  
  public boolean wallCollision(int pos_x, int pos_y, ArrayList<Point> points)
  {
    points.add(new Point(pos_x, pos_y));
    points.add(new Point(pos_x+width, pos_y));
    points.add(new Point(pos_x, pos_y+height));
    points.add(new Point(pos_x+width, pos_y+height));
    double center_x = ((double)pos_x + width)/2.0f;
    double center_y = ((double)pos_y + height)/2.0f;
    double radian = Math.toRadians(angle);
    for (Point p: points)
    {
      double px = p.x - center_x;
      double py = p.y - center_y;
      p.x = (int) Math.round(px*Math.cos(radian) + py*Math.sin(radian) + center_x);
      p.y = (int) Math.round(px*Math.sin(radian) - py*Math.cos(radian) + center_y);
      // To be continued ... 
    }
    return false;
  }
  
  public void render()
  {
    
  }
}
