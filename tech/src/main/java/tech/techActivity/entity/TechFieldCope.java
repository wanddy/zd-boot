package tech.techActivity.entity;

import auth.entity.DictItem;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import commons.poi.excel.annatotion.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 报名表单配置
 * @Author: zd-boot
 * @Date:   2020-12-02
 * @Version: V1.0
 */
@Data
public class TechFieldCope implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
	@ApiModelProperty(value = "主键")
	private String id;
	/**字段别名*/
	@Excel(name = "字段别名", width = 15)
	@ApiModelProperty(value = "字段别名")
	private String fieldName;
	/**字段类型*/
	@Excel(name = "字段类型", width = 15, dicCode = "field_type")
	@ApiModelProperty(value = "字段类型")
	private String fieldType;
	/**字典配置*/
	@Excel(name = "字典配置", width = 15, dictTable = "sys_dict", dicText = "dict_name", dicCode = "dict_code")
	@ApiModelProperty(value = "字典配置")
	private String fieldDict;
	/**排序*/
	@Excel(name = "排序", width = 15)
	@ApiModelProperty(value = "排序")
	private Integer sort;
	/**活动id*/
	@ApiModelProperty(value = "活动id")
	private String techId;

	/**活动id*/
	@ApiModelProperty(value = "内容")
	private String test;


}
