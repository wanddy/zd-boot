package auth.aspect;

import java.lang.reflect.Method;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import commons.annotation.AutoLog;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import commons.constant.CommonConstant;
import commons.auth.vo.LoginUser;
import commons.util.IPUtils;
import commons.util.SpringContextUtils;
import auth.entity.Log;
import auth.domain.log.service.ISysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;


/**
 * 系统日志，切面处理类
 *
 * @Author Lee
 */
@Aspect
@Component
public class AutoLogAspect {
    @Autowired
    private ISysLogService sysLogService;

    @Pointcut("@annotation(commons.annotation.AutoLog)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        //保存日志
        saveSysLog(point, time);

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Log log = new Log();
        AutoLog syslog = method.getAnnotation(AutoLog.class);
        if (syslog != null) {
            //注解上的描述,操作日志内容
            log.setLogContent(syslog.value());
            log.setLogType(syslog.logType());

        }

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.setMethod(className + "." + methodName + "()");


        //设置操作类型
        if (log.getLogType() == CommonConstant.LOG_TYPE_2) {
            log.setOperateType(getOperateType(methodName, syslog.operateType()));
        }

        //获取request
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        //请求的参数
        log.setRequestParam(getReqestParams(request, joinPoint));

        //设置IP地址
        log.setIp(IPUtils.getIpAddr(request));

        //获取登录用户信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (sysUser != null) {
            log.setUserid(sysUser.getUsername());
            log.setUsername(sysUser.getRealname());

        }
        //耗时
        log.setCostTime(time);
        //保存系统日志
        sysLogService.save(log);
    }

    /**
     * 获取操作类型
     */
    private int getOperateType(String methodName, int operateType) {
        if (operateType > 0) {
            return operateType;
        }
        if (methodName.startsWith("list")) {
            return CommonConstant.OPERATE_TYPE_1;
        }
        if (methodName.startsWith("add")) {
            return CommonConstant.OPERATE_TYPE_2;
        }
        if (methodName.startsWith("edit")) {
            return CommonConstant.OPERATE_TYPE_3;
        }
        if (methodName.startsWith("delete")) {
            return CommonConstant.OPERATE_TYPE_4;
        }
        if (methodName.startsWith("import")) {
            return CommonConstant.OPERATE_TYPE_5;
        }
        if (methodName.startsWith("export")) {
            return CommonConstant.OPERATE_TYPE_6;
        }
        return CommonConstant.OPERATE_TYPE_1;
    }

    /**
     * @param request:   request
     * @param joinPoint: joinPoint
     * @Description: 获取请求参数
     * @author: Lee
     * @date: 2020/4/16 0:10
     * @Return: java.lang.String
     */
    private String getReqestParams(HttpServletRequest request, JoinPoint joinPoint) {
        String httpMethod = request.getMethod();
        String params = "";
        if ("POST".equals(httpMethod) || "PUT".equals(httpMethod) || "PATCH".equals(httpMethod)) {
            Object[] paramsArray = joinPoint.getArgs();
            // java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.EModel. isAsyncStarted() returns false)
            //  https://my.oschina.net/mengzhang6/blog/2395893
            Object[] arguments = new Object[paramsArray.length];
            for (int i = 0; i < paramsArray.length; i++) {
                if (paramsArray[i] instanceof ServletRequest || paramsArray[i] instanceof ServletResponse || paramsArray[i] instanceof MultipartFile) {
                    //ServletRequest不能序列化，从入参里排除，否则报异常：java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.EModel. isAsyncStarted() returns false)
                    //ServletResponse不能序列化 从入参里排除，否则报异常：java.lang.IllegalStateException: getOutputStream() has already been called for this response
                    continue;
                }
                arguments[i] = paramsArray[i];
            }

            params = JSONObject.toJSONString(arguments);
        } else {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            // 请求的方法参数值
            Object[] args = joinPoint.getArgs();
            // 请求的方法参数名称
            LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
            String[] paramNames = u.getParameterNames(method);
            if (args != null && paramNames != null) {
                for (int i = 0; i < args.length; i++) {
                    params += "  " + paramNames[i] + ": " + args[i];
                }
            }
        }
        return params;
    }
}
