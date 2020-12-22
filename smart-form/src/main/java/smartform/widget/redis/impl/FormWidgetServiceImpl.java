package smartform.widget.redis.impl;

import org.springframework.stereotype.Component;
import smartform.common.service.impl.RedisServiceImpl;
import smartform.common.redis.JedisMgr;
import smartform.widget.model.WidgetBase;
import smartform.widget.redis.FormWidgetService;

@Component
public class FormWidgetServiceImpl extends RedisServiceImpl<WidgetBase> implements FormWidgetService {

	public final String KEY = "widget";
	@Override
	public String getKey(String id){
		return JedisMgr.KeyHead + KEY + JedisMgr.KeySpan + id;
	}

	@Override
	public Class<WidgetBase> getClazz(){
		return WidgetBase.class;
	}
}
