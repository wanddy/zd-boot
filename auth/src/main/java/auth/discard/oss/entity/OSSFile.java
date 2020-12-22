package auth.discard.oss.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import commons.system.base.entity.JeecgEntity;

@Data
@TableName("oss_file")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OSSFile extends JeecgEntity {

	private static final long serialVersionUID = 1L;

	private String fileName;

	private String url;

}
