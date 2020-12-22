package auth.domain.depart.service.impl;

import java.util.*;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import auth.domain.depart.mapper.SysDepartMapper;
import auth.domain.relation.depar.role.mapper.SysDepartRoleMapper;
import auth.domain.relation.permission.depart.mapper.SysDepartPermissionMapper;
import auth.domain.relation.permission.depart.role.mapper.SysDepartRolePermissionMapper;
import auth.domain.relation.user.depart.mapper.SysUserDepartMapper;
import auth.domain.relation.user.depart.role.mapper.SysDepartRoleUserMapper;
import auth.entity.*;
import commons.constant.CacheConstant;
import commons.constant.CommonConstant;
import commons.constant.FillRuleConstant;
import commons.util.FillRuleUtil;
import commons.util.YouBianCodeUtil;
import auth.discard.model.DepartIdModel;
import auth.discard.model.SysDepartTreeModel;
import auth.domain.depart.service.ISysDepartService;
import auth.discard.util.FindsDepartsChildrenUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 部门服务实现类
 */
@Slf4j
@Service
public class SysDepartServiceImpl extends ServiceImpl<SysDepartMapper, Depart> implements ISysDepartService {

    private final SysUserDepartMapper userDepartMapper;

    private final SysDepartRoleMapper sysDepartRoleMapper;

    private final SysDepartPermissionMapper departPermissionMapper;

    private final SysDepartRolePermissionMapper departRolePermissionMapper;

    private final SysDepartRoleUserMapper departRoleUserMapper;

    @Autowired
    public SysDepartServiceImpl(SysUserDepartMapper userDepartMapper, SysDepartRoleMapper sysDepartRoleMapper, SysDepartPermissionMapper departPermissionMapper, SysDepartRolePermissionMapper departRolePermissionMapper, SysDepartRoleUserMapper departRoleUserMapper) {
        this.userDepartMapper = userDepartMapper;
        this.sysDepartRoleMapper = sysDepartRoleMapper;
        this.departPermissionMapper = departPermissionMapper;
        this.departRolePermissionMapper = departRolePermissionMapper;
        this.departRoleUserMapper = departRoleUserMapper;
    }

