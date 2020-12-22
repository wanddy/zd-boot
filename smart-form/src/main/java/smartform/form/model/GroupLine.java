package smartform.form.model;

import com.alibaba.fastjson.annotation.JSONField;
import smartform.common.model.BaseData;
import smartform.widget.model.WidgetBase;
import smartform.widget.model.deserializer.WidgetBaseListDeserializer;

import java.util.List;

/**
 * @ClassName: GroupLine
 * @Description: 组-行
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class GroupLine extends BaseData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 固定行ID
	 */
	private String dataId;
	
	/**
	 * 行类型 1.普通行，2.合计行 
	 */
	private Integer lineType = 1;
	
	/**
	 * 行号
	 */
	private Integer lineNum;
	
	/**
	 * 列字段列表，没有为''
	 */
	private List<String> columnNum;
	
	/**
	 * 当前行状态
	 */
	private Integer state;
	
	/** 
	* @Fields createdAt : 创建时间
	*/ 
	private Long createdAt;
	
	/** 
	* @Fields sort : 排序值。后端只负责从小到大排序
	*/ 
	private Integer sort;
	
	/** 
	* @Fields totalRowName : gridView合计行名称（前端使用）
	*/ 
	private String totalRowName;
	
	/**
	 * 字段列表
	 */
	@JSONField(deserializeUsing = WidgetBaseListDeserializer.class)
	private List<WidgetBase> fieldList;

	public Integer getLineType() {
		return lineType;
	}

	public void setLineType(Integer lineType) {
		this.lineType = lineType;
	}

	public Integer getLineNum() {
		return lineNum;
	}

	public void setLineNum(Integer lineNum) {
		this.lineNum = lineNum;
	}

	public List<String> getColumnNum() {
		return columnNum;
	}

	public void setColumnNum(List<String> columnNum) {
		this.columnNum = columnNum;
	}

	public List<WidgetBase> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<WidgetBase> filedList) {
		this.fieldList = filedList;
	}

	@Override
	public String toString() {
		return "GroupLine [dataId=" + dataId + ", lineType=" + lineType + ", lineNum=" + lineNum + ", columnNum="
				+ columnNum + ", state=" + state + ", fieldList=" + fieldList + ", totalRowName=" + totalRowName + "]";
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getTotalRowName() {
		return totalRowName;
	}

	public void setTotalRowName(String totalRowName) {
		this.totalRowName = totalRowName;
	}
	
	
}