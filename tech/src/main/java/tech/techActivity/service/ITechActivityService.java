package tech.techActivity.service;

import commons.api.vo.Result;
import tech.techActivity.entity.TechField;
import tech.techActivity.entity.TechActivity;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 活动表
 * @Author: zd-boot
 * @Date:   2020-12-02
 * @Version: V1.0
 */
public interface ITechActivityService extends IService<TechActivity> {

	/**
	 * 添加一对多
	 *
	 */
	public void saveMain(TechActivity techActivity,List<TechField> techFieldList) ;

	/**
	 * 修改一对多
	 *
	 */
	public void updateMain(TechActivity techActivity,List<TechField> techFieldList);

	/**
	 * 删除一对多
	 */
	public void delMain (String id);

	/**
	 * 批量删除一对多
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);

	Result<?> saveCode(TechActivity techActivity);

	String getOpenId(String code);

	Result<?> queryById(String id, String openId);

	void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException;

	/**
	 * 下载二维码
	 * @param id
	 * @param type
	 * @param request
	 * @param response
	 */
    void download(String id,String type, HttpServletRequest request, HttpServletResponse response) throws MalformedURLException;

}
