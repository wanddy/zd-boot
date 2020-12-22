package smartform.form.model.entity;

import java.io.Serializable;

/** 
* @ClassName: FormContentComponentEntity 
* @Description: 用于表单内容组件表
* @author quhanlin
* @date 2018年11月1日 下午5:59:44
*/
public class FormContentComponentEntity extends FormContentComponentBase implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "FormContentComponentEntity [getId()=" + getId() + ", getCreatedAt()=" + getCreatedAt()
				+ ", getModifiedAt()=" + getModifiedAt() + ", getContentId()=" + getContentId() + ", getWorkType()="
				+ getWorkType() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
	}
}
