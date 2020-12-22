package smartform.form.redis;

import smartform.common.service.RedisService;
import smartform.form.model.SmartFormContent;

import java.util.concurrent.TimeUnit;

/**
* @ClassName: SmartFormContentService
* @Description: SmartForm内容实体的redis存储
* @author quhanlin
* @date 2018年10月6日 下午3:54:42
*
*/
public interface SmartFormContentService extends RedisService<SmartFormContent> {

	/**
	 * 获取表单内容额外参数数据
	 * @param id
	 * @return
	 */
	String getExtraData(String id);

	/**
	 * 设置表单内容额外参数数据
	 * @param id
	 * @param extraData
	 */
	void updateExtraData(String id, String extraData);


	/**
	 * 设置过期时间
	 * @param id
	 * @param timeout
	 * @param timeUnit
	 */
	void expire(String id, long timeout, TimeUnit timeUnit);

	/**
	 * 删除额外数据
	 * @param id
	 */
	void deletExtraData(String id);

}
