package auth.domain.relation.user.depart.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import auth.domain.user.mapper.UserMapper;
import auth.domain.user.service.DefUserService;
import auth.entity.Depart;
import auth.entity.User;
import auth.entity.UserDepart;
import commons.util.oConvertUtils;
import auth.domain.relation.user.depart.mapper.SysUserDepartMapper;
import auth.discard.model.DepartIdModel;
import auth.domain.depart.service.ISysDepartService;
import auth.domain.relation.user.depart.service.ISysUserDepartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 用户部门 实现类
 */
@Slf4j
@Service
public class SysUserDepartServiceImpl extends ServiceImpl<SysUserDepartMapper, UserDepart> implements ISysUserDepartService {

    private final ISysDepartService sysDepartService;

    private final DefUserService defUserService;

    @Autowired
    public SysUserDepartServiceImpl(ISysDepartService sysDepartService, DefUserService defUserService) {
        this.sysDepartService = sysDepartService;
        this.defUserService = defUserService;
    }

    /**
     * 根据用户id查询部门信息
     */
    @Override
    public List<DepartIdModel> queryDepartIdsOfUser(String userId) {
        LambdaQueryWrapper<UserDepart> queryUDep = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Depart> queryDep = new LambdaQueryWrapper<>();
        try {
            queryUDep.eq(UserDepart::getUserId, userId);
            List<String> depIdList = new ArrayList<>();
            List<DepartIdModel> depIdModelList = new ArrayList<>();
            List<UserDepart> userDepList = this.list(queryUDep);
            if (userDepList != null && userDepList.size() > 0) {
                for (UserDepart userDepart : userDepList) {
                    depIdList.add(userDepart.getDepId());
                }
                queryDep.in(Depart::getId, depIdList);
                List<Depart> depList = sysDepartService.list(queryDep);
                if (depList != null && depList.size() > 0) {
                    for (Depart depart : depList) {
                        depIdModelList.add(new DepartIdModel().convertByUserDepart(depart));
                    }
                }
                return depIdModelList;
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return null;


    }


    /**
     * 根据部门id查询用户信息
     */
    @Override
    public List<User> queryUserByDepId(String depId) {
        LambdaQueryWrapper<UserDepart> queryUDep = new LambdaQueryWrapper<>();
        queryUDep.eq(UserDepart::getDepId, depId);
        List<String> userIdList = new ArrayList<>();
        List<UserDepart> uDepList = this.list(queryUDep);
        if (uDepList != null && uDepList.size() > 0) {
            for (UserDepart uDep : uDepList) {
                userIdList.add(uDep.getUserId());
            }
            List<User> userList = defUserService.listByIds(userIdList);
            //update-begin-author:taoyan date:201905047 for:接口调用查询返回结果不能返回密码相关信息
            for (User User : userList) {
                User.setSalt("");
                User.setPassword("");
            }
            //update-end-author:taoyan date:201905047 for:接口调用查询返回结果不能返回密码相关信息
            return userList;
        }
        return new ArrayList<User>();
    }

    /**
     * 根据部门code，查询当前部门和下级部门的 用户信息
     */
    @Override
    public List<User> queryUserByDepCode(String depCode, String userId) {
        LambdaQueryWrapper<Depart> queryByDepCode = new LambdaQueryWrapper<>();
        queryByDepCode.likeRight(Depart::getOrgCode, depCode);
        List<Depart> sysDepartList = sysDepartService.list(queryByDepCode);
        List<String> depIds = sysDepartList.stream().map(Depart::getId).collect(Collectors.toList());

        LambdaQueryWrapper<UserDepart> queryUDep = new LambdaQueryWrapper<>();
        queryUDep.in(UserDepart::getDepId, depIds);
        List<String> userIdList = new ArrayList<>();
        List<UserDepart> uDepList = this.list(queryUDep);
        if (uDepList != null && uDepList.size() > 0) {
            for (UserDepart uDep : uDepList) {
                userIdList.add(uDep.getUserId());
            }
            LambdaQueryWrapper<User> queryUser = new LambdaQueryWrapper<>();
            queryUser.in(User::getId, userIdList);
            if (oConvertUtils.isNotEmpty(userId)) {
                queryUser.notLike(User::getId, userId);
            }
            List<User> userList = defUserService.list(queryUser);
            //update-begin-author:taoyan date:201905047 for:接口调用查询返回结果不能返回密码相关信息
            for (User User : userList) {
                User.setSalt("");
                User.setPassword("");
            }
            //update-end-author:taoyan date:201905047 for:接口调用查询返回结果不能返回密码相关信息
            return userList;
        }
        return new ArrayList<>();
    }
}
