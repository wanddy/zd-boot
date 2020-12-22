package auth.aspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import auth.domain.permission.service.ISysPermissionService;
import auth.domain.relation.permission.rule.serivce.ISysPermissionDataRuleService;
import auth.domain.user.service.UserService;
import auth.entity.Permission;
import auth.entity.PermissionDataRule;
import commons.annotation.PermissionData;
import commons.auth.utils.JeecgDataAutorUtils;
import commons.auth.vo.SysPermissionDataRuleModel;
import commons.auth.vo.SysUserCacheInfo;
import commons.auth.utils.JwtUtil;
import commons.util.SpringContextUtils;
import commons.util.oConvertUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据权限切面处理类
 * 当被请求的方法有注解PermissionData时,会在往当前request中写入数据权限信息
 *
 * @Version: 1.0
 */
@Aspect
@Component
@Slf4j
public class PermissionDataAspect {

    private final ISysPermissionService sysPermissionService;

    private final ISysPermissionDataRuleService sysPermissionDataRuleService;

    private final UserService userService;

    @Autowired
    public PermissionDataAspect(ISysPermissionService sysPermissionService, ISysPermissionDataRuleService sysPermissionDataRuleService, UserService userService) {
        this.sysPermissionService = sysPermissionService;
        this.sysPermissionDataRuleService = sysPermissionDataRuleService;
        this.userService = userService;
    }

    @Pointcut("@annotation(commons.annotation.PermissionData)")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        PermissionData pd = method.getAnnotation(PermissionData.class);
        String component = pd.pageComponent();
        List<Permission> currentSyspermission;
        if (oConvertUtils.isNotEmpty(component)) {
            //1.通过注解属性pageComponent 获取菜单
            LambdaQueryWrapper<Permission> query = new LambdaQueryWrapper<Permission>();
            query.eq(Permission::getDelFlag, 0);
            query.eq(Permission::getComponent, component);
            currentSyspermission = sysPermissionService.list(query);
        } else {
            String requestMethod = request.getMethod();
            String requestPath = request.getRequestURI().substring(request.getContextPath().length());
            requestPath = filterUrl(requestPath);
            log.info("拦截请求 >> " + requestPath + ";请求类型 >> " + requestMethod);
            //1.直接通过前端请求地址查询菜单
            LambdaQueryWrapper<Permission> query = new LambdaQueryWrapper<Permission>();
            query.eq(Permission::getMenuType, 2);
            query.eq(Permission::getDelFlag, 0);
            query.eq(Permission::getUrl, requestPath);
            currentSyspermission = sysPermissionService.list(query);
            //2.未找到 再通过自定义匹配URL 获取菜单
            if (currentSyspermission == null || currentSyspermission.size() == 0) {
                //通过自定义URL匹配规则 获取菜单（实现通过菜单配置数据权限规则，实际上针对获取数据接口进行数据规则控制）
                String userMatchUrl = UrlMatchEnum.getMatchResultByUrl(requestPath);
                LambdaQueryWrapper<Permission> queryQserMatch = new LambdaQueryWrapper<Permission>();
                queryQserMatch.eq(Permission::getMenuType, 1);
                queryQserMatch.eq(Permission::getDelFlag, 0);
                queryQserMatch.eq(Permission::getUrl, userMatchUrl);
                if (oConvertUtils.isNotEmpty(userMatchUrl)) {
                    currentSyspermission = sysPermissionService.list(queryQserMatch);
                }
            }
            //3.未找到 再通过正则匹配获取菜单
            if (currentSyspermission == null || currentSyspermission.size() == 0) {
                //通过正则匹配权限配置
                String regUrl = getRegexpUrl(requestPath);
                if (regUrl != null) {
                    currentSyspermission = sysPermissionService.list(new LambdaQueryWrapper<Permission>().eq(Permission::getMenuType, 2).eq(Permission::getUrl, regUrl).eq(Permission::getDelFlag, 0));
                }
            }
        }
        //3.通过用户名+菜单ID 找到权限配置信息 放到request中去
        if (currentSyspermission != null && currentSyspermission.size() > 0) {
            String username = JwtUtil.getUserNameByToken(request);
            List<SysPermissionDataRuleModel> dataRules = new ArrayList<SysPermissionDataRuleModel>();
            for (Permission permission : currentSyspermission) {
                // update-begin--Author:scott Date:20191119 for：数据权限规则编码不规范，项目存在相同包名和类名 #722
                List<PermissionDataRule> temp = sysPermissionDataRuleService.queryPermissionDataRules(username, permission.getId());
                if (temp != null && temp.size() > 0) {
                    //dataRules.addAll(temp);
                    dataRules = oConvertUtils.entityListToModelList(temp, SysPermissionDataRuleModel.class);
                }
                // update-end--Author:scott Date:20191119 for：数据权限规则编码不规范，项目存在相同包名和类名 #722
            }
            if (dataRules != null && dataRules.size() > 0) {
                JeecgDataAutorUtils.installDataSearchCondition(request, dataRules);
                SysUserCacheInfo userinfo = userService.getCacheUser(username);
                JeecgDataAutorUtils.installUserInfo(request, userinfo);
            }
        }
        return point.proceed();
    }

    private String filterUrl(String requestPath) {
        String url = "";
        if (oConvertUtils.isNotEmpty(requestPath)) {
            url = requestPath.replace("//", "/");
            if (url.contains("//")) {
                url = filterUrl(url);
            }
			/*if(url.startsWith("/")){
				url=url.substring(1);
			}*/
        }
        return url;
    }

    /**
     * 获取请求地址
     *
     * @param request
     * @return
     */
    private String getJgAuthRequestPath(HttpServletRequest request) {
        String queryString = request.getQueryString();
        String requestPath = request.getRequestURI();
        if (oConvertUtils.isNotEmpty(queryString)) {
            requestPath += "?" + queryString;
        }
        if (requestPath.contains("&")) {// 去掉其他参数(保留一个参数) 例如：loginController.do?login
            requestPath = requestPath.substring(0, requestPath.indexOf("&"));
        }
        if (requestPath.contains("=")) {
            if (requestPath.contains(".do")) {
                requestPath = requestPath.substring(0, requestPath.indexOf(".do") + 3);
            } else {
                requestPath = requestPath.substring(0, requestPath.indexOf("?"));
            }
        }
        requestPath = requestPath.substring(request.getContextPath().length() + 1);// 去掉项目路径
        return filterUrl(requestPath);
    }

    private boolean moHuContain(List<String> list, String key) {
        for (String str : list) {
            if (key.contains(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 匹配前端传过来的地址 匹配成功返回正则地址
     * AntPathMatcher匹配地址
     * ()* 匹配0个或多个字符
     * ()**匹配0个或多个目录
     */
    private String getRegexpUrl(String url) {
        List<String> list = sysPermissionService.queryPermissionUrlWithStar();
        if (list != null && list.size() > 0) {
            for (String p : list) {
                PathMatcher matcher = new AntPathMatcher();
                if (matcher.match(p, url)) {
                    return p;
                }
            }
        }
        return null;
    }

}
