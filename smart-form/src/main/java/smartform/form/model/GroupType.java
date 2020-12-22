package smartform.form.model;

/** 
* @ClassName: GroupType 
* @Description: 组类型
* @author quhanlin
* @date 2018年10月25日 下午3:22:28 
*  
*/
public enum GroupType {
	/**
	 * 超级组
	 */
	SUPER(1),
	/**
	 * 样式组
	 */
	STYLE(2),
	/**
	 * 表格组/可复制组
	 */
	TABLE(3),
	/** 
	* @Fields GRID : 栅格组
	*/ 
	GRID(4);

	/**
	 * 枚举值
	 */
	public final int value;

	// 构造方法必须是private或者默认
	private GroupType(int value) {
		this.value = value;
	}
}
