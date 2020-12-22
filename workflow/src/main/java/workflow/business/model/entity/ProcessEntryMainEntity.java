package workflow.business.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2020年09月15日 9:40
 * <p>
 * 业务主表
 */
@Data
@ToString
@TableName("process_entry_main")
public class ProcessEntryMainEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * UUID，32位
     */
    private String id;
    /**
     * 课题类型1工作流审批项目 2智能表单填报项目
     */
    private String projectType;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 表单id
     */
    private String formId;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 项目类型
     */
    private String categoryId;
    /**
     * 流程key
     */
    private String processKey;
    /**
     * 已发布流程id，流程定义id
     */
    private String processDefinitionId;
    /**
     * 流程实例id
     */
    private String processInstanceId;
    /**
     * 申报开始时间yyyy-MM-dd hh:mm:ss
     */
    private Long startTime;
    /**
     * 申报结束时间yyyy-MM-dd hh:mm:ss
     */
    private Long endTime;
    /**
     * 申报结束时间yyyy-MM-dd hh:mm:ss
     */
    private Long createdAt;
    /**
     * 修改时间yyyy-MM-dd hh:mm:ss
     */
    private Long modifiedAt;
    /**
     * 状态 1：有效
     */
    private int state;
    /**
     * 是否常用 1：是
     */
    private int commonly;

}
