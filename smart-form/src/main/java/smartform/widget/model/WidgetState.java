package smartform.widget.model;

/**
 * @ClassName: WidgetState
 * @Description: 字段状态枚举
 * @author quhanlin
 * @date 2018年9月20日 下午14:20:28
 * 
 */
public enum WidgetState {
	/**
	 * 删除状态
	 */
	DEL(0),
	/**
	 * 基础控件
	 */
	BASE(1),
	/**
	 * 预设控件
	 */
	PREFAB(2),
	/**
	 * 被表单/组引用
	 */
	FORM(3),
	/**
	 * 用于规则
	 */
	RULE(4);

	/**
	 * 枚举值
	 */
	public final int value;

	// 构造方法必须是private或者默认
	private WidgetState(int value) {
		this.value = value;
	}
}
