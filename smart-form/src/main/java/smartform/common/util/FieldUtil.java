package smartform.common.util;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import smartform.common.redis.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author KaminanGTO
 * @ClassName: FieldUtil
 * @Description: Field解析工具
 * @date 2017年6月12日 下午6:45:00
 */
public class FieldUtil {

    // public static final ObjectMapper objectMapper = new ObjectMapper();

    // static {
    // objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    // objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
    // false); // work
    // // for
    // // compatible
    // // with
    // // old
    // objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
    // true);
    // objectMapper.setSerializationInclusion(Include.NON_NULL);
    //
    // // version
    // }
    /**
     * fastJson 序列化时写入类型信息
     */
    static SerializerFeature[] features;

    static {
        features = new SerializerFeature[]{SerializerFeature.WriteClassName,
                // SerializerFeature.SkipTransientField,
                // SerializerFeature.DisableCircularReferenceDetect
        };
    }

    /**
     * @param clazz
     * @return Map<String, String> 返回类型
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws JsonProcessingException
     * @Title: ClassToMap
     * @Description: 把类的属性转换成字符串数组
     */
    @Deprecated
    public static <T> Map<String, String> ClassToMap(T clazz)
            throws IllegalArgumentException, IllegalAccessException, JsonProcessingException {
        Map<String, String> datas = new HashMap<String, String>();
        Field[] fields = clazz.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 判断是否是不需要读写的数据
            JsonIgnore juu = field.getAnnotation(JsonIgnore.class);
            if (juu != null && juu.value())
                continue;
            // 拥有特殊类型标记的数据也不写入
            if (field.getAnnotation(JedisType.class) != null)
                continue;

            datas.put(field.getName(), GetAsString(clazz, field));
        }
        return datas;
    }

    /**
     * @param datas
     * @return String[] 返回类型
     * @throws JsonProcessingException
     * @Title: MakeStringArray
     * @Description: 其他类型数据转字符串数组
     */
    @SuppressWarnings("unchecked")
    public static <T> String[] MakeStringArray(T... datas) throws JsonProcessingException {
        int size = datas.length;
        String[] strlist = new String[size];
        for (int i = 0; i < size; i++) {
            strlist[i] = FieldUtil.GetAsString(datas[i]);
        }
        return strlist;
    }

    /**
     * @param listdata
     * @return String[] 返回类型
     * @throws JsonProcessingException 参数说明
     * @Title: MakeStringArray
     * @Description: Set类型数据转字符串数组
     */
    public static <T> String[] MakeStringArray(Collection<T> setdata) throws JsonProcessingException {
        int size = setdata.size();
        String[] strlist = new String[size];
        Iterator<T> it = setdata.iterator();
        int idx = 0;
        while (it.hasNext()) {
            strlist[idx++] = FieldUtil.GetAsString(it.next());
        }
        return strlist;
    }

    /**
     * @param datas
     * @return Map<String, Double> 返回类型
     * @Title: MakeSortSetMap
     * @Description: IJedisSortSet列表转字典
     */
    @SuppressWarnings("unchecked")
    public static <T extends IJedisSortSet> Set<TypedTuple<String>> MakeSortSetMap(T... datas) {
        Set<TypedTuple<String>> ssetmap = new HashSet<TypedTuple<String>>();
        for (IJedisSortSet ijss : datas) {
            ssetmap.add(new RedisTuple(ijss.GetStrKey(), ijss.GetScore()));
        }
        return ssetmap;
    }

    /**
     * @param datas
     * @return Map<String, Double> 返回类型
     * @Title: MakeSortSetMap
     * @Description: IJedisSortSet列表转字典
     */
    public static <T extends IJedisSortSet> Set<TypedTuple<String>> MakeSortSetMap(Collection<T> datas) {
        Set<TypedTuple<String>> ssetmap = new HashSet<TypedTuple<String>>();
        for (IJedisSortSet ijss : datas) {
            ssetmap.add(new RedisTuple(ijss.GetStrKey(), ijss.GetScore()));
        }
        return ssetmap;
    }

    /**
     * @param mapdata
     * @return Map<String, Double> 返回类型
     * @throws JsonProcessingException
     * @Title: MakeSortSetMap
     * @Description: IJedisSortSet列表转字典
     */
    public static <T> Set<TypedTuple<String>> MakeSortSetMap(Map<T, Double> mapdata) throws JsonProcessingException {
        Set<TypedTuple<String>> ssetmap = new HashSet<TypedTuple<String>>();
        Set<Entry<T, Double>> set = mapdata.entrySet();
        Iterator<Entry<T, Double>> iter = set.iterator();
        while (iter.hasNext()) {
            Entry<T, Double> data = iter.next();
            ssetmap.add(new RedisTuple(GetAsString(data.getKey()), data.getValue()));

        }
        return ssetmap;

    }

    /**
     * @param strlist
     * @param classzz
     * @return List<T> 返回类型
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @Title: StrListToObjList
     * @Description: 字符串列表，转对象列表，此方法不处理IJedisData类型对象 IJedisData对象请使用
     * {@link FieldUtil#StrListToJedisList}
     */
    public static <T> List<T> StrListToObjList(Collection<String> strlist, Class<T> clazz) throws JsonParseException,
            JsonMappingException, InstantiationException, IllegalAccessException, IOException {
        List<T> objlist = new ArrayList<T>();
        for (String strdata : strlist) {
            objlist.add(StrToObject(clazz, strdata));
        }
        return objlist;
    }

    /**
     * @param strlist
     * @param jt
     * @return Collection<T> 返回类型
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException            参数说明
     * @Title: StrListToObjList
     * @Description: 字符串列表，转对象列表，此方法不处理IJedisData类型对象 IJedisData对象请使用
     * {@link FieldUtil#StrListToJedisList}
     */
    public static <T> Collection<T> StrListToObjList(Collection<String> strlist, JavaType javatype, JedisType jt)
            throws JsonParseException, JsonMappingException, InstantiationException, IllegalAccessException,
            IOException {
        Collection<T> objlist;
        // if(jt.jType() == JType.LIST)
        objlist = new ArrayList<T>();
        // else
        // objlist = new HashSet<T>();
        for (String strdata : strlist) {
            objlist.add(StrToObject(javatype, strdata));
        }
        return objlist;
    }

    /**
     * @param strlist
     * @param clazz
     * @return List<T> 返回类型
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException            参数说明
     * @Title: StrListToJedisList
     * @Description: 字符串列表，转IJedisData对象列表
     */
    public static <T extends IJedisData> List<T> StrListToJedisList(Collection<String> strlist, Class<T> clazz)
            throws JsonParseException, JsonMappingException, InstantiationException, IllegalAccessException,
            IOException {
        List<T> objlist = new ArrayList<T>();
        for (String strdata : strlist) {
            objlist.add(StrToJedisData(clazz, strdata));
        }
        return objlist;
    }

    /**
     * @param <T>
     * @param field
     * @return String 返回类型
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws JsonProcessingException
     * @Title: GetAsString
     * @Description: 根据field数据转换成str数据
     */
    public static <T> String GetAsString(T clazz, Field field)
            throws IllegalArgumentException, IllegalAccessException, JsonProcessingException {
        // 判断是否是jedis属性类
        if (clazz instanceof IJedisData) {
            IJedisData ijd = (IJedisData) clazz;
            return ijd.ToJedisString();
        }
        if ("java.lang.String".equals(field.getType().getName())) {
            return String.valueOf(field.get(clazz));
        } else if ("java.util.Date".equals(field.getType().getName())) {
            Object date = field.get(clazz);
            if (date != null)
                return String.valueOf(((Date) date).getTime());
        }
        return JSONObject.toJSONString(field.get(clazz), features);
        // return objectMapper.writeValueAsString(field.get(clazz));
    }

    /**
     * @param data
     * @return String 返回类型
     * @throws JsonProcessingException 参数说明
     * @Title: GetAsString
     * @Description: 根据对象转换成str数据
     */
    public static <T> String GetAsString(T data) throws JsonProcessingException {
        if (data instanceof IJedisData) {
            IJedisData ijd = (IJedisData) data;
            return ijd.ToJedisString();
        }
        return JSONObject.toJSONString(data);
        // return objectMapper.writeValueAsString(data);
    }

    /**
     * @param clazz
     * @param field
     * @param strdata
     * @return boolean 返回类型
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @Title: SetStrParam
     * @Description: 根据str数据，转换对应需要的数据，写入class
     */
    public static <T> boolean SetStrParam(T clazz, Field field, String strdata)
            throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, InstantiationException,
            JsonParseException, JsonMappingException, IOException {
        if (clazz instanceof IJedisData) {
            IJedisData ijd = (IJedisData) clazz;
            return ijd.SetAsJedisString(strdata);
        }
        // System.out.println(strdata);
        if ("java.lang.String".equals(field.getType().getName())) {
            if (!strdata.equals("null"))
                field.set(clazz, strdata);
            return true;
        } else if ("java.util.Date".equals(field.getType().getName())) {
            try {
                if (!strdata.equals("null")) {
                    Date date = new Date(Long.parseLong(strdata));
                    field.set(clazz, date);
                }
            } catch (NumberFormatException ex) {
            }
            return true;
        }
        Type type = MakeJsonType(field);
        try {
            field.set(clazz, JSONObject.parseObject(strdata, type));
        } catch (JSONException ex) {
        }
        // JavaType javaType = MakeJavaType(field);
        // field.set(clazz, objectMapper.readValue(strdata, javaType));
        return true;
    }

    /**
     * @param jedis
     * @param jt
     * @param clazz
     * @param field
     * @return boolean 返回类型
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws InstantiationException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @Title: SetJedisTypeParam
     * @Description: 给JedisBaseDoc中使用JedisType标记的变量从数据库获取并赋值
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean SetJedisTypeParam(JedisType jt, T clazz, Field field)
            throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException, JsonParseException, JsonMappingException, InstantiationException, IOException {
        // 获取key值
        String key = jt.key().length() == 0 ? field.getName() : jt.key();
        if (jt.paramKey().length() > 0) {
            Field f = clazz.getClass().getField(jt.paramKey());
            key += String.valueOf(f.get(clazz));
        }
        // 根据不同的类型，读取对应的数据并赋值
        switch (jt.jType()) {
            case MAP: // map类型暂无需要单独储蓄的需求
            {
                return false;
            }
            case SET: {
                // JavaType javatype = MakeJavaType(field);
                // field.set(clazz,
                // JedisMgr.GetInstance().getSetDiff(javatype.getContentType(), jt,
                // key));
            }
            break;
            case LIST: {
                // JavaType javatype = MakeJavaType(field);
                // field.set(clazz, JedisMgr.GetInstance().getListRange(key, 0, -1,
                // javatype.getContentType(), jt));
            }
            break;
            case SORTSET: {
                // JavaType javatype = MakeJavaType(field);
                // field.set(clazz, JedisMgr.GetInstance().getSortSetList(key, 0,
                // -1,
                // (Class<IJedisSortSet>)
                // Class.forName(javatype.getContentType().toCanonical()), jt));
            }
            break;
            default:
                return false;
        }
        return true;
    }

    /**
     * @param jedis
     * @param jt
     * @param clazz
     * @param field
     * @return String 返回类型
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws JsonProcessingException
     * @throws InstantiationException
     * @throws IOException              参数说明
     * @Title: GetJedisTypeToJson
     * @Description: 读取JedisType数据并转换成json字符串
     */
    @SuppressWarnings("unchecked")
    public static <T> String GetJedisTypeToJson(JedisType jt, T clazz, Field field) throws NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException,
            JsonParseException, JsonMappingException, JsonProcessingException, InstantiationException, IOException {
        String value = "";
        // 获取key值
        String key = jt.key().length() == 0 ? field.getName() : jt.key();
        if (jt.paramKey().length() > 0) {
            Field f = clazz.getClass().getField(jt.paramKey());
            key += String.valueOf(f.get(clazz));
        }
        // 根据不同的类型，读取对应的数据并赋值
        switch (jt.jType()) {
            case MAP: // map类型暂无需要单独储蓄的需求
            {
                return value;
            }
            case SET: {
                // JavaType javatype = MakeJavaType(field);
                // value =
                // GetAsString(JedisMgr.GetInstance().getSetDiff(javatype.getContentType(),
                // jt, key));
            }
            break;
            case LIST: {
                // JavaType javatype = MakeJavaType(field);
                // value = GetAsString(JedisMgr.GetInstance().getListRange(key, 0,
                // -1, javatype.getContentType(), jt));
            }
            break;
            case SORTSET: {
                // JavaType javatype = MakeJavaType(field);
                // value = GetAsString(JedisMgr.GetInstance().getSortSetList(key, 0,
                // -1,
                // (Class<IJedisSortSet>)
                // Class.forName(javatype.getContentType().toCanonical()), jt));
            }
            break;
            default:
                return value;
        }
        return value;
    }

    /**
     * @param jedis
     * @param jt
     * @param clazz
     * @param field 参数说明
     * @return void 返回类型
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws JsonProcessingException
     * @Title: UpdateJedisTypeParam
     * @Description: 保存JedisBaseDoc中使用JedisType标记的变量到数据库（完全覆盖方式）
     */
    @SuppressWarnings("unchecked")
    public static <T> long UpdateJedisTypeParam(JedisType jt, T clazz, Field field) throws NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException, JsonProcessingException {
        long l = 0;
        // 获取key值
        String key = jt.key().length() == 0 ? field.getName() : jt.key();
        if (jt.paramKey().length() > 0) {
            Field f = clazz.getClass().getField(jt.paramKey());
            key += String.valueOf(f.get(clazz));
        }
        // 根据不同的类型，读取对应的数据
        switch (jt.jType()) {
            case MAP: // map类型暂无需要单独储蓄的需求
            {

            }
            break;
            case SET: {
                l = JedisMgr.GetInstance().updateSet(key, (Collection<?>) field.get(clazz));
            }
            break;
            case LIST: {
                l = JedisMgr.GetInstance().updateList(key, (Collection<?>) field.get(clazz));
            }
            break;
            case SORTSET: {
                l = JedisMgr.GetInstance().updateSortSet(key, (Collection<IJedisSortSet>) field.get(clazz));
            }
            break;
            default:
                break;

        }
        return l;
    }

    /**
     * @param clazz
     * @param strdata
     * @return T 返回类型
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @Title: StrToObject
     * @Description: 字符串转对象，使用json反序列化，不额外处理IJedisData类型对象。
     * 要转成IJedisData对象请使用{@link FieldUtil#StrToJedisData}
     */
    public static <T> T StrToObject(Class<T> clazz, String strdata) throws InstantiationException,
            IllegalAccessException, JsonParseException, JsonMappingException, IOException {
        return JSONObject.parseObject(strdata, clazz);
        // return objectMapper.readValue(strdata, clazz);
    }

    /**
     * @param jt
     * @param strdata
     * @return T 返回类型
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException          参数说明
     * @Title: StrToObject
     * @Description: 字符串转对象，使用json反序列化，不额外处理IJedisData类型对象。
     * 要转成IJedisData对象请使用{@link FieldUtil#StrToJedisData}
     */
    @SuppressWarnings("unchecked")
    // public static <T> T StrToObject(JavaType jt, String strdata)
    // throws JsonParseException, JsonMappingException, IOException {
    // if ("java.lang.String".equals(jt.toCanonical()) && strdata.indexOf("\"")
    // == -1) {
    // return (T) strdata;
    // }
    // return objectMapper.readValue(strdata, jt);
    // }

    public static <T> T StrToObject(Type type, String strdata) {
        return JSONObject.parseObject(strdata, type);
    }

    /**
     * @param clazz
     * @param strdata
     * @return T 返回类型
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException            参数说明
     * @Title: StrToJedisData
     * @Description: 字符串转IJedisData对象
     */
    public static <T extends IJedisData> T StrToJedisData(Class<T> clazz, String strdata) throws InstantiationException,
            IllegalAccessException, JsonParseException, JsonMappingException, IOException {
        T ijd = clazz.newInstance();
        ijd.SetAsJedisString(strdata);
        return ijd;
    }

    /**
     * @param st
     * @return Collection<IJedisSortSet> 返回类型
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @Title: TupleListToJSSList
     * @Description: Tuple类型（sortset读取后的类型）转IJedisSortSet列表
     */
    public static <T extends IJedisSortSet> Collection<IJedisSortSet> TupleListToJSSList(Set<TypedTuple<String>> st,
                                                                                         Class<T> clazz, JedisType jt) throws InstantiationException, IllegalAccessException {
        Collection<IJedisSortSet> list = new ArrayList<IJedisSortSet>();
        // if(jt.jType() == JType.LIST)
        // list = new ArrayList<IJedisSortSet>();
        // else
        // list = new HashSet<IJedisSortSet>();
        Iterator<TypedTuple<String>> iter = st.iterator();
        while (iter.hasNext()) {
            TypedTuple<String> tu = iter.next();
            T jss = clazz.newInstance();
            jss.SetCcore(tu.getScore());
            jss.SetStrKey(tu.getValue());
            list.add(jss);
        }
        return list;
    }

    /**
     * @param field
     * @return JavaType 返回类型
     * @throws ClassNotFoundException
     * @Title: MakeJavaType
     * @Description: 组合javaType
     */
    // public static JavaType MakeJavaType(Field field) throws
    // ClassNotFoundException {
    // JavaType javaType = null;
    // String fieldtypename = field.getGenericType().getTypeName();
    // fieldtypename = fieldtypename.replaceAll(" ", "");
    // javaType =
    // objectMapper.getTypeFactory().constructFromCanonical(fieldtypename);
    // return javaType;
    // }
    public static Type MakeJsonType(Field field) {
        return field.getGenericType();
    }

    /**
     * @param typename
     * @param fldType
     * @return List<Class < ?>> 返回类型
     * @throws ClassNotFoundException 参数说明
     * @Title: GetClassList
     * @Description: 递归函数，根据列表类型名获取使用类列表
     */
    @Deprecated
    public static List<Class<?>> GetClassList(String typename, String fldType) throws ClassNotFoundException {
        // System.out.println(typename + " " + fldType);
        List<Class<?>> clazzList = new ArrayList<Class<?>>();
        switch (fldType) {
            case "List":
            case "Set": {
                int p = typename.indexOf("<");
                // 判断list内容中还是否有列表类型
                if (p > -1) {
                    Class<?> clazz = Class.forName(typename.substring(0, p));
                    clazzList.add(clazz);
                    // 递归获取后续类型
                    typename = typename.substring(p + 1, typename.lastIndexOf(">"));
                    clazzList.addAll(GetClassList(typename, clazz.getSimpleName()));
                } else {
                    clazzList.add(Class.forName(typename));
                }
            }
            break;
            case "Map": {
                String[] typenames = typename.split(",");
                // 加入左侧类型
                clazzList.add(Class.forName(typenames[0]));
                String rtypename = typenames[1];
                int p = rtypename.indexOf("<");
                // 判断MAP右边还是否存在列表类型
                if (p > -1) {
                    rtypename = rtypename.substring(0, p);
                    Class<?> clazz = Class.forName(rtypename);
                    clazzList.add(clazz);
                    // 递归获取后续类型
                    typename = typename.substring(typenames[0].length() + 1);
                    clazzList.addAll(GetClassList(typename, clazz.getSimpleName()));
                } else {
                    // 加入右侧类型并结束
                    clazzList.add(Class.forName(rtypename));
                }

            }
            break;
            default: {
                System.out.println("未知类型  " + fldType);
            }
        }

        return clazzList;
    }

}
