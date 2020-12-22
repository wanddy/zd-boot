package smartform.common.service;


import smartform.common.model.BaseData;

import java.util.List;

/**
* @ClassName: RedisListService
* @Description: redis列表数据存储
* @author quhanlin
* @date 2018年10月6日 下午6:36:37
* @param <T>
*/
public interface RedisListAllService<T extends BaseData>{

	void updateList(T t);

	void updateList(List<T> list);

	T get(String id);

	List<T> get(String... ids);

	void deleteItem(String... ids);

	void delete();

	boolean hasKey();
}
