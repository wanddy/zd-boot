package smartform.common.redis;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import smartform.common.util.FieldUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: JedisBaseDoc
 * @Description: Jedis数据基类，继承该类的数据都已map方式存放，所有的一级属性都可用做排序
 *               需要注意，由于查询时，并不可以直接按列表中某个属性排序，所有继承该类的属性中，需要查询的数据保存格式不应该包含2层以上列表属性，如List(List)
 * @author KaminanGTO
 * @date 2017年6月12日 下午3:18:32
 * 
 */
public abstract class JedisBaseDoc {

	static Logger logger = LoggerFactory.getLogger(JedisBaseDoc.class);

	/**
	 * @Fields KeySpan : Key的分隔符
	 */
	public static final String KeySpan = ":";

	/**
	 * @Fields KeyHead : Key的头数据
	 */
	public static final String KeyHead = "ACT:";

	/**
	 * @Fields key : 数据key，一个类使用一个key(该key不可重复,如遇到重复则会覆盖数据），方便模糊查询数据
	 * 
	 */
	@JedisIgnore
	@JsonIgnore
	@JSONField(serialize=false)
	protected String key;

	/**
	 * @Fields type : 数据类型，用于colr批量查询相同类型数据时使用
	 */
	@JsonIgnore
	@JedisIgnore(value = false)
	@JSONField(serialize=false)
	public String type;

	public JedisBaseDoc(String type) {
		this.type = type;
	}

	/**
	 * @Title: GetDBKey
	 * @Description: 默认的key获取方法
	 * @param type
	 * @param id
	 * @return 参数说明
	 * @return String 返回类型
	 * 
	 */
	public static String GetDBKey(String type, String id) {
		return JedisBaseDoc.KeyHead + type + JedisBaseDoc.KeySpan + id;
	}

	/**
	 * @Title: getKey
	 * @Description: 获取当前实体的Key
	 * @return 参数说明
	 * @return String 返回类型
	 * 
	 */
	@JsonIgnore
	@JSONField(serialize=false)
	public abstract String getKey();

	/**
	 * @Title: init
	 * @Description: 初始化数据，在load结束后会调用
	 * @return void 返回类型
	 * 
	 */
	public abstract void init();

	/**
	 * @Title: update
	 * @Description: 更新所有数据，没有数据则创建新数据
	 * @param jedis
	 *            参数说明
	 * @return void 返回类型
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws JsonProcessingException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * 
	 */
	public void update(StringRedisTemplate stringRedisTemplate) throws IllegalArgumentException, IllegalAccessException, JsonProcessingException,
			NoSuchFieldException, SecurityException {
		// 生成需要写入的普通数据
		Map<String, String> datas = new HashMap<String, String>();
		Field[] fields = this.getClass().getFields();
		for (Field field : fields) {
			// 排除静态属性
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			// 判断是否是不需要读写的数据
			JedisIgnore juu = field.getAnnotation(JedisIgnore.class);
			if (juu != null && juu.value())
				continue;
			// 拥有特殊类型标记的数据写入到特定地方
			JedisType jt = field.getAnnotation(JedisType.class);
			if (jt != null) {
				FieldUtil.UpdateJedisTypeParam(jt, this, field);
				continue;
			}
			datas.put(field.getName(), FieldUtil.GetAsString(this, field));
		}
		// 写入所有数据
		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
		operations.putAll(getKey(), datas);
	}

	/**
	 * @Title: update
	 * @Description: 更新指定变量名的数据
	 * @param jedis
	 * @param params
	 *            参数说明
	 * @return void 返回类型
	 * @throws JsonProcessingException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * 
	 */
	public void update(StringRedisTemplate stringRedisTemplate, String... params) throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, JsonProcessingException {
		// 生成需要写入的普通数据
		Map<String, String> datas = new HashMap<String, String>();
		// 循环获取需要保存的变量
		for (String param : params) {
			Field field = this.getClass().getField(param);
			// 排除静态属性
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			// 判断是否是不需要读写的数据
			JedisIgnore juu = field.getAnnotation(JedisIgnore.class);
			if (juu != null && juu.value())
				continue;
			// 拥有特殊类型标记的数据写入到特定地方
			JedisType jt = field.getAnnotation(JedisType.class);
			if (jt != null) {
				FieldUtil.UpdateJedisTypeParam(jt, this, field);
				continue;
			}

			datas.put(field.getName(), FieldUtil.GetAsString(this, field));
		}
		// 判断是否有正常需要写入数据
		if (datas.size() > 0) {
			// 写入所有数据
			HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
			operations.putAll(getKey(), datas);
		}

	}

