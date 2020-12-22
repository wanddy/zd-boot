package smartform.common.error;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartFormError extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 错误信息
     */
    private String message;
    /**
     * 业务错误编码
     */
    private int code = 60402;

    public SmartFormError() {
    }

    public SmartFormError(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", code);
        map.put("message", message);
        String json = JSON.toJSONString(map);
        return json;
    }

}
