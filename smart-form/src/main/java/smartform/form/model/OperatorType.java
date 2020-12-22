package smartform.form.model;

/** 
* @ClassName: OperatorType 
* @Description: 符号类型
* @author quhanlin
* @date 2018年10月25日 下午3:22:28 
*  
*/
public enum OperatorType {
	/**
	 * 1.加
	 */
	ADD(1),
	/**
	 * 2.减
	 */
	MINUS(2),
	/**
	 * 3.乘 
	 */
	MULTIPLY(3),
	/**
	 * 4.除 
	 */
	DIVISION(4),
	/**
	 * 5.等于 
	 */
	EQUAL(5),
	/**
	 * 6.大于 
	 */
	GREATERTHAN(6),
	/**
	 * 7.小于 
	 */
	LESSTHAN(7),
	/**
	 * 8.大于等于 
	 */
	GREATERTHANANDEQUAL(8),
	/**
	 * 9.小于等于 
	 */
	LESSTHANANDEQUAL(9),
	/**
	 * 10.不等于 
	 */
	NOTEQUAL(10),
	/**
	 * 11.或 
	 */
	OR(11),
	/**
	 * 12.和 
	 */
	AND(12),
	/**
	 * 13.结果
	 */
	RESULT(13);
	
	/**
	 * 枚举值
	 */
	public final int value;

	// 构造方法必须是private或者默认
	private OperatorType(int value) {
		this.value = value;
	}
	
	public static OperatorType valueOf(int value)
	{
		if(value < 1 || value > OperatorType.values().length)
		{
			return null;
		}
		return OperatorType.values()[value-1];
	}
}
