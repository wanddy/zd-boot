//
// Source code recreated from OnlCgreportAPI .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.form.entity;

import lombok.Data;
import java.util.List;

@Data
public class OnlCgreportModel {
    private OnlCgreportHead head;
    private List<OnlCgreportParam> params;
    private String deleteParamIds;
    private List<OnlCgreportItem> items;
    private String deleteItemIds;
}
