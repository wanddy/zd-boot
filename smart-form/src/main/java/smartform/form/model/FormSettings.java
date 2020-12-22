package smartform.form.model;

import java.io.Serializable;

/**
 * @ClassName: FormSettings
 * @Description: 表单额外设置
 * @author hou
 * @date 2019年1月17日 下午15:00:00
 */
public class FormSettings implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	* 分页模式  
	*/
	private Integer pageMode;
	
	/**
	 * 是否隐藏保存按钮
	 */
	private Boolean hideSaveButton;

	public Integer getPageMode() {
		return pageMode;
	}

	public void setPageMode(Integer pageMode) {
		this.pageMode = pageMode;
	}
	
	public Boolean getHideSaveButton() {
		return hideSaveButton;
	}

	public void setHideSaveButton(Boolean hideSaveButton) {
		this.hideSaveButton = hideSaveButton;
	}

	@Override
	public String toString() {
		return "FormSettings [pageMode=" + pageMode + ", hideSaveButton=" + hideSaveButton + "]";
	}
}
