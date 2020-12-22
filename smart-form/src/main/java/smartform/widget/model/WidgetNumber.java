package smartform.widget.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName: WidgetNumber
 * @Description: 数字控件
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class WidgetNumber extends WidgetBase implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 数字默认值
	 */
	private BigDecimal defValue;
	/**
	 * 后缀类型
	 */
	private Integer suffixType;
	/**
	 * 后缀文本
	 */
	private String suffix;

	/**
	 * 使用启用sql查询
	 */
	private Boolean useSql;

	/**
	 * sql字符串
	 */
	private String sqlStr;
	
	/** 
	* @Fields maxNumber : 最大值，前端判定用
	*/ 
	private String maxNumber;

	public BigDecimal getDefValue() {
		/*if(null!=super.getDefValue()){
			defValue = new BigDecimal(super.getDefValue().toString());
			super.setDefValue(null);
		}*/
		return defValue;
	}

	public void setDefValue(BigDecimal defValue) {
		this.defValue = defValue;
	}

	public Integer getSuffixType() {
		return suffixType;
	}

	public void setSuffixType(Integer suffixType) {
		this.suffixType = suffixType;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public String toString() {
		return "WidgetNumber [defValue=" + defValue + ", suffixType=" + suffixType + ", suffix=" + suffix + ", useSql="
				+ useSql + ", sqlStr=" + sqlStr + ", maxNumber=" + maxNumber + "]";
	}

	public Boolean getUseSql() {
		return useSql;
	}

	public void setUseSql(Boolean useSql) {
		this.useSql = useSql;
	}

	public String getSqlStr() {
		return sqlStr;
	}

	public void setSqlStr(String sqlStr) {
		this.sqlStr = sqlStr;
	}

	public String getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(String maxNumber) {
		this.maxNumber = maxNumber;
	}

}