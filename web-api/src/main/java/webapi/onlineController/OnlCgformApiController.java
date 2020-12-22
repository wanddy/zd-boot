package webapi.onlineController;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import commons.annotation.OnlineAuth;
import commons.annotation.PermissionData;
import commons.api.vo.Result;
import commons.util.TokenUtils;
import commons.util.oConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smartcode.config.exception.BusinessException;
import smartcode.config.utils.EnhanceJsUtil;
import smartcode.form.entity.OnlCgformEnhanceJs;
import smartcode.form.entity.OnlCgformHead;
import smartcode.form.model.AModel;
import smartcode.form.model.BModel;
import smartcode.form.model.OnlGenerateModel;
import smartcode.form.service.OnlCgformFieldService;
import smartcode.form.service.OnlCgformHeadService;
import smartcode.form.service.OnlineService;
import smartcode.form.utils.DbSelectUtils;
import smartcode.form.utils.dUtils;
import smartcode.form.utils.downloadUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/24 10:04
 * @Description: zdit.zdboot.auth.online.controller
 **/
@RestController("onlCgformApiController")
@RequestMapping({"/online/cgform/api"})
public class OnlCgformApiController {
    private static final Logger log = LoggerFactory.getLogger(OnlCgformApiController.class);
    @Autowired
    private OnlCgformHeadService onlCgformHeadService;
    @Autowired
    private OnlineService onlineService;
    @Autowired
    private OnlCgformFieldService onlCgformFieldService;

    /**
     * 代码生成
     * @param paramJSONObject
     * @return
     */
    @PostMapping({"/codeGenerate"})
    public Result generateCode (@RequestBody JSONObject paramJSONObject){
        OnlGenerateModel onlGenerateModel = JSONObject.parseObject(paramJSONObject.toJSONString(), OnlGenerateModel.class);

        List<String> list = null;
        try {
            if ("1".equals(onlGenerateModel.getJformType())) {
                 list = onlCgformHeadService.generateCode(onlGenerateModel);
            } else {
                list = onlCgformHeadService.generateOneToMany(onlGenerateModel);
            }
            return Result.ok(list);
        } catch (Exception exception) {
            exception.printStackTrace();
            return Result.error(exception.getMessage());
        }
    }

    /**
     * 数据库表查询
     * @param tbname
     * @param id
     * @return
     */
    @GetMapping({"/checkOnlyTable"})
    public Result checkOnlyTable(@RequestParam("tbname") String tbname, @RequestParam("id") String id) {
        OnlCgformHead onlCgformHead;
        if (oConvertUtils.isEmpty(id)) {
            if (dUtils.table(tbname)) {
                return Result.ok(-1);
            }
            onlCgformHead = onlCgformHeadService.getOne(new QueryWrapper<OnlCgformHead>().eq("table_name",tbname));
            if (oConvertUtils.isNotEmpty(onlCgformHead)) {
                return Result.ok(-1);
            }
        } else {
            onlCgformHead = onlCgformHeadService.getById(id);
            if (!tbname.equals(onlCgformHead.getTableName()) && dUtils.table(tbname)) {
                return Result.ok(-1);
            }
        }

        return Result.ok(1);
    }

    /**
     * 新增表
     * @param aModel
     * @return
     */
    @PostMapping({"/addAll"})
    public Result addAll(@RequestBody AModel aModel) {
        try {
            String tableName = aModel.getHead().getTableName();
            return dUtils.table(tableName) ? Result.error("数据库表[" + tableName + "]已存在,请从数据库导入表单") : this.onlCgformHeadService.addAll(aModel);
        } catch (Exception e) {
            log.error("OnlCgformApiController.addAll()发生异常：" + e.getMessage(), e);
            return Result.error("操作失败");
        }
    }

    /**
     * 同步数据库
     * @param code
     * @param synMethod
     * @return
     */
    @PostMapping({"/doDbSynch/{code}/{synMethod}"})
    public Result<?> doDbSynch(@PathVariable("code") String code, @PathVariable("synMethod") String synMethod) {
        try {
            long timeMillis = System.currentTimeMillis();
            this.onlCgformHeadService.doDbSynch(code, synMethod);
            log.info("==同步数据库消耗时间" + (System.currentTimeMillis() - timeMillis) + "毫秒");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("同步数据库失败，" + DbSelectUtils.findKeyword(e));
        }

        return Result.ok("同步数据库成功!");
    }

