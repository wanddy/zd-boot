package smartform.widget.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: WidgetTime
 * @Description: 时间控件
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class WidgetTime extends WidgetBase implements Serializable {


	private static final long serialVersionUID = 1L;

	/**
	 * 时间格式
	 */
	/** 1.步长模式，2.自由模式 */
	private Integer timeType;
	/**
	 * 是否可以清除
	 */
	private Boolean clear;
	/**
	 * 步长开始时间
	 */
	private String timeStart;
	/**
	 * 步长结束时间
	 */
	private String timeEnd;
	/**
	 * 步长
	 */
	private String timeStep;
	/**
	 * 起始是否默认当前
	 */
	private Boolean defStartNow;
	/**
	 * 结束是否默认当前
	 */
	private Boolean defEndNow;
	/**
	 * 时间起始默认值
	 */
	private String defEndTime;
	/**
	 * 时间结束默认值
	 */
	private String defStartTime;

	/**
	 * 结束时间别名
	 */
	private String endAlias;
	
	/**
	 * 时间限定起始
	 */
	private Date limitStartTime;
	/**
	 * 时间限定结束
	 */
	private Date limitEndTime;
	/**
	 * 限定当前时间,0.不限定,1.开始限定,2.结束限定
	 */
	private int limitNow;
	/**
	 * 起始提示
	 */
	private String startPlace;
	/**
	 * 结束提示
	 */
	private String endPlace;

	public Integer getTimeType() {
		return timeType;
	}

	public void setTimeType(Integer timeType) {
		this.timeType = timeType;
	}

	public Boolean getClear() {
		return clear;
	}

	public void setClear(Boolean clear) {
		this.clear = clear;
	}

	public String getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(String timeStart) {
		this.timeStart = timeStart;
	}

	public String getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(String timeEnd) {
		this.timeEnd = timeEnd;
	}

	public String getTimeStep() {
		return timeStep;
	}

	public void setTimeStep(String timeStep) {
		this.timeStep = timeStep;
	}

	public Boolean getDefStartNow() {
		return defStartNow;
	}

	public void setDefStartNow(Boolean defStartNow) {
		this.defStartNow = defStartNow;
	}

	public Boolean getDefEndNow() {
		return defEndNow;
	}

	public void setDefEndNow(Boolean defEndNow) {
		this.defEndNow = defEndNow;
	}

	public String getDefEndTime() {
		return defEndTime;
	}

	public void setDefEndTime(String defEndTime) {
		this.defEndTime = defEndTime;
	}

	public String getDefStartTime() {
		return defStartTime;
	}

	public void setDefStartTime(String defStartTime) {
		this.defStartTime = defStartTime;
	}

	public Date getLimitStartTime() {
		return limitStartTime;
	}

	public void setLimitStartTime(Date limitStartTime) {
		this.limitStartTime = limitStartTime;
	}

	public Date getLimitEndTime() {
		return limitEndTime;
	}

	public void setLimitEndTime(Date limitEndTime) {
		this.limitEndTime = limitEndTime;
	}

	public int getLimitNow() {
		return limitNow;
	}

	public void setLimitNow(int limitNow) {
		this.limitNow = limitNow;
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
		return "WidgetTime [timeType=" + timeType + ", clear=" + clear + ", timeStart=" + timeStart + ", timeEnd="
				+ timeEnd + ", timeStep=" + timeStep + ", defStartNow=" + defStartNow + ", defEndNow=" + defEndNow
				+ ", defEndTime=" + defEndTime + ", defStartTime=" + defStartTime + ", endAlias=" + endAlias
				+ ", limitStartTime=" + limitStartTime + ", limitEndTime=" + limitEndTime + ", limitNow=" + limitNow
				+ ", startPlace=" + startPlace + ", endPlace=" + endPlace + "]";
	}

	public String getEndAlias() {
		return endAlias;
	}

	public void setEndAlias(String endAlias) {
		this.endAlias = endAlias;
	}

}