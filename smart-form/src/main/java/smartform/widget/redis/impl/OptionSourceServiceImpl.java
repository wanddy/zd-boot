package smartform.widget.redis.impl;

import org.springframework.stereotype.Component;
import smartform.common.service.impl.RedisServiceImpl;
import smartform.common.redis.JedisMgr;
import smartform.widget.model.OptionSource;
import smartform.widget.redis.OptionSourceService;

@Component
public class OptionSourceServiceImpl extends RedisServiceImpl<OptionSource> implements OptionSourceService {
	public final String KEY = "optionsource";

	@Override
	public String getKey(String id){
		return JedisMgr.KeyHead + KEY + JedisMgr.KeySpan + id;
	}

	@Override
	public Class<OptionSource> getClazz(){
		return OptionSource.class;
	}
}
