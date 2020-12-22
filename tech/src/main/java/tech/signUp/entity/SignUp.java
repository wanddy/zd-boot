package tech.signUp.entity;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.*;
import commons.annotation.Dict;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import commons.poi.excel.annatotion.Excel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import tech.techActivity.entity.TechField;

/**
 * @Description: 报名表
 * @Author: zd-boot
 * @Date:   2020-11-24
 * @Version: V1.0
 */
@Data
@TableName("sign_up")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sign_up对象", description="报名表")
public class SignUp implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;

	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;

	/**姓名*/
	@Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
    private java.lang.String name;

	/**单位名称*/
	@Excel(name = "单位名称", width = 15)
    @ApiModelProperty(value = "单位名称")
    private java.lang.String unitName;

	/**手机号码*/
	@Excel(name = "手机号码", width = 15)
    @ApiModelProperty(value = "手机号码")
    private java.lang.String phoneNumber;

    /**活动名*/
    @Excel(name = "活动名", width = 15)
    @TableField(exist = false)
    private java.lang.String tech;

	/**活动名*/
    @ApiModelProperty(value = "活动名")
    @Dict(dictTable = "tech_activity", dicText = "headline", dicCode = "id")
    private java.lang.String techName;

    /**用户信息*/
    private java.lang.String openId;

    /**创建日期*/
    @Excel(name = "创建日期", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;

    @Dict(dicCode = "audit_status")
    @Excel(name = "审核状态",replace={"未审核_1","已通过_2","已退回_3"}, width = 15,dicCode = "audit_status")
    @ApiModelProperty(value = "审核状态")
    private java.lang.String audit;

    @Dict(dicCode = "sign_status")
    @Excel(name = "签到状态",replace={"已签到_1","未签到_2"}, width = 15,dicCode = "sign_status")
    @ApiModelProperty(value = "签到状态")
    private java.lang.String status;

    /**统计标记*/
    private java.lang.String type;

    /**json文本*/
    private java.lang.String fieldTest;

	/**删除标记*/
    @ApiModelProperty(value = "删除标记")
    @Dict(dicCode = "del_flag")
    @TableLogic
    private java.lang.String delFlag;

    @TableField(exist = false)
    private List<TechField> techFieldList;


}
