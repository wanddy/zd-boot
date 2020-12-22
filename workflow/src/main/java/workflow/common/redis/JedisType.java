package workflow.common.redis;

import java.lang.annotation.*;

/** 
* @ClassName: JedisType 
* @Description: Jedis特殊类型注解，有注解的属性将存放在特定的Key中（如List,Set,Map）
* 						已废弃实现，请勿使用
* @author KaminanGTO
* @date 2017年6月15日 上午11:33:49 
*  
*/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Deprecated
public @interface JedisType {
	
	/** 
	* @ClassName: JType 
	* @Description: 特殊类型枚举
	* @author KaminanGTO
	* @date 2017年6月15日 上午11:47:30 
	*  
	*/
	public enum JType { MAP,SET,LIST,SORTSET };
	
	/** 
	* @Title: jType 
	* @Description: 所使用的类型
	* @return  参数说明 
	* @return JType    返回类型 
	* 
	*/
	JType jType() default JType.LIST;
	
	/** 
	* @Title: Key 
	* @Description: 属性存放的扩展key，如果为空时，使用该标记所对应的变量名做为key
	* @return  参数说明 
	* @return String    返回类型 
	* 
	*/
	String key() default "";
	
	/** 
	* @Title: paramKey 
	* @Description: 参数key，直接读取对应类中的相同变量名的属性做为Key的后续。读取的属性直接调用String.valueOf强转String
	* 							组合后key= key+paramKey
	* @return  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	String paramKey() default "";
}
