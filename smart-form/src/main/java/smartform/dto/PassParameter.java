package smartform.dto;

import lombok.Data;
import smartform.widget.model.OptionSourceInput;

/**
 * @Author: QiHangZhang
 * @Date: 2020/8/27 15:27
 * @Description: 前端参数传递封装
 */
@Data
public class PassParameter {


    private String id;

    private Integer state;

    private OptionSourceInput pageOption;

    private Integer categoryType;

    //新建 修改表单传参
    private String form;

    private String source;

    //新建表单分类
    private String category;

    //修改组件状态（是否发布）
    private boolean release;

    //新建  修改组件
    private String group;

    //新建  修改组件
    private String data;

    private boolean storage;

}
