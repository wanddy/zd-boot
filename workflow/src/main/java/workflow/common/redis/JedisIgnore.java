package workflow.common.redis;

import java.lang.annotation.*;

/**
 * @ClassName: JedisIgnore
 * @Description: Jedis序列化剔除标记--扩展支持solr序列化判定
 * @author Admin
 * @date 2017年6月25日 下午6:31:28
 * 
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JedisIgnore {
	boolean value() default true;
	
	/** 
	* @Title: saveSolr 
	* @Description: 是否要保存到solr
	* @return  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean saveSolr() default false;
}
