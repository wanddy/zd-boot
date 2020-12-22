package smartform.form.redis.impl;

import org.springframework.stereotype.Component;
import smartform.common.service.impl.RedisServiceImpl;
import smartform.common.redis.JedisMgr;
import smartform.form.model.Group;
import smartform.form.redis.GroupService;

@Component
public class GroupServiceImpl extends RedisServiceImpl<Group> implements GroupService {
	public final String KEY = "group";

	@Override
	public String getKey(String id) {
		return JedisMgr.KeyHead + KEY + JedisMgr.KeySpan + id;
	}

	@Override
	public Class<Group> getClazz() {
		return Group.class;
	}
}
