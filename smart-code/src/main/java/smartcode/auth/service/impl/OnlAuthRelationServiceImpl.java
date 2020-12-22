package smartcode.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import smartcode.auth.entity.OnlAuthRelation;
import smartcode.auth.mapper.OnlAuthRelationMapper;
import smartcode.auth.service.OnlAuthRelationService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service("onlAuthRelationServiceImpl")
public class OnlAuthRelationServiceImpl extends ServiceImpl<OnlAuthRelationMapper, OnlAuthRelation> implements OnlAuthRelationService {

    public void saveRoleAuth(String roleId, String cgformId, int type, List<String> authIds) {
        LambdaQueryWrapper lambdaQueryWrapper = (new LambdaQueryWrapper<OnlAuthRelation>()).eq(OnlAuthRelation::getCgformId, cgformId).eq(OnlAuthRelation::getType, type).eq(OnlAuthRelation::getRoleId, roleId);
        this.baseMapper.delete(lambdaQueryWrapper);
        ArrayList list = new ArrayList();
        Iterator iterator = authIds.iterator();

        while(iterator.hasNext()) {
            String id = (String)iterator.next();
            OnlAuthRelation onlAuthRelation = new OnlAuthRelation();
            onlAuthRelation.setAuthId(id);
            onlAuthRelation.setCgformId(cgformId);
            onlAuthRelation.setRoleId(roleId);
            onlAuthRelation.setType(type);
            list.add(onlAuthRelation);
        }

        this.saveBatch(list);
    }
}
