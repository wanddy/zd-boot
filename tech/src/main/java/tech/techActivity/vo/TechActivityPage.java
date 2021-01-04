package tech.techActivity.vo;

import java.util.List;

import commons.annotation.Dict;
import tech.techActivity.entity.TechField;
import lombok.Data;
import commons.poi.excel.annatotion.Excel;
import commons.poi.excel.annatotion.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 活动表
 * @Author: zd-boot
 * @Date:   2020-12-02
 * @Version: V1.0
 */
@Data
@ApiModel(value="tech_activityPage对象", description="活动表")
public class TechActivityPage {

	/**主键*/
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
	/**最多报名人数*/
	private java.lang.String peopleMax;
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
	@Excel(name = "活动状态", width = 15)
	@ApiModelProperty(value = "活动状态")
	private java.lang.String status;
	/**二维码地址*/
	@Excel(name = "二维码地址", width = 15)
	@ApiModelProperty(value = "二维码地址")
	private java.lang.String url;
	/**删除标记*/
	@Excel(name = "删除标记", width = 15)
	@ApiModelProperty(value = "删除标记")
	private java.lang.String delFlag;

	@ApiModelProperty(value = "活动资料")
	private java.lang.String fileUrl;

	/**
	 * 审批部门
	 */
	@Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
	private String deptCode;

	/**
	 * 审批部门code
	 */
	private String departCode;

	/**
	 * 报名是否需要审批
	 */
	@Dict(dicCode = "audit_type")
	private String auditType;

	@ExcelCollection(name="报名表单配置")
	@ApiModelProperty(value = "报名表单配置")
	private List<TechField> techFieldList;


	/**
	 * 活动是否需要审批
	 */
	@Dict(dicCode = "audit_type")
	private String auditTech;

}
