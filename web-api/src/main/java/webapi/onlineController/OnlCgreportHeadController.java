package webapi.onlineController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.auth.vo.DynamicDataSourceModel;
import commons.system.api.ISysBaseAPI;
import commons.util.SqlInjectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smartcode.form.entity.OnlCgreportHead;
import smartcode.form.entity.OnlCgreportItem;
import smartcode.form.entity.OnlCgreportModel;
import smartcode.form.entity.OnlCgreportParam;
import smartcode.form.service.OnlCgreportHeadService;
import smartcode.form.service.OnlCgreportItemService;
import smartcode.form.service.OnlCgreportParamService;
import smartcode.form.utils.DbSelectUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/21 15:32
 * @Description: zdit.zdboot.auth.online.controller
 **/
@RestController("onlCgreportHeadController")
@RequestMapping({"/online/cgreport/head"})
public class OnlCgreportHeadController {

    private static final Logger logger = LoggerFactory.getLogger(OnlCgreportHeadController.class);
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private OnlCgreportHeadService onlCgreportHeadService;
    @Autowired
    private OnlCgreportParamService onlCgreportParamService;
    @Autowired
    private OnlCgreportItemService onlCgreportItemService;


    /**
     * online表单列表页
     * @param onlCgreportHead
     * @param paramInteger1
     * @param paramInteger2
     * @param paramHttpServletRequest
     * @return
     */
    @GetMapping({"/list"})
    public Result getOnlineList(OnlCgreportHead onlCgreportHead,
                                @RequestParam(name = "pageNo", defaultValue = "1") Integer paramInteger1,
                                @RequestParam(name = "pageSize", defaultValue = "10") Integer paramInteger2,
                                HttpServletRequest paramHttpServletRequest) {
        Result result = new Result();
        QueryWrapper queryWrapper = QueryGenerator.initQueryWrapper(onlCgreportHead, paramHttpServletRequest.getParameterMap());
        Page page = new Page(paramInteger1.intValue(), paramInteger2.intValue());
        IPage iPage = onlCgreportHeadService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(iPage);
        return result;
    }

    /**
     * sql解析
     * @param sql
     * @param dbKey
     * @return
     */
    @GetMapping({"/parseSql"})
    public Result<?> parseSql(@RequestParam(name = "sql") String sql, @RequestParam(name = "dbKey",required = false) String dbKey) {
        if (StringUtils.isNotBlank(dbKey)) {
            DynamicDataSourceModel model = this.sysBaseAPI.getDynamicDbSourceByCode(dbKey);
            if (model == null) {
                return Result.error("数据源不存在");
            }
        }

        HashMap map = new HashMap();
        ArrayList list1 = new ArrayList();
        ArrayList list2 = new ArrayList();
        List list3;
        List list4;

        try {
            logger.info("Online报表，sql解析：" + sql);
            this.sysBaseAPI.addLog("Online报表，sql解析：" + sql, 2, 2);
            SqlInjectionUtil.specialFilterContentForOnlineReport(sql);
            list3 = this.onlCgreportHeadService.getSqlFields(sql, dbKey);
            list4 = this.onlCgreportHeadService.getSqlParams(sql);
            int i = 1;

            String str;
            Iterator iterator;
            for(iterator = list3.iterator(); iterator.hasNext(); i++) {
                str = (String)iterator.next();
                OnlCgreportItem onlCgreportItem = new OnlCgreportItem();
                onlCgreportItem.setFieldName(str.toLowerCase());
                onlCgreportItem.setFieldTxt(str);
                onlCgreportItem.setIsShow(1);
                onlCgreportItem.setOrderNum(i);
                onlCgreportItem.setId(DbSelectUtils.a());
                onlCgreportItem.setFieldType("String");
                list1.add(onlCgreportItem);
            }

            iterator = list4.iterator();

            while(iterator.hasNext()) {
                str = (String)iterator.next();
                OnlCgreportParam onlCgreportParam = new OnlCgreportParam();
                onlCgreportParam.setParamName(str);
                onlCgreportParam.setParamTxt(str);
                list2.add(onlCgreportParam);
            }

            map.put("fields", list1);
            map.put("params", list2);
            return Result.ok(map);
        } catch (Exception e) {
            String str = "解析失败，";
            if ( e.getMessage().indexOf("Connection refused: connect") != -1) {
                str = str + "数据源连接失败.";
            } else if (e.getMessage().indexOf("值可能存在SQL注入风险") != -1) {
                str = str + "SQL可能存在SQL注入风险.";
            } else if (e.getMessage().indexOf("该报表sql没有数据") != -1) {
                str = str + "报表sql查询数据为空，无法解析字段.";
            } else if (e.getMessage().indexOf("SqlServer不支持SQL内排序") != -1) {
                str = str + "SqlServer不支持SQL内排序.";
            } else {
                str = str + "SQL语法错误.";
            }

            return Result.error(str);
        }
    }

    /**
     * 新增
     * @param onlCgreportModel
     * @return
     */
    @PostMapping({"/add"})
    public Result<?> add(@RequestBody OnlCgreportModel onlCgreportModel) {
        Result result = new Result();

        try {
            String str = DbSelectUtils.a();
            OnlCgreportHead onlCgreportHead = onlCgreportModel.getHead();
            List<OnlCgreportParam> onlCgreportParamList = onlCgreportModel.getParams();
            List<OnlCgreportItem> onlCgreportItemList = onlCgreportModel.getItems();
            onlCgreportHead.setId(str);
            Iterator iterator = onlCgreportParamList.iterator();

            while(iterator.hasNext()) {
                OnlCgreportParam onlCgreportParam = (OnlCgreportParam)iterator.next();
                onlCgreportParam.setCgrheadId(str);
            }

            iterator = onlCgreportItemList.iterator();

            while(iterator.hasNext()) {
                OnlCgreportItem onlCgreportItem = (OnlCgreportItem)iterator.next();
                onlCgreportItem.setFieldName(onlCgreportItem.getFieldName().trim().toLowerCase());
                onlCgreportItem.setCgrheadId(str);
            }

            this.onlCgreportHeadService.save(onlCgreportHead);
            this.onlCgreportParamService.saveBatch(onlCgreportParamList);
            this.onlCgreportItemService.saveBatch(onlCgreportItemList);
            result.success("添加成功！");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.error500("操作失败");
        }

        return result;
    }

    /**
     * 修改全部
     * @param onlCgreportModel
     * @return
     */
    @PutMapping({"/editAll"})
    public Result<?> editAll(@RequestBody OnlCgreportModel onlCgreportModel) {
        try {
            return this.onlCgreportHeadService.editAll(onlCgreportModel);
        } catch (Exception var3) {
            logger.error(var3.getMessage(), var3);
            return Result.error("操作失败");
        }
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping({"/delete"})
    public Result<?> delete(@RequestParam(name = "id",required = true) String id) {
        return this.onlCgreportHeadService.delete(id);
    }

    /**
     * 批量删除
      * @param ids
     * @return
     */
    @DeleteMapping({"/deleteBatch"})
    public Result<?> deleteBatch(@RequestParam(name = "ids",required = true) String ids) {
        return this.onlCgreportHeadService.bathDelete(ids.split(","));
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping({"/queryById"})
    public Result<OnlCgreportHead> queryById(@RequestParam(name = "id",required = true) String id) {
        Result result = new Result();
        OnlCgreportHead onlCgreportHead = this.onlCgreportHeadService.getById(id);
        result.setResult(onlCgreportHead);
        return result;
    }

}
