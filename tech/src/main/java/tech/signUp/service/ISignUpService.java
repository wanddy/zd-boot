package tech.signUp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import commons.api.vo.Result;
import commons.auth.vo.LoginUser;
import tech.signUp.entity.SignUp;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 报名表
 * @Author: zd-boot
 * @Date:   2020-11-24
 * @Version: V1.0
 */
public interface ISignUpService extends IService<SignUp> {

    Result<?> edit(SignUp signUp);

    Result<?> add(SignUp signUp);

    /**
     * 查询表字段
     * @return
     */
    Result<?> getFieldList();

    /**
     * 批量审批
     * @param asList
     * @param audit
     * @return
     */
    Result<?> batchUpdate(List<String> asList,String audit);

    /**
     * 导出
     * @param signUp
     * @return
     */
    List<SignUp> getList(SignUp signUp, LoginUser user);


    Result<?> queryList(SignUp sign);
}
