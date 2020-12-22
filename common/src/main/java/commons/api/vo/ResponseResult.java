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
public class ResponseResult<T> {

    /**
     * 成功失败标志
     */
    private boolean flag = true;

    /**
     * 状态码
     */
    private int status;

    /**
     * 返回实体
     */
    private T data;

    public ResponseResult<T> ok() {
        this.status = HttpStatus.OK.value();
        return this;
    }

    public ResponseResult<T> ok(T data) {
        this.status = HttpStatus.OK.value();
        this.data = data;
        return this;
    }
}
