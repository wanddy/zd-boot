package auth.discard.cas.controller;

import java.util.List;

import auth.discard.cas.util.XmlUtils;
import auth.domain.user.service.UserService;
import auth.entity.Depart;
import auth.entity.User;
import commons.api.vo.Result;
import commons.constant.CommonConstant;
import commons.auth.utils.JwtUtil;
import commons.util.RedisUtil;
import auth.discard.cas.util.CASServiceUtil;
import auth.domain.depart.service.ISysDepartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * CAS单点登录客户端登录认证
 */
@Slf4j
@RestController
@RequestMapping("/cas/client")
public class CasClientController {

    private final UserService userService;

    private final ISysDepartService sysDepartService;

    private final RedisUtil redisUtil;

    @Autowired
    public CasClientController(UserService userService, ISysDepartService sysDepartService, RedisUtil redisUtil) {
        this.userService = userService;
        this.sysDepartService = sysDepartService;
        this.redisUtil = redisUtil;
    }


    @GetMapping("/validateLogin")
    public Object validateLogin(@RequestParam(name = "ticket") String ticket,
                                @RequestParam(name = "service") String service) {
        Result result = new Result<JSONObject>();
        log.info("Rest api login.");
        try {
//            String validateUrl = prefixUrl + "/p3/serviceValidate";
            String validateUrl = "/p3/serviceValidate";
            String res = CASServiceUtil.getSTValidate(validateUrl, ticket, service);
            log.info("res." + res);
            final String error = XmlUtils.getTextForElement(res, "authenticationFailure");
            if (StringUtils.isNotEmpty(error)) {
                throw new Exception(error);
            }
            final String principal = XmlUtils.getTextForElement(res, "user");
            if (StringUtils.isEmpty(principal)) {
                throw new Exception("No principal was found in the response from the CAS server.");
            }
            log.info("-------token----username---" + principal);
            //1. 校验用户是否有效
            User user = userService.getUserByName(principal);
            result = userService.checkUserIsEffective(user);
            if (!result.isSuccess()) {
                return result;
            }
            String token = JwtUtil.sign(user.getUsername(), user.getPassword());
            // 设置超时时间
            redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
            redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);
            //获取用户部门信息
            JSONObject obj = new JSONObject();
            List<Depart> departs = sysDepartService.queryUserDeparts(user.getId());
            obj.put("departs", departs);
            if (departs == null || departs.size() == 0) {
                obj.put("multi_depart", 0);
            } else if (departs.size() == 1) {
                userService.updateUserDepart(principal, departs.get(0).getOrgCode());
                obj.put("multi_depart", 1);
            } else {
                obj.put("multi_depart", 2);
            }
            obj.put("token", token);
            obj.put("userInfo", user);
            result.setResult(obj);
            result.success("登录成功");

        } catch (Exception e) {
            //EModel.printStackTrace();
            result.error500(e.getMessage());
        }
        return new HttpEntity<>(result);
    }


}
