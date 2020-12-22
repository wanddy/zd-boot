package auth.entity;

import auth.entity.base.BaseAuditingEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import commons.annotation.Dict;
import commons.poi.excel.annatotion.Excel;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * 职务表
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_position")
public class Position {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private java.lang.String id;
    /**
     * 职务编码
     */
    @Excel(name = "职务编码", width = 15)
    private java.lang.String code;
    /**
     * 职务名称
     */
    @Excel(name = "职务名称", width = 15)
    private java.lang.String name;
    /**
     * 职级
     */
    @Excel(name = "职级", width = 15,dicCode ="position_rank")
    @Dict(dicCode = "position_rank")
    private java.lang.String postRank;
    /**
     * 公司id
     */
    @Excel(name = "公司id", width = 15)
    private java.lang.String companyId;
    /**
     * 创建人
     */
    private java.lang.String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date createTime;
    /**
     * 修改人
     */
    private java.lang.String updateBy;
    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;
    /**
     * 组织机构编码
     */
    @Excel(name = "组织机构编码", width = 15)
    private java.lang.String sysOrgCode;
}
