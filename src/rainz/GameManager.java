package rainz;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class GameManager {
  
  class GameStateEnums {
    static final int TITLE_SCREEN = 0;
    static final int GAME_ACTION = 1;
    static final int LEVEL_INTRO = 2;
    static final int LEVEL_END = 3;
  }
  public int gameState = GameStateEnums.TITLE_SCREEN;

  class LevelResultEnum {
    static final int NOT_FINISHED = 0;
    static final int PLAYER_WIN = 1;
    static final int PLAYER_LOSE = 2;
  }

  class GamePlayer extends GameObject {
    public int cash = 0;
    public GameObject vehicle = null;
    
    public int current_kills = 0;
  }
  
  private static GameManager singleton = null;
  
  public GameObject thePlayer;
  
  
  // **************** Per level data ****************

  public LevelLoader levelLoader = new LevelLoader();
  public int currentLevel = 1;
  
  public long globalTimer = 0;
  
  // Global Effect Timers
  public int timerStunAll = 0;
  
  
  
  public static final int IMAGE_W = 20;
  public static final int IMAGE_H = 20;
  
  public static final int IMAGE_PLAYER = 0;
  public static final int IMAGE_GHOST = 1;
  public static final int IMAGE_WALL = 2;
  public HashMap<Integer, BufferedImage> allImages = new HashMap<Integer, BufferedImage>();

  public ArrayList<GameObject> gameObjs = new ArrayList<GameObject>();
  
  public static synchronized GameManager singleInstance()
  {
    if (singleton == null)
      singleton = new GameManager();
    return singleton;
  }
  
  private GameManager()
  {
    
  }
  
  public boolean initGame()
  {
    // Load all images
    BufferedImage img_all;
    try {
      img_all = ImageIO.read(new File("/Documents and Settings/yuzhao.CORP/workspace/All.png"));
    }
    catch (IOException e) {
      System.out.println("Cannot load image!");
      e.printStackTrace();
      return false;
    }
    int num_images = img_all.getWidth()/IMAGE_W;
    for (int i = 0; i < num_images; ++i)
    {
      BufferedImage img = img_all.getSubimage(i*IMAGE_W, 0, IMAGE_W, IMAGE_H);
      allImages.put(i, img);
    }
    System.out.println("Number of images:"+num_images);
    
    // To do: load all models
    
    loadNextLevel();

    int [][] sp_map = levelLoader.computeShortestPathMap(8,17);
    //dumpSPMap(sp_map, gameManager.levelLoader.map_rows, gameManager.levelLoader.map_cols);
    
    GameObject mo = new GameObject();
    mo.init(allImages.get(0),sp_map);
    gameObjs.add(mo);
    mo = new GameObject();
    mo.init(allImages.get(0),sp_map);
    mo.setMapLocation(24, 17);
    gameObjs.add(mo);
    thePlayer = new GameObject();
    mo.init(allImages.get(0),sp_map);
    thePlayer.setMapLocation(22, 16);
    gameObjs.add(thePlayer);
    
    return true;
  }
  
  public void loadNextLevel()
  {
    // Show level intro text
    gameState = GameStateEnums.LEVEL_INTRO;
    levelLoader.loadMapFromFile("/Documents and Settings/yuzhao.CORP/workspace/GameMap.csv");
    
    // Load textures
    
    // Setup enemies and player
    
    // Force GC
    Runtime.getRuntime().gc();

    // show start button
    gameState = GameStateEnums.GAME_ACTION;
  }

  public void endScreen()
  {
    gameState = GameStateEnums.LEVEL_END;      
  }

  public int levelResult()
  {
    if (/*player dead && */ levelLoader.friendlyBases.size() == 0)
      return LevelResultEnum.PLAYER_LOSE;
    else if (levelLoader.enemyBases.size() == 0 /* && time out && enemies_killed > goal*/)
      return LevelResultEnum.PLAYER_WIN;
    else
      return  LevelResultEnum.NOT_FINISHED;
  }
  
  
}
