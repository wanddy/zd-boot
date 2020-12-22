package smartcode.form.service;

import com.alibaba.fastjson.JSONObject;
import smartcode.form.entity.OnlCgformEnhanceJs;
import smartcode.form.entity.OnlCgformHead;
import smartcode.form.model.BModel;
public interface OnlineService {

    JSONObject queryOnlineFormObj(OnlCgformHead var1, OnlCgformEnhanceJs var2);

    JSONObject queryOnlineFormObj(OnlCgformHead var1);

    BModel queryOnlineConfig(OnlCgformHead var3);
}
