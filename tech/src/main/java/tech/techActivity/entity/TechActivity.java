package tech.techActivity.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import commons.poi.excel.annatotion.Excel;
import commons.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tech.signUp.entity.SignUp;

/**
 * @Description: 活动表
 * @Author: zd-boot
 * @Date:   2020-12-02
 * @Version: V1.0
 */
@ApiModel(value="tech_activity对象", description="活动表")
@Data
@TableName("tech_activity")
public class TechActivity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
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
	/**活动名称*/
	@Excel(name = "活动名称", width = 15)
    @ApiModelProperty(value = "活动名称")
    private java.lang.String headline;
	/**活动介绍*/
	@Excel(name = "活动介绍", width = 15)
    @ApiModelProperty(value = "活动介绍")
    private java.lang.String introduce;
	/**活动地点*/
	@Excel(name = "活动地点", width = 15)
    @ApiModelProperty(value = "活动地点")
    private java.lang.String place;
	/**活动报名截止时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private java.util.Date time;
    /**活动签到开始时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private java.util.Date signTime;
    /**活动签到截止时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private java.util.Date signEndTime;
	/**活动联系方式*/
	@Excel(name = "活动联系方式", width = 15)
    @ApiModelProperty(value = "活动联系方式")
    private java.lang.String contact;
	/**活动开始时间*/
	@Excel(name = "活动开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "活动开始时间")
    private java.util.Date startTime;
	/**活动结束时间*/
	@Excel(name = "活动结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "活动结束时间")
    private java.util.Date endTime;
	/**活动状态*/
    @Excel(name = "活动状态", width = 15,dicCode = "activity_status")
    @ApiModelProperty(value = "活动状态")
    @Dict(dicCode = "activity_status")
    private java.lang.Long status;
	/**二维码地址*/
	@Excel(name = "二维码地址", width = 15)
    @ApiModelProperty(value = "二维码地址")
    private java.lang.String url;

    @ApiModelProperty(value = "二维码地址")
    private java.lang.String cancelUrl;

    @ApiModelProperty(value = "活动资料")
    private java.lang.String fileUrl;

    @Dict(dicCode = "audit_status")
    @Excel(name = "审核状态",replace={"未审核_1","已通过_2","已退回_3"}, width = 15,dicCode = "audit_status")
    @ApiModelProperty(value = "审核状态")
    private java.lang.String audit;

    /**最多报名人数*/
    private java.lang.String peopleMax;
    /**人数比*/
    @TableField(exist = false)
    private java.lang.String peopleMaxText;

	/**删除标记*/
	@Excel(name = "删除标记", width = 15)
    @ApiModelProperty(value = "删除标记")
    @TableLogic
    private java.lang.String delFlag;

	@TableField(exist = false)
	private String type;

    @TableField(exist = false)
    private SignUp signUp;

    /**
     * 审批人
     */
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    private String deptCode;

    /**
     * 审批部门code
     */
    private String departCode;

    /**
     * 报名需要审批
     */
    @Dict(dicCode = "audit_type")
    private String auditType;

    /**
     * 活动是否需要审批
     */
    @Dict(dicCode = "audit_type")
    private String auditTech;
}
