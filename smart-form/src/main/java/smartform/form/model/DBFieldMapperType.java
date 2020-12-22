package smartform.form.model;

public enum DBFieldMapperType {
	/**
	 * 参数
	 */
	PARAM(1), 
	/**
	 * 字段别名（表名.别名）
	 */
	ALIAS(2), 
	/**
	 * 组件状态关系表
	 */
	STATETABLE(3), 
	/**
	 * 上传列表
	 */
	UPLOADTABLE(4),
	
	/**
	 * 结束日期
	 */
	ENDDATE(5);

	/**
	 * 枚举值
	 */
	public final int value;

	// 构造方法必须是private或者默认
	private DBFieldMapperType(int value) {
		this.value = value;
	}
}
