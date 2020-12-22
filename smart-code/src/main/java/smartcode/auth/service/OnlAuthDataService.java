package smartcode.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smartcode.auth.entity.OnlAuthData;

public interface OnlAuthDataService extends IService<OnlAuthData> {
    void deleteOne(String var1);
}
