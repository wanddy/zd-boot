package workflow.business.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2020年09月15日 9:40
 *
 * 业务主表
 */
@Data
@ToString
@TableName("project_pages")
public class ProjectPagesEntity implements Serializable {
    private static final long serialVersionUID = 1L;
   /**
   UUID，32位
   */
    private String id;
    /**
     业务主id，对应projectmain表id
     */
    private String contentId;
    /**
     表单id
     */
    private String formId;
    /**
     分页id
     */
    private String pageId;
    /**
     分页状态
     */
    private String pageStatus;
    /**
     申报结束时间yyyy-MM-dd hh:mm:ss
     */
    private Long createdAt;

      /**
     修改时间yyyy-MM-dd hh:mm:ss
     */
    private Long modifiedAt;
    /**
     用户id
     */
    private String userId;

}
