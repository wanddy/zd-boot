package smartform.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import smartform.common.service.RedisListService;
import smartform.common.model.BaseData;
import smartform.common.redis.JedisMgr;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisListServiceImpl<T extends BaseData> implements RedisListService<T> {

	@Override
	public Class<T> getClazz() {
		return null;
	}

	@Override
	public String getListKey(String keyId) {
		return JedisMgr.KeyHead + getClazz().getSimpleName() + keyId != null ?  JedisMgr.KeySpan + keyId : "";
	}

	@Override
	public void updateList(String keyId, T t) {
		try {
			JedisMgr.GetInstance().updateHash(getListKey(keyId), t.getId(), t);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void updateList(String keyId, List<T> list) {
		Map<String, String> datas = new HashMap<String, String>();
		for (T t : list) {
			datas.put(t.getId(), JSONObject.toJSONString(t));
//			try {
//
//				datas.put(t.getId(), FieldUtil.objectMapper.writeValueAsString(t));
//			} catch (JsonProcessingException e) {
//				e.printStackTrace();
//			}
		}
		JedisMgr.GetInstance().updateHash(getListKey(keyId), datas);
	}

	@Override
	public T get(String keyId, String id) {
		T t = null;
		try {
			t = JedisMgr.GetInstance().getHash(getListKey(keyId), id, getClazz());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}

	@Override
	public List<T> get(String keyId, String... ids) {
		List<T> list = null;
		List<String> idlist = Arrays.asList(ids);
		try {
			list = JedisMgr.GetInstance().getHash(getListKey(keyId), idlist, getClazz());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void deleteItem(String keyId, String... ids) {
		JedisMgr.GetInstance().deleteHash(getListKey(keyId), ids);
	}

	@Override
	public void delete(String keyId) {
		JedisMgr.GetInstance().delete(keyId);
	}

	@Override
	public boolean hasKey(String keyId) {
		return JedisMgr.GetInstance().hasKey(getListKey(keyId));
	}
}
