package commons.auth.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import commons.api.vo.ResponseResult;
import commons.exception.FilterFailureReason;
import commons.api.vo.ResponseError;

@Slf4j
public class ErrorHolder {

    public static String responseToJson(FilterFailureReason reason, String path) {
        return JSON.toJSONString(ResponseError.builder()
                .status(reason.getCode())
                .error(reason)
                .message(reason.getDesc())
                .path(path)
                .timestamp(System.currentTimeMillis())
                .build());
    }

}
