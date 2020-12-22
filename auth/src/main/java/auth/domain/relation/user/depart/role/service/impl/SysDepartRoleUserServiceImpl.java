package auth.domain.relation.user.depart.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import commons.util.oConvertUtils;
import auth.entity.DepartRole;
import auth.entity.DepartRoleUser;
import auth.domain.relation.depar.role.mapper.SysDepartRoleMapper;
import auth.domain.relation.user.depart.role.mapper.SysDepartRoleUserMapper;
import auth.domain.relation.user.depart.role.service.ISysDepartRoleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 部门角色人员信息
 * @Author: jeecg-boot
 * @Date:   2020-02-13
 * @Version: V1.0
 */
@Service
public class SysDepartRoleUserServiceImpl extends ServiceImpl<SysDepartRoleUserMapper, DepartRoleUser> implements ISysDepartRoleUserService {
    @Autowired
    private SysDepartRoleMapper sysDepartRoleMapper;

    @Override
    public void deptRoleUserAdd(String userId, String newRoleId, String oldRoleId) {
        List<String> add = getDiff(oldRoleId,newRoleId);
        if(add!=null && add.size()>0) {
            List<DepartRoleUser> list = new ArrayList<>();
            for (String roleId : add) {
                if(oConvertUtils.isNotEmpty(roleId)) {
                    DepartRoleUser rolepms = new DepartRoleUser(userId, roleId);
                    list.add(rolepms);
                }
            }
            this.saveBatch(list);
        }
        List<String> remove = getDiff(newRoleId,oldRoleId);
        if(remove!=null && remove.size()>0) {
            for (String roleId : remove) {
                this.remove(new QueryWrapper<DepartRoleUser>().lambda().eq(DepartRoleUser::getUserId, userId).eq(DepartRoleUser::getDroleId, roleId));
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeDeptRoleUser(List<String> userIds, String depId) {
        for(String userId : userIds){
            List<DepartRole> departRoleList = sysDepartRoleMapper.selectList(new QueryWrapper<DepartRole>().eq("depart_id",depId));
            List<String> roleIds = departRoleList.stream().map(DepartRole::getId).collect(Collectors.toList());
            if(roleIds != null && roleIds.size()>0){
                QueryWrapper<DepartRoleUser> query = new QueryWrapper<>();
                query.eq("user_id",userId).in("role_id",roleIds);
                this.remove(query);
            }
        }
    }

    /**
     * 从diff中找出main中没有的元素
     * @param main
     * @param diff
     * @return
     */
    private List<String> getDiff(String main, String diff){
        if(oConvertUtils.isEmpty(diff)) {
            return null;
        }
        if(oConvertUtils.isEmpty(main)) {
            return Arrays.asList(diff.split(","));
        }

        String[] mainArr = main.split(",");
        String[] diffArr = diff.split(",");
        Map<String, Integer> map = new HashMap<>();
        for (String string : mainArr) {
            map.put(string, 1);
        }
        List<String> res = new ArrayList<String>();
        for (String key : diffArr) {
            if(oConvertUtils.isNotEmpty(key) && !map.containsKey(key)) {
                res.add(key);
            }
        }
        return res;
    }
}
