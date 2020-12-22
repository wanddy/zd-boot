package smartcode.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smartcode.form.entity.OnlCgreportItem;

import java.util.List;
import java.util.Map;

public interface OnlCgreportItemService extends IService<OnlCgreportItem> {
    List<Map<String, String>> getAutoListQueryInfo(String var1);
}
