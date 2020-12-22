package smartform.form.model.entity;

import smartform.form.model.FormContentFieldBase;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: FormContentDateEntity
 * @Description: 日期表
 * @author hou
 * @date 2018年10月04日 下午17:05:34
 * 
 */
@Deprecated
public class FormContentDateEntity extends FormContentFieldBase implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 开始日期
	 */
	private Date startValue;
	
	/**
	 * 结束日期
	 */
	private Date endValue;
	
	/**
	 * 0.无范围；1.开始；2.结束
	 */
	// private Integer rangeType;
	
	public FormContentDateEntity() {
		super();
	}

	public Date getStartValue() {
		return startValue;
	}

	public void setStartValue(Date startValue) {
		this.startValue = startValue;
	}

	public Date getEndValue() {
		return endValue;
	}

	public void setEndValue(Date endValue) {
		this.endValue = endValue;
	}
	
	
//    /**
//     * setter for rangeType
//     * @param rangeType
//     */
//	public void setRangeType(Integer rangeType) {
//		this.rangeType = rangeType;
//	}
//
//    /**
//     * getter for rangeType
//     */
//	public Integer getRangeType() {
//		return rangeType;
//	}
	
}
