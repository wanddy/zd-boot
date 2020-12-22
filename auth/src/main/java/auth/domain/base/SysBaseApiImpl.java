package auth.domain.base;

import auth.discard.message.entity.SysMessageTemplate;
import auth.discard.message.service.ISysMessageTemplateService;
import auth.discard.message.websocket.WebSocket;
import auth.domain.category.mapper.SysCategoryMapper;
import auth.domain.data.service.ISysDataSourceService;
import auth.domain.depart.mapper.SysDepartMapper;
import auth.domain.depart.service.ISysDepartService;
import auth.domain.dict.service.ISysDictService;
import auth.domain.log.mapper.SysLogMapper;
import auth.domain.notice.mapper.SysAnnouncementMapper;
import auth.domain.notice.mapper.SysAnnouncementSendMapper;
import auth.domain.permission.mapper.SysPermissionMapper;
import auth.domain.relation.user.depart.service.ISysUserDepartService;
import auth.domain.relation.user.role.mapper.SysUserRoleMapper;
import auth.domain.role.mapper.SysRoleMapper;
import auth.domain.user.mapper.UserMapper;
import auth.entity.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import commons.auth.vo.*;
import commons.constant.CacheConstant;
import commons.constant.CommonConstant;
import commons.constant.DataBaseConstant;
import commons.constant.WebsocketConst;
import commons.exception.ZdException;
import commons.system.api.ISysBaseAPI;
import commons.util.*;
import commons.util.oss.OssBootUtil;
import commons.util.qiniu.QiNiuUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * 底层共通业务API，提供其他独立模块调用
 */
@Slf4j
@Service
public class SysBaseApiImpl implements ISysBaseAPI {
    /**
     * 当前系统数据库类型
     */
    private static String DB_TYPE = "";

    private final ISysMessageTemplateService sysMessageTemplateService;

    private final SysLogMapper sysLogMapper;

    private final SysUserRoleMapper sysUserRoleMapper;

    private final ISysDepartService sysDepartService;

    private final ISysDictService sysDictService;

    private final SysPermissionMapper sysPermissionMapper;

    private final SysAnnouncementMapper sysAnnouncementMapper;

    private final SysAnnouncementSendMapper sysAnnouncementSendMapper;

    private final WebSocket webSocket;

    private final SysRoleMapper roleMapper;

    private final SysDepartMapper departMapper;

    private final SysCategoryMapper categoryMapper;

    private final ISysDataSourceService dataSourceService;

    private final UserMapper userMapper;

    private final ISysUserDepartService sysUserDepartService;

    @Autowired
    public SysBaseApiImpl(ISysMessageTemplateService sysMessageTemplateService, SysLogMapper sysLogMapper, SysUserRoleMapper sysUserRoleMapper, ISysDepartService sysDepartService, ISysDictService sysDictService, SysPermissionMapper sysPermissionMapper, SysAnnouncementMapper sysAnnouncementMapper, SysAnnouncementSendMapper sysAnnouncementSendMapper, WebSocket webSocket, SysRoleMapper roleMapper, SysDepartMapper departMapper, SysCategoryMapper categoryMapper, ISysDataSourceService dataSourceService, UserMapper userMapper, ISysUserDepartService sysUserDepartService) {
        this.sysMessageTemplateService = sysMessageTemplateService;
        this.sysLogMapper = sysLogMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysDepartService = sysDepartService;
        this.sysDictService = sysDictService;
        this.sysPermissionMapper = sysPermissionMapper;
        this.sysAnnouncementMapper = sysAnnouncementMapper;
        this.sysAnnouncementSendMapper = sysAnnouncementSendMapper;
        this.webSocket = webSocket;
        this.roleMapper = roleMapper;
        this.departMapper = departMapper;
        this.categoryMapper = categoryMapper;
        this.dataSourceService = dataSourceService;
        this.userMapper = userMapper;
        this.sysUserDepartService = sysUserDepartService;
    }

