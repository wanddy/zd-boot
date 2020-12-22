package auth.entity.base;

import java.util.Date;

public interface IBaseAuditing {

    String getId();

    void setId(String id);

    String getCreateBy();

    void setCreateBy(String createBy);

    Date getCreateTime();

    void setCreateTime(Date createTime);

    String getUpdateBy();

    void setUpdateBy(String updateBy);

    Date getUpdateTime();

    void setUpdateTime(Date updateTime);
}
