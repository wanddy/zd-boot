package smartform.form.redis.impl;

import org.springframework.stereotype.Component;
import smartform.common.service.impl.RedisListServiceImpl;
import smartform.common.redis.JedisMgr;
import smartform.form.model.FormFieldBase;
import smartform.form.redis.ContentFieldListService;

/**
* @ClassName: ContentFieldListServiceImpl
* @Description: 表单内容数据字段列表操作类
* @author quhanlin
* @date 2018年10月6日 下午8:04:09
*
*/
@Component
public class ContentFieldListServiceImpl extends RedisListServiceImpl<FormFieldBase> implements ContentFieldListService {
	public final String KEY = "contentfields";

	@Override
	public Class<FormFieldBase> getClazz() {
		return FormFieldBase.class;
	}

	@Override
	public String getListKey(String keyId) {
		return JedisMgr.KeyHead + KEY + JedisMgr.KeySpan + keyId;
	}
}
