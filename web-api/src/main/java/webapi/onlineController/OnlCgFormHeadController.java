package webapi.onlineController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.constant.enums.CgformEnum;
import commons.auth.utils.JwtUtil;
import commons.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.google.common.collect.Lists;
import smartcode.config.exception.DBException;
import smartcode.form.entity.OnlCgformHead;
import smartcode.form.service.OnlCgformHeadService;
import smartcode.form.utils.DbReadTableUtil;
import smartcode.form.utils.DbSelectUtils;
import smartcode.form.utils.PathCodeUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.sql.SQLException;
import java.util.*;

/**
 * @Author: LiuHongYan
 * @Date: 2020/9/9 15:38
 * @Description: 智能代码--head
 **/
@RestController("OnlCgFormHeadController")
@RequestMapping({"/online/cgform/head"})
public class OnlCgFormHeadController {

    private static final List<String> tableHeader = Lists.newArrayList(new String[]{"act_", "ext_act_", "design_", "onl_", "sys_", "qrtz_"});
    private static String tableName;

    @Autowired
    private OnlCgformHeadService onlCgformHeadService;

    /**
     * online表单开发列表页
     *
     * @param onlCgformHead
     * @param pageNo
     * @param pageSize
     * @param paramHttpServletRequest
     * @return
     */
    @GetMapping({"/list"})
    public Result onlineCgformList(OnlCgformHead onlCgformHead,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest paramHttpServletRequest) {
        Result result = new Result();
        QueryWrapper queryWrapper = QueryGenerator.initQueryWrapper(onlCgformHead, paramHttpServletRequest.getParameterMap());
        Page page = new Page(pageNo.intValue(), pageSize.intValue());
        IPage iPage = onlCgformHeadService.page(page, queryWrapper);
        if (onlCgformHead.getCopyType() != null && onlCgformHead.getCopyType().intValue() == 0) {
            onlCgformHeadService.initCopyState(iPage.getRecords());
        }
        result.setSuccess(true);
        result.setResult(iPage);
        return result;
    }

    /**
     * 根据code查询表配置信息
     *
     * @param code
     * @return
     */
    @GetMapping({"/tableInfo"})
    public Result tableInfo(@RequestParam(name = "code", required = true) String code) {
        OnlCgformHead onlCgformHead = onlCgformHeadService.getById(code);
        if (onlCgformHead == null) {
            return Result.error("未找到对应实体");
        } else {
            HashMap map = new HashMap();
            map.put("main", onlCgformHead);
            if (onlCgformHead.getTableType() == 2) {
                String subTableStr = onlCgformHead.getSubTableStr();
                if (oConvertUtils.isNotEmpty(subTableStr)) {
                    List list = new ArrayList();
                    String[] sub = subTableStr.split(",");
                    int len = sub.length;

                    for (int i = 0; i < len; i++) {
                        String a = sub[i];
                        QueryWrapper queryWrapper = new QueryWrapper<OnlCgformHead>();
                        queryWrapper.eq("table_name", a);
                        OnlCgformHead onlCgformHeadEntity = onlCgformHeadService.getOne(queryWrapper);
                        list.add(onlCgformHeadEntity);
                    }
                    list.sort(Comparator.comparing(OnlCgformHead::getTabOrderNum).reversed());
                    map.put("sub", list);
                }
            }

            Integer tableType = onlCgformHead.getTableType();
            if ("Y".equals(onlCgformHead.getIsTree())) {
                tableType = 3;
            }

            List jspModel = CgformEnum.getJspModelList(tableType);
            map.put("jspModeList", jspModel);
            map.put("projectPath", PathCodeUtils.m());
            return Result.ok(map);
        }
    }

    /**
     * 文件路径
     *
     * @return
     */
    @GetMapping({"/rootFile"})
    public Result<?> rootFile() {
        JSONArray jsons = new JSONArray();
        File[] files = File.listRoots();
        int length = files.length;

        for (int i = 0; i < length; i++) {
            File file = files[i];
            JSONObject json = new JSONObject();
            if (file.isDirectory()) {
                System.out.println(file.getPath());
                json.put("key", file.getAbsolutePath());
                json.put("title", file.getPath());
                json.put("opened", false);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("icon", "custom");
                json.put("scopedSlots", jsonObject);
                json.put("isLeaf", file.listFiles() == null || file.listFiles().length == 0);
            }

            jsons.add(json);
        }

        return Result.ok(jsons);
    }

