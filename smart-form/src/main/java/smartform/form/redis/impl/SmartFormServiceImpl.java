package smartform.form.redis.impl;

import org.springframework.stereotype.Component;
import smartform.common.service.impl.RedisServiceImpl;
import smartform.common.redis.JedisMgr;
import smartform.form.model.SmartForm;
import smartform.form.redis.SmartFormService;

@Component
public class SmartFormServiceImpl extends RedisServiceImpl<SmartForm> implements SmartFormService {
	public final String KEY = "smartform";

	@Override
	public String getKey(String id) {
		return JedisMgr.KeyHead + KEY + JedisMgr.KeySpan + id;
	}

	@Override
	public Class<SmartForm> getClazz() {
		return SmartForm.class;
	}
}
