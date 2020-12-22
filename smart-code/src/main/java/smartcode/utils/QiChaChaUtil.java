package smartcode.utils;

import java.io.IOException;
import java.util.regex.Pattern;

import cn.hutool.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QiChaChaUtil {

    // 获取返回码 Res Code
    public static class HttpCodeRegex {
        private static final String ABNORMAL_REGIX = "(101)|(102)";
        private static final Pattern pattern = Pattern.compile(ABNORMAL_REGIX);
        protected static boolean isAbnornalRequest(final String status) {
            return pattern.matcher(status).matches();
        }
    }

    // 获取Auth Code
    public static final String[] RandomAuthentHeader(String appkey,String seckey) {
        String timeSpan = String.valueOf(System.currentTimeMillis() / 1000);
        String[] authentHeaders = new String[] { DigestUtils.md5Hex(appkey.concat(timeSpan).concat(seckey)).toUpperCase(), timeSpan };
        return authentHeaders;
    }

    // 解析JSON
    public static String FormartJson(String jsonString, String key){
        JSONObject jObject = new JSONObject(jsonString);
        return (String) jObject.get(key);
    }

    // pretty print 返回值
    public static void PrettyPrintJson(String jsonString){
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object obj = mapper.readValue(jsonString, Object.class);
            String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
