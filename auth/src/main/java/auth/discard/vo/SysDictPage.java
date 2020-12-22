package auth.discard.vo;

import lombok.Data;
import auth.entity.DictItem;

import java.util.List;

@Data
public class SysDictPage {

    /**
     * 主键
     */
    private String id;
    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典编码
     */
    private String dictCode;
    /**
     * 删除状态
     */
    private Integer delFlag;
    /**
     * 描述
     */
    private String description;

    private List<DictItem> dictItemList;

}
