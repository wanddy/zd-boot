package smartform.form.model;

/** 
* @ClassName: FormFieldType 
* @Description: 表单分页中的字段类型
* @author quhanlin
* @date 2018年9月29日 下午4:19:14 
*  
*/
public enum FormFieldType {

	/**
	 * 字段
	 */
	WIDGET(1),
	/**
	* 组
	*/
	GROUP(2),
	
	/**
	* 分页
	*/
	PAGE(3);

	/**
	 * 枚举值
	 */
	public final int value;

	// 构造方法必须是private或者默认
	private FormFieldType(int value) {
		this.value = value;
	}
}