    @Override
    public void addLog(String LogContent, Integer logType, Integer operatetype) {
        Log log = new Log();
        //注解上的描述,操作日志内容
        log.setLogContent(LogContent);
        log.setLogType(logType);
        log.setOperateType(operatetype);

        //请求的方法名
        //请求的参数

        try {
            //获取request
            HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
            //设置IP地址
            log.setIp(IPUtils.getIpAddr(request));
        } catch (Exception e) {
            log.setIp("127.0.0.1");
        }

        //获取登录用户信息
        LoginUser User = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (User != null) {
            log.setUserid(User.getUsername());
            log.setUsername(User.getRealname());

        }
        //保存系统日志
        sysLogMapper.insert(log);
    }

    @Override
    @Cacheable(cacheNames = CacheConstant.SYS_USERS_CACHE, key = "#username")
    public LoginUser getUserByName(String username) {
        if (oConvertUtils.isEmpty(username)) {
            return null;
        }
        LoginUser loginUser = new LoginUser();
        User user = userMapper.getUserByName(username);
        if (user == null) {
            return null;
        }
        BeanUtils.copyProperties(user, loginUser);
        return loginUser;
    }

    @Override
    public LoginUser getUserById(String id) {
        if (oConvertUtils.isEmpty(id)) {
            return null;
        }
        LoginUser loginUser = new LoginUser();
        User User = userMapper.selectById(id);
        if (User == null) {
            return null;
        }
        BeanUtils.copyProperties(User, loginUser);
        return loginUser;
    }

    @Override
    public List<String> getRolesByUsername(String username) {
        return sysUserRoleMapper.getRoleByUserName(username);
    }

    @Override
    public List<String> getDepartIdsByUsername(String username) {
        List<Depart> list = sysDepartService.queryDepartsByUsername(username);
        List<String> result = new ArrayList<>(list.size());
        for (Depart depart : list) {
            result.add(depart.getId());
        }
        return result;
    }

    @Override
    public List<String> getDepartNamesByUsername(String username) {
        List<Depart> list = sysDepartService.queryDepartsByUsername(username);
        List<String> result = new ArrayList<>(list.size());
        for (Depart depart : list) {
            result.add(depart.getDepartName());
        }
        return result;
    }

    @Override
    public Set<String> getUserRolesSet(String username) {
        // 查询用户拥有的角色集合
        List<String> roles = sysUserRoleMapper.getRoleByUserName(username);
        log.info("-------通过数据库读取用户拥有的角色Rules------username： " + username + ",Roles size: " + roles.size());
        return new HashSet<>(roles);
    }

    @Override
    public Set<String> getUserPermissionsSet(String username) {
        Set<String> permissionSet = new HashSet<>();
        List<Permission> permissionList = sysPermissionMapper.queryByUser(username);
        for (Permission po : permissionList) {
//			// TODO URL规则有问题？
//			if (oConvertUtils.isNotEmpty(po.getUrl())) {
//				permissionSet.add(po.getUrl());
//			}
            if (oConvertUtils.isNotEmpty(po.getPerms())) {
                permissionSet.add(po.getPerms());
            }
        }
        log.info("-------通过数据库读取用户拥有的权限Perms------username： " + username + ",Perms size: " + permissionSet.size());
        return permissionSet;
    }

    @Override
    public String getDatabaseType() throws SQLException {
        if (oConvertUtils.isNotEmpty(DB_TYPE)) {
            return DB_TYPE;
        }
        javax.sql.DataSource dataSource = SpringContextUtils.getApplicationContext().getBean(javax.sql.DataSource.class);
        return getDatabaseTypeByDataSource(dataSource);
    }

    @Override
    @Cacheable(value = CacheConstant.SYS_DICT_CACHE, key = "#code")
    public List<DictModel> queryDictItemsByCode(String code) {
        return sysDictService.queryDictItemsByCode(code);
    }

    @Override
    public List<DictModel> queryTableDictItemsByCode(String table, String text, String code) {
        return sysDictService.queryTableDictItemsByCode(table, text, code);
    }

    @Override
    public List<DictModel> queryAllDepartBackDictModel() {
        return sysDictService.queryAllDepartBackDictModel();
    }

    @Override
    public List<JSONObject> queryAllDepart(Wrapper wrapper) {
        //noinspection unchecked
        return JSON.parseArray(JSON.toJSONString(sysDepartService.list(wrapper))).toJavaList(JSONObject.class);
    }

