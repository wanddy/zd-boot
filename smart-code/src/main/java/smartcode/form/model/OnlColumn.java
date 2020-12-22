package smartcode.form.model;

import lombok.Data;

@Data
public class OnlColumn {
    private String title;
    private String dataIndex;
    private String align;
    private String customRender;
    private DModel scopedSlots;
    private String hrefSlotName;
    private boolean sorter = false;

    public OnlColumn(String title, String dataIndex) {
        this.align = "center";
        this.title = title;
        this.dataIndex = dataIndex;
    }
}
