package workflow.common.redis;

/**
 * @ClassName: JedisMapView
 * @Description: map视图实体，只做精准查询（如用户名密码）
 * @author Admin
 * @date 2017年6月25日 下午5:10:57
 * 
 */
public class JedisMapView {

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
	 * @Fields key : Map的field
	 */
	public String field;

	public JedisMapView() {
	}

	public JedisMapView(String field, String jdata) {
		this.field = field;
		setAsJedisData(jdata);
	}

	public JedisMapView(String field, String data, String value) {
		this.field = field;
		this.data = data;
		this.value = value;
	}

	/**
	 * @Title: ToSaveStr
	 * @Description: 组合保存字符串
	 * @return 参数说明
	 * @return String 返回类型
	 * 
	 */
	public String toSaveStr() {
		return this.data + ValueSpan + this.value;
	}

	/**
	 * @Title: SetAsJedisData
	 * @Description: 根据保存的数据序列化
	 * @param jdata
	 *            参数说明
	 * @return boolean 返回类型
	 * 
	 */
	public boolean setAsJedisData(String jdata) {
		String[] strs = jdata.split(ValueSpan);
		if (strs.length != 2)
			return false;
		this.data = strs[0];
		this.value = strs[1];
		return true;
	}

	/** 
	* @Title: getJData 
	* @Description: 获取视图Value
	* @param data
	* @param value
	* @return  参数说明 
	* @return String    返回类型 
	* 
	*/
	public static String GetValue(String data, String value) {
		return data + ValueSpan + value;
	}
}
