package smartcode.form.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import commons.system.api.ISysBaseAPI;
import commons.util.oConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartcode.config.query.QuerySqlConfig;
import smartcode.config.utils.EnhanceJsUtil;
import smartcode.form.entity.OnlCgformButton;
import smartcode.form.entity.OnlCgformEnhanceJs;
import smartcode.form.entity.OnlCgformField;
import smartcode.form.entity.OnlCgformHead;
import smartcode.form.model.*;
import smartcode.form.service.OnlCgformFieldService;
import smartcode.form.service.OnlCgformHeadService;
import smartcode.form.service.OnlineService;
import smartcode.form.utils.DbSelectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/27 15:53
 * @Description: zdit.zdboot.smartcode.online.service.impl
 **/

@Service
public class OnlineServiceImpl implements OnlineService {

    @Autowired
    private OnlCgformFieldService onlCgformFieldService;
    @Autowired
    private OnlCgformHeadService onlCgformHeadService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Override
    public JSONObject queryOnlineFormObj(OnlCgformHead head, OnlCgformEnhanceJs onlCgformEnhanceJs) {
        JSONObject var3 = new JSONObject();
        String var4 = head.getId();
        List<OnlCgformField> var5 = this.onlCgformFieldService.queryAvailableFields(head.getId(), head.getTableName(), (String)null, false);
        List<String> var6 = this.onlCgformFieldService.queryDisabledFields(head.getTableName());
        EnhanceJsUtil.a(onlCgformEnhanceJs, head.getTableName(), var5);
        EModel var7 = null;
        if ("Y".equals(head.getIsTree())) {
            var7 = new EModel();
            var7.setCodeField("id");
            var7.setFieldName(head.getTreeParentIdField());
            var7.setPidField(head.getTreeParentIdField());
            var7.setPidValue("0");
            var7.setHsaChildField(head.getTreeIdField());
            var7.setTableName(DbSelectUtils.regex(head.getTableName()));
            var7.setTextField(head.getTreeFieldname());
        }

        JSONObject var8 = DbSelectUtils.a(var5, var6, var7);
        var8.put("table", head.getTableName());
        var3.put("schema", var8);
        var3.put("head", head);
        List<OnlCgformButton> var9 = this.onlCgformHeadService.queryButtonList(var4, false);
        if (var9 != null && var9.size() > 0) {
            var3.put("cgButtonList", var9);
        }

        if (onlCgformEnhanceJs != null && oConvertUtils.isNotEmpty(onlCgformEnhanceJs.getCgJs())) {
            String var10 = EnhanceJsUtil.c(onlCgformEnhanceJs.getCgJs(), var9);
            onlCgformEnhanceJs.setCgJs(var10);
            var3.put("enhanceJs", EnhanceJsUtil.a(onlCgformEnhanceJs.getCgJs()));
        }

        return var3;
    }

    @Override
    public JSONObject queryOnlineFormObj(OnlCgformHead var1) {
        return null;
    }

