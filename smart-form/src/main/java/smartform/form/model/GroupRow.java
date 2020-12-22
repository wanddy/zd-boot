package smartform.form.model;

import com.alibaba.fastjson.annotation.JSONField;
import smartform.common.model.BaseData;
import smartform.widget.model.WidgetBase;
import smartform.widget.model.deserializer.WidgetBaseListDeserializer;

import java.util.List;

/** 
* @ClassName: GroupRow 
* @Description: 组栅格列
* @author KaminanGTO
* @date 2019年3月17日 下午2:43:24 
*  
*/
public class GroupRow extends BaseData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** 
	* @Fields span : 列栅格宽度
	*/ 
	private Integer span;
	
	/**
	 * 字段列表
	 */
	@JSONField(deserializeUsing = WidgetBaseListDeserializer.class)
	private List<WidgetBase> fieldList;

	public Integer getSpan() {
		return span;
	}

	public void setSpan(Integer span) {
		this.span = span;
	}

	public List<WidgetBase> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<WidgetBase> fieldList) {
		this.fieldList = fieldList;
	}

	@Override
	public String toString() {
		return "GroupLine [span=" + span + ", fieldList=" + fieldList + "]";
	}

}