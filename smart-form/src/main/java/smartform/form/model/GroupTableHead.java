package smartform.form.model;

import java.io.Serializable;

/**
 * @ClassName: GroupTableHead
 * @Description: 表头设置
 * @author quhanlin
 * @date 2018年11月8日 上午11:10:28
 * 
 */
public class GroupTableHead implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 表头名称，要跟字段标题对应
	 */
	private String name;

	/**
	 * 表头宽度百分比
	 */
	private Integer widthRatio;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getWidthRatio() {
		return widthRatio;
	}

	public void setWidthRatio(Integer widthRatio) {
		this.widthRatio = widthRatio;
	}

	@Override
	public String toString() {
		return "GroupTableHead [name=" + name + ", widthRatio=" + widthRatio + "]";
	}

}
