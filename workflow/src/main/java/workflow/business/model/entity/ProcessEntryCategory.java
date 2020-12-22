package workflow.business.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import smartform.common.model.BaseData;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: FormCategory
 * @Description: 表单分类
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
@Data
@ToString
@TableName("process_entry_category")
public class ProcessEntryCategory extends BaseData implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * UUID，32位
	 */
	private String id;
	/**
	 * 分类名
	 */
	private String name;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 创建时间
	 */
	private Long createdAt;
	/**
	 * 修改时间
	 */
	private Long modifiedAt;
	/**
	 * 状态 1：有效
	 */
	private int state;
}