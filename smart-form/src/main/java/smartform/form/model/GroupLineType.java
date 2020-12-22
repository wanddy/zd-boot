package smartform.form.model;

/**
 * @ClassName: GroupLineType
 * @Description: 组行类型
 * @author quhanlin
 * @date 2018年10月25日 下午3:22:28
 * 
 */
public enum GroupLineType {
	/**
	 * 普通行
	 */
	COMMON(1),
	/**
	 * 合计行
	 */
	TOTAL(2);
	
	/**
	 * 枚举值
	 */
	public final int value;

	// 构造方法必须是private或者默认
	private GroupLineType(int value) {
		this.value = value;
	}
}