    @Override
    public void sendSysAnnouncement(String fromUser, String toUser, String title, String msgContent) {
        this.sendSysAnnouncement(fromUser, toUser, title, msgContent, CommonConstant.MSG_CATEGORY_2);
    }

    @Override
    public void sendSysAnnouncement(String fromUser, String toUser, String title, String msgContent, String setMsgCategory) {
        Announcement announcement = new Announcement();
        announcement.setTitile(title);
        announcement.setMsgContent(msgContent);
        announcement.setSender(fromUser);
        announcement.setPriority(CommonConstant.PRIORITY_M);
        announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
        announcement.setSendStatus(CommonConstant.HAS_SEND);
        announcement.setSendTime(new Date());
        announcement.setMsgCategory(setMsgCategory);
        announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
        sysAnnouncementMapper.insert(announcement);
        // 2.插入用户通告阅读标记表记录
        String[] userIds = toUser.split(",");
        String anntId = announcement.getId();
        for (String userId : userIds) {
            if (oConvertUtils.isNotEmpty(userId)) {
                User User = userMapper.getUserByName(userId);
                if (User == null) {
                    continue;
                }
                AnnouncementSend announcementSend = new AnnouncementSend();
                announcementSend.setId(anntId);
                announcementSend.setUserId(User.getId());
                announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                sysAnnouncementSendMapper.insert(announcementSend);
                JSONObject obj = new JSONObject();
                obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                obj.put(WebsocketConst.MSG_USER_ID, User.getId());
                obj.put(WebsocketConst.MSG_ID, announcement.getId());
                obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
                webSocket.sendOneMessage(User.getId(), obj.toJSONString());
            }
        }

    }

    @Override
    public void sendSysAnnouncement(String fromUser, String toUser, String title, String msgContent, String setMsgCategory, String busType, String busId) {
        Announcement announcement = new Announcement();
        announcement.setTitile(title);
        announcement.setMsgContent(msgContent);
        announcement.setSender(fromUser);
        announcement.setPriority(CommonConstant.PRIORITY_M);
        announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
        announcement.setSendStatus(CommonConstant.HAS_SEND);
        announcement.setSendTime(new Date());
        announcement.setMsgCategory(setMsgCategory);
        announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
        announcement.setBusId(busId);
        announcement.setBusType(busType);
        announcement.setOpenType(Objects.requireNonNull(SysAnnmentTypeEnum.getByType(busType)).getOpenType());
        announcement.setOpenPage(Objects.requireNonNull(SysAnnmentTypeEnum.getByType(busType)).getOpenPage());
        sysAnnouncementMapper.insert(announcement);
        // 2.插入用户通告阅读标记表记录
        String userId = toUser;
        String[] userIds = userId.split(",");
        String anntId = announcement.getId();
        for (String id : userIds) {
            if (oConvertUtils.isNotEmpty(id)) {
                User User = userMapper.getUserByName(id);
                if (User == null) {
                    continue;
                }
                AnnouncementSend announcementSend = new AnnouncementSend();
                announcementSend.setId(anntId);
                announcementSend.setUserId(User.getId());
                announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                sysAnnouncementSendMapper.insert(announcementSend);
                JSONObject obj = new JSONObject();
                obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                obj.put(WebsocketConst.MSG_USER_ID, User.getId());
                obj.put(WebsocketConst.MSG_ID, announcement.getId());
                obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
                webSocket.sendOneMessage(User.getId(), obj.toJSONString());
            }
        }
    }

    @Override
    public void updateSysAnnounReadFlag(String busType, String busId) {
        Announcement announcement = sysAnnouncementMapper.selectOne(new QueryWrapper<Announcement>().eq("bus_type", busType).eq("bus_id", busId));
        if (announcement != null) {
            LoginUser User = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            String userId = User.getId();
            LambdaUpdateWrapper<AnnouncementSend> updateWrapper = new UpdateWrapper().lambda();
            updateWrapper.set(AnnouncementSend::getReadFlag, CommonConstant.HAS_READ_FLAG);
            updateWrapper.set(AnnouncementSend::getReadTime, new Date());
            updateWrapper.last("where annt_id ='" + announcement.getId() + "' and user_id ='" + userId + "'");
            AnnouncementSend announcementSend = new AnnouncementSend();
            sysAnnouncementSendMapper.update(announcementSend, updateWrapper);
        }
    }

