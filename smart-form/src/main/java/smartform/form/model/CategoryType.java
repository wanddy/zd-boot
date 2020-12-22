package smartform.form.model;

/** 
* @ClassName: CategoryType 
* @Description: 分类类型
* @author quhanlin
* @date 2018年10月25日 下午3:22:28 
*  
*/
public enum CategoryType {
	/**
	 * 表单
	 */
	SMARTFORM(1),
	/**
	 * 定制组件
	 */
	GROUP(2);

	/**
	 * 枚举值
	 */
	public final int value;

	// 构造方法必须是private或者默认
	private CategoryType(int value) {
		this.value = value;
	}
}
