package smartform.widget.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: WidgetDate
 * @Description: 日期控件
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class WidgetDate extends WidgetBase implements Serializable {


	private static final long serialVersionUID = 1L;

	/**
	 * 日期格式 1：年，2：月，3：周；4：日期，5：日期+时间
	 */
	private Integer dateType;
	/**
	 * 是否可以清除
	 */
	private Boolean clear;
	/**
	 * 日期范围是否联动
	 */
	private Boolean link;
	/**
	 * 起始是否默认当天
	 */
	private Boolean defStartToday;
	/**
	 * 结束是否默认当天
	 */
	private Boolean defEndToday;
	/**
	 * 日期起始默认值
	 */
	private Date defStartDate;
	/**
	 * 日期结束默认值
	 */
	private Date defEndDate;
	
	/**
	 * 结束日期数据字段别名
	 */
	private String endAlias;
	
	/**
	 * 日期起始范围限定
	 */
	private Date limitStartDate;
	/**
	 * 日期结束范围限定
	 */
	private Date limitEndDate;
	/**
	 * 日期是否限定当天,0.不限定,1.开始限定,2.结束限定
	 */
	private Integer limitToday;
	/**
	 * 起始提示
	 */
	private String startPlace;
	/**
	 * 结束提示
	 */
	private String endPlace;

	private String defValue;

	public String getDefValue() {
		/*if(null!=super.getDefValue()){
			defValue = (String) super.getDefValue();
			super.setDefValue(null);
		}*/
		return defValue;
	}

	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}

	public Integer getDateType() {
		return dateType;
	}

	public void setDateType(Integer dateType) {
		this.dateType = dateType;
	}

	public Boolean getClear() {
		return clear;
	}

	public void setClear(Boolean clear) {
		this.clear = clear;
	}

	public Boolean getLink() {
		return link;
	}

	public void setLink(Boolean link) {
		this.link = link;
	}

	public Boolean getDefStartToday() {
		return defStartToday;
	}

	public void setDefStartToday(Boolean defStartToday) {
		this.defStartToday = defStartToday;
	}

	public Boolean getDefEndToday() {
		return defEndToday;
	}

	public void setDefEndToday(Boolean defEndToday) {
		this.defEndToday = defEndToday;
	}

	public Date getDefStartDate() {
		return defStartDate;
	}

	public void setDefStartDate(Date defStartDate) {
		this.defStartDate = defStartDate;
	}

	public Date getDefEndDate() {
		return defEndDate;
	}

	public void setDefEndDate(Date defEndDate) {
		this.defEndDate = defEndDate;
	}

	public Date getLimitStartDate() {
		return limitStartDate;
	}

	public void setLimitStartDate(Date limitStartDate) {
		this.limitStartDate = limitStartDate;
	}

	public Date getLimitEndDate() {
		return limitEndDate;
	}

	public void setLimitEndDate(Date limitEndDate) {
		this.limitEndDate = limitEndDate;
	}

	public Integer getLimitToday() {
		return limitToday;
	}

	public void setLimitToday(Integer limitToday) {
		this.limitToday = limitToday;
	}

	public String getStartPlace() {
		return startPlace;
	}

	public void setStartPlace(String startPlace) {
		this.startPlace = startPlace;
	}

	public String getEndPlace() {
		return endPlace;
	}

	public void setEndPlace(String endPlace) {
		this.endPlace = endPlace;
	}

	@Override
	public String toString() {
		return "WidgetDate [dateType=" + dateType + ", clear=" + clear + ", link=" + link + ", defStartToday="
				+ defStartToday + ", defEndToday=" + defEndToday + ", defStartDate=" + defStartDate + ", defEndDate="
				+ defEndDate + ", endAlias=" + endAlias + ", limitStartDate=" + limitStartDate + ", limitEndDate="
				+ limitEndDate + ", limitToday=" + limitToday + ", startPlace=" + startPlace + ", endPlace=" + endPlace
				+ "]";
	}

	public String getEndAlias() {
		return endAlias;
	}

	public void setEndAlias(String endAlias) {
		this.endAlias = endAlias;
	}

}