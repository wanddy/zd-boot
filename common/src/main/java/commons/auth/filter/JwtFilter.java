package commons.auth.filter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import commons.auth.bo.JwtToken;
import commons.exception.FilterFailureReason;
import commons.exception.ZdException;
import commons.auth.utils.ErrorHolder;
import commons.auth.bo.DefContents;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 鉴权登录拦截器
 **/
@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        try {
            String token = httpServletRequest.getHeader(DefContents.X_ACCESS_TOKEN);
            JwtToken jwtToken = new JwtToken(token);
            // 提交给realm进行登入，如果错误他会抛出异常并被捕获
            getSubject(request, httpServletResponse).login(jwtToken);
            // 如果没有抛出异常则代表登入成功，返回true
            return true;
        } catch (ZdException e) {
            log.error(e.getMessage(), e);
            val reason = FilterFailureReason.valueOf(e.getFilterFailureReason());
            responseWrite(httpServletRequest, httpServletResponse, reason);
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            val zdException = (ZdException) e.getCause();
            val reason = FilterFailureReason.valueOf(zdException.getFilterFailureReason());
            responseWrite(httpServletRequest, httpServletResponse, reason);
            return false;
        }
    }

    @SneakyThrows
    private void responseWrite(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterFailureReason reason) {
        PrintWriter out = httpServletResponse.getWriter();
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setStatus(reason.getCode());
        out.write(ErrorHolder.responseToJson(reason, httpServletRequest.getRequestURI()));
        out.flush();
    }


}
