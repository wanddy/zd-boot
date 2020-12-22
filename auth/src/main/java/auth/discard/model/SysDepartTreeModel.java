package auth.discard.model;

import lombok.*;
import lombok.experimental.Accessors;
import auth.entity.Depart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 部门表 存储树结构数据的实体类
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysDepartTreeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 对应SysDepart中的id字段,前端数据树中的key
     */
    private String key;

    /**
     * 对应SysDepart中的id字段,前端数据树中的value
     */
    private String value;

    /**
     * 对应depart_name字段,前端数据树中的title
     */
    private String title;


    private boolean isLeaf;
    // 以下所有字段均与SysDepart相同

    private String id;

    private String parentId;

    private String departName;

    private String departNameEn;

    private String departNameAbbr;

    private Integer departOrder;

    private String description;

    private String orgCategory;

    private String orgType;

    private String orgCode;

    private String mobile;

    private String fax;

    private String address;

    private String memo;

    private String status;

    private String delFlag;

    private List<SysDepartTreeModel> children = new ArrayList<>();


    /**
     * 将SysDepart对象转换成SysDepartTreeModel对象
     *
     * @param depart
     */
    public SysDepartTreeModel(Depart depart) {
        this.key = depart.getId();
        this.value = depart.getId();
        this.title = depart.getDepartName();
        this.id = depart.getId();
        this.parentId = depart.getParentId();
        this.departName = depart.getDepartName();
        this.departNameEn = depart.getDepartNameEn();
        this.departNameAbbr = depart.getDepartNameAbbr();
        this.departOrder = depart.getDepartOrder();
        this.description = depart.getDescription();
        this.orgCategory = depart.getOrgCategory();
        this.orgType = depart.getOrgType();
        this.orgCode = depart.getOrgCode();
        this.mobile = depart.getMobile();
        this.fax = depart.getFax();
        this.address = depart.getAddress();
        this.memo = depart.getMemo();
        this.status = depart.getStatus();
        this.delFlag = depart.getDelFlag();
    }

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
        SysDepartTreeModel model = (SysDepartTreeModel) o;
        return Objects.equals(id, model.id) &&
                Objects.equals(parentId, model.parentId) &&
                Objects.equals(departName, model.departName) &&
                Objects.equals(departNameEn, model.departNameEn) &&
                Objects.equals(departNameAbbr, model.departNameAbbr) &&
                Objects.equals(departOrder, model.departOrder) &&
                Objects.equals(description, model.description) &&
                Objects.equals(orgCategory, model.orgCategory) &&
                Objects.equals(orgType, model.orgType) &&
                Objects.equals(orgCode, model.orgCode) &&
                Objects.equals(mobile, model.mobile) &&
                Objects.equals(fax, model.fax) &&
                Objects.equals(address, model.address) &&
                Objects.equals(memo, model.memo) &&
                Objects.equals(status, model.status) &&
                Objects.equals(delFlag, model.delFlag) &&
                Objects.equals(children, model.children);
    }

    /**
     * 重写hashCode方法
     */
    @Override
    public int hashCode() {

        return Objects.hash(id, parentId, departName, departNameEn, departNameAbbr,
                departOrder, description, orgCategory, orgType, orgCode, mobile, fax, address,
                memo, status, delFlag, children);
    }

}
