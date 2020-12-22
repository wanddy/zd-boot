package workflow.business.mapper;

import workflow.business.model.UserReplace;

import java.util.List;

public interface UserReplaceDao {

	/**
	* @Title: get
	* @Description: 获取数据
	* @param id
	* @return  参数说明
	* @return UserReplace    返回类型
	*
	*/
	UserReplace get(String id);

	/**
	* @Title: deleteParams
	* @Description: 删除单个参数
	* @param id
	* @param paramkey  参数说明
	* @return void    返回类型
	*
	*/
	void deleteParams(String id, String paramkey);

	/**
	* @Title: deleteParams
	* @Description: 批量删除参数
	* @param id
	* @param paramkeys  参数说明
	* @return void    返回类型
	*
	*/
	void deleteParams(String id, List<String> paramkeys);

}
