package tech.techActivity.entity;

import java.io.Serializable;

import auth.entity.Dict;
import auth.entity.DictItem;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import commons.poi.excel.annatotion.Excel;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 报名表单配置
 * @Author: zd-boot
 * @Date:   2020-12-02
 * @Version: V1.0
 */
@ApiModel(value="tech_activity对象", description="活动表")
@Data
@TableName("tech_field")
public class TechField implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
	@ApiModelProperty(value = "主键")
	private java.lang.String id;
	/**字段别名*/
	@Excel(name = "字段别名", width = 15)
	@ApiModelProperty(value = "字段别名")
	private java.lang.String fieldName;
	/**字段类型*/
	@Excel(name = "字段类型", width = 15, dicCode = "field_type")
	@ApiModelProperty(value = "字段类型")
	private java.lang.String fieldType;
	/**字典配置*/
	@Excel(name = "字典配置", width = 15, dictTable = "sys_dict", dicText = "dict_name", dicCode = "dict_code")
	@ApiModelProperty(value = "字典配置")
	private java.lang.String fieldDict;
	/**排序*/
	@Excel(name = "排序", width = 15)
	@ApiModelProperty(value = "排序")
	private java.lang.Integer sort;
	/**活动id*/
	@ApiModelProperty(value = "活动id")
	private java.lang.String techId;

	/**活动id*/
	@ApiModelProperty(value = "内容")
	private java.lang.String test;

	/**活动id*/
	@TableField(exist = false)
	private java.lang.String itemText;

	@TableField(exist = false)
	private List<DictItem> dictList;
}
