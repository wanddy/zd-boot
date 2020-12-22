package smartcode.form.model;

import commons.auth.vo.DictModel;
import lombok.Data;
import smartcode.form.entity.OnlCgformButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class BModel {
    private String code;
    private String formTemplate;
    private String description;
    private String currentTableName;
    private Integer tableType;
    private String paginationFlag;
    private String checkboxFlag;
    private Integer scrollFlag;
    private List<OnlColumn> columns;
    private List<String> hideColumns;
    private Map<String, List<DictModel>> dictOptions = new HashMap();
    private List<OnlCgformButton> cgButtonList;
    private List<HrefSlots> fieldHrefSlots;
    private String enhanceJs;
    private List<ForeignKey> foreignKeys;
    private String pidField;
    private String hasChildrenField;
    private String textField;

}
