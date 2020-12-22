package smartcode.form.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: LiuHongYan
 * @Date: 2020/9/9 16:04
 * @Description: 智能代码--head表头
 **/
@Data
@TableName("onl_cgform_head")
public class OnlCgformHead implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.UUID)
    private String id;
    private String tableName;
    private Integer tableType;
    private Integer tableVersion;
    private String tableTxt;
    private String isCheckbox;
    private String isDbSynch;
    private String isPage;
    private String isTree;
    private String idSequence;
    private String idType;
    private String queryMode;
    private Integer relationType;
    private String subTableStr;
    private Integer tabOrderNum;
    private String treeParentIdField;
    private String treeIdField;
    private String treeFieldname;
    private String formCategory;
    private String formTemplate;
    private String updateBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String createBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private Integer copyType;
    private Integer copyVersion;
    private String physicId;
    private Integer scroll;
    @TableField(exist = false)
    private String taskId;
    @TableField(exist = false)
    private Integer hascopy;
    @TableField(exist = false)
    private String themeTemplate;
    @TableField(exist = false)
    private String formTemplateMobile;
}
