package workflow.business.mapper.impl;

import org.springframework.stereotype.Component;
import workflow.business.mapper.OptionDao;
import workflow.business.model.Option;
import workflow.common.redis.JedisMgr_wf;

@Component
public class OptionDaoImpl extends workflow.business.mapper.impl.RedisDaoImpl<Option> implements OptionDao{

	public final String KEY = "Option";

	@Override
	public String getKey(String id){
		return JedisMgr_wf.KeyHead + KEY + JedisMgr_wf.KeySpan + id;
	}

	@Override
	public Class<Option> getClazz(){
		return Option.class;
	}
}
