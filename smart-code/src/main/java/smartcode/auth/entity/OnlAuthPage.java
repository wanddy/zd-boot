package smartcode.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import commons.poi.excel.annatotion.Excel;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("onl_auth_page")
public class OnlAuthPage implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(
            type = IdType.ASSIGN_ID
    )
    private String id;
    @Excel(
            name = "online表id",
            width = 15.0D
    )
    private String cgformId;
    @Excel(
            name = "字段名/按钮编码",
            width = 15.0D
    )
    private String code;
    @Excel(
            name = "1字段 2按钮",
            width = 15.0D
    )
    private Integer type;
    @Excel(
            name = "3可编辑 5可见",
            width = 15.0D
    )
    private Integer control;
    @Excel(
            name = "3列表 5表单",
            width = 15.0D
    )
    private Integer page;
    @Excel(
            name = "1有效 0无效",
            width = 15.0D
    )
    private Integer status;
    @JsonFormat(
            timezone = "GMT+8",
            pattern = "yyyy-MM-dd"
    )
    @DateTimeFormat(
            pattern = "yyyy-MM-dd"
    )
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private String updateBy;
    @JsonFormat(
            timezone = "GMT+8",
            pattern = "yyyy-MM-dd"
    )
    @DateTimeFormat(
            pattern = "yyyy-MM-dd"
    )
    @JsonIgnore
    private Date updateTime;

    public OnlAuthPage() {
    }

    public OnlAuthPage(String cgformId, String code, int page, int control) {
        this.type = 1;
        this.cgformId = cgformId;
        this.code = code;
        this.control = control;
        this.page = page;
        this.status = 1;
    }
}
