package smartform.common.service;


import smartform.common.model.BaseData;

import java.util.List;

/**
 * @param <T>
 * @author quhanlin
 * @ClassName: RedisListService
 * @Description: redis列表数据存储
 * @date 2018年10月6日 下午6:36:37
 */
public interface RedisListService<T extends BaseData> {

    Class<T> getClazz();

    String getListKey(String keyId);

    void updateList(String keyId, T t);

    void updateList(String keyId, List<T> list);

    T get(String keyId, String id);

    List<T> get(String keyId, String... ids);

    void deleteItem(String keyId, String... ids);

    void delete(String keyId);

    boolean hasKey(String id);
}
