package commons.exception;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
public enum FilterFailureReason implements EnumDescription, Serializable {

    NOT_CREDENTIALS(400, "credentials is empty"),

    BAD_REQUEST(400, "Bad Request"),


    UNAUTHORIZED(401, "unauthorized"),

    UNSUPPORTED_TOKEN(401, "unsupported token"),

    INVALID_TOKEN(401, "invalid token"),


    LOCKED_ACCOUNT(403, "account lock"),

    DISABLED_ACCOUNT(403, "account disabled"),

    NOT_EXIST_USER(403, "not exist user"),

    UNKNOWN_ACCOUNT(403, "login failed, account does not exist"),


    NOT_FOUND(404, "Not Found"),


    INTERNAL_SERVER_ERROR(500, "Internal Server Error");


    private final int code;

    private final String desc;

    FilterFailureReason(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
