package rainz;

import java.awt.Point;

public class AI {
  public abstract class AIStrategy {
    abstract public void nextAction(GameObject host);
  }

  public class AIPatrol extends AIStrategy {
    public Point point1;
    public Point point2;
    public AIPatrol(Point p1, Point p2)
    {
      point1 = p1;
      point2 = p2;
    }
    
    @Override
    public void nextAction(GameObject host)
    {
      GameManager mgr = GameManager.singleInstance();
    }
  }
  
}
