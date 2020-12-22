package smartform.common.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/** 
* @ClassName: JedisViewMgr 
* @Description: Jedis视图数据管理
* @author KaminanGTO
* @date 2017年6月24日 下午8:06:20 
*  
*/
public class JedisViewMgr {
	
	private static final Logger logger = LoggerFactory.getLogger(JedisViewMgr.class);

	/** 
	* @Title: SaveView 
	* @Description: 保存视图数据（非覆盖，遇到重复的value会覆盖）
	* @param jedis
	* @param key
	* @param datas
	* @return
	* @throws JsonProcessingException  参数说明
	* @return long    返回类型 
	* 
	*/
	@SuppressWarnings("unchecked")
	public static <T extends JedisBaseView> long SaveView(String key, T... datas) throws JsonProcessingException
	{
		//创建保存的数据
		Set<TypedTuple<String>> map = new HashSet<TypedTuple<String>>();
		for(T jbv : datas)
		{
			map.add(new RedisTuple(jbv.ToSaveStr(), jbv.score));
		}
		return JedisMgr.GetInstance().pushSortSetByStrMap(key, map);
	}
	
	/** 
	* @Title: SaveView 
	* @Description: 保存视图数据（非覆盖，遇到重复的value会覆盖）
	* @param jedis
	* @param key
	* @param datas
	* @return
	* @throws JsonProcessingException  参数说明
	* @return long    返回类型 
	* 
	*/
	public static <T extends JedisBaseView> long SaveView(String key, List<T> datas) throws JsonProcessingException
	{
		//创建保存的数据
		Set<TypedTuple<String>> map = new HashSet<TypedTuple<String>>();
		for(T jbv : datas)
		{
			map.add(new RedisTuple(jbv.ToSaveStr(), jbv.score));
		}
		
		return JedisMgr.GetInstance().pushSortSetByStrMap(key, map);
	}
	
	/** 
	* @Title: SaveView 
	* @Description: 保存视图数据（非覆盖，遇到重复的value会覆盖）
	* @param jedis
	* @param key
	* @param value
	* @param score
	* @return
	* @throws JsonProcessingException  参数说明
	* @return long    返回类型 
	* 
	*/
	public static boolean SaveView(String key, String value, double score) throws JsonProcessingException
	{
		
		return JedisMgr.GetInstance().pushSortSet(key, value, score);
	}
	
	/** 
	* @Title: RemoveView 
	* @Description: 删除视图内部数据
	* @param jedis
	* @param key
	* @param values
	* @return  参数说明 
	* @return long    返回类型 
	* 
	*/
	public static  long RemoveView(String key, String... values)
	{
		return JedisMgr.GetInstance().removeSortSet(key, values);
	}
	
	/** 
	* @Title: RemoveView 
	* @Description: 删除视图内部数据
	* @param jedis
	* @param key
	* @param values
	* @return  参数说明 
	* @return long    返回类型 
	* 
	*/
	public static  long RemoveView(String key, JedisBaseView... views)
	{
		int size = views.length;
		String values[] = new String[size];
		for(int i = 0;i < size;i++)
		{
			values[i] = views[i].data;
		}
		return JedisMgr.GetInstance().removeSortSet(key, values);
	}
	
	/** 
	* @Title: LoadView 
	* @Description: 降序 分页 获取视图列表
	* @param jedis
	* @param key
	* @param offset
	* @param count
	* @param clazz
	* @return
	* @throws InstantiationException
	* @throws IllegalAccessException  参数说明 
	* @return List<T>    返回类型 
	* 
	*/
	public static <T extends JedisBaseView> List<T> LoadViewRev(String key, int offset, int count, Class<T> clazz) throws InstantiationException, IllegalAccessException
	{
		Set<TypedTuple<String>> sets = JedisMgr.GetInstance().getSortSetRevListWithScores(key, offset, count);
		List<T> list = new ArrayList<T>();
		for(TypedTuple<String> t : sets)
		{
			T jbv = clazz.newInstance();
			if(jbv.SetAsTuple(t))
				list.add(jbv);
			else
				logger.error("序列化视图数据失败", key, t.getValue() );
		}
		return list;
	}
	
	/** 
	* @Title: LoadView 
	* @Description: 读取视图数据
	* @param jedis
	* @param key
	* @param min
	* @param max
	* @param clazz
	* @return
	* @throws InstantiationException
	* @throws IllegalAccessException  参数说明 
	* @return List<T>    返回类型 
	* 
	*/
	public static <T extends JedisBaseView> List<T> LoadView(String key, String min, String max, Class<T> clazz) throws InstantiationException, IllegalAccessException
	{
		
		Set<String> sets = JedisMgr.GetInstance().getSortSetListByLex(key, min, max);
		List<T> list = new ArrayList<T>();
		for(String str : sets)
		{
			T jbv = clazz.newInstance();
			if(jbv.SetAsJedisData(str))
				list.add(jbv);
			else
				logger.error("序列化视图数据失败", key, str );
		}
		return list;
	}
	
	/** 
	* @Title: LoadView 
	* @Description: 读取视图数据， 分页获取
	* @param jedis
	* @param key
	* @param min
	* @param max
	* @param offset
	* @param count
	* @param clazz
	* @return
	* @throws InstantiationException
	* @throws IllegalAccessException  参数说明 
	* @return List<T>    返回类型 
	* 
	*/
	public static <T extends JedisBaseView> List<T> LoadView(String key, String min, String max, int offset, int count, Class<T> clazz) throws InstantiationException, IllegalAccessException
	{
		Set<String> sets = JedisMgr.GetInstance().getSortSetListByLex(key, min, max, offset, count);
		List<T> list = new ArrayList<T>();
		for(String str : sets)
		{
			T jbv = clazz.newInstance();
			if(jbv.SetAsJedisData(str))
				list.add(jbv);
			else
				logger.error("序列化视图数据失败", key, str );
		}
		return list;
	}
	
	/** 
	* @Title: LoadViewByScore 
	* @Description: 分页获取，读取视图数据--按score排序获取--降序获取
	* @param jedis
	* @param key
	* @param min
	* @param max
	* @param offset
	* @param count
	* @param clazz
	* @return  参数说明 
	* @return List<T>    返回类型 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	* 
	*/
	public static <T extends JedisBaseView> List<T> LoadViewByScoreRev(String key, double min, double max, int offset, int count, Class<T> clazz) throws InstantiationException, IllegalAccessException
	{
		Set<TypedTuple<String>> sets = JedisMgr.GetInstance().getSortSetRevListWithScores(key, min, max, offset, count);
		
		List<T> list = new ArrayList<T>();
		for(TypedTuple<String> t : sets)
		{
			T jbv = clazz.newInstance();
			if(jbv.SetAsTuple(t))
				list.add(jbv);
			else
				logger.error("序列化视图数据失败", key, t.getValue());
			
		}
		return list;
	}
	
}
