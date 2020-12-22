//
// Source code recreated from OnlAuthDataServiceImpl .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smartcode.auth.entity.OnlAuthRelation;

import java.util.List;

public interface OnlAuthRelationService extends IService<OnlAuthRelation> {
    void saveRoleAuth(String var1, String var2, int var3, List<String> var4);
}
