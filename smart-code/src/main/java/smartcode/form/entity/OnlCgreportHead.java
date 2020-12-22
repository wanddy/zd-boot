package smartcode.form.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import commons.annotation.Dict;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/21 15:27
 * @Description: zdit.zdboot.auth.online.entity
 **/
@Data
@TableName("onl_cgreport_head")
public class OnlCgreportHead implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String code;
    private String name;
    private String cgrSql;
    private String returnValField;
    private String returnTxtField;
    private String returnType;
    @Dict(dicCode = "code", dicText = "name", dictTable = "sys_data_source")
    private String dbSource;
    private String content;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String updateBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String createBy;

}
