package webapi.onlineController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import commons.api.vo.Result;
import commons.util.oConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smartcode.auth.entity.OnlAuthData;
import smartcode.auth.entity.OnlAuthPage;
import smartcode.auth.entity.OnlAuthRelation;
import smartcode.auth.service.OnlAuthDataService;
import smartcode.auth.service.OnlAuthPageService;
import smartcode.auth.service.OnlAuthRelationService;
import smartcode.auth.vo.AuthColumnVO;
import smartcode.auth.vo.AuthPageVO;
import smartcode.form.entity.OnlCgformButton;
import smartcode.form.entity.OnlCgformField;
import smartcode.form.service.OnlCgformButtonService;
import smartcode.form.service.OnlCgformFieldService;

import java.util.*;

/**
 * @Author: LiuHongYan
 * @Date: 2020/10/12 09:42
 * @Description: 代码生成权限设置
 **/
@RestController("onlCgformAuthController")
@RequestMapping({"/online/cgform/api"})
public class OnlCgformAuthController {
    private static final Logger logger = LoggerFactory.getLogger(OnlCgformAuthController.class);
    @Autowired
    private OnlCgformFieldService onlCgformFieldService;
    @Autowired
    private OnlAuthDataService onlAuthDataService;
    @Autowired
    private OnlAuthPageService onlAuthPageService;
    @Autowired
    private OnlCgformButtonService onlCgformButtonService;
    @Autowired
    private OnlAuthRelationService onlAuthRelationService;

    @GetMapping({"/authData/{cgformId}"})
    public Result<List<OnlAuthData>> getOnlAuthDataList(@PathVariable("cgformId") String cgformId) {
        Result result = new Result();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("cgform_id", cgformId);
        List list = this.onlAuthDataService.list(queryWrapper);
        result.setResult(list);
        result.setSuccess(true);
        return result;
    }

    @PostMapping({"/authData"})
    public Result<OnlAuthData> postAuthData(@RequestBody OnlAuthData onlAuthData) {
        Result result = new Result();

        try {
            this.onlAuthDataService.save(onlAuthData);
            result.success("添加成功！");
        } catch (Exception var4) {
            logger.error(var4.getMessage(), var4);
            result.error500("操作失败");
        }
        return result;
    }

    @PutMapping({"/authData"})
    public Result<OnlAuthData> putAuthData(@RequestBody OnlAuthData authData) {
        this.onlAuthDataService.updateById(authData);
        return new Result();
    }

    @DeleteMapping({"/authData/{id}"})
    public Result<?> deleteByAuth(@PathVariable("id") String id) {
        this.onlAuthDataService.deleteOne(id);
        return Result.ok("删除成功!");
    }

    @GetMapping({"/authButton/{cgformId}"})
    public Result<Map<String, Object>> getByOnlAuthByCgformId(@PathVariable("cgformId") String cgformId) {
        Result result = new Result();
        LambdaQueryWrapper<OnlCgformButton> query = new LambdaQueryWrapper<>();
        LambdaQueryWrapper lambda = query.eq(OnlCgformButton::getCgformHeadId, cgformId).eq(OnlCgformButton::getButtonStatus, 1).select(OnlCgformButton::getButtonCode, OnlCgformButton::getButtonName, OnlCgformButton::getButtonStyle);
        List buttonList = this.onlCgformButtonService.list(lambda);
        LambdaQueryWrapper<OnlAuthPage> queryByAuthPage = new LambdaQueryWrapper<>();
        LambdaQueryWrapper wrapper = queryByAuthPage.eq(OnlAuthPage::getCgformId, cgformId).eq(OnlAuthPage::getType, 2);
        List authList = this.onlAuthPageService.list(wrapper);
        HashMap map = new HashMap();
        map.put("buttonList", buttonList);
        map.put("authList", authList);
        result.setResult(map);
        result.setSuccess(true);
        return result;
    }

