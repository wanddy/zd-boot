package smartform.common.service;


import smartform.common.model.BaseData;
import smartform.common.model.PageList;

public interface RedisService<T extends BaseData> {

    Class<T> getClazz();

    String getKey(String id);

    T get(String id);

    T get(String id, String... params);

    void update(T entity);

    void update(T entity, String... params);

    void updateOnce(String id, String name, String value);

    void delete(String id);

    void delete(String id, String... params);

    boolean hasKey(String id);

    boolean hasView();

    String getViewKey();

    PageList<T> getList(int pageNum, int pageSize);

    PageList<T> getList(int pageNum, int pageSize, String... params);
}
