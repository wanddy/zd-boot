package tech.techActivity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import commons.api.vo.Result;
import commons.auth.vo.LoginUser;
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

	IPage<TechActivity> getPage(Page<TechActivity> page, TechActivity techActivity, LoginUser sysUser);

	/**
	 * 活动模板消息发送
	 * @param techActivity
	 * @param templateId7
	 * @param value
	 */
    void getTemplate(TechActivity techActivity, String templateId7, String value);

	/**
	 * 公众号活动列表
	 * @param headline
	 * @param place
	 * @param startTime
	 * @return
	 */
	List<TechActivity> appList(String headline, String place, String startTime,String status);

	/**
	 * 活动审批
	 * @param techActivity
	 * @param templateId8
	 * @param value
	 */
	void getTemplate1(TechActivity techActivity, String templateId8, String value);
}