    @PostMapping({"/authButton"})
    public Result<OnlAuthPage> getOnlAuthPage(@RequestBody OnlAuthPage onlAuthPage) {
        Result result = new Result();
        try {
            String id = onlAuthPage.getId();
            boolean boo = false;
            if (oConvertUtils.isNotEmpty(id)) {
                OnlAuthPage authPage = this.onlAuthPageService.getById(id);
                if (authPage != null) {
                    boo = true;
                    authPage.setStatus(1);
                    this.onlAuthPageService.updateById(authPage);
                }
            }

            if (!boo) {
                onlAuthPage.setStatus(1);
                this.onlAuthPageService.save(onlAuthPage);
            }

            result.setResult(onlAuthPage);
            result.success("操作成功！");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    @PutMapping({"/authButton/{id}"})
    public Result<?> putById(@PathVariable("id") String id) {
        LambdaUpdateWrapper lambdaUpdateWrapper = (new UpdateWrapper<OnlAuthPage>()).lambda().eq(OnlAuthPage::getId, id).set(OnlAuthPage::getStatus, 0);
        this.onlAuthPageService.update(lambdaUpdateWrapper);
        return Result.ok("操作成功");
    }

    @GetMapping({"/authColumn/{cgformId}"})
    public Result<List<AuthColumnVO>> getAuthColumnVOByCgformId(@PathVariable("cgformId") String cgformId) {
        Result result = new Result();
        LambdaQueryWrapper<OnlCgformField> queryOnlCgformField = new LambdaQueryWrapper();
        queryOnlCgformField.eq(OnlCgformField::getCgformHeadId, cgformId);
        queryOnlCgformField.orderByAsc(OnlCgformField::getOrderNum);
        List<OnlCgformField> onlCgformFieldList = this.onlCgformFieldService.list(queryOnlCgformField);
        if (onlCgformFieldList == null || onlCgformFieldList.size() == 0) {
            Result.error("未找到对应字段信息!");
        }

        LambdaQueryWrapper queryOnlAuthPage = (new LambdaQueryWrapper<OnlAuthPage>()).eq(OnlAuthPage::getCgformId, cgformId).eq(OnlAuthPage::getType, 1);
        List<OnlAuthPage> onlAuthPageList = this.onlAuthPageService.list(queryOnlAuthPage);
        List authColumnVOList = new ArrayList<AuthColumnVO>();
        Iterator iterator = onlCgformFieldList.iterator();

        while (iterator.hasNext()) {
            OnlCgformField onlCgformField = (OnlCgformField) iterator.next();
            AuthColumnVO authColumnVO = new AuthColumnVO(onlCgformField);
            Integer integer = 0;
            boolean boo1 = false;
            boolean boo2 = false;
            boolean boo3 = false;

            for (OnlAuthPage onlAuthPage : onlAuthPageList) {
                if (onlCgformField.getDbFieldName().equals(onlAuthPage.getCode())) {
                    integer = onlAuthPage.getStatus();
                    if (onlAuthPage.getPage() == 3 && onlAuthPage.getControl() == 5) {
                        boo1 = true;
                    }
                    if (onlAuthPage.getPage() == 5) {
                        if (onlAuthPage.getControl() == 5) {
                            boo2 = true;
                        } else if (onlAuthPage.getControl() == 3) {
                            boo3 = true;
                        }
                    }
                }
            }
            authColumnVO.setStatus(integer);
            authColumnVO.setListShow(boo1);
            authColumnVO.setFormShow(boo2);
            authColumnVO.setFormEditable(boo3);
            authColumnVOList.add(authColumnVO);
        }

        result.setResult(authColumnVOList);
        Result.ok("加载字段权限数据完成");
        return result;
    }

    @PutMapping({"/authColumn"})
    public Result<?> putOnlAuthPage(@RequestBody AuthColumnVO authColumnVO) {
        Result result = new Result();

        try {
            if (authColumnVO.getStatus() == 1) {
                this.onlAuthPageService.enableAuthColumn(authColumnVO);
            } else {
                this.onlAuthPageService.disableAuthColumn(authColumnVO);
            }
            result.success("操作成功！");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.error500("操作失败");
        }

        return result;
    }

    @PostMapping({"/authColumn"})
    public Result<?> postByAuthColumn(@RequestBody AuthColumnVO authColumnVO) {
        Result result = new Result();

        try {
            this.onlAuthPageService.switchAuthColumn(authColumnVO);
            result.success("操作成功！");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    @GetMapping({"/authPage/{cgformId}/{type}"})
    public Result<List<AuthPageVO>> getAuthPageVOByCgformIdAndType(@PathVariable("cgformId") String cgformId, @PathVariable("type") int type) {
        Result result = new Result();
        List list = this.onlAuthPageService.queryAuthByFormId(cgformId, type);
        result.setResult(list);
        result.setSuccess(true);
        return result;
    }

    @GetMapping({"/validAuthData/{cgformId}"})
    public Result<List<OnlAuthData>> getOnlAuthDataByCgformId(@PathVariable("cgformId") String cgformId) {
        Result result = new Result();
        LambdaQueryWrapper lambdaQueryWrapper = (new LambdaQueryWrapper<OnlAuthData>()).eq(OnlAuthData::getCgformId, cgformId).eq(OnlAuthData::getStatus, 1).select(OnlAuthData::getId, OnlAuthData::getRuleName);
        List list = this.onlAuthDataService.list(lambdaQueryWrapper);
        result.setResult(list);
        result.setSuccess(true);
        return result;
    }

    @GetMapping({"/roleAuth"})
    public Result<List<OnlAuthRelation>> getByRoleAuth(@RequestParam("roleId") String roleId, @RequestParam("cgformId") String cgformId, @RequestParam("type") Integer type) {
        Result result = new Result();
        LambdaQueryWrapper lambdaQueryWrapper = (new LambdaQueryWrapper<OnlAuthRelation>()).eq(OnlAuthRelation::getRoleId, roleId).eq(OnlAuthRelation::getCgformId, cgformId).eq(OnlAuthRelation::getType, type).select(OnlAuthRelation::getAuthId);
        List list = this.onlAuthRelationService.list(lambdaQueryWrapper);
        result.setResult(list);
        result.setSuccess(true);
        return result;
    }

    @PostMapping({"/roleColumnAuth/{roleId}/{cgformId}"})
    public Result<?> postByRoleColumnAuth(@PathVariable("roleId") String roleId, @PathVariable("cgformId") String cgformId, @RequestBody JSONObject jsonObject) {
        Result result = new Result();
        JSONArray jsonArray = jsonObject.getJSONArray("authId");
        List var6 = jsonArray.toJavaList(String.class);
        this.onlAuthRelationService.saveRoleAuth(roleId, cgformId, 1, var6);
        result.setSuccess(true);
        return result;
    }

    @PostMapping({"/roleButtonAuth/{roleId}/{cgformId}"})
    public Result<?> postByRoleButtonAuth(@PathVariable("roleId") String roleId, @PathVariable("cgformId") String cgformId, @RequestBody JSONObject jsonObject) {
        Result result= new Result();
        JSONArray jsonArray = jsonObject.getJSONArray("authId");
        List list = jsonArray.toJavaList(String.class);
        this.onlAuthRelationService.saveRoleAuth(roleId, cgformId, 2, list);
        result.setSuccess(true);
        return result;
    }

    @PostMapping({"/roleDataAuth/{roleId}/{cgformId}"})
    public Result<?> postByRoleDataAuth(@PathVariable("roleId") String roleId, @PathVariable("cgformId") String cgformId, @RequestBody JSONObject jsonObject) {
        Result result = new Result();
        JSONArray jsonArray = jsonObject.getJSONArray("authId");
        List list = jsonArray.toJavaList(String.class);
        this.onlAuthRelationService.saveRoleAuth(roleId, cgformId, 3, list);
        result.setSuccess(true);
        return result;
    }
}
