package workflow.common.redis;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisZSetCommands.Limit;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;
import workflow.business.mapper.RedisDao;
import workflow.common.utils.FieldUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: JedisMgr
 * @Description: jedis管理
 * @author KaminanGTO
 * @date 2017年6月12日 下午5:54:09
 *
 */
@Component
@PropertySource("classpath:workflow.properties")
public class JedisMgr_wf {
	private static final Logger logger = LoggerFactory.getLogger(JedisMgr_wf.class);

	private static JedisMgr_wf _instance = null;

	/**
	 * @Fields KeySpan : Key的分隔符
	 */
	public static final String KeySpan = ":";

	/**
	 * @Fields KeyHead : Key的头数据
	 */
	public static String KeyHead = "workflow:";

	@Value("${workflowredis.keyhead}")
	public void setKeyHead(String KeyHead) {
		JedisMgr_wf.KeyHead = KeyHead;
	}

	public static JedisMgr_wf GetInstance() {
		return _instance;
	}

	@Autowired
	public StringRedisTemplate stringRedisTemplate;

	//@Autowired
	public RedisTemplate<String, Object> redisTemplate;

	public JedisMgr_wf() {
		_instance = this;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// 数据操作//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// ----------------------------value--------------------------
	public void setValue(String key, String value)
	{
		ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
		operations.set(key, value);
	}

	public void setValue(String key, String value, long timeout)
	{
		ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
		operations.set(key, value, timeout, TimeUnit.SECONDS);
	}

	// ----------------------------RedisService--------------------------

	public boolean hasKey(String key)
	{
		return stringRedisTemplate.hasKey(key);
	}

	/**
	* @Title: updateDao
	* @Description: 更新所有数据，没有数据则创建新数据
	* @param key  参数说明
	* @return void    返回类型
	 * @throws JsonProcessingException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	*
	*/
	@SuppressWarnings("rawtypes")
	public <T> void update(String key, T entity) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, JsonProcessingException
	{
		// 生成需要写入的普通数据
		Map<String, String> datas = new HashMap<String, String>();
		List<Field> fieldList = new ArrayList<>();
		Class tempClass = entity.getClass();
		while (tempClass != null) {// 当父类为null的时候说明到达了最上层的父类(Object类).
			fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
			tempClass = tempClass.getSuperclass(); // 得到父类,然后赋给自己
		}
		// Field[] fields = this.getClass().getFields();
		for (Field field : fieldList) {
			field.setAccessible(true);
			// 排除静态属性
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			// 判断是否是不需要读写的数据
			JedisIgnore juu = field.getAnnotation(JedisIgnore.class);
			if (juu != null && juu.value())
				continue;
			// 拥有特殊类型标记的数据写入到特定地方
//			JedisType jt = field.getAnnotation(JedisType.class);
//			if (jt != null) {
//				FieldUtil.UpdateJedisTypeParam(jt, entity, field);
//				continue;
//			}
			datas.put(field.getName(), FieldUtil.GetAsString(entity, field));
		}

		// 写入所有数据
		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
		operations.putAll(key, datas);
	}

	/**
	* @Title: update
	* @Description: 更新指定变量名的数据
	* @param key
	* @param entity
	* @param params
	* @throws NoSuchFieldException
	* @throws SecurityException
	* @throws IllegalArgumentException
	* @throws IllegalAccessException
	* @throws JsonProcessingException  参数说明
	* @return void    返回类型
	*
	*/
	@SuppressWarnings("rawtypes")
	public <T> void update(String key, T entity, String... params) throws NoSuchFieldException,
		SecurityException, IllegalArgumentException, IllegalAccessException, JsonProcessingException {
		// 生成需要写入的普通数据
		Map<String, String> datas = new HashMap<String, String>();
		Map<String, Field> fieldMap = new HashMap<String, Field>();
		List<Field> fieldList = new ArrayList<>();
		Class tempClass = entity.getClass();
		while (tempClass != null) {// 当父类为null的时候说明到达了最上层的父类(Object类).
			fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
			Field[] fields = tempClass.getDeclaredFields();
			for (Field field : fields) {
				fieldMap.put(field.getName(), field);
			}
			tempClass = tempClass.getSuperclass(); // 得到父类,然后赋给自己
		}
		// 循环获取需要保存的变量
		for (String param : params) {
			//Field field = this.getClass().getField(param);
			Field field = fieldMap.get(param);
			field.setAccessible(true);
			// 排除静态属性
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			// 判断是否是不需要读写的数据
			JedisIgnore juu = field.getAnnotation(JedisIgnore.class);
			if (juu != null && juu.value())
				continue;
			// 拥有特殊类型标记的数据写入到特定地方
//			JedisType jt = field.getAnnotation(JedisType.class);
//			if (jt != null) {
//				FieldUtil.UpdateJedisTypeParam(jt, entity, field);
//				continue;
//			}

			datas.put(field.getName(), FieldUtil.GetAsString(entity, field));
		}
		// 判断是否有正常需要写入数据
		if (datas.size() > 0) {
			// 写入所有数据
			HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
			operations.putAll(key, datas);
		}
	}

	/**
	* @Title: delete
	* @Description: 删除数据
	* @param key  参数说明
	* @return void    返回类型
	*
	*/
	public void delete(String key)
	{
		stringRedisTemplate.delete(key);
	}

	/**
	* @Title: deleteParam
	* @Description: 删除指定key
	* @param key
	* @param keys  参数说明
	* @return void    返回类型
	*
	*/
	public void deleteParam(String key, String... keys) {
		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
		operations.delete(key, keys);
//		List<String> keyList = Arrays.asList(keys);
//		deleteParam(key, keyList);
	}

	/**
	* @Title: deleteParam
	* @Description: 删除指定key
	* @param mapper
	* @param keyList  参数说明
	* @return void    返回类型
	*
	*/
//	public void deleteParam(String key, List<String> keyList) {
//		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
//		operations.delete(key, keyList);
//	}

	/**
	 * @Title: updateOnce
	 * @Description: 更新单个数据
	 * @param key
	 * @param name
	 * @param value
	 *            参数说明
	 * @return void 返回类型
	 *
	 */
	public <T extends RedisDao<?>> void updateOnce(String key, String name, String value) {
		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
		operations.put(key, name, value);
	}

	/**
	 * @Title: load
	 * @Description: 从redis加载数据，当数据不存在时，返回空实体
	 * @param key
	 * @param clazz
	 * @return 参数说明
	 * @return T 返回类型
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws ClassNotFoundException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 *
	 */
	@SuppressWarnings("rawtypes")
	public <T> T get(String key, Class<T> clazz)
			throws InstantiationException, IllegalAccessException, JsonParseException, JsonMappingException,
			ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IOException {

		if(!stringRedisTemplate.hasKey(key))
			return null;

		T jclass = clazz.newInstance();

		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();

		Map<String, String> datas = operations.entries(key);
		// 进行空判定
		if (datas.size() == 0)
			return null;
		// 反射获取所有变量
		// Field[] fields = jclass.getClass().getDeclaredFields();
		List<Field> fieldList = new ArrayList<>();
		Class tempClass = clazz;
		while (tempClass != null) {// 当父类为null的时候说明到达了最上层的父类(Object类).
			fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
			tempClass = tempClass.getSuperclass(); // 得到父类,然后赋给自己
		}
		for (Field field : fieldList) {
			field.setAccessible(true);
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
		return jclass;
	}

	@SuppressWarnings("rawtypes")
	public <T> T get(String key, Class<T> clazz, String... params)
			throws InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException,
			JsonParseException, JsonMappingException, ClassNotFoundException, IllegalArgumentException, IOException {

		if(!stringRedisTemplate.hasKey(key))
			return null;

		T jclass = clazz.newInstance();
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
		// 反射获取所有变量
		// Field[] fields = jclass.getClass().getDeclaredFields();
		Map<String, Field> fieldMap = new HashMap<String, Field>();
		List<Field> fieldList = new ArrayList<>();
		Class tempClass = clazz;
		while (tempClass != null) {// 当父类为null的时候说明到达了最上层的父类(Object类).
			fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
			Field[] fields = tempClass.getDeclaredFields();
			for (Field field : fields) {
				fieldMap.put(field.getName(), field);
			}
			tempClass = tempClass.getSuperclass(); // 得到父类,然后赋给自己
		}

		for (int i = 0; i < size; i++) {
			// Field field = jclass.getClass().getDeclaredField(params[i]);
			Field field = fieldMap.get(params[i]);
			field.setAccessible(true);
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
		return jclass;
	}

	// ----------------------------Hash--------------------------

	public <T> void updateHash(String key, String hashKey, T t) throws JsonProcessingException
	{
		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
		String value = JSONObject.toJSONString(t);
		//String value = FieldUtil.objectMapper.writeValueAsString(t);
		operations.put(key, hashKey, value);
	}

	public void updateHash(String key, Map<String, String> datas)
	{
		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
		operations.putAll(key, datas);
	}

	public <T> T getHash(String key, String hashKey, Class<T> t) throws JsonParseException, JsonMappingException, IOException
	{
		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
		String value = operations.get(key, hashKey);
		return JSONObject.parseObject(value, t);
		//return FieldUtil.objectMapper.readValue(value, t);
	}

	public Map<String, String> getHash(String key)
	{
		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
		return operations.entries(key);
	}

	/**
	 * 获取key中的具体hash属性
	 * @param key
	 * @param hashKeys
	 * @param t
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public <T> List<T> getHash(String key, List<String> hashKeys, Class<T> t) throws JsonParseException, JsonMappingException, IOException
	{
		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
		List<String> values = operations.multiGet(key, hashKeys);
		List<T> list = new ArrayList<T>();
		for(String value : values)
		{
			try{
				if(value != null)
				{
					list.add(JSONObject.parseObject(value, t));
					//list.add(FieldUtil.objectMapper.readValue(value, t));
				}
			} catch(Exception ex) {
				logger.error("getHash List", ex);
			}
		}
		return list;
	}

	public void deleteHash(String key, String... ids)
	{
		HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
		operations.delete(key, ids);
	}

	// ----------------------------List--------------------------

	/**
	 * @Title: UpdateList
	 * @Description: 更新列表型数据（完全覆盖），如数据不存在时创建新数据
	 * @param key
	 * @param datas
	 * @return
	 * @throws JsonProcessingException
	 *             参数说明
	 * @return long 返回类型
	 *
	 */
	@SuppressWarnings("unchecked")
	public <T> long updateList(String key, T... datas) throws JsonProcessingException {
		String[] strlist = FieldUtil.MakeStringArray(datas);
		stringRedisTemplate.delete(key);
		ListOperations<String, String> operations = stringRedisTemplate.opsForList();
		return operations.leftPushAll(key, strlist);
	}

	/**
	 * @Title: UpdateList
	 * @Description: 更新列表型数据（完全覆盖），如数据不存在时创建新数据
	 * @param key
	 * @param key
	 * @param listdata
	 *            参数说明
	 * @return void 返回类型
	 * @throws JsonProcessingException
	 *
	 */
	public <T> long updateList(String key, Collection<T> listdata) throws JsonProcessingException {
		String[] strlist = FieldUtil.MakeStringArray(listdata);
		stringRedisTemplate.delete(key);
		ListOperations<String, String> operations = stringRedisTemplate.opsForList();
		return operations.rightPushAll(key, strlist);
	}

	/**
	 * @Title: UpdateListOnce
	 * @Description: 更新列表中单个数据
	 * @param key
	 * @param key
	 * @param index
	 * @param data
	 * @return 参数说明
	 * @return String 返回类型
	 * @throws JsonProcessingException
	 *
	 */
	public <T> void updateListOnce(String key, long index, T data) throws JsonProcessingException {
		ListOperations<String, String> operations = stringRedisTemplate.opsForList();
		operations.set(key, index, FieldUtil.GetAsString(data));
	}

	/**
	 * @Title: PushList
	 * @Description: 添加列表数据（默认加在最右边）
	 * @param key
	 *            参数说明
	 * @return void 返回类型
	 * @throws JsonProcessingException
	 *
	 */
	@SuppressWarnings("unchecked")
	public <T> long pushList(String key, T... datas) throws JsonProcessingException {

		return pushList(key, datas, true);
	}

	/**
	 * @Title: PushList
	 * @Description: 添加列表数据
	 * @param key
	 * @param isRight
	 * @param datas
	 * @return
	 * @throws JsonProcessingException
	 *             参数说明
	 * @return long 返回类型
	 *
	 */
	@SuppressWarnings("unchecked")
	public <T> long pushList(String key, boolean isRight, T... datas) throws JsonProcessingException {
		ListOperations<String, String> operations = stringRedisTemplate.opsForList();

		if (isRight)
			return operations.rightPushAll(key, FieldUtil.MakeStringArray(datas));
		else
			return operations.leftPushAll(key, FieldUtil.MakeStringArray(datas));
	}

	/**
	 * @Title: PushList
	 * @Description: 添加列表数据（默认加在最右边）
	 * @param key
	 * @param listdata
	 *            参数说明
	 * @return void 返回类型
	 * @throws JsonProcessingException
	 *
	 */
	public <T> long pushList(String key, List<T> listdata) throws JsonProcessingException {
		return pushList(key, listdata, true);
	}

	/**
	 * @Title: PushList
	 * @Description: 添加列表数据
	 * @param key
	 * @param isRight
	 * @param listdata
	 * @return
	 * @throws JsonProcessingException
	 *             参数说明
	 * @return long 返回类型
	 *
	 */
	public <T> long pushList(String key, boolean isRight, List<T> listdata) throws JsonProcessingException {
		ListOperations<String, String> operations = stringRedisTemplate.opsForList();

		if (isRight)
			return operations.rightPushAll(key, FieldUtil.MakeStringArray(listdata));
		else
			return operations.leftPushAll(key, FieldUtil.MakeStringArray(listdata));
	}

	/**
	 * @Title: ListPop
	 * @Description: 列表移除单个数据（从列表头移除）
	 * @param key
	 * @return 参数说明
	 * @return String 返回移除的数据
	 *
	 */
	public String listPop(String key) {
		return listPop(key, true);
	}

	/**
	 * @Title: ListPop
	 * @Description: 列表移除单个数据
	 * @param key
	 * @param isTop
	 *            是否从头部移除（为false时从尾部删除）
	 * @return 参数说明
	 * @return String 返回移除的数据
	 *
	 */
	public String listPop(String key, boolean isTop) {
		ListOperations<String, String> operations = stringRedisTemplate.opsForList();

		if (isTop)
			return operations.leftPop(key);
		else
			return operations.rightPop(key);
	}

	/**
	 * @Title: GetListLen
	 * @Description: 获取列表长度，如key不存在，返回0
	 * @param key
	 * @return 参数说明
	 * @return Long 返回类型
	 *
	 */
	public Long getListLength(String key) {
		ListOperations<String, String> operations = stringRedisTemplate.opsForList();

		return operations.size(key);
	}

	/**
	 * @Title: GetListIndex
	 * @Description: 获取列表中，Index下标的元素 头元素下表从0开始，1为第二个元素
	 *               使用负数也可以直接取得尾元素：-1为倒数第一个，-2倒数第二个元素
	 * @param key
	 * @param index
	 * @return 参数说明
	 * @return String 返回类型
	 *
	 */
	public String getListIndex(String key, long index) {
		ListOperations<String, String> operations = stringRedisTemplate.opsForList();
		return operations.index(key, index);
	}

	/**
	 * @Title: GetListIndex
	 * @Description: 获取列表中，Index下标的元素，并转换成实体--该方法不实现IJedisData接口数据，如需要
	 *               转换IJedisData接口数据，请调用{@link JedisMgr_wf.GetListIndexEX}
	 *               头元素下表从0开始，1为第二个元素 使用负数也可以直接取得尾元素：-1为倒数第一个，-2倒数第二个元素
	 * @param key
	 * @param index
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 *             参数说明
	 * @return T 返回类型
	 *
	 */
	public <T> T getListIndex(String key, long index, Class<T> clazz) throws JsonParseException, JsonMappingException,
			InstantiationException, IllegalAccessException, IOException {
		String strdata = getListIndex(key, index);
		return FieldUtil.StrToObject(clazz, strdata);
	}

	/**
	 * @Title: GetListIndexEX
	 * @Description: 获取列表中，Index下标的元素，并转换成IJedisData实体 头元素下表从0开始，1为第二个元素
	 *               使用负数也可以直接取得尾元素：-1为倒数第一个，-2倒数第二个元素
	 * @param key
	 * @param index
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 *             参数说明
	 * @return T 返回类型
	 *
	 */
	public <T extends IJedisData> T getListIndexEX(String key, long index, Class<T> clazz) throws JsonParseException,
			JsonMappingException, InstantiationException, IllegalAccessException, IOException {
		String strdata = getListIndex(key, index);
		return FieldUtil.StrToJedisData(clazz, strdata);
	}

	/**
	 * @Title: GetListRange
	 * @Description: 获得key 中指定区间内的元素，区间以偏移量 start 和 end 指定（分页）
	 *               注意：end也在取值范围内，是闭区间取值 获取所有start=0, end=-1
	 * @param key
	 * @param start
	 * @param end
	 * @return 参数说明
	 * @return List<String> 返回类型
	 *
	 */
	public List<String> getListRange(String key, long start, long end) {
		ListOperations<String, String> operations = stringRedisTemplate.opsForList();
		return operations.range(key, start, end);
	}

	/**
	 * @Title: GetListRange
	 * @Description: 获得key 中指定区间内的元素并转换成对象列表，该方法不实现IJedisData接口数据，如需要
	 *               转换IJedisData接口数据，请调用{@link JedisMgr_wf.GetListRangeEx} ，区间以偏移量
	 *               start 和 end 指定（分页） 注意：end也在取值范围内，是闭区间取值 获取所有start=0, end=-1
	 * @param key
	 * @param start
	 * @param end
	 * @param clazz
	 * @return 参数说明
	 * @return List<T> 返回类型
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 *
	 */
	public <T> List<T> getListRange(String key, long start, long end, Class<T> clazz) throws JsonParseException,
			JsonMappingException, InstantiationException, IllegalAccessException, IOException {
		return FieldUtil.StrListToObjList(getListRange(key, start, end), clazz);
	}

	/**
	 * @Title: GetListRange
	 * @Description: 获得key 中指定区间内的元素并转换成对象列表，该方法不实现IJedisData接口数据，如需要
	 *               转换IJedisData接口数据，请调用{@link JedisMgr_wf.GetListRangeEx} ，区间以偏移量
	 *               start 和 end 指定（分页） 注意：end也在取值范围内，是闭区间取值 获取所有start=0, end=-1
	 * @param key
	 * @param start
	 * @param end
	 * @param jt
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 *             参数说明
	 * @return Collection<T> 返回类型
	 *
	 */
	public <T> Collection<T> getListRange(String key, long start, long end, JavaType javatype, JedisType jt)
			throws JsonParseException, JsonMappingException, InstantiationException, IllegalAccessException,
			IOException {
		return FieldUtil.StrListToObjList(getListRange(key, start, end), javatype, jt);
	}

	/**
	 * @Title: GetListRangeEx
	 * @Description: 获得key 中指定区间内的元素并转换成IJedisData对象列表 ，区间以偏移量 start 和 end
	 *               指定（分页） 注意：end也在取值范围内，是闭区间取值 获取所有start=0, end=-1
	 * @param key
	 * @param start
	 * @param end
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 *             参数说明
	 * @return List<T> 返回类型
	 *
	 */
	public <T extends IJedisData> List<T> getListRangeEx(String key, long start, long end, Class<T> clazz)
			throws JsonParseException, JsonMappingException, InstantiationException, IllegalAccessException,
			IOException {
		return FieldUtil.StrListToJedisList(getListRange(key, start, end), clazz);
	}

	// ----------------------------Set--------------------------

	/**
	 * @Title: UpdateSet
	 * @Description: 更新Set数据（完全覆盖），如无数据时，创建新数据
	 * @param jedis
	 * @param key
	 * @param datas
	 * @return
	 * @throws JsonProcessingException
	 *             参数说明
	 * @return long 返回类型
	 *
	 */
	@SuppressWarnings("unchecked")
	public <T> long updateSet(String key, T... datas) throws JsonProcessingException {
		SetOperations<String, String> operations = stringRedisTemplate.opsForSet();

		String[] strlist = FieldUtil.MakeStringArray(datas);
		stringRedisTemplate.delete(key);
		return operations.add(key, strlist);
	}

	/**
	 * @Title: UpdateSet
	 * @Description: 更新Set数据（完全覆盖），如无数据时，创建新数据
	 * @param jedis
	 * @param key
	 * @param setdata
	 * @return 参数说明
	 * @return long 返回类型
	 * @throws JsonProcessingException
	 *
	 */
	public <T> long updateSet(String key, Collection<T> setdata) throws JsonProcessingException {
		if (setdata == null)
			return 0;
		SetOperations<String, String> operations = stringRedisTemplate.opsForSet();

		String[] strlist = FieldUtil.MakeStringArray(setdata);
		stringRedisTemplate.delete(key);
		return operations.add(key, strlist);
	}

	/**
	 * @Title: PushSet
	 * @Description: 添加Set数据（已存在的数据将被忽略）
	 * @param jedis
	 * @param key
	 * @param datas
	 * @return 参数说明
	 * @return long 返回类型
	 * @throws JsonProcessingException
	 *
	 */
	@SuppressWarnings("unchecked")
	public <T> long pushSet(String key, T... datas) throws JsonProcessingException {
		SetOperations<String, String> operations = stringRedisTemplate.opsForSet();
		return operations.add(key, FieldUtil.MakeStringArray(datas));
	}

	/**
	 * @Title: PushSet
	 * @Description: 添加Set数据（已存在的数据将被忽略）
	 * @param jedis
	 * @param key
	 * @param setdata
	 * @return
	 * @throws JsonProcessingException
	 *             参数说明
	 * @return long 返回类型
	 *
	 */
	public <T> long pushSet(String key, Set<T> setdata) throws JsonProcessingException {
		if (setdata == null)
			return 0;
		SetOperations<String, String> operations = stringRedisTemplate.opsForSet();

		return operations.add(key, FieldUtil.MakeStringArray(setdata));
	}

	/**
	 * @Title: RemoveSet
	 * @Description: 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
	 * @param jedis
	 * @param key
	 * @param members
	 * @return 参数说明
	 * @return long 返回类型
	 *
	 */
	public long removeSet(String key, String members) {
		SetOperations<String, String> operations = stringRedisTemplate.opsForSet();

		return operations.remove(key, members);
	}

	/**
	 * @Title: GetSetLength
	 * @Description: 获取set长度
	 * @param jedis
	 * @param key
	 * @return 参数说明
	 * @return long 返回类型
	 *
	 */
	public long getSetLength(String key) {
		SetOperations<String, String> operations = stringRedisTemplate.opsForSet();

		return operations.size(key);
	}

	/**
	 * @Title: GetSetDiff
	 * @Description: 获取一个集合的全部成员，该集合是所有给定集合之间的差集 即返回该集合在其他集合没有的成员
	 * @param jedis
	 * @param keys
	 * @return 参数说明
	 * @return Collection<String> 返回类型
	 *
	 */
	public Collection<String> getSetDiff(String... keys) {
		SetOperations<String, String> operations = stringRedisTemplate.opsForSet();
		int length = keys.length;
		if (length > 1) {
			// 如果key数量大于1，则使用多集合查询
			List<String> otherKeys = new ArrayList<String>();
			for (int i = 1; i < length; i++) {
				otherKeys.add(keys[i]);
			}
			return operations.difference(keys[0], otherKeys);
		}
		return operations.members(keys[0]);

	}

	/**
	 * @Title: GetSetDiff
	 * @Description: 获取一个集合的全部成员，该集合是所有给定集合之间的差集 即返回该集合在其他集合没有的成员
	 *               并转化为对象列表--该方法不实现IJedisData接口数据，如需要
	 *               转换IJedisData接口数据，请调用{@link JedisMgr_wf.GetSetDiffEx}
	 * @param jedis
	 * @param clazz
	 * @param keys
	 * @return 参数说明
	 * @return Collection<T> 返回类型
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 *
	 */
	public <T> Collection<T> getSetDiff(Class<T> clazz, String... keys) throws JsonParseException, JsonMappingException,
			InstantiationException, IllegalAccessException, IOException {
		return FieldUtil.StrListToObjList(getSetDiff(keys), clazz);
	}

	/**
	 * @Title: GetSetDiff
	 * @Description: 获取一个集合的全部成员，该集合是所有给定集合之间的差集 即返回该集合在其他集合没有的成员
	 *               并转化为对象列表--该方法不实现IJedisData接口数据，如需要
	 *               转换IJedisData接口数据，请调用{@link JedisMgr_wf.GetSetDiffEx}
	 * @param jedis
	 * @param clazz
	 * @param keys
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 *             参数说明
	 * @return Collection<T> 返回类型
	 *
	 */
	public <T> Collection<T> getSetDiff(JavaType javatype, JedisType jt, String... keys) throws JsonParseException,
			JsonMappingException, InstantiationException, IllegalAccessException, IOException {
		return FieldUtil.StrListToObjList(getSetDiff(keys), javatype, jt);
	}

	/**
	 * @Title: GetSetDiffEx
	 * @Description: 获取一个集合的全部成员，该集合是所有给定集合之间的差集 即返回该集合在其他集合没有的成员
	 *               并转化为IJedisData对象列表
	 * @param jedis
	 * @param clazz
	 * @param keys
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 *             参数说明
	 * @return Collection<T> 返回类型
	 *
	 */
	public <T extends IJedisData> Collection<T> getSetDiffEx(Class<T> clazz, String... keys) throws JsonParseException,
			JsonMappingException, InstantiationException, IllegalAccessException, IOException {
		return FieldUtil.StrListToJedisList(getSetDiff(keys), clazz);
	}

	// ----------------------------SortSet--------------------------

	/**
	 * @Title: UpdateSortSet
	 * @Description: 更新SortSet数据（完全覆盖），如无数据时，创建新数据
	 * @param jedis
	 * @param key
	 * @param mapdata
	 * @return
	 * @throws JsonProcessingException
	 *             参数说明
	 * @return long 返回类型
	 *
	 */
	public <T> long updateSortSet(String key, Map<T, Double> mapdata) throws JsonProcessingException {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
		Set<TypedTuple<String>> ssetmap = FieldUtil.MakeSortSetMap(mapdata);
		stringRedisTemplate.delete(key);
		return operations.add(key, ssetmap);
	}

	/**
	 * @Title: UpdateSortSet
	 * @Description: 更新SortSet数据（完全覆盖），如无数据时，创建新数据
	 * @param jedis
	 * @param key
	 * @param listdata
	 * @return 参数说明
	 * @return long 返回类型
	 *
	 */
	public <T extends IJedisSortSet> long updateSortSet(String key, Collection<T> listdata) {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
		Set<TypedTuple<String>> ssetmap = FieldUtil.MakeSortSetMap(listdata);
		stringRedisTemplate.delete(key);
		return operations.add(key, ssetmap);
	}

	/**
	 * @Title: PushSortSet
	 * @Description: 添加SortSet数据（已存在的key将更新score）
	 * @param key
	 * @param datas
	 * @return 参数说明
	 * @return long 返回类型
	 *
	 */
	@SuppressWarnings("unchecked")
	public <T extends IJedisSortSet> long pushSortSet(String key, T... datas) {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
		return operations.add(key, FieldUtil.MakeSortSetMap(datas));
	}

	/**
	 * @Title: PushSortSet
	 * @Description: 添加SortSet数据（已存在的key将更新score）
	 * @param key
	 * @param mapdata
	 * @return
	 * @throws JsonProcessingException
	 *             参数说明
	 * @return long 返回类型
	 *
	 */
	public <T> long pushSortSet(String key, Map<T, Double> mapdata) throws JsonProcessingException {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();

		return operations.add(key, FieldUtil.MakeSortSetMap(mapdata));
	}

	public boolean pushSortSet(String key, String value, Double score) throws JsonProcessingException {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();

		return operations.add(key, value, score);
	}

	/**
	* @Title: pushIncrSortSet
	* @Description: 添加自增长sortset数据。
	* @param key
	* @param value
	* @param defScore
	* @return  参数说明
	* @return Double    返回类型
	*
	*/
	public Double pushIncrSortSet(String key, String value, Double defScore)
	{
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
		return operations.incrementScore(key, value, defScore);
	}

	/**
	 * @Title: PushSortSetByStrMap
	 * @Description: 添加SortSet数据，根据strMap（已存在的key将更新score）
	 * @param jedis
	 * @param key
	 * @param mapdata
	 * @return
	 * @throws JsonProcessingException
	 *             参数说明
	 * @return long 返回类型
	 *
	 */
	public long pushSortSetByStrMap(String key, Set<TypedTuple<String>> mapdata) throws JsonProcessingException {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();

		return operations.add(key, mapdata);
	}

	/**
	 * @Title: PushSortSet
	 * @Description: 添加SortSet数据（已存在的key将更新score）
	 * @param jedis
	 * @param key
	 * @param listdata
	 * @return 参数说明
	 * @return long 返回类型
	 *
	 */
	public <T extends IJedisSortSet> long pushSortSet(String key, List<T> listdata) {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();

		return operations.add(key, FieldUtil.MakeSortSetMap(listdata));
	}

	/**
	 * @Title: PushSortSet
	 * @Description: 添加SortSet数据（已存在的key将更新score）
	 * @param jedis
	 * @param key
	 * @param listdata
	 * @return 参数说明
	 * @return long 返回类型
	 *
	 */
	public <T extends IJedisSortSet> long pushSortSet(String key, Set<T> setdata) {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();

		return operations.add(key, FieldUtil.MakeSortSetMap(setdata));
	}

	/**
	 * @Title: RemoveSortSet
	 * @Description: 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略
	 * @param jedis
	 * @param key
	 * @param members
	 * @return 参数说明
	 * @return long 返回类型
	 *
	 */
	public long removeSortSet(String key, String... members) {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
		return operations.remove(key, members);
	}

	/**
	 * @Title: RemoveSortSetRangeByRank
	 * @Description: 移除有序集 key 中，指定排名(rank)区间内的所有成员，包含 start 和 stop 在内
	 * @param jedis
	 * @param key
	 * @param start
	 * @param end
	 * @return 参数说明
	 * @return long 返回类型
	 *
	 */
	public long removeSortSetRangeByRank(String key, long start, long end) {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
		return operations.removeRange(key, start, end);
	}

	/**
	 * @Title: RemoveSortSetRangeByScore
	 * @Description: 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员
	 * @param jedis
	 * @param key
	 * @param start
	 * @param end
	 * @return 参数说明
	 * @return Long 返回类型
	 *
	 */
	public Long removeSortSetRangeByScore(String key, double start, double end) {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();

		return operations.removeRangeByScore(key, start, end);
	}

	/**
	 * @Title: GetSortSetLength
	 * @Description: 获取SortSet内的数量
	 * @param jedis
	 * @param key
	 * @return 参数说明
	 * @return long 返回类型
	 *
	 */
	public Long getSortSetLength(String key) {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();

		return operations.zCard(key);
	}

	/**
	 * @Title: GetSortSetLength
	 * @Description: 获取SortSet内的数量，根据min和max区间获取
	 * @param jedis
	 * @param key
	 * @param min
	 * @param max
	 * @return 参数说明
	 * @return long 返回类型
	 *
	 */
	// public long GetSortSetLength(String key, String min, String max) {
	// ZSetOperations<String, String> operations =
	// stringRedisTemplate.opsForZSet();
	// operations.
	// return operations.
	// }

	/**
	 * @Title: GetSortSetLength
	 * @Description: 获取SortSet内的数量，根据score的min和max区间获取
	 * @param jedis
	 * @param key
	 * @param min
	 * @param max
	 * @return 参数说明
	 * @return long 返回类型
	 *
	 */
	public Long getSortSetLength(String key, Double min, Double max) {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
		return operations.count(key, min, max);
	}

	/**
	 * @Title: GetSortSetList
	 * @Description: 返回有序集 key 中，指定区间内的成员，从0开始，-1可取最后一个
	 *               注意，当前此方法只返回list类型，如果需要set类型，需要后续扩展
	 * @param jedis
	 * @param key
	 * @param start
	 * @param end
	 * @return 参数说明
	 * @return Collection<IJedisSortSet> 返回类型
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 *
	 */
	public <T extends IJedisSortSet> Collection<IJedisSortSet> getSortSetList(String key, long start, long end,
			Class<T> clazz, JedisType jt) throws InstantiationException, IllegalAccessException {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();

		return FieldUtil.TupleListToJSSList(operations.rangeWithScores(key, start, end), clazz, jt);
	}

	/**
	 * @Title: GetSortSetListByLex
	 * @Description: 根据value中的值，判断大小来取区间数据
	 * @param jedis
	 * @param key
	 * @param min
	 * @param max
	 * @return 参数说明
	 * @return Set<String> 返回类型
	 *
	 */
	public Set<String> getSortSetListByLex(String key, String min, String max) {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
		Range range = new Range();
		range.gte(min);
		range.lte(max);
		return operations.rangeByLex(key, range);
	}

	/**
	 * @Title: GetSortSetListByLex
	 * @Description: 根据value中的值，判断大小来取区间数据 ，分页获取，从offset取count个数据
	 * @param jedis
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return 参数说明
	 * @return Set<String> 返回类型
	 *
	 */
	public Set<String> getSortSetListByLex(String key, String min, String max, int offset, int count) {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
		Range range = new Range();
		range.gte(min);
		range.lte(max);
		Limit limit = new Limit();
		limit.count(count);
		limit.offset(offset);
		return operations.rangeByLex(key, range, limit);
	}

	public Set<TypedTuple<String>> getSortSetListWithScores(String key, long offset, long count) {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
		return operations.rangeWithScores(key, offset, count);

	}

	public Set<TypedTuple<String>> getSortSetListWithScores(String key, double min, double max, long offset,
			long count) {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
		return operations.rangeByScoreWithScores(key, min, max, offset, count);
	}

	public Set<TypedTuple<String>> getSortSetRevListWithScores(String key, long offset, long count) {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
		return operations.reverseRangeWithScores(key, offset, count);

	}

	public Set<TypedTuple<String>> getSortSetRevListWithScores(String key, double min, double max, long offset,
			long count) {
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
		return operations.reverseRangeByScoreWithScores(key, min, max, offset, count);
	}

	/**
	* @Title: getSortSetScore
	* @Description: 获取sortset得分
	* @param key
	* @param value
	* @return  参数说明
	* @return Double    返回类型
	*
	*/
	public Double getSortSetScore(String key, String value)
	{
		ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
		return operations.score(key, value);
	}

}
