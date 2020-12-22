package auth.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import commons.auth.vo.LoginUser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
@Component
public class InitObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        val date = localDateTimeToDate();
        this.strictInsertFill(metaObject, "createTime", Date.class, date);
        setCreateOrUpdateBy(metaObject, "createBy");

        this.strictInsertFill(metaObject, "updateTime", Date.class, date);
        setCreateOrUpdateBy(metaObject, "updateBy");
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        val date = localDateTimeToDate();
        this.strictInsertFill(metaObject, "updateTime", Date.class, date);
        setCreateOrUpdateBy(metaObject, "updateBy");
    }

    private Date localDateTimeToDate() {
        Instant instant = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    private void setCreateOrUpdateBy(MetaObject metaObject, String fieldName) {
        try {
            LoginUser principal = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (!ObjectUtils.isEmpty(principal)) {
                this.strictInsertFill(metaObject, fieldName, String.class, principal.getUsername());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
