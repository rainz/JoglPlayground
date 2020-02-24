package rainz;

import java.util.HashMap;
import java.util.Stack;

public class PoolManager {

  public static final int GAME_OBJECTS = 0;
  
  public HashMap< Integer, ObjectPool<?> > pools = new HashMap< Integer, ObjectPool<?> >();
  
  class ObjectPool<T> {
    public T[] objects;
    private Stack<T> poolStack = new Stack<T>();
    
    public ObjectPool(T [] objs)
    {
      objects = objs;
      reset();
    }
    
    public void reset()
    {
      poolStack.clear();
      for (int i = 0; i < objects.length; ++i)
        poolStack.push(objects[i]);      
    }
    
    
    public T get()
    {
      if (!poolStack.empty())
        return poolStack.pop();
      return null;
    }
    
    public void putBack(Object obj)
    {
      poolStack.push((T)obj);
    }
  }
  
  public PoolManager()
  {
    // Create GameObject Pool
    int poolSize = 256;
    GameObject [] gameObjects = new GameObject[poolSize];
    for (int i = 0; i < poolSize; ++i)
    {
      gameObjects[i] = new GameObject();
    }
    pools.put(GAME_OBJECTS, new ObjectPool<GameObject>(gameObjects));
  }
  
  public Object get(int poolID)
  {
    return pools.get(poolID).get();
  }
  
  public void putBack(GameObject obj, int poolID)
  {
    pools.get(poolID).putBack(obj);
  }
}
