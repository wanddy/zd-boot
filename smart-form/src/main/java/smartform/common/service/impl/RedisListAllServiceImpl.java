package smartform.common.service.impl;


import smartform.common.service.RedisListAllService;
import smartform.common.model.BaseData;

import java.util.List;

/**
* @ClassName: RedisListAllServiceImpl
* @Description: 列表数据无keyId模式（所有列表都存储在一个redis key中），封装自 RedisListServiceImpl
* @author quhanlin
* @date 2018年10月6日 下午8:52:24
*
* @param <T>
*/
public class RedisListAllServiceImpl<T extends BaseData> extends RedisListServiceImpl<T> implements RedisListAllService<T> {

	@Override
	public void updateList(T t) {
		this.updateList(null, t);
	}

	@Override
	public void updateList(List<T> list) {
		this.updateList(null, list);
	}

	@Override
	public T get(String id) {
		return this.get(null, id);
	}

	@Override
	public List<T> get(String... ids) {
		return this.get(null, ids);
	}

	@Override
	public void deleteItem(String... ids) {
		this.deleteItem(null, ids);
	}

	@Override
	public void delete() {
		this.delete(null);
	}

	@Override
	public boolean hasKey() {
		return this.hasKey(null);
	}
}
