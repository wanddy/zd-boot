package commons.api.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 文档
 * @Author: jeecg-boot
 * @Date: 2020-06-09
 * @Version: V1.0
 */
@Data
@TableName("oa_wps_file")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
//@ApiModel(value = "oa_wps_file对象", description = "文档")
public class OaWpsModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ID_WORKER_STR)
    private String id;
    /**
     * name
     */
    private String name;
    /**
     * version
     */
    private Integer version;
    /**
     * size
     */
    private Integer size;
    /**
     * downloadUrl
     */
    private String downloadUrl;
    /**
     * deleted
     */
    private String deleted;
    /**
     * canDelete
     */
    private String canDelete;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    /**
     * 更新人
     */
    private String updateBy;
    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;
    /**
     * 组织机构编码
     */
    private String sysOrgCode;

    @TableField(exist = false)
    private String userId;
}