    /**
     * 表单功能测试
     * @param code
     * @param request
     * @return
     */
    @OnlineAuth("getFormItem")
    @GetMapping({"/getFormItem/{code}"})
    public Result<?> b(@PathVariable("code") String code, HttpServletRequest request) {
        OnlCgformHead onlCgformHead = this.onlCgformHeadService.getById(code);
        if (onlCgformHead == null) {
            Result.error("表不存在");
        }

        Result result = new Result();
        OnlCgformEnhanceJs var5 = this.onlCgformHeadService.queryEnhanceJs(code, "form");
        JSONObject var6 = this.onlineService.queryOnlineFormObj(onlCgformHead, var5);
        if (onlCgformHead.getTableType() == 2) {
            JSONObject var7 = var6.getJSONObject("schema");
            String var8 = onlCgformHead.getSubTableStr();
            if (oConvertUtils.isNotEmpty(var8)) {
                ArrayList var9 = new ArrayList();
                String[] code0 = var8.split(",");
                int code1 = code0.length;

                for(int code2 = 0; code2 < code1; ++code2) {
                    String code3 = code0[code2];
                    OnlCgformHead code4 = this.onlCgformHeadService.getOne(new QueryWrapper<OnlCgformHead>().eq("table_name",code3));
                    if (code4 != null) {
                        var9.add(code4);
                    }
                }

                if (var9.size() > 0) {
                    Collections.sort(var9);
                    Iterator code5 = var9.iterator();

                    while(code5.hasNext()) {
                        OnlCgformHead code6 = (OnlCgformHead)code5.next();
                        List code7 = this.onlCgformFieldService.queryAvailableFields(code6.getId(), code6.getTableName(), (String)null, false);
                        EnhanceJsUtil.b(var5, code6.getTableName(), code7);
                        JSONObject code8 = new JSONObject();
                        List code9 = this.onlCgformFieldService.queryDisabledFields(code6.getTableName());
                        if (1 == code6.getRelationType()) {
                            code8 = DbSelectUtils.a(code7, code9, null);
                        } else {
                            code8.put("columns", DbSelectUtils.a(code7, code9));
                        }

                        code8.put("id", code6.getId());
                        code8.put("describe", code6.getTableTxt());
                        code8.put("key", code6.getTableName());
                        code8.put("view", "tab");
                        code8.put("order", code6.getTabOrderNum());
                        code8.put("relationType", code6.getRelationType());
                        var7.getJSONObject("properties").put(code6.getTableName(), code8);
                    }
                }
            }

            if (var5 != null && oConvertUtils.isNotEmpty(var5.getCgJs())) {
                var6.put("enhanceJs", EnhanceJsUtil.a(var5.getCgJs()));
            }
        }

        result.setResult(var6);
        return result;
    }

    @OnlineAuth("getColumns")
    @GetMapping({"/getColumns/{code}"})
    public Result<?> getColumns(@PathVariable("code") String var1) {
        Result var2 = new Result();
        OnlCgformHead var3 = this.onlCgformHeadService.getById(var1);
        if (var3 == null) {
            var2.error500("实体不存在");
            return var2;
        } else {
            BModel var4 = this.onlineService.queryOnlineConfig(var3);
            var2.setResult(var4);
            return var2;
        }
    }


    @PermissionData
    @OnlineAuth("getData")
    @GetMapping({"/getData/{code}"})
    public Result<Map<String, Object>> a(@PathVariable("code") String code, HttpServletRequest request) {
        Result result = new Result();
        OnlCgformHead onlCgformHead = this.onlCgformHeadService.getById(code);
        if (onlCgformHead == null) {
            result.error500("实体不存在");
            return result;
        } else {
            try {
                String tableName = onlCgformHead.getTableName();
                Map map = DbSelectUtils.a(request);
                Map map1 = this.onlCgformFieldService.queryAutolistPage(tableName, code, map, null);
                this.a(onlCgformHead, map1);
                result.setResult(map1);
            } catch (Exception var8) {
                result.error500("数据库查询失败，" + var8.getMessage());
            }

            return result;
        }
    }

    @PutMapping({"/editAll"})
    public Result<?> editAll(@RequestBody AModel model) {
        try {
            return this.onlCgformHeadService.editAll(model);
        } catch (Exception var3) {
//            OnlCgreportAPI.error("OnlCgformApiController.editAll()发生异常：" + var3.getMessage(), var3);
            return Result.error("操作失败");
        }
    }


    @OnlineAuth("form")
    @PostMapping({"/form/{code}"})
    public Result<String> postFormByCode(@PathVariable("code") String var1, @RequestBody JSONObject var2, HttpServletRequest var3) {
        Result var4 = new Result();

        try {
            String var5 = DbSelectUtils.a();
            var2.put("id", var5);
            String var6 = TokenUtils.getTokenByRequest(var3);
            this.onlCgformHeadService.saveManyFormData(var1, var2, var6);
            var4.setSuccess(true);
            var4.setResult(var5);
        } catch (Exception var7) {
//            OnlCgreportAPI.error("OnlCgformApiController.formAdd()发生异常：", var7);
            var4.setSuccess(false);
            var4.setMessage("保存失败，" + DbSelectUtils.findKeyword(var7));
        }

        return var4;
    }

