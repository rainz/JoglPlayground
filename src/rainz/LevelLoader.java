package rainz;

import java.util.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class LevelLoader {
  
  public class MapEnum {
    public static final int GROUND = 0;
    public static final int WALL = 1;
  }

  public class DirectionEnum {
    public static final int HERE = 0;
    public static final int DOWN = 1;
    public static final int RIGHT = 2;
    public static final int LEFT = 3;
    public static final int UP = 4;
    public static final int CANT_REACH = Integer.MAX_VALUE;
  }
  
  public static final int BLOCK_W = 20;
  public static final int BLOCK_H = 20;
  
  public int map_rows = -1;
  public int map_cols = -1;
  public int[][] map_data;

  // Minimum enemies player must kill to complete this level
  public int minEnemyKill = 0;
  
  public long timeLeft = 5*60*1000; // in milliseconds
  
  public ArrayList<GameObject> enemyBases;
  public ArrayList<GameObject> friendlyBases;
  
  public String introText;
  
  public boolean loadMapFromFile(String file_name)
  {
    BufferedReader reader;
    try
    {
      reader = new BufferedReader(new FileReader(file_name));
      String line = null;
      ArrayList< ArrayList<Integer> > map_array = new  ArrayList< ArrayList<Integer> >();
      while ((line = reader.readLine()) != null)
      {
        String tokens[] = line.split(",");
        ArrayList<Integer> line_vals = new ArrayList<Integer>();
        for (int i = 0; i < tokens.length; ++i)
        {
          if (tokens[i].trim().equals(""))
            continue;
          line_vals.add(Integer.parseInt(tokens[i]));
        }
        if (map_cols < 0)
          map_cols = line_vals.size();
        else if (map_cols != line_vals.size()){
          System.out.println("Inconsistent column widths!");
          return false;
        }
        map_array.add(line_vals);
      }
      
      map_rows = map_array.size();
      map_data = new int[map_rows][map_cols];
      for (int i = 0; i < map_rows; ++i)
      {
        ArrayList<Integer> row_vals = map_array.get(i);
        for (int j = 0; j < map_cols; ++j)
          map_data[i][j] = row_vals.get(j);
        System.out.println();
      }
      
      reader.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public ArrayList<Rectangle> computeWallBlocks()
  {
    if (map_rows < 0 || map_cols < 0)
      return null;
    
    ArrayList<Rectangle> blocks = new ArrayList<Rectangle>();
    boolean visited[][] = new boolean[map_rows][map_cols];
    for (int i = 0; i < map_rows; ++i)
    {
      for (int j = 0; j <map_cols; ++j)
      {
        if (map_data[i][j] == MapEnum.GROUND || visited[i][j])
          continue;
        //First expand to the right
        int right = j;
        do {
          ++right;
        } while (right < map_cols && 
                 map_data[i][right] == MapEnum.WALL &&
                 !visited[i][right]);
        // Then expand downwards
        int bottom = i;
        boolean bExpandable = true;
        do {
          ++bottom;
          if (bottom >= map_rows)
            break;
          for (int col = j; col < right; ++col)
          {
            bExpandable = (map_data[bottom][col] == MapEnum.WALL &&
                           !visited[bottom][col]);
            if (!bExpandable)
              break;
          }
        } while (bExpandable);
        
        for (int ii = i; ii < bottom; ++ii)
          for (int jj = j; jj < right; ++jj )
            visited[ii][jj] = true;
        blocks.add(new Rectangle(j, i, right-j, bottom-i));
      }
    }
    return blocks;
  }
  
  public int[][] computeShortestPathMap(int row, int col)
  {
    // Here x and y of a Point are used as row and column
    Point dest = new Point(row, col);
    // Initialize shortest path map and distance map
    int[][] shortest_path_map = new int[map_rows][map_cols];
    for (int i = 0; i < map_rows; ++i)
      for (int j = 0; j < map_cols; ++j)
        shortest_path_map[i][j] = DirectionEnum.CANT_REACH;
    
    int[][] distance_map = new int[map_rows][map_cols];
    for (int i = 0; i < map_rows; ++i)
      for (int j = 0; j < map_cols; ++j)
        distance_map[i][j] = Integer.MAX_VALUE;
    
    class ProcessRecord {
      Point toPoint;
      Point fromPoint;
      int distance;
      public ProcessRecord(Point to, Point from, int dist)
      {
        toPoint = to;
        fromPoint = from;
        distance = dist;
      }
    }
    // The queue for breadth-first-search
    ArrayList<ProcessRecord> processQ = new ArrayList<ProcessRecord>();
    
    ProcessRecord rec = new ProcessRecord(dest, dest, 0);
    if (pointBlocked(dest))
      return shortest_path_map; // destination is in a wall??
    
    processQ.add(rec); // initial point: the final destination
    
    Point toP, fromP;
    int distance;
    for (int processIdx = 0; processIdx < processQ.size(); ++processIdx)
    {
      rec = processQ.get(processIdx);
      toP = rec.toPoint; // "to" point is the point being examined right now
      fromP = rec.fromPoint; // "from" point is for preventing us from going back
      distance = rec.distance;
      if (distance_map[toP.x][toP.y] <= distance)
        continue; // a shorter path already exists

      distance_map[toP.x][toP.y] = distance;
      shortest_path_map[toP.x][toP.y] = determineDir(toP, fromP);
      
      // Adding neighbor locations
      ArrayList<Point> neighbors = new ArrayList<Point>();
      // Down
      if (toP.x + 1 < map_rows)
        neighbors.add(new Point(toP.x + 1, toP.y));
      // Right
      if (toP.y + 1 < map_cols)
        neighbors.add(new Point(toP.x, toP.y + 1));
      // Left
      if (toP.y - 1 >= 0)
        neighbors.add(new Point(toP.x, toP.y - 1));
      // Up
      if (toP.x - 1 >= 0)
        neighbors.add(new Point(toP.x - 1, toP.y));
      for (int i = 0; i < neighbors.size(); ++i) {
        Point nb = neighbors.get(i);
        if (nb.equals(fromP))
          continue; // don't go back
        if (pointBlocked(nb))
          continue; // blocked
        // New point to explore. Append at the end of the queue
        ProcessRecord new_rec = new ProcessRecord(nb, toP, distance + 1);
        processQ.add(new_rec);
      }
    }
    
    return shortest_path_map;
  }
  
  private int determineDir(Point startPoint, Point endPoint)
  {
    int dir;
    int row_diff = endPoint.x - startPoint.x;
    int col_diff = endPoint.y - startPoint.y;
    if (row_diff == 0 && col_diff == 0)
      dir = DirectionEnum.HERE;
    else if (row_diff == 0 && col_diff == -1)
      dir = DirectionEnum.LEFT;
    else if (row_diff == 0 && col_diff == 1)
      dir = DirectionEnum.RIGHT;
    else if (row_diff == -1 && col_diff == 0)
      dir = DirectionEnum.UP;
    else if (row_diff == 1 && col_diff == 0)
      dir = DirectionEnum.DOWN;
    else
      dir = DirectionEnum.CANT_REACH;
    
    return dir;
  }
  
  private boolean pointBlocked(Point p)
  {
    return (map_data[p.x][p.y] != MapEnum.GROUND);
  }
  

}
