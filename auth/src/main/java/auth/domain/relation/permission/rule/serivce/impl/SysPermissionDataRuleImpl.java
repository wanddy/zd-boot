package auth.domain.relation.permission.rule.serivce.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import auth.domain.permission.mapper.SysPermissionMapper;
import auth.domain.relation.permission.rule.mapper.SysPermissionDataRuleMapper;
import auth.domain.relation.permission.rule.serivce.ISysPermissionDataRuleService;
import commons.auth.query.QueryGenerator;
import commons.constant.CommonConstant;
import commons.util.oConvertUtils;
import lombok.extern.slf4j.Slf4j;
import auth.entity.Permission;
import auth.entity.PermissionDataRule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 菜单权限规则  服务实现类
 */
@Slf4j
@Service
public class SysPermissionDataRuleImpl extends ServiceImpl<SysPermissionDataRuleMapper, PermissionDataRule> implements ISysPermissionDataRuleService {

    @Resource
    private SysPermissionMapper sysPermissionMapper;

    /**
     * 根据菜单id查询其对应的权限数据
     */
    @Override
    public List<PermissionDataRule> getPermRuleListByPermId(String permissionId) {
        LambdaQueryWrapper<PermissionDataRule> query = new LambdaQueryWrapper<>();
        query.eq(PermissionDataRule::getPermissionId, permissionId);
        query.orderByDesc(PermissionDataRule::getCreateTime);
        return this.list(query);
    }

    /**
     * 根据前端传递的权限名称和权限值参数来查询权限数据
     */
    @Override
    public List<PermissionDataRule> queryPermissionRule(PermissionDataRule permRule) {
        QueryWrapper<PermissionDataRule> queryWrapper = QueryGenerator.initQueryWrapper(permRule, null);
        return this.list(queryWrapper);
    }

    @Override
    public List<PermissionDataRule> queryPermissionDataRules(String username, String permissionId) {
        List<String> idsList = this.baseMapper.queryDataRuleIds(username, permissionId);
        //update-begin--Author:scott  Date:20191119  for：数据权限失效问题处理--------------------
        if (idsList == null || idsList.size() == 0) {
            return null;
        }
        //update-end--Author:scott  Date:20191119  for：数据权限失效问题处理--------------------
        Set<String> set = new HashSet<>();
        for (String ids : idsList) {
            if (oConvertUtils.isEmpty(ids)) {
                continue;
            }
            String[] arr = ids.split(",");
            for (String id : arr) {
                if (oConvertUtils.isNotEmpty(id)) {
                    set.add(id);
                }
            }
        }
        if (set.size() == 0) {
            return null;
        }
        return this.baseMapper.selectList(new QueryWrapper<PermissionDataRule>().in("id", set).eq("status", CommonConstant.STATUS_1));
    }

    @Override
    @Transactional
    public void savePermissionDataRule(PermissionDataRule permissionDataRule) {
        this.save(permissionDataRule);
        Permission permission = sysPermissionMapper.selectById(permissionDataRule.getPermissionId());
        if (permission != null && (permission.getRuleFlag() == null || permission.getRuleFlag().equals(CommonConstant.RULE_FLAG_0))) {
            permission.setRuleFlag(CommonConstant.RULE_FLAG_1);
            sysPermissionMapper.updateById(permission);
        }
    }

    @Override
    @Transactional
    public void deletePermissionDataRule(String dataRuleId) {
        PermissionDataRule dataRule = this.baseMapper.selectById(dataRuleId);
        if (dataRule != null) {
            this.removeById(dataRuleId);
            Integer count = this.baseMapper.selectCount(new LambdaQueryWrapper<PermissionDataRule>().eq(PermissionDataRule::getPermissionId, dataRule.getPermissionId()));
            //注:同一个事务中删除后再查询是会认为数据已被删除的 若事务回滚上述删除无效
            if (count == null || count == 0) {
                Permission permission = sysPermissionMapper.selectById(dataRule.getPermissionId());
                if (permission != null && permission.getRuleFlag().equals(CommonConstant.RULE_FLAG_1)) {
                    permission.setRuleFlag(CommonConstant.RULE_FLAG_0);
                    sysPermissionMapper.updateById(permission);
                }
            }
        }

    }

}