    /**
     * 查找本地目录
     *
     * @param parentPath
     * @return
     */
    @GetMapping({"/fileTree"})
    public Result<?> e(@RequestParam(name = "parentPath", required = true) String parentPath) {
        JSONArray json = new JSONArray();
        File file = new File(parentPath);
        File[] files = file.listFiles();
        int length = files.length;

        for (int i = 0; i < length; i++) {
            File file1 = files[i];
            if (file1.isDirectory() && oConvertUtils.isNotEmpty(file1.getPath())) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", file1.getAbsolutePath());
                jsonObject.put("title", file1.getPath().substring(file1.getPath().lastIndexOf(File.separator) + 1));
                jsonObject.put("isLeaf", file1.listFiles() == null || file1.listFiles().length == 0);
                jsonObject.put("opened", false);
                JSONObject tbnames0 = new JSONObject();
                tbnames0.put("icon", "custom");
                jsonObject.put("scopedSlots", tbnames0);
                json.add(jsonObject);
            }
        }

        return Result.ok(json);
    }

    /**
     * 查询表单没有的数据库表信息
     * @param request
     * @return
     */
    @GetMapping({"/queryTables"})
    public Result<?> queryTables(HttpServletRequest request) {
        String token = JwtUtil.getUserNameByToken(request);
        if (!"admin".equals(token)) {
            return Result.error("noadminauth");
        } else {
            new ArrayList();

            List<String> stringList;
            try {
                stringList = DbReadTableUtil.a();
            } catch (SQLException e) {
                return Result.error("同步失败，未获取数据库表信息");
            }

            DbSelectUtils.sortDb(stringList);
            List<String> list = onlCgformHeadService.queryOnlinetables();
            stringList.removeAll(list);
            ArrayList arrayList = new ArrayList();
            Iterator iterator = stringList.iterator();

            while (iterator.hasNext()) {
                String table = (String) iterator.next();
                if (!this.getTableHeader(table)) {
                    HashMap map = new HashMap();
                    map.put("id", table);
                    arrayList.add(map);
                }
            }
            return Result.ok(arrayList);
        }
    }

    /**
     * 根据表生成表单
     * @param tbnames
     * @param request
     * @return
     */
    @PostMapping({"/transTables/{tbnames}"})
//    @RequiresRoles("admin")("admin")({"admin"})
    public Result<?> transTables(@PathVariable("tbnames") String tbnames, HttpServletRequest request) {
        String token = JwtUtil.getUserNameByToken(request);
        if (!"admin".equals(token)) {
            return Result.error("noadminauth");
        } else if (oConvertUtils.isEmpty(tbnames)) {
            return Result.error("未识别的表名信息");
        } else if (tableName != null && tableName.equals(tbnames)) {
            return Result.error("不允许重复生成!");
        } else {
            tableName = tbnames;
            String[] split = tbnames.split(",");

            for(int i = 0; i < split.length; i++) {
                if (oConvertUtils.isNotEmpty(split[i])) {
                    int var6 = onlCgformHeadService.count(new QueryWrapper<OnlCgformHead>().eq("table_name",split[i]));
                    if (var6 <= 0) {
                        this.onlCgformHeadService.saveDbTable2Online(split[i]);
                    }
                }
            }

            tableName = null;
            return Result.ok("同步完成!");
        }
    }

    /**
     * 获取表前缀
     *
     * @param table
     * @return
     */
    private boolean getTableHeader(String table) {
        Iterator iterator = tableHeader.iterator();

        String str;
        do {
            if (!iterator.hasNext()) {
                return false;
            }

            str = (String) iterator.next();
        } while (!table.startsWith(str) && !table.startsWith(str.toUpperCase()));

        return true;
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping({"/delete"})
    public Result<?> a(@RequestParam(name = "id",required = true) String id) {
        try {
            this.onlCgformHeadService.deleteRecordAndTable(id);
        } catch (DBException var3) {
            return Result.error("删除失败" + var3.getMessage());
        } catch (SQLException var4) {
            return Result.error("删除失败" + var4.getMessage());
        }
        return Result.ok("删除成功!");
    }

    @DeleteMapping({"/removeRecord"})
    public Result<?> removeRecord(@RequestParam(name = "id",required = true) String id) {
        try {
            this.onlCgformHeadService.deleteRecord(id);
        } catch (DBException e) {
            return Result.error("移除失败" + e.getMessage());
        }

        return Result.ok("移除成功!");
    }


}
