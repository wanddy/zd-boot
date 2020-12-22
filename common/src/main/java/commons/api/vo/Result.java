package commons.api.vo;

import java.io.Serializable;

import commons.constant.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接口返回数据格式
 */
@Data
@Builder
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    //@ApiModelProperty(value = "成功标志")
    @Builder.Default
    private boolean success = true;

    /**
     * 返回处理消息
     */
    //@ApiModelProperty(value = "返回处理消息")
    @Builder.Default
    private String message = "操作成功！";

    /**
     * 返回代码
     */
    //@ApiModelProperty(value = "返回代码")
    @Builder.Default
    private Integer code = 0;

    /**
     * 返回数据对象 data
     */
    //@ApiModelProperty(value = "返回数据对象")
    private T result;

    /**
     * 时间戳
     */
    //@ApiModelProperty(value = "时间戳")
    @Builder.Default
    private long timestamp = System.currentTimeMillis();

    public Result() {
        this.setSuccess(true);
    }

    public Result<T> success(String message) {
        this.message = message;
        this.code = CommonConstant.SC_OK_200;
        this.success = true;
        return this;
    }


    public static Result<Object> ok() {
        Result<Object> r = new Result<>();
        r.setSuccess(true);
        r.setCode(CommonConstant.SC_OK_200);
        r.setMessage("成功");
        return r;
    }

    public static Result<Object> ok(String msg) {
        Result<Object> r = new Result<>();
        r.setSuccess(true);
        r.setCode(CommonConstant.SC_OK_200);
        r.setMessage(msg);
        return r;
    }

    public static Result<Object> ok(Object data) {
        Result<Object> r = new Result<>();
        r.setSuccess(true);
        r.setCode(CommonConstant.SC_OK_200);
        r.setResult(data);
        return r;
    }

    public static Result<Object> error(String msg) {
        return error(CommonConstant.SC_INTERNAL_SERVER_ERROR_500, msg);
    }

    public static Result<Object> error(int code, String msg) {
        Result<Object> r = new Result<>();
        r.setCode(code);
        r.setMessage(msg);
        r.setSuccess(false);
        return r;
    }

    public Result<T> error500(String message) {
        this.message = message;
        this.code = CommonConstant.SC_INTERNAL_SERVER_ERROR_500;
        this.success = false;
        return this;
    }

    /**
     * 无权限访问返回结果
     */
    public static Result<Object> noach(String msg) {
        return error(CommonConstant.SC_JEECG_NO_AUTHZ, msg);
    }
}
