package smartform.form.model;

/** 
* @ClassName: ContentStateType 
* @Description: 表单内容状态枚举
* @author quhanlin
* @date 2018年10月6日 下午6:05:13 
*  
*/
public enum ContentStateType {

	/**
	 * 全部状态
	 */
	All(10),
	/**
	* 未填写
	*/
	UNFILL(-1),
	/**
	* 删除状态
	*/
	DEL(0),
	/**
	* 暂存状态（表单规则验证未通过）
	*/
	STORAGE(1),
	/** 
	* @Fields SAVE : 保存状态
	*/ 
	SAVE(2),
	/** 
	* @Fields LOCK : 锁定状态（无法修改）
	*/ 
	LOCK(3);
//	/**
//	* 提交中状态
//	*/
//	COMMITTING(2),
//	/**
//	* 已提交状态
//	*/
//	SUBMIT(3),
//	/**
//	* 退回状态
//	*/
//	REFUSE(4),
//	/**
//	* 退回时已提交状态，该状态下可以再次提交退回的表单
//	*/
//	REFUSESUBMIT(5),
//	
//	/**
//	 * 临时状态，表单未填报前给的状态
//	 */
//	TRANSIENT(6);
	
	/**
	 * 枚举值
	 */
	public final int value;

	// 构造方法必须是private或者默认
	private ContentStateType(int value) {
		this.value = value;
	}

}
