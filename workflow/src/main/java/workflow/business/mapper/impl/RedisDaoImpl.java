package workflow.business.mapper.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import workflow.business.mapper.RedisDao;
import workflow.business.model.BaseData;
import workflow.common.redis.JedisMgr_wf;

import java.io.IOException;

public class RedisDaoImpl<T extends BaseData> implements RedisDao<T> {

	@Override
	public String getKey(String id) {
		return id;
	}

	@Override
	public Class<T> getClazz() {
		return null;
	}

	@Override
	public T get(String id) {
		try {
			return JedisMgr_wf.GetInstance().get(getKey(id), getClazz());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException | IOException e) {

			e.printStackTrace();
		}
		return null;
	}

	@Override
	public T get(String id, String... params) {
		try {
			return JedisMgr_wf.GetInstance().get(getKey(id), getClazz(), params);
		} catch (InstantiationException | IllegalAccessException | NoSuchFieldException | SecurityException
				| ClassNotFoundException | IllegalArgumentException | IOException e) {

			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void update(BaseData entity) {
		try {
			JedisMgr_wf.GetInstance().update(getKey(entity.getId()), entity);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
				| JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(BaseData entity, String... params) {
		try {
			JedisMgr_wf.GetInstance().update(getKey(entity.getId()), entity, params);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
				| JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateOnce(String id, String name, String value) {
		JedisMgr_wf.GetInstance().updateOnce(getKey(id), name, value);
	}

	@Override
	public void delete(String id) {
		JedisMgr_wf.GetInstance().delete(getKey(id));
	}

	@Override
	public void delete(String id, String... params) {
		JedisMgr_wf.GetInstance().deleteParam(getKey(id), params);
	}

	@Override
	public boolean hasKey(String id) {
		return JedisMgr_wf.GetInstance().hasKey(getKey(id));
	}
}