    @Override
    public BModel queryOnlineConfig(OnlCgformHead head) {
        String headId = head.getId();
        List<OnlCgformField> var3 = this.getOnlCgformFieldList(headId);
        List<String> var4 = this.onlCgformFieldService.selectOnlineHideColumns(head.getTableName());
        ArrayList var5 = new ArrayList();
        HashMap var6 = new HashMap();
        ArrayList var7 = new ArrayList();
        ArrayList var8 = new ArrayList();
        ArrayList var9 = new ArrayList();
        Iterator var10 = var3.iterator();

        String var14;
        while(var10.hasNext()) {
            OnlCgformField var11 = (OnlCgformField)var10.next();
            String var12 = var11.getDbFieldName();
            String var13 = var11.getMainTable();
            var14 = var11.getMainField();
            if (oConvertUtils.isNotEmpty(var14) && oConvertUtils.isNotEmpty(var13)) {
                ForeignKey var15 = new ForeignKey(var12, var14);
                var8.add(var15);
            }

            if (1 == var11.getIsShowList() && !"id".equals(var12) && !var4.contains(var12) && !var9.contains(var12)) {
                OnlColumn var28 = new OnlColumn(var11.getDbFieldTxt(), var12);
                String var16 = var11.getDictField();
                String var17 = var11.getFieldShowType();
                if (oConvertUtils.isNotEmpty(var16) && !"popup".equals(var17)) {
                    Object var18 = new ArrayList();
                    if (oConvertUtils.isNotEmpty(var11.getDictTable())) {
                        var18 = this.sysBaseAPI.queryTableDictItemsByCode(var11.getDictTable(), var11.getDictText(), var16);
                    } else if (oConvertUtils.isNotEmpty(var11.getDictField())) {
                        var18 = this.sysBaseAPI.queryDictItemsByCode(var16);
                    }

                    var6.put(var12, var18);
                    var28.setCustomRender(var12);
                }

                List var29;
                if ("switch".equals(var17)) {
                    var29 = DbSelectUtils.a(var11);
                    var6.put(var12, var29);
                    var28.setCustomRender(var12);
                }

                List var20;
                String var31;
                if ("link_down".equals(var17)) {
                    var31 = var11.getDictTable();
                    QuerySqlConfig var19 = (QuerySqlConfig) JSONObject.parseObject(var31, QuerySqlConfig.class);
                    var20 = this.sysBaseAPI.queryTableDictItemsByCode(var19.getTable(), var19.getTxt(), var19.getKey());
                    var6.put(var12, var20);
                    var28.setCustomRender(var12);
                    var5.add(var28);
                    String var21 = var19.getLinkField();
                    this.a(var3, var9, var5, var12, var21);
                }

                if ("sel_tree".equals(var17)) {
                    String[] var33 = var11.getDictText().split(",");
                    List var30 = this.sysBaseAPI.queryTableDictItemsByCode(var11.getDictTable(), var33[2], var33[0]);
                    var6.put(var12, var30);
                    var28.setCustomRender(var12);
                }

                if ("cat_tree".equals(var17)) {
                    var31 = var11.getDictText();
                    if (oConvertUtils.isEmpty(var31)) {
                        String var32 = DbSelectUtils.e(var11.getDictField());
                        var20 = this.sysBaseAPI.queryFilterTableDictInfo("SYS_CATEGORY", "NAME", "ID", var32);
                        var6.put(var12, var20);
                        var28.setCustomRender(var12);
                    } else {
                        var28.setCustomRender("_replace_text_" + var31);
                    }
                }

                if ("sel_depart".equals(var17)) {
                    var29 = this.sysBaseAPI.queryAllDepartBackDictModel();
                    var6.put(var12, var29);
                    var28.setCustomRender(var12);
                }

                if ("sel_user".equals(var11.getFieldShowType())) {
                    var29 = this.sysBaseAPI.queryTableDictItemsByCode("SYS_USER", "REALNAME", "USERNAME");
                    var6.put(var12, var29);
                    var28.setCustomRender(var12);
                }

                if (var17.indexOf("file") >= 0) {
                    var28.setScopedSlots(new DModel("fileSlot"));
                } else if (var17.indexOf("image") >= 0) {
                    var28.setScopedSlots(new DModel("imgSlot"));
                } else if (var17.indexOf("editor") >= 0) {
                    var28.setScopedSlots(new DModel("htmlSlot"));
                } else if (var17.equals("date")) {
                    var28.setScopedSlots(new DModel("dateSlot"));
                } else if (var17.equals("pca")) {
                    var28.setScopedSlots(new DModel("pcaSlot"));
                }

                if (StringUtils.isNotBlank(var11.getFieldHref())) {
                    var31 = "fieldHref_" + var12;
                    var28.setHrefSlotName(var31);
                    var7.add(new HrefSlots(var31, var11.getFieldHref()));
                }

                if ("1".equals(var11.getSortFlag())) {
                    var28.setSorter(true);
                }

                if (!"link_down".equals(var17)) {
                    var5.add(var28);
                }
            }
        }

        BModel var22 = new BModel();
        var22.setCode(headId);
        var22.setTableType(head.getTableType());
        var22.setFormTemplate(head.getFormTemplate());
        var22.setDescription(head.getTableTxt());
        var22.setCurrentTableName(head.getTableName());
        var22.setPaginationFlag(head.getIsPage());
        var22.setCheckboxFlag(head.getIsCheckbox());
        var22.setScrollFlag(head.getScroll());
        var22.setColumns(var5);
        var22.setDictOptions(var6);
        var22.setFieldHrefSlots(var7);
        var22.setForeignKeys(var8);
        var22.setHideColumns(var4);
        List var23 = this.onlCgformHeadService.queryButtonList(headId, true);
        ArrayList var24 = new ArrayList();
        Iterator var25 = var23.iterator();

        while(var25.hasNext()) {
            OnlCgformButton var27 = (OnlCgformButton)var25.next();
            if (!var4.contains(var27.getButtonCode())) {
                var24.add(var27);
            }
        }

        var22.setCgButtonList(var24);
        OnlCgformEnhanceJs var26 = this.onlCgformHeadService.queryEnhanceJs(headId, "list");
        if (var26 != null && oConvertUtils.isNotEmpty(var26.getCgJs())) {
            var14 = EnhanceJsUtil.b(var26.getCgJs(), var23);
            var22.setEnhanceJs(var14);
        }

        if ("Y".equals(head.getIsTree())) {
            var22.setPidField(head.getTreeParentIdField());
            var22.setHasChildrenField(head.getTreeIdField());
            var22.setTextField(head.getTreeFieldname());
        }

        return var22;
    }

    private void a(List<OnlCgformField> var1, List<String> var2, List<OnlColumn> var3, String var4, String var5) {
        if (oConvertUtils.isNotEmpty(var5)) {
            String[] var6 = var5.split(",");
            String[] var7 = var6;
            int var8 = var6.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                String var10 = var7[var9];
                Iterator var11 = var1.iterator();

                while(var11.hasNext()) {
                    OnlCgformField var12 = (OnlCgformField)var11.next();
                    String var13 = var12.getDbFieldName();
                    if (1 == var12.getIsShowList() && var10.equals(var13)) {
                        var2.add(var10);
                        OnlColumn var14 = new OnlColumn(var12.getDbFieldTxt(), var13);
                        var14.setCustomRender(var4);
                        var3.add(var14);
                        break;
                    }
                }
            }
        }
    }

    private List<OnlCgformField> getOnlCgformFieldList(String headId) {
        QueryWrapper queryWrapper = new QueryWrapper<OnlCgformField>();
        queryWrapper.eq("cgform_head_id", headId);
        queryWrapper.orderByAsc("order_num");
        return this.onlCgformFieldService.list(queryWrapper);
    }
}
