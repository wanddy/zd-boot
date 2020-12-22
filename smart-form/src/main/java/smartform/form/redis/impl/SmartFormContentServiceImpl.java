package smartform.form.redis.impl;

import org.springframework.stereotype.Component;
import smartform.common.service.impl.RedisServiceImpl;
import smartform.common.redis.JedisMgr;
import smartform.form.model.SmartFormContent;
import smartform.form.redis.SmartFormContentService;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: SmartFormContentServiceImpl
 * @Description: SmartForm内容实体的redis存储
 * @author quhanlin
 * @date 2018年10月6日 下午3:53:36
 *
 */
@Component
public class SmartFormContentServiceImpl extends RedisServiceImpl<SmartFormContent> implements SmartFormContentService {
	public final String KEY = "SmartFormContent";
	public final String ExtraDataKEY = "SmartFormContentExtraData";

	public String getExtraDataKey(String id) {
		return JedisMgr.KeyHead + ExtraDataKEY + JedisMgr.KeySpan + id;
	}

	@Override
	public String getKey(String id) {
		return JedisMgr.KeyHead + KEY + JedisMgr.KeySpan + id;
	}

	@Override
	public Class<SmartFormContent> getClazz() {
		return SmartFormContent.class;
	}

	@Override
	public String getExtraData(String id) {
		return JedisMgr.GetInstance().get(getExtraDataKey(id));
	}

	@Override
	public void updateExtraData(String id, String extraData) {
		JedisMgr.GetInstance().updateOnce(getExtraDataKey(id), extraData);
	}

	@Override
	public void expire(String id, long timeout, TimeUnit timeUnit){
		JedisMgr.GetInstance().expire(getExtraDataKey(id), timeout, timeUnit);
	}

	@Override
	public void deletExtraData(String id){
		JedisMgr.GetInstance().delete(getExtraDataKey(id));
	}
}
