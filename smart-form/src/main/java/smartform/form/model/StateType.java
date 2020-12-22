package smartform.form.model;

/**
 * @ClassName: StateType
 * @Description: 常用状态枚举
 * @author quhanlin
 * @date 2018年9月17日 下午12:21:31
 */
public enum StateType {

	/**
	 * 全部状态
	 */
	All(10),
	/**
	* 删除状态
	*/
	DEL(0),
	/**
	* 新建状态
	*/
	DEV(1),
	/**
	* 发布状态
	*/
	RELEASE(2);

	/**
	 * 枚举值
	 */
	public final int value;

	// 构造方法必须是private或者默认
	private StateType(int value) {
		this.value = value;
	}

}
