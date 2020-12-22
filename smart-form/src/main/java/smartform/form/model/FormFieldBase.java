package smartform.form.model;

import com.baomidou.mybatisplus.annotation.TableField;
import smartform.common.model.BaseData;

/**
 * @ClassName: FormFieldBase
 * @Description: 表单字段基类
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class FormFieldBase extends BaseData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 字段分类 widget: 1, group: 2
	 */
	@TableField(exist = false)
	private int type;
	
	/** 
	* @Fields styleClassName : 样式类名，为空时使用默认样式
	*/
	@TableField(exist = false)
	private String styleClassName;

	@TableField(exist = false)
	private String alias;

/*	@TableField(exist = false)
	private Object defValue;

	public Object getDefValue() {
		return defValue;
	}

	public void setDefValue(Object defValue) {
		this.defValue = defValue;
	}*/

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public FormFieldBase() {
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getStyleClassName() {
		return styleClassName;
	}

	public void setStyleClassName(String styleClassName) {
		this.styleClassName = styleClassName;
	}

	@Override
	public String toString() {
		return "FormFieldBase [type=" + type + "]";
	}

}