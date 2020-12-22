package smartcode.form.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/24 10:10
 * @Description: zdit.zdboot.auth.online.model
 **/
@Data
public class OnlGenerateModel implements Serializable {
    private static final long b = 684098897071177558L;
    private String code;
    private String projectPath;
    private String packageStyle;
    private String ftlDescription;
    private String jformType;
    private String tableName;
    private String entityPackage;
    private String entityName;
    private String jspMode;
    private List<OnlGenerateModel> subList;
}