	/**
	 * @Title: delete
	 * @Description: 删除数据
	 * @param jedis
	 *            参数说明
	 * @return void 返回类型
	 * 
	 */
	public void delete(StringRedisTemplate stringRedisTemplate) {
		stringRedisTemplate.delete(getKey());
	}

	/**
	 * @Title: deleteParam
	 * @Description: 删除指定key
	 * @param jedis
	 * @param keys
	 *            参数说明
	 * @return void 返回类型
	 * 
	 */
	public void deleteParam(StringRedisTemplate stringRedisTemplate, String... keys) {
		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
		List<String> keyList = Arrays.asList(keys);
		operations.delete(getKey(), keyList);
	}

	/**
	 * @Title: updateOnce
	 * @Description: 更新单个数据
	 * @param jedis
	 * @param name
	 * @param value
	 *            参数说明
	 * @return void 返回类型
	 * 
	 */
	public void updateOnce(StringRedisTemplate stringRedisTemplate, String name, String value) {
		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
		operations.put(getKey(), name, value);
	}

	/**
	 * @Title: Load
	 * @Description: 从数据库加载数据，当数据不存在时，返回空实体
	 * @param jedis
	 * @param key
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 *             参数说明
	 * @return T 返回类型
	 * 
	 */
	public static <T extends smartform.common.redis.JedisBaseDoc> T Load(StringRedisTemplate stringRedisTemplate, String key, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IllegalArgumentException, IllegalAccessException,
			ClassNotFoundException, InstantiationException, IOException, NoSuchFieldException, SecurityException {
		T jclass = clazz.newInstance();
		jclass.key = key;
		// 反射获取所有变量
		Field[] fields = jclass.getClass().getFields();
		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
		
		Map<String, String> datas = operations.entries(key);
		// 进行空判定
		if (datas.size() == 0)
			return null;
		for (Field field : fields) {
			// 排除静态属性
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			// 判断是否是不需要读写的数据
			JedisIgnore juu = field.getAnnotation(JedisIgnore.class);
			if (juu != null && juu.value())
				continue;
			// 拥有特殊类型标记的数据需要特殊读取
			JedisType jt = field.getAnnotation(JedisType.class);
			if (jt != null) {
				FieldUtil.SetJedisTypeParam(jt, jclass, field);
				continue;
			}
			// 遍历所有变量，读取数据库中的数据
			String paramName = field.getName();
			if (datas.containsKey(paramName)) {
				FieldUtil.SetStrParam(jclass, field, datas.get(paramName));
			}
		}
		jclass.init();
		return jclass;
	}

	/**
	 * @Title: Load
	 * @Description: 从数据库加载指定变量的数据，该方法不判断是否是不存在的key，如果需要判断，先使用jedis.exists(key)判断是否存在
	 * @param jedis
	 * @param key
	 * @param clazz
	 * @param params
	 * @return 参数说明
	 * @return T 返回类型
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * 
	 */
	public static <T extends smartform.common.redis.JedisBaseDoc> T Load(StringRedisTemplate stringRedisTemplate, String key, Class<T> clazz, String... params)
			throws NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException,
            JsonParseException, JsonMappingException, ClassNotFoundException, IllegalArgumentException, IOException {
		T jclass = clazz.newInstance();
		jclass.key = key;
		// 反射获取所有变量
		// Field[] fields = jclass.getClass().getFields();
		// 数据库获取对应变量
		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
		List<String> paramList = Arrays.asList(params);
		List<String> datas = operations.multiGet(key, paramList);
		// 进行长度判定，如果获取长度不等于请求长度，则返回空
		int size = params.length;
		if (datas.size() != size)
			return null;

		for (int i = 0; i < size; i++) {
			Field field = jclass.getClass().getField(params[i]);
			// 排除静态属性
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			// 判断是否是不需要读写的数据
			JedisIgnore juu = field.getAnnotation(JedisIgnore.class);
			if (juu != null && juu.value())
				continue;
			// 拥有特殊类型标记的数据需要特殊读取
			JedisType jt = field.getAnnotation(JedisType.class);
			if (jt != null) {
				FieldUtil.SetJedisTypeParam(jt, jclass, field);
				continue;
			}
			String d = datas.get(i);
			if (d != null)
				// 根据下标给类中变量赋值
				FieldUtil.SetStrParam(jclass, field, datas.get(i));
		}
		jclass.init();
		return jclass;
	}

}
