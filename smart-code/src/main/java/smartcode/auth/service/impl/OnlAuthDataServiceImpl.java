//
// Source code recreated from OnlAuthDataServiceImpl .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartcode.auth.entity.OnlAuthData;
import smartcode.auth.entity.OnlAuthRelation;
import smartcode.auth.mapper.OnlAuthDataMapper;
import smartcode.auth.mapper.OnlAuthRelationMapper;
import smartcode.auth.service.OnlAuthDataService;

@Service("onlAuthDataServiceImpl")
public class OnlAuthDataServiceImpl extends ServiceImpl<OnlAuthDataMapper, OnlAuthData> implements OnlAuthDataService {
    @Autowired
    private OnlAuthRelationMapper onlAuthRelationMapper;

    public OnlAuthDataServiceImpl() {
    }

    public void deleteOne(String id) {
        this.removeById(id);
        LambdaQueryWrapper<OnlAuthRelation> var2 = new LambdaQueryWrapper();
        this.onlAuthRelationMapper.delete((Wrapper)var2.eq(OnlAuthRelation::getAuthId, id));
    }
}
