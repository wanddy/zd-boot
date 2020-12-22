package commons.api.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseError {

    /**
     * 成功失败标志
     */
    private boolean flag = false;


    /**
     * 返回处理消息
     */
    private String message;

    /**
     * 状态码
     */
    private int status;

    /**
     * 错误
     */
    private Object error;

    /**
     * 请求地址
     */
    private String path;

    /**
     * 时间戳
     */
    private long timestamp;

}
