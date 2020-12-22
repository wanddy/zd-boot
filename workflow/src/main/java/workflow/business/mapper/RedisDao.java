package workflow.business.mapper;

import workflow.business.model.BaseData;

public interface RedisDao<T extends BaseData> {

	Class<T> getClazz();

	String getKey(String id);

	T get(String id);

	T get(String id, String...params);

	void update(T entity);

	void update(T entity, String...params);

	void updateOnce(String id, String name, String value);

	void delete(String id);

	void delete(String id, String...params);

	boolean hasKey(String id);
}
