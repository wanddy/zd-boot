package workflow.common.utils;

import workflow.common.constant.ActivitiConstant;

import java.util.ArrayList;
import java.util.List;

/** 
* @ClassName: WorkflowUtil 
* @Description: 工作流工具类
* @author KaminanGTO
* @date Jul 30, 2019 5:53:49 PM 
*  
*/
public class WorkflowUtil {

	/**
	 * @Fields TASK_JUDGE_HEAD : 任务判断头
	 */
	public static final String TASK_JUDGE_HEAD = ActivitiConstant.TASK_JUDGE_HEAD + ActivitiConstant.FORM_PROPERTY_KEY_SPAN;
	/**
	 * @Fields TASK_JUDGE_HEAD : 用户判断头
	 */
	public static final String TASK_USER_HEAD = ActivitiConstant.TASK_USER_ID_HEAD + ActivitiConstant.FORM_PROPERTY_KEY_SPAN;
	/** 
	* @Title: makeClaims 
	* @Description: 组合可操作者查询信息
	* @param userId 用户ID
	* @param region 区域
	* @param unitIds 部门列表
	* @param adminUnitIds 管理员部门列表
	* @param itemsUnitIds 事项部门列表
	* @param jobIdList 角色列表
	* @return  参数说明 
	* @return List<String>    返回类型 
	* 
	*/
	public static List<String> makeClaims(String userId, String region, List<String> unitIds,
			List<String> adminUnitIds, List<String> itemsUnitIds, List<String> jobIdList)
	{
		List<String> claims = new ArrayList<String>();
		if (CheckDataUtil.isNotNull(userId))
		{
			claims.add(userId);
		}
		// 判断是否要查询候选角色
		if (CheckDataUtil.isNotNull(jobIdList)) {
			claims.addAll(jobIdList);
			// 判断是否要查询部门
			if (CheckDataUtil.isNotNull(unitIds)) {
				for (String jobId : jobIdList) {
					for (String unitId : unitIds) {
						String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claims.add(claimGroup);
					}
				}
			}
			// 判断是否要查询部门管理员
			if (CheckDataUtil.isNotNull(adminUnitIds)) {
				for (String jobId : jobIdList) {
					for (String unitId : adminUnitIds) {
						String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claims.add(claimGroup);
						// 也判断非管理员部门
						claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claims.add(claimGroup);
					}
				}
			}
			// 判断是否要查询事项部门
			if (CheckDataUtil.isNotNull(itemsUnitIds)) {
				for (String jobId : jobIdList) {
					for (String unitId : itemsUnitIds) {
						String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ITEMS_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claims.add(claimGroup);
					}
				}
			}
			// 判断是否要查询区域
			if (CheckDataUtil.isNotNull(region)) {
				for (String jobId : jobIdList) {
					String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
							+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_REGION + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
							+ region;
					claims.add(claimGroup);
				}
			}

		}
		// 判断无角色部门保持
		if (CheckDataUtil.isNotNull(unitIds)) {
			for (String unitId : unitIds) {
				String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claims.add(claimGroup);
			}
		}
		// 判断无角色管理员部门保持
		if (CheckDataUtil.isNotNull(adminUnitIds)) {
			for (String unitId : adminUnitIds) {
				String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claims.add(claimGroup);
				// 也判断非管理员部门
				claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claims.add(claimGroup);
			}
		}
		// 判断无角色事项部门保持
		if (CheckDataUtil.isNotNull(itemsUnitIds)) {
			for (String unitId : itemsUnitIds) {
				String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ITEMS_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claims.add(claimGroup);
			}
		}
		// 判断无角色区域保持
		if (CheckDataUtil.isNotNull(region)) {
			String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_REGION
					+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + region;
			claims.add(claimGroup);
		}
		return claims;
	}
	
}