    @OnlineAuth("getQueryInfo")
    @GetMapping({"/getQueryInfo/{code}"})
    public Result<?> getQueryInfo(@PathVariable("code") String var1) {
        try {
            List var2 = this.onlCgformFieldService.getAutoListQueryInfo(var1);
            return Result.ok(var2);
        } catch (Exception var3) {
//            cgreportAUtils.error("OnlCgformApiController.getQueryInfo()发生异常：" + var3.getMessage(), var3);
            return Result.error("查询失败");
        }
    }

    @OnlineAuth("form")
    @DeleteMapping({"/form/{code}/{id}"})
    public Result<?> deleteFormByCodeAndId(@PathVariable("code") String code, @PathVariable("id") String id) {
        try {
            OnlCgformHead onlCgformHead = this.onlCgformHeadService.getById(code);
            if (onlCgformHead == null) {
                return Result.error("实体不存在");
            }

            if ("Y".equals(onlCgformHead.getIsTree())) {
                id = this.onlCgformFieldService.queryTreeChildIds(onlCgformHead, id);
            }

            if (id.indexOf(",") > 0) {
                if (onlCgformHead.getTableType() == 2) {
                    this.onlCgformFieldService.deleteAutoListMainAndSub(onlCgformHead, id);
                } else {
                    String tableName = onlCgformHead.getTableName();
                    this.onlCgformFieldService.deleteAutoListById(tableName, id);
                }
            } else {
                this.onlCgformHeadService.deleteOneTableInfo(code, id);
            }
        } catch (Exception var5) {
            return Result.error("删除失败");
        }

        return Result.ok("删除成功!");
    }

    @GetMapping({"/form/{code}/{id}"})
    public Result<?> formByCodeAndId(@PathVariable("code") String var1, @PathVariable("id") String var2) {
        try {
            Map var3 = this.onlCgformHeadService.queryManyFormData(var1, var2);
            return Result.ok(DbSelectUtils.b(var3));
        } catch (Exception var4) {
            log.error("Online表单查询异常：" + var4.getMessage(), var4);
            return Result.error("查询失败，" + var4.getMessage());
        }
    }

    @OnlineAuth("form")
    @PutMapping({"/form/{code}"})
    public Result<?> putFormByCode(@PathVariable("code") String var1, @RequestBody JSONObject var2) {
        try {
            this.onlCgformHeadService.editManyFormData(var1, var2);
        } catch (Exception var4) {
            log.error("OnlCgformApiController.formEdit()发生异常：" + var4.getMessage(), var4);
            return Result.error("修改失败，" + DbSelectUtils.a(var4));
        }
        return Result.ok("修改成功！");
    }

    private void a(OnlCgformHead onlCgformHead, Map<String, Object> map) throws BusinessException {
        List<Map<String, Object>> list = (List)map.get("records");
        this.onlCgformHeadService.executeEnhanceList(onlCgformHead, "query", list);
    }

    @GetMapping({"/downGenerateCode"})
    public void downGenerateCode(@RequestParam("fileList") List<String> var1, HttpServletRequest var2, HttpServletResponse var3) {
        List var4 = (List)var1.stream().filter((var0) -> {
            return var0.indexOf("src/main/java") == -1 && var0.indexOf("src%5Cmain%5Cjava") == -1;
        }).collect(Collectors.toList());
        if (var1 != null && (var4 == null || var4.size() <= 0)) {
            String var5 = "生成代码_" + System.currentTimeMillis() + ".zip";
            final String var6 = "/opt/temp/codegenerate/" + var5;
            File var7 = downloadUtils.a(var1, var6);
            if (var7.exists()) {
                var3.setContentType("application/force-download");
                var3.addHeader("Content-Disposition", "attachment;fileName=" + var5);
                byte[] var8 = new byte[1024];
                FileInputStream var9 = null;
                BufferedInputStream var10 = null;

                try {
                    var9 = new FileInputStream(var7);
                    var10 = new BufferedInputStream(var9);
                    ServletOutputStream var11 = var3.getOutputStream();

                    for(int var12 = var10.read(var8); var12 != -1; var12 = var10.read(var8)) {
                        var11.write(var8, 0, var12);
                    }
                } catch (Exception var25) {
                    var25.printStackTrace();
                } finally {
                    if (var10 != null) {
                        try {
                            var10.close();
                        } catch (IOException var24) {
                            var24.printStackTrace();
                        }
                    }

                    if (var9 != null) {
                        try {
                            var9.close();
                        } catch (IOException var23) {
                            var23.printStackTrace();
                        }
                    }

                    class NamelessClass_1 extends Thread {
                        NamelessClass_1() {
                        }

                        public void run() {
                            try {
                                Thread.sleep(10000L);
                                FileUtil.del(var6);
                            } catch (InterruptedException var2) {
                                var2.printStackTrace();
                            }

                        }
                    }

                    (new NamelessClass_1()).start();
                }
            }

        } else {
            log.error(" fileList 不合法！！！", var1);
        }
    }
}
