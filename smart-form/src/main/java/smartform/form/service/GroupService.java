package smartform.form.service;

import smartform.form.model.Group;
import smartform.form.model.GroupInput;
import smartform.form.model.GroupPagination;

/**
 * @ClassName: GroupService
 * @Description: 超级组件服务
 * @author hou
 * @date 2018年9月22日 上午11:42:25
 */
public interface GroupService {

	/**
	 * 获取超级组件实体
	 * 
	 * @param id
	 * @return
	 */
	Group groupById(String id);

	/**
	 * 获取超级组件列表
	 * @param page 分页参数
	 * @param hasFieldList 是否拉取每个组的fieldList详情，获取详情时会自动填充选项数据
	 * @return
	 */
	GroupPagination groupList(GroupInput page,boolean hasFieldList);

	/**
	 * 创建超级组件
	 * @param group
	 * @return
	 */
	String createGroup(String group);

	/**
	 * 更新超级组件信息
	 * @param group
	 * @return
	 */
	String updateGroup(String group);
	
	/**
	 * 发布超级组件
	 * @param id
	 * @param isRelease
	 * @return
	 */
	String updateGroupState(String id, boolean isRelease);

	/**
	 *  复制超级组件
	 * @param id
	 * @return
	 */
	String copyGroup(String id);
	
	/**
	 * 删除超级组件
	 * @param id
	 * @return
	 */
	String deleteGroup(String id);
}
