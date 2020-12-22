package smartform.common.redis;

import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

/** 
* @ClassName: JedisBaseView
* @Description: 视图保存数据基类（可继承或直接使用）
* @author KaminanGTO
* @date 2017年6月24日 下午7:42:50 
*  
*/
public class JedisBaseView {
	
	/**
	 * @Fields KeySpan : Value的分隔符
	 */
	public static final String ValueSpan = "~";
	
	/** 
	* @Fields data : 视图的判定数据
	*/ 
	public String data;
	
	/** 
	* @Fields value : 视图的值数据（通常用来保存key）
	*/ 
	public String value;
	
	/** 
	* @Fields score : 排序得分（可选），默认0（要根据score排序时才赋值，否则必须使用默认值）
	*/ 
	public double score;
	
	public JedisBaseView()
	{
		score = 0;
	}
	
	public JedisBaseView(String data, String value)
	{
		this.data = data;
		this.value = value;
		score = 0;
	}
	
	public JedisBaseView(String data, String value, double score)
	{
		this.data = data;
		this.value = value;
		this.score = score;
	}
	
	public JedisBaseView(String data, double score)
	{
		this.data = data;
		this.value = null;
		this.score = score;
	}
	
	/** 
	* @Title: ToSaveStr 
	* @Description: 组合保存字符串
	* @return  参数说明 
	* @return String    返回类型 
	* 
	*/
	public String ToSaveStr()
	{
		if(value == null)
			return this.data;
		return this.data + ValueSpan + this.value;
	}
	
	/** 
	* @Title: SetAsJedisData 
	* @Description: 根据保存的数据序列化
	* @param jdata  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	public boolean SetAsJedisData(String jdata)
	{
		String[] strs = jdata.split(ValueSpan);
		if(strs.length > 2)
			return false;
		this.data = strs[0];
		if(strs.length == 2)
			this.value = strs[1];
		return true;
	}
	
	/** 
	* @Title: SetAsTuple 
	* @Description: 根据保存的sortset数据序列化
	* @param t
	* @return  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	public boolean SetAsTuple(TypedTuple<String> t)
	{
		this.score = t.getScore();
		return SetAsJedisData(t.getValue());
	}

}
