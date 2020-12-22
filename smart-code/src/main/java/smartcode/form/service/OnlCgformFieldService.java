package smartcode.form.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import smartcode.form.entity.OnlCgformField;
import smartcode.form.entity.OnlCgformHead;

import java.util.List;
import java.util.Map;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/24 10:35
 * @Description: zdit.zdboot.auth.online.service
 **/
public interface OnlCgformFieldService extends IService<OnlCgformField> {
    List<OnlCgformField> queryAvailableFields(String id, String tableName, String s, boolean b);

    List<String> queryDisabledFields(String tableName);

    List<String> selectOnlineHideColumns(String tableName);

    Map<String, Object> queryAutolistPage(String tbname, String headId, Map<String, Object> params, List<String> needList);

    List<OnlCgformField> queryFormFieldsByTableName(String var1);

    List<Map<String, String>> getAutoListQueryInfo(String var1);

    String queryTreeChildIds(OnlCgformHead onlCgformHead, String id);

    void deleteAutoListMainAndSub(OnlCgformHead onlCgformHead, String id);

    void deleteAutoListById(String tableName, String id);

    void updateTreeNodeNoChild(String var1, String var2, String var3);

    void saveFormData(List<OnlCgformField> var1, String var2, JSONObject var3);

    void saveFormData(String var1, String var2, JSONObject var3, boolean var4);

    void saveTreeFormData(String code, String var6, JSONObject json, String treeIdField, String treeParentIdField);

    void editTreeFormData(String var1, String var2, JSONObject var3, String var4, String var5);

    void editFormData(String var1, String var2, JSONObject var3, boolean var4);

    void deleteAutoList(String var1, String var2, String var3);

    List<OnlCgformField> queryFormFields(String var1, boolean var2);

    Map<String, Object> queryFormData(List<OnlCgformField> var1, String var2, String var3);

    List<Map<String, Object>> querySubFormData(List<OnlCgformField> var1, String var2, String var3, String var4);
}
