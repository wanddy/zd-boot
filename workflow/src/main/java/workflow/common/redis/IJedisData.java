package workflow.common.redis;

/** 
* @ClassName: IJedisData 
* @Description: 	需要写数据库的特殊类型数据接口，
* 							需要注意当此类满足以下任意条件时，将被当成通用数据解析
* 							1.放在2级列表中（如List List IJedisData)
* 							2.放在JedisBaseDoc中的列表属性，并且不单独写入的情况下。（因为JedisBaseDoc本身就是map格式存放）
* @author KaminanGTO
* @date 2017年6月12日 下午7:13:36 
*  
*/
public interface IJedisData {

	/** 
	* @Title: ToJedisString 
	* @Description: 序列化成字符串数据 
	* @return  参数说明 
	* @return String    返回类型 
	* 
	*/
	public String ToJedisString();
	
	/** 
	* @Title: SetAsJedisString 
	* @Description: 根据字符串数据反序列化
	* @param str
	* @return  参数说明 
	* @return boolean    是否反序列化成功 
	* 
	*/
	public boolean SetAsJedisString(String str);
	
}
