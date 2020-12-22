package auth.entity;

import auth.entity.base.BaseAuditingEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 分类字典
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_category")
public class Category extends BaseAuditingEntity implements Serializable, Comparable<Category> {

    /**
     * 父级节点
     */
    private String pid;

    /**
     * 类型名称
     */
    private String name;

    /**
     * 类型编码
     */
    private String code;

    /**
     * 所属部门
     */
    private String sysOrgCode;

    /**
     * 是否有子节点
     */
    private String hasChild;

    /**
     * 区分字段
     */
    @TableField(exist = false)
    private Integer type;

    @TableField(exist = false)
    private String dictionary_name;

    @TableField(exist = false)
    private String dictionary_code;

    @TableField(exist = false)
    private String label;

    @TableField(exist = false)
    private String value;

    /**
     * 递归子级
     */
    @TableField(exist = false)
    private List<Category> categoryList;

    @Override
    public int compareTo(Category o) {
        //比较条件我们定的是按照code的长度升序
        // <0：当前对象比传入对象小。
        // =0：当前对象等于传入对象。
        // >0：当前对象比传入对象大。
        return this.code.length() - o.code.length();
    }

    @Override
    public String toString() {
        return "SysCategory [code=" + code + ", name=" + name + "]";
    }
}