    @Override
    public String parseTemplateByCode(String templateCode, Map<String, String> map) {
        List<SysMessageTemplate> sysSmsTemplates = sysMessageTemplateService.selectByCode(templateCode);
        if (sysSmsTemplates == null || sysSmsTemplates.size() == 0) {
            throw new ZdException("消息模板不存在，模板编码：" + templateCode);
        }
        SysMessageTemplate sysSmsTemplate = sysSmsTemplates.get(0);
        //模板内容
        String content = sysSmsTemplate.getTemplateContent();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String str = "${" + entry.getKey() + "}";
                content = content.replace(str, entry.getValue());
            }
        }
        return content;
    }

    @Override
    public void sendSysAnnouncement(String fromUser, String toUser, String title, Map<String, String> map, String templateCode) {
        List<SysMessageTemplate> sysSmsTemplates = sysMessageTemplateService.selectByCode(templateCode);
        if (sysSmsTemplates == null || sysSmsTemplates.size() == 0) {
            throw new ZdException("消息模板不存在，模板编码：" + templateCode);
        }
        SysMessageTemplate sysSmsTemplate = sysSmsTemplates.get(0);
        //模板标题
        title = title == null ? sysSmsTemplate.getTemplateName() : title;
        //模板内容
        String content = sysSmsTemplate.getTemplateContent();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String str = "${" + entry.getKey() + "}";
                title = title.replace(str, entry.getValue());
                content = content.replace(str, entry.getValue());
            }
        }

        Announcement announcement = new Announcement();
        announcement.setTitile(title);
        announcement.setMsgContent(content);
        announcement.setSender(fromUser);
        announcement.setPriority(CommonConstant.PRIORITY_M);
        announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
        announcement.setSendStatus(CommonConstant.HAS_SEND);
        announcement.setSendTime(new Date());
        announcement.setMsgCategory(CommonConstant.MSG_CATEGORY_2);
        announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
        sysAnnouncementMapper.insert(announcement);
        // 2.插入用户通告阅读标记表记录
        String[] userIds = toUser.split(",");
        String anntId = announcement.getId();
        for (String userId : userIds) {
            if (oConvertUtils.isNotEmpty(userId)) {
                User User = userMapper.getUserByName(userId);
                if (User == null) {
                    continue;
                }
                AnnouncementSend announcementSend = new AnnouncementSend();
                announcementSend.setId(anntId);
                announcementSend.setUserId(User.getId());
                announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                sysAnnouncementSendMapper.insert(announcementSend);
                JSONObject obj = new JSONObject();
                obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                obj.put(WebsocketConst.MSG_USER_ID, User.getId());
                obj.put(WebsocketConst.MSG_ID, announcement.getId());
                obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
                webSocket.sendOneMessage(User.getId(), obj.toJSONString());
            }
        }
    }

    @Override
    public void sendSysAnnouncement(String fromUser, String toUser, String title, Map<String, String> map, String templateCode, String busType, String busId) {
        List<SysMessageTemplate> sysSmsTemplates = sysMessageTemplateService.selectByCode(templateCode);
        if (sysSmsTemplates == null || sysSmsTemplates.size() == 0) {
            throw new ZdException("消息模板不存在，模板编码：" + templateCode);
        }
        SysMessageTemplate sysSmsTemplate = sysSmsTemplates.get(0);
        //模板标题
        title = title == null ? sysSmsTemplate.getTemplateName() : title;
        //模板内容
        String content = sysSmsTemplate.getTemplateContent();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String str = "${" + entry.getKey() + "}";
                title = title.replace(str, entry.getValue());
                content = content.replace(str, entry.getValue());
            }
        }
        Announcement announcement = new Announcement();
        announcement.setTitile(title);
        announcement.setMsgContent(content);
        announcement.setSender(fromUser);
        announcement.setPriority(CommonConstant.PRIORITY_M);
        announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
        announcement.setSendStatus(CommonConstant.HAS_SEND);
        announcement.setSendTime(new Date());
        announcement.setMsgCategory(CommonConstant.MSG_CATEGORY_2);
        announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
        announcement.setBusId(busId);
        announcement.setBusType(busType);
        announcement.setOpenType(Objects.requireNonNull(SysAnnmentTypeEnum.getByType(busType)).getOpenType());
        announcement.setOpenPage(Objects.requireNonNull(SysAnnmentTypeEnum.getByType(busType)).getOpenPage());
        sysAnnouncementMapper.insert(announcement);
        // 2.插入用户通告阅读标记表记录
        String[] userIds = toUser.split(",");
        String anntId = announcement.getId();
        for (String userId : userIds) {
            if (oConvertUtils.isNotEmpty(userId)) {
                User User = userMapper.getUserByName(userId);
                if (User == null) {
                    continue;
                }
                AnnouncementSend announcementSend = new AnnouncementSend();
                announcementSend.setId(anntId);
                announcementSend.setUserId(User.getId());
                announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                sysAnnouncementSendMapper.insert(announcementSend);
                JSONObject obj = new JSONObject();
                obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                obj.put(WebsocketConst.MSG_USER_ID, User.getId());
                obj.put(WebsocketConst.MSG_ID, announcement.getId());
                obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
                webSocket.sendOneMessage(User.getId(), obj.toJSONString());
            }
        }
    }

    /**
     * 获取数据库类型
     */
    private String getDatabaseTypeByDataSource(javax.sql.DataSource dataSource) {
        if (StringUtils.isEmpty(DB_TYPE)) {
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData md = connection.getMetaData();
                String dbType = md.getDatabaseProductName().toLowerCase();
                if (dbType.contains("mysql")) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_MYSQL;
                } else if (dbType.contains("oracle")) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_ORACLE;
                } else if (dbType.contains("sqlserver") || dbType.contains("sql server")) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_SQLSERVER;
                } else if (dbType.contains("postgresql")) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_POSTGRESQL;
                } else {
                    throw new ZdException("数据库类型:[" + dbType + "]不识别!");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return DB_TYPE;

    }

    @Override
    public List<DictModel> queryAllDict() {
        // 查询并排序
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("create_time");
        List<Dict> dicts = sysDictService.list(queryWrapper);
        // 封装成 model
        List<DictModel> list = new ArrayList<>();
        for (Dict dict : dicts) {
            list.add(new DictModel(dict.getDictCode(), dict.getDictName()));
        }

        return list;
    }

    @Override
    public List<SysCategoryModel> queryAllDSysCategory() {
        List<Category> ls = categoryMapper.selectList(null);
        return oConvertUtils.entityListToModelList(ls, SysCategoryModel.class);
    }

    @Override
    public List<DictModel> queryFilterTableDictInfo(String table, String text, String code, String filterSql) {
        return sysDictService.queryTableDictItemsByCodeAndFilter(table, text, code, filterSql);
    }

    @Override
    public List<String> queryTableDictByKeys(String table, String text, String code, String[] keyArray) {
        return sysDictService.queryTableDictByKeys(table, text, code, Joiner.on(",").join(keyArray));
    }

    @Override
    public List<ComboModel> queryAllUser() {
        List<ComboModel> list = new ArrayList<>();
        List<User> userList = userMapper.selectList(new QueryWrapper<User>().eq("status", 1).eq("del_flag", 0));
        for (User user : userList) {
            ComboModel model = new ComboModel();
            model.setTitle(user.getRealname());
            model.setId(user.getId());
            model.setUsername(user.getUsername());
            list.add(model);
        }
        return list;
    }

    @Override
    public JSONObject queryAllUser(String[] userIds, int pageNo, int pageSize) {
        JSONObject json = new JSONObject();
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>().eq("status", 1).eq("del_flag", 0);
        List<ComboModel> list = new ArrayList<>();
        Page<User> page = new Page<>(pageNo, pageSize);
        IPage<User> pageList = userMapper.selectPage(page, queryWrapper);
        for (User user : pageList.getRecords()) {
            ComboModel model = new ComboModel();
            model.setUsername(user.getUsername());
            model.setTitle(user.getRealname());
            model.setId(user.getId());
            model.setEmail(user.getEmail());
            if (oConvertUtils.isNotEmpty(userIds)) {
                for (String userId : userIds) {
                    if (userId.equals(user.getId())) {
                        model.setChecked(true);
                    }
                }
            }
            list.add(model);
        }
        json.put("list", list);
        json.put("total", pageList.getTotal());
        return json;
    }

    @Override
    public List<JSONObject> queryAllUser(Wrapper wrapper) {
        //noinspection unchecked
        return JSON.parseArray(JSON.toJSONString(userMapper.selectList(wrapper))).toJavaList(JSONObject.class);
    }

    @Override
    public List<ComboModel> queryAllRole() {
        List<ComboModel> list = new ArrayList<>();
        List<Role> roleList = roleMapper.selectList(new QueryWrapper<Role>());
        for (Role role : roleList) {
            ComboModel model = new ComboModel();
            model.setTitle(role.getRoleName());
            model.setId(role.getId());
            list.add(model);
        }
        return list;
    }

    @Override
    public List<ComboModel> queryAllRole(String[] roleIds) {
        List<ComboModel> list = new ArrayList<>();
        List<Role> roleList = roleMapper.selectList(new QueryWrapper<>());
        for (Role role : roleList) {
            ComboModel model = new ComboModel();
            model.setTitle(role.getRoleName());
            model.setId(role.getId());
            model.setRoleCode(role.getRoleCode());
            if (oConvertUtils.isNotEmpty(roleIds)) {
                for (String roleId : roleIds) {
                    if (roleId.equals(role.getId())) {
                        model.setChecked(true);
                    }
                }
            }
            list.add(model);
        }
        return list;
    }

    @Override
    public List<String> getRoleIdsByUsername(String username) {
        return sysUserRoleMapper.getRoleIdByUserName(username);
    }

    @Override
    public String getDepartIdsByOrgCode(String orgCode) {
        return departMapper.queryDepartIdByOrgCode(orgCode);
    }

    @Override
    public DictModel getParentDepartId(String departId) {
        Depart depart = departMapper.getParentDepartId(departId);
        return new DictModel(depart.getId(), depart.getParentId());
    }

    @Override
    public List<SysDepartModel> getAllSysDepart() {
        List<SysDepartModel> departModelList = new ArrayList<>();
        List<Depart> departList = departMapper.selectList(new QueryWrapper<Depart>().eq("del_flag", "0"));
        for (Depart depart : departList) {
            SysDepartModel model = new SysDepartModel();
            BeanUtils.copyProperties(depart, model);
            departModelList.add(model);
        }
        return departModelList;
    }

    @Override
    public DynamicDataSourceModel getDynamicDbSourceById(String dbSourceId) {
        DataSource dbSource = dataSourceService.getById(dbSourceId);
        return new DynamicDataSourceModel(dbSource);
    }

    @Override
    public DynamicDataSourceModel getDynamicDbSourceByCode(String dbSourceCode) {
        DataSource dbSource = dataSourceService.getOne(new LambdaQueryWrapper<DataSource>().eq(DataSource::getCode, dbSourceCode));
        return new DynamicDataSourceModel(dbSource);
    }

    @Override
    public List<String> getDeptHeadByDepId(String deptId) {
        List<User> userList = userMapper.selectList(new QueryWrapper<User>().like("depart_ids", deptId).eq("status", 1).eq("del_flag", 0));
        List<String> list = new ArrayList<>();
        for (User user : userList) {
            list.add(user.getUsername());
        }
        return list;
    }

    @Override
    public String upload(MultipartFile file, String bizPath, String uploadType) {
        String url;
        if (CommonConstant.UPLOAD_TYPE_QINIU.equals(uploadType)) {
            url = QiNiuUtil.upload(file, bizPath);
        } else if (CommonConstant.UPLOAD_TYPE_MINIO.equals(uploadType)) {
            url = MinioUtil.upload(file, bizPath);
        } else {
            url = OssBootUtil.upload(file, bizPath);
        }
        return url;
    }

    @Override
    public String upload(MultipartFile file, String bizPath, String uploadType, String customBucket) {
        String url;
        if (CommonConstant.UPLOAD_TYPE_MINIO.equals(uploadType)) {
            url = MinioUtil.upload(file, bizPath, customBucket);
        } else {
            url = OssBootUtil.upload(file, bizPath, customBucket);
        }
        return url;
    }

    @Override
    public void viewAndDownload(String filePath, String uploadpath, String uploadType, HttpServletResponse response) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            if (filePath.startsWith("http")) {
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                if (CommonConstant.UPLOAD_TYPE_MINIO.equals(uploadType)) {
                    String bucketName = filePath.replace(MinioUtil.getMinioUrl(), "").split("/")[0];
                    String objectName = filePath.replace(MinioUtil.getMinioUrl() + bucketName, "");
                    inputStream = MinioUtil.getMinioFile(bucketName, objectName);
                    if (inputStream == null) {
                        bucketName = CommonConstant.UPLOAD_CUSTOM_BUCKET;
                        objectName = filePath.replace(OssBootUtil.getStaticDomain() + "/", "");
                        inputStream = OssBootUtil.getOssFile(objectName, bucketName);
                    }
                } else {
                    String bucketName = CommonConstant.UPLOAD_CUSTOM_BUCKET;
                    String objectName = filePath.replace(OssBootUtil.getStaticDomain() + "/", "");
                    inputStream = OssBootUtil.getOssFile(objectName, bucketName);
                    if (inputStream == null) {
                        bucketName = filePath.replace(MinioUtil.getMinioUrl(), "").split("/")[0];
                        objectName = filePath.replace(MinioUtil.getMinioUrl() + bucketName, "");
                        inputStream = MinioUtil.getMinioFile(bucketName, objectName);
                    }
                }
                response.addHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            } else {
                // 本地文件处理
                filePath = filePath.replace("..", "");
                if (filePath.endsWith(",")) {
                    filePath = filePath.substring(0, filePath.length() - 1);
                }
                String fullPath = uploadpath + File.separator + filePath;
                String downloadFilePath = uploadpath + File.separator + fullPath;
                File file = new File(downloadFilePath);
                inputStream = new BufferedInputStream(new FileInputStream(fullPath));
                response.addHeader("Content-Disposition", "attachment;fileName=" + new String(file.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            }
            response.setContentType("application/force-download");// 设置强制下载不打开
            outputStream = response.getOutputStream();
            if (inputStream != null) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
                response.flushBuffer();
            }
        } catch (IOException e) {
            response.setStatus(404);
            log.error("预览文件失败" + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void sendWebSocketMsg(String[] userIds, String cmd) {
        JSONObject obj = new JSONObject();
        obj.put(WebsocketConst.MSG_CMD, cmd);
        webSocket.sendMoreMessage(userIds, obj.toJSONString());
    }

    @Override
    public List<LoginUser> queryAllUserByIds(String[] userIds) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>().eq("status", 1).eq("del_flag", 0);
        queryWrapper.in("id", (Object) userIds);
        List<LoginUser> loginUsers = new ArrayList<>();
        List<User> sysUsers = userMapper.selectList(queryWrapper);
        for (User user : sysUsers) {
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(user, loginUser);
            loginUsers.add(loginUser);
        }
        return loginUsers;
    }

    /**
     * 推送签到人员信息
     *
     * @param userId
     */
    @Override
    public void meetingSignWebsocket(String userId) {
        JSONObject obj = new JSONObject();
        obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_SIGN);
        obj.put(WebsocketConst.MSG_USER_ID, userId);
        //TODO 目前全部推送，后面修改
        webSocket.sendAllMessage(obj.toJSONString());
    }

    @Override
    public List<LoginUser> queryUserByNames(String[] userNames) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>().eq("status", 1).eq("del_flag", 0);
        queryWrapper.in("username", (Object) userNames);
        List<LoginUser> loginUsers = new ArrayList<>();
        List<User> sysUsers = userMapper.selectList(queryWrapper);
        for (User user : sysUsers) {
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(user, loginUser);
            loginUsers.add(loginUser);
        }
        return loginUsers;
    }
}
