package smartcode.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smartcode.form.entity.OnlCgformIndex;

import java.util.List;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/24 10:31
 * @Description: zdit.zdboot.auth.online.service
 **/
public interface OnlCgformIndexService extends IService<OnlCgformIndex> {
    boolean isExistIndex(String var16);

    void createIndex(String code, String databaseType, String tableName);

    List<OnlCgformIndex>  getCgformIndexsByCgformId(String id);
}
