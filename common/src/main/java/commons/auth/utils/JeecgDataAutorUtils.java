package commons.auth.utils;

import commons.auth.vo.SysPermissionDataRuleModel;
import commons.auth.vo.SysUserCacheInfo;
import commons.util.SpringContextUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据权限查询规则容器工具类
 */
public class JeecgDataAutorUtils {

    public static final String MENU_DATA_AUTHOR_RULES = "MENU_DATA_AUTHOR_RULES";

    public static final String MENU_DATA_AUTHOR_RULE_SQL = "MENU_DATA_AUTHOR_RULE_SQL";

    public static final String SYS_USER_INFO = "SYS_USER_INFO";

    /**
     * 往链接请求里面，传入数据查询条件
     */
    public static synchronized void installDataSearchCondition(HttpServletRequest request, List<SysPermissionDataRuleModel> dataRules) {
        // 1.先从request获取MENU_DATA_AUTHOR_RULES，如果存则获取到LIST
        List<SysPermissionDataRuleModel> list = loadDataSearchCondition();
        if (list == null) {
            // 2.如果不存在，则new一个list
            list = new ArrayList<>();
        }
        list.addAll(dataRules);
        // 3.往list里面增量存指
        request.setAttribute(MENU_DATA_AUTHOR_RULES, list);
    }

    /**
     * 获取请求对应的数据权限规则
     */
    @SuppressWarnings("unchecked")
    public static synchronized List<SysPermissionDataRuleModel> loadDataSearchCondition() {
        return (List<SysPermissionDataRuleModel>) SpringContextUtils.getHttpServletRequest().getAttribute(MENU_DATA_AUTHOR_RULES);

    }

    /**
     * 获取请求对应的数据权限SQL
     */
    public static synchronized String loadDataSearchConditionSQLString() {
        return (String) SpringContextUtils.getHttpServletRequest().getAttribute(MENU_DATA_AUTHOR_RULE_SQL);
    }

    /**
     * 往链接请求里面，传入数据查询条件
     *
     * @param request
     * @param sql
     */
    public static synchronized void installDataSearchCondition(HttpServletRequest request, String sql) {
        String ruleSql = (String) loadDataSearchConditionSQLString();
        if (!StringUtils.hasText(ruleSql)) {
            request.setAttribute(MENU_DATA_AUTHOR_RULE_SQL, sql);
        }
    }

    public static synchronized void installUserInfo(HttpServletRequest request, SysUserCacheInfo userInfo) {
        request.setAttribute(SYS_USER_INFO, userInfo);
    }

    public static synchronized SysUserCacheInfo loadUserInfo() {
        return (SysUserCacheInfo) SpringContextUtils.getHttpServletRequest().getAttribute(SYS_USER_INFO);

    }
}
