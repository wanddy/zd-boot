package auth.entity;

import auth.entity.base.BaseAuditingEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;

/**
 * 部门
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
@TableName(value = "sys_depart")
public class Depart extends BaseAuditingEntity implements Serializable {


    /**
     * 父机构ID
     */
    private String parentId;

    /**
     * 机构/部门名称
     */
    private String departName;

    /**
     * 英文名
     */
    private String departNameEn;

    /**
     * 缩写
     */
    private String departNameAbbr;

    /**
     * 排序
     */
    private Integer departOrder;

    /**
     * 描述
     */
    private String description;

    /**
     * 机构类别 1组织机构，2岗位
     */
    private String orgCategory;

    /**
     * 机构类型
     */
    private String orgType;

    /**
     * 机构编码
     */
    private String orgCode;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 传真
     */
    private String fax;

    /**
     * 地址
     */
    private String address;

    /**
     * 备注
     */
    private String memo;

    /**
     * 状态（1启用，0不启用）
     */
    @commons.annotation.Dict(dicCode = "depart_status")
    private String status;

    /**
     * 删除状态（0，正常，1已删除）
     */
    @commons.annotation.Dict(dicCode = "del_flag")
    private String delFlag;

    /**
     * 重写equals方法
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Depart depart = (Depart) o;
        return Objects.equals(getId(), depart.getId()) &&
                Objects.equals(parentId, depart.parentId) &&
                Objects.equals(departName, depart.departName) &&
                Objects.equals(departNameEn, depart.departNameEn) &&
                Objects.equals(departNameAbbr, depart.departNameAbbr) &&
                Objects.equals(departOrder, depart.departOrder) &&
                Objects.equals(description, depart.description) &&
                Objects.equals(orgCategory, depart.orgCategory) &&
                Objects.equals(orgType, depart.orgType) &&
                Objects.equals(orgCode, depart.orgCode) &&
                Objects.equals(mobile, depart.mobile) &&
                Objects.equals(fax, depart.fax) &&
                Objects.equals(address, depart.address) &&
                Objects.equals(memo, depart.memo) &&
                Objects.equals(status, depart.status) &&
                Objects.equals(delFlag, depart.delFlag);
    }

    /**
     * 重写hashCode方法
     */
    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), getId(), parentId, departName,
                departNameEn, departNameAbbr, departOrder, description, orgCategory,
                orgType, orgCode, mobile, fax, address, memo, status,
                delFlag);
    }
}
