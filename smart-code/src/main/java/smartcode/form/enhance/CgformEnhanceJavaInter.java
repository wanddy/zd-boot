package smartcode.form.enhance;

import com.alibaba.fastjson.JSONObject;
import smartcode.config.exception.BusinessException;

import java.util.Map;

public interface CgformEnhanceJavaInter {
    /** @deprecated */
    @Deprecated
    int execute(String var1, Map<String, Object> var2) throws BusinessException;

    int execute(String var1, JSONObject var2) throws BusinessException;
}
