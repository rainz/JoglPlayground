package rainz;

import java.awt.Rectangle;
import java.util.*;

import rainz.Collada.ColladaGeometry;
import rainz.Collada.ColladaObject;
import rainz.Collada.ColladaPolyList;
import rainz.Collada.ColladaRoot;
import rainz.Collada.ColladaSource;

public class playground {

  public static void main(String[] args)
  {
    //testCollada(args);
    //testSceneLoader(args);
    //testPlay(args);
    //testLoadCollada(args);
    testJogl(args);
    //testPool(args);
  }
  
  static void testPool(String[] args)
  {
  }
  
  static void testPlay(String [] args)
  {
    new PlayWindow();    
  }
  
  static void testSceneLoader(String [ ] args)
  {
    LevelLoader sl = new LevelLoader();
    sl.loadMapFromFile("/Documents and Settings/yuzhao.CORP/workspace/GameMap.csv");
 
    ArrayList<Rectangle> blocks = sl.computeWallBlocks();
    for (int idx = 0; idx < blocks.size(); ++idx)
    {
      Rectangle rect = blocks.get(idx);
      for (int i = 0; i < rect.height; ++i)
      {
        for (int j = 0; j < rect.width; ++j)
        {
          sl.map_data[rect.y+i][rect.x+j] += (1+idx)*10;
        }
      }
    }
    
    for (int i = 0; i < sl.map_rows; ++i)
    {
      for (int j = 0; j < sl.map_cols; ++j)
      {
        System.out.format("%03d", sl.map_data[i][j]);
        System.out.print(',');
      }
      System.out.println();
    }
 
  }
  
  static void testJogl(String [] args)
  {
    new JoglWindow();
  }
}
