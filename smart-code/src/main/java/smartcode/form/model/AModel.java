//
// Source code recreated from AModel .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.form.model;

import lombok.Data;
import smartcode.form.entity.OnlCgformField;
import smartcode.form.entity.OnlCgformHead;
import smartcode.form.entity.OnlCgformIndex;
import java.util.List;

@Data
public class AModel {
    private OnlCgformHead head;
    private List<OnlCgformField> fields;
    private List<String> deleteIndexIds;
    private List<OnlCgformIndex> indexs;
    private List<String> deleteFieldIds;
}
