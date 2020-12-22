package auth.domain.relation.user.depart.service;


import java.util.List;

import auth.entity.User;
import auth.entity.UserDepart;
import auth.discard.model.DepartIdModel;


import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * SysUserDpeart用户组织机构service
 * </p>
 * @Author ZhiLin
 *
 */
public interface ISysUserDepartService extends IService<UserDepart> {


	/**
	 * 根据指定用户id查询部门信息
	 * @param userId
	 * @return
	 */
	List<DepartIdModel> queryDepartIdsOfUser(String userId);


	/**
	 * 根据部门id查询用户信息
	 * @param depId
	 * @return
	 */
	List<User> queryUserByDepId(String depId);
  	/**
	 * 根据部门code，查询当前部门和下级部门的用户信息
	 */
	public List<User> queryUserByDepCode(String depCode,String userId);
}
