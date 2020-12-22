package workflow.business.model;

import com.baomidou.mybatisplus.annotation.TableName;
import workflow.business.model.entity.ProjectMainEntity;

import java.io.Serializable;
import java.util.Date;


/**
 * 新增业务任务 实体类
 */
public class ProjectMainInputEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 业务主表
	 */
	private ProjectMainEntity projectMainEntity;

	/**
	 * 表单填报内容
	 */
	private String data;

	/**
	 *
	 */
	private boolean isStorage;

	public ProjectMainEntity getProjectMainEntity() {
		return projectMainEntity;
	}

	public void setProjectMainEntity(ProjectMainEntity projectMainEntity) {
		this.projectMainEntity = projectMainEntity;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public boolean isStorage() {
		return isStorage;
	}

	public void setStorage(boolean storage) {
		isStorage = storage;
	}
}