    @Override
    public List<SysDepartTreeModel> queryMyDeptTreeList(String departIds) {
        //根据部门id获取所负责部门
        LambdaQueryWrapper<Depart> query = new LambdaQueryWrapper<>();
        String[] codeArr = this.getMyDeptParentOrgCode(departIds);
        for (String s : codeArr) {
            query.or().likeRight(Depart::getOrgCode, s);
        }
        query.eq(Depart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        query.orderByAsc(Depart::getDepartOrder);
        //将父节点ParentId设为null
        List<Depart> listDepts = this.list(query);
        for (String s : codeArr) {
            for (Depart dept : listDepts) {
                if (dept.getOrgCode().equals(s)) {
                    dept.setParentId(null);
                }
            }
        }
        // 调用wrapTreeDataToTreeList方法生成树状数据
        return FindsDepartsChildrenUtil.wrapTreeDataToTreeList(listDepts);
    }

    /**
     * queryTreeList 对应 queryTreeList 查询所有的部门数据,以树结构形式响应给前端
     */
    @Cacheable(value = CacheConstant.SYS_DEPARTS_CACHE)
    @Override
    public List<SysDepartTreeModel> queryTreeList() {
        LambdaQueryWrapper<Depart> query = new LambdaQueryWrapper<>();
        query.eq(Depart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        query.orderByAsc(Depart::getDepartOrder);
        List<Depart> list = this.list(query);
        // 调用wrapTreeDataToTreeList方法生成树状数据
        return FindsDepartsChildrenUtil.wrapTreeDataToTreeList(list);
    }

    @Cacheable(value = CacheConstant.SYS_DEPART_IDS_CACHE)
    @Override
    public List<DepartIdModel> queryDepartIdTreeList() {
        LambdaQueryWrapper<Depart> query = new LambdaQueryWrapper<>();
        query.eq(Depart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        query.orderByAsc(Depart::getDepartOrder);
        List<Depart> list = this.list(query);
        // 调用wrapTreeDataToTreeList方法生成树状数据
        return FindsDepartsChildrenUtil.wrapTreeDataToDepartIdTreeList(list);
    }

    /**
     * saveDepartData 对应 add 保存用户在页面添加的新的部门对象数据
     */
    @Override
    @Transactional
    public void saveDepartData(Depart Depart, String username) {
        if (Depart != null && username != null) {
            if (Depart.getParentId() == null) {
                Depart.setParentId("");
            }
            String s = UUID.randomUUID().toString().replace("-", "");
            Depart.setId(s);
            // 先判断该对象有无父级ID,有则意味着不是最高级,否则意味着是最高级
            // 获取父级ID
            String parentId = Depart.getParentId();
            //update-begin--Author:baihailong  Date:20191209 for：部门编码规则生成器做成公用配置
            JSONObject formData = new JSONObject();
            formData.put("parentId", parentId);
            String[] codeArray = (String[]) FillRuleUtil.executeRule(FillRuleConstant.DEPART, formData);
            //update-end--Author:baihailong  Date:20191209 for：部门编码规则生成器做成公用配置
            Depart.setOrgCode(codeArray[0]);
            String orgType = codeArray[1];
            Depart.setOrgType(String.valueOf(orgType));
            Depart.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
            this.save(Depart);
        }

    }

    /**
     * saveDepartData 的调用方法,生成部门编码和部门类型（作废逻辑）
     */
    private String[] generateOrgCode(String parentId) {
        //update-begin--Author:Steve  Date:20190201 for：组织机构添加数据代码调整
        LambdaQueryWrapper<Depart> query = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Depart> query1 = new LambdaQueryWrapper<>();
        String[] strArray = new String[2];
        // 创建一个List集合,存储查询返回的所有SysDepart对象
        List<Depart> departList;
        // 定义新编码字符串
        String newOrgCode;
        // 定义旧编码字符串
        String oldOrgCode;
        // 定义部门类型
        String orgType;
        // 如果是最高级,则查询出同级的org_code, 调用工具类生成编码并返回
        if (StringUtils.isEmpty(parentId)) {
            // 线判断数据库中的表是否为空,空则直接返回初始编码
            query1.eq(Depart::getParentId, "").or().isNull(Depart::getParentId);
            query1.orderByDesc(Depart::getOrgCode);
            departList = this.list(query1);
            if (departList == null || departList.size() == 0) {
                strArray[0] = YouBianCodeUtil.getNextYouBianCode(null);
                strArray[1] = "1";
                return strArray;
            } else {
                Depart depart = departList.get(0);
                oldOrgCode = depart.getOrgCode();
                orgType = depart.getOrgType();
                newOrgCode = YouBianCodeUtil.getNextYouBianCode(oldOrgCode);
            }
        } else { // 反之则查询出所有同级的部门,获取结果后有两种情况,有同级和没有同级
            // 封装查询同级的条件
            query.eq(Depart::getParentId, parentId);
            // 降序排序
            query.orderByDesc(Depart::getOrgCode);
            // 查询出同级部门的集合
            List<Depart> parentList = this.list(query);
            // 查询出父级部门
            Depart depart = this.getById(parentId);
            // 获取父级部门的Code
            String parentCode = depart.getOrgCode();
            // 根据父级部门类型算出当前部门的类型
            orgType = String.valueOf(Integer.parseInt(depart.getOrgType()) + 1);
            // 处理同级部门为null的情况
            if (parentList == null || parentList.size() == 0) {
                // 直接生成当前的部门编码并返回
                newOrgCode = YouBianCodeUtil.getSubYouBianCode(parentCode, null);
            } else { //处理有同级部门的情况
                // 获取同级部门的编码,利用工具类
                String subCode = parentList.get(0).getOrgCode();
                // 返回生成的当前部门编码
                newOrgCode = YouBianCodeUtil.getSubYouBianCode(parentCode, subCode);
            }
        }
        // 返回最终封装了部门编码和部门类型的数组
        strArray[0] = newOrgCode;
        strArray[1] = orgType;
        return strArray;
        //update-end--Author:Steve  Date:20190201 for：组织机构添加数据代码调整
    }


    /**
     * removeDepartDataById 对应 delete方法 根据ID删除相关部门数据
     *
     */
    /*
     * @Override
     *
     * @Transactional public boolean removeDepartDataById(String id) {
     * System.out.println("要删除的ID 为=============================>>>>>"+id); boolean
     * flag = this.removeById(id); return flag; }
     */

    /**
     * updateDepartDataById 对应 edit 根据部门主键来更新对应的部门数据
     */
    @Override
    @Transactional
    public Boolean updateDepartDataById(Depart Depart, String username) {
        if (Depart != null && username != null) {
//			Depart.setupdateBy(username);
            this.updateById(Depart);
            return true;
        } else {
            return false;
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchWithChildren(List<String> ids) {
        List<String> idList = new ArrayList<>();
        for (String id : ids) {
            idList.add(id);
            this.checkChildrenExists(id, idList);
        }
        this.removeByIds(idList);
        //根据部门id获取部门角色id
        List<String> roleIdList = new ArrayList<>();
        LambdaQueryWrapper<DepartRole> query = new LambdaQueryWrapper<>();
        query.select(DepartRole::getId).in(DepartRole::getDepartId, idList);
        List<DepartRole> depRoleList = sysDepartRoleMapper.selectList(query);
        for (DepartRole deptRole : depRoleList) {
            roleIdList.add(deptRole.getId());
        }
        //根据部门id删除用户与部门关系
        userDepartMapper.delete(new LambdaQueryWrapper<UserDepart>().in(UserDepart::getDepId, idList));
        //根据部门id删除部门授权
        departPermissionMapper.delete(new LambdaQueryWrapper<DepartPermission>().in(DepartPermission::getDepartId, idList));
        //根据部门id删除部门角色
        sysDepartRoleMapper.delete(new LambdaQueryWrapper<DepartRole>().in(DepartRole::getDepartId, idList));
        if (roleIdList.size() > 0) {
            //根据角色id删除部门角色授权
            departRolePermissionMapper.delete(new LambdaQueryWrapper<DepartRolePermission>().in(DepartRolePermission::getRoleId, roleIdList));
            //根据角色id删除部门角色用户信息
            departRoleUserMapper.delete(new LambdaQueryWrapper<DepartRoleUser>().in(DepartRoleUser::getDroleId, roleIdList));
        }
    }

    @Override
    public List<String> getSubDepIdsByDepId(String departId) {
        return this.baseMapper.getSubDepIdsByDepId(departId);
    }

    @Override
    public List<String> getMySubDepIdsByDepId(String departIds) {
        //根据部门id获取所负责部门
        String[] codeArr = this.getMyDeptParentOrgCode(departIds);
        return this.baseMapper.getSubDepIdsByOrgCodes(codeArr);
    }

    /**
     * <p>
     * 根据关键字搜索相关的部门数据
     * </p>
     */
    @Override
    public List<SysDepartTreeModel> searhBy(String keyWord, String myDeptSearch, String departIds) {
        LambdaQueryWrapper<Depart> query = new LambdaQueryWrapper<>();
        List<SysDepartTreeModel> newList = new ArrayList<>();
        //myDeptSearch不为空时为我的部门搜索，只搜索所负责部门
        if (!StringUtils.isEmpty(myDeptSearch)) {
            //departIds 为空普通用户或没有管理部门
            if (StringUtils.isEmpty(departIds)) {
                return newList;
            }
            //根据部门id获取所负责部门
            String[] codeArr = this.getMyDeptParentOrgCode(departIds);
            for (String s : codeArr) {
                query.or().likeRight(Depart::getOrgCode, s);
            }
            query.eq(Depart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        }
        query.like(Depart::getDepartName, keyWord);
        //update-begin--Author:huangzhilin  Date:20140417 for：[bugfree号]组织机构搜索回显优化--------------------
        SysDepartTreeModel model;
        List<Depart> departList = this.list(query);
        if (departList.size() > 0) {
            for (Depart depart : departList) {
                model = new SysDepartTreeModel(depart);
                model.setChildren(null);
                //update-end--Author:huangzhilin  Date:20140417 for：[bugfree号]组织机构搜索功回显优化----------------------
                newList.add(model);
            }
            return newList;
        }
        return null;
    }

    /**
     * 根据部门id删除并且删除其可能存在的子级任何部门
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String id) {
        List<String> idList = new ArrayList<>();
        idList.add(id);
        this.checkChildrenExists(id, idList);
        //清空部门树内存
        //FindsDepartsChildrenUtil.clearDepartIdModel();
        boolean ok = this.removeByIds(idList);
        //根据部门id获取部门角色id
        List<String> roleIdList = new ArrayList<>();
        LambdaQueryWrapper<DepartRole> query = new LambdaQueryWrapper<>();
        query.select(DepartRole::getId).in(DepartRole::getDepartId, idList);
        List<DepartRole> depRoleList = sysDepartRoleMapper.selectList(query);
        for (DepartRole deptRole : depRoleList) {
            roleIdList.add(deptRole.getId());
        }
        //根据部门id删除用户与部门关系
        userDepartMapper.delete(new LambdaQueryWrapper<UserDepart>().in(UserDepart::getDepId, idList));
        //根据部门id删除部门授权
        departPermissionMapper.delete(new LambdaQueryWrapper<DepartPermission>().in(DepartPermission::getDepartId, idList));
        //根据部门id删除部门角色
        sysDepartRoleMapper.delete(new LambdaQueryWrapper<DepartRole>().in(DepartRole::getDepartId, idList));
        if (roleIdList.size() > 0) {
            //根据角色id删除部门角色授权
            departRolePermissionMapper.delete(new LambdaQueryWrapper<DepartRolePermission>().in(DepartRolePermission::getRoleId, roleIdList));
            //根据角色id删除部门角色用户信息
            departRoleUserMapper.delete(new LambdaQueryWrapper<DepartRoleUser>().in(DepartRoleUser::getDroleId, roleIdList));
        }
        return ok;
    }

    /**
     * delete 方法调用
     */
    private void checkChildrenExists(String id, List<String> idList) {
        LambdaQueryWrapper<Depart> query = new LambdaQueryWrapper<>();
        query.eq(Depart::getParentId, id);
        List<Depart> departList = this.list(query);
        if (departList != null && departList.size() > 0) {
            for (Depart depart : departList) {
                idList.add(depart.getId());
                this.checkChildrenExists(depart.getId(), idList);
            }
        }
    }

    @Override
    public List<Depart> queryUserDeparts(String userId) {
        return baseMapper.queryUserDeparts(userId);
    }

    @Override
    public List<Depart> queryDepartsByUsername(String username) {
        return baseMapper.queryDepartsByUsername(username);
    }

    /**
     * 根据用户所负责部门ids获取父级部门编码
     */
    private String[] getMyDeptParentOrgCode(String departIds) {
        //根据部门id查询所负责部门
        LambdaQueryWrapper<Depart> query = new LambdaQueryWrapper<>();
        query.eq(Depart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        query.in(Depart::getId, Arrays.asList(departIds.split(",")));
        query.orderByAsc(Depart::getOrgCode);
        List<Depart> list = this.list(query);
        //查找根部门
        if (list == null || list.size() == 0) {
            return null;
        }
        String orgCode = this.getMyDeptParentNode(list);
        return orgCode.split(",");
    }

    /**
     * 获取负责部门父节点
     */
    private String getMyDeptParentNode(List<Depart> list) {
        Map<String, String> map = new HashMap<>();
        //1.先将同一公司归类
        for (Depart dept : list) {
            String code = dept.getOrgCode().substring(0, 3);
            if (map.containsKey(code)) {
                String mapCode = map.get(code) + "," + dept.getOrgCode();
                map.put(code, mapCode);
            } else {
                map.put(code, dept.getOrgCode());
            }
        }
        StringBuilder parentOrgCode = new StringBuilder();
        //2.获取同一公司的根节点
        for (String str : map.values()) {
            String[] arrStr = str.split(",");
            parentOrgCode.append(",").append(this.getMinLengthNode(arrStr));
        }
        return parentOrgCode.substring(1);
    }

    /**
     * 获取同一公司中部门编码长度最小的部门
     */
    private String getMinLengthNode(String[] str) {
        int min = str[0].length();
        StringBuilder orgCode = new StringBuilder(str[0]);
        for (int i = 1; i < str.length; i++) {
            if (str[i].length() <= min) {
                min = str[i].length();
                orgCode.append(",").append(str[i]);
            }
        }
        return orgCode.toString();
    }

    /**
     * 获取部门树信息根据关键字
     */
    @Override
    public List<SysDepartTreeModel> queryTreeByKeyWord(String keyWord) {
        LambdaQueryWrapper<Depart> query = new LambdaQueryWrapper<>();
        query.eq(Depart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        query.orderByAsc(Depart::getDepartOrder);
        List<Depart> list = this.list(query);
        // 调用wrapTreeDataToTreeList方法生成树状数据
        List<SysDepartTreeModel> listResult = FindsDepartsChildrenUtil.wrapTreeDataToTreeList(list);
        List<SysDepartTreeModel> treelist = new ArrayList<>();
        if (StringUtils.isNotBlank(keyWord)) {
            this.getTreeByKeyWord(keyWord, listResult, treelist);
        } else {
            return listResult;
        }
        return treelist;
    }

    /**
     * 根据关键字筛选部门信息
     */
    public void getTreeByKeyWord(String keyWord, List<SysDepartTreeModel> allResult, List<SysDepartTreeModel> newResult) {
        for (SysDepartTreeModel model : allResult) {
            if (model.getDepartName().contains(keyWord)) {
                newResult.add(model);
            } else if (model.getChildren() != null) {
                getTreeByKeyWord(keyWord, model.getChildren(), newResult);
            }
        }
    }
}
