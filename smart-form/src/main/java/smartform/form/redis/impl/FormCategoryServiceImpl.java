package smartform.form.redis.impl;

import org.springframework.stereotype.Component;
import smartform.common.service.impl.RedisServiceImpl;
import smartform.common.redis.JedisMgr;
import smartform.form.model.FormCategory;
import smartform.form.redis.FormCategoryService;

@Component
public class FormCategoryServiceImpl extends RedisServiceImpl<FormCategory> implements FormCategoryService {
	public final String KEY = "formcategory";

	@Override
	public String getKey(String id) {
		return JedisMgr.KeyHead + KEY + JedisMgr.KeySpan + id;
	}

	@Override
	public Class<FormCategory> getClazz() {
		return FormCategory.class;
	}
}
