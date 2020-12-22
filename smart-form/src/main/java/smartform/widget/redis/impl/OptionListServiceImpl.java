package smartform.widget.redis.impl;

import org.springframework.stereotype.Component;
import smartform.common.service.impl.RedisListAllServiceImpl;
import smartform.common.redis.JedisMgr;
import smartform.widget.model.Option;
import smartform.widget.redis.OptionListService;

/**
* @ClassName: ContentFieldListServiceImpl
* @Description: 表单内容数据字段列表操作类
* @author quhanlin
* @date 2018年10月6日 下午8:04:09
*
*/
@Component
public class OptionListServiceImpl extends RedisListAllServiceImpl<Option> implements OptionListService {
	public final String KEY = "options";

	@Override
	public Class<Option> getClazz() {
		return Option.class;
	}

	@Override
	public String getListKey(String keyId) {
		// 不使用keyId存储，所有options存到一个redis key中
		return JedisMgr.KeyHead + KEY;
	}
}
