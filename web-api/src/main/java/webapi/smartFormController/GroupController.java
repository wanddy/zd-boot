package webapi.smartFormController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smartform.dto.PassParameter;
import smartform.form.model.Group;
import smartform.form.model.GroupInput;
import smartform.form.model.GroupPagination;
import smartform.form.service.GroupService;

import java.util.logging.Logger;

/**
 * @ClassName: GroupResolver
 * @Description: 超级组件接口
 * @author hou
 * @date 2018年9月18日 下午3:18:36
 *
 */
@Api(value="超级组件接口")
@RestController
@RequestMapping("group")
public class GroupController {
	Logger log = Logger.getLogger(GroupController.class.getName());

	@Autowired
	private GroupService groupService;

	/**
	 * 获取超级组件实体
	 * @param id
	 * @return
	 */
	@ApiOperation("获取超级组件实体")
	@RequestMapping(value = "/groupById",method = {RequestMethod.GET})
	public Group groupById(String id) {
		return groupService.groupById(id);
	}

	/**
	 * 获取超级组件列表
	 *
	 * @param page
	 * @return
	 */
	@ApiOperation("获取超级组件列表")
	@GetMapping(value = "/groupList")
	public GroupPagination groupList(@ModelAttribute GroupInput page) {
		return groupService.groupList(page, true);
	}

	/**
	 * 创建超级组件
	 * @param passParameter
	 * @return
	 */
	@ApiOperation("创建超级组件")
	@PostMapping(value = "/createGroup")
	public String createGroup(@RequestBody PassParameter passParameter) {
		return groupService.createGroup(passParameter.getGroup());
	}

	/**
	 * 更新超级组件信息
	 * @param passParameter
	 * @return
	 */
	@ApiOperation("更新超级组件信息")
	@RequestMapping(value = "/updateGroup")
	public String updateGroup(@RequestBody PassParameter passParameter) {
		return groupService.updateGroup(passParameter.getGroup());
	}

	/**
	 * 发布超级组件
	 * @param passParameter
	 * @return
	 */
	@ApiOperation("发布超级组件")
	@PostMapping(value = "/updateGroupState")
	public String updateGroupState(@RequestBody PassParameter passParameter) {
		return groupService.updateGroupState(passParameter.getId(), passParameter.isRelease());
	}

	/**
	 * 复制超级组件
	 * @param passParameter
	 * @return
	 */
	@ApiOperation("复制超级组件")
	@PostMapping(value = "/copyGroup")
	public String copyGroup(@RequestBody PassParameter passParameter) {
		return groupService.copyGroup(passParameter.getId());
	}

	/**
	 * 删除超级组件
	 * @param passParameter
	 * @return
	 */
	@ApiOperation("删除超级组件")
	@PostMapping(value = "/deleteGroup")
	public String deleteGroup(@RequestBody PassParameter passParameter) {
		return groupService.deleteGroup(passParameter.getId());
	}

}
