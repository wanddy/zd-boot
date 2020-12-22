package workflow.common.redis;

/** 
* @ClassName: IJedisSortSet 
* @Description: Jedis有序Set接口，如需存放进入有序Set，必须实现该接口
* @author KaminanGTO
* @date 2017年6月15日 下午12:07:43 
*  
*/
public interface IJedisSortSet {
	
	/** 
	* @Title: GetStrKey 
	* @Description: 获取字符串Key
	* @return  参数说明 
	* @return String    返回类型 
	* 
	*/
	public String GetStrKey();
	
	/** 
	* @Title: SetStrKey 
	* @Description: 根据字符串Key序列化
	* @param key
	* @return  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	public boolean SetStrKey(String key);
	
	/** 
	* @Title: GetScore 
	* @Description: 获取得分（排序使用）
	* @return  参数说明 
	* @return Double    返回类型 
	* 
	*/
	public Double GetScore();
	
	/** 
	* @Title: SetCcore 
	* @Description: 设置得分（数据实例化时调用）
	* @param score
	* @return  参数说明 
	* @return Double    返回类型 
	* 
	*/
	public void SetCcore(Double score);
	
}
