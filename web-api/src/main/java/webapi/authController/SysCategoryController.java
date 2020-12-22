package webapi.authController;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import auth.discard.model.TreeSelectModel;
import auth.domain.category.service.ISysCategoryService;
import auth.entity.Category;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.auth.vo.DictModel;
import commons.auth.vo.LoginUser;
import commons.util.oConvertUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 分类字典
 * @Author: jeecg-boot
 * @Date: 2019-05-29
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/category")
@Slf4j
public class SysCategoryController {

    private final ISysCategoryService sysCategoryService;

    @Autowired
    public SysCategoryController(ISysCategoryService sysCategoryService) {
        this.sysCategoryService = sysCategoryService;
    }

    /**
     * 分页列表查询
     *
     * @param category
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping(value = "/rootList")
    public Result<IPage<Category>> queryPageList(Category category,
                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 HttpServletRequest req) {
        if (oConvertUtils.isEmpty(category.getPid())) {
            category.setPid("0");
        }
        Result<IPage<Category>> result = new Result<IPage<Category>>();

        //--author:os_chengtgen---date:20190804 -----for: 分类字典页面显示错误,issues:377--------start
        //QueryWrapper<SysCategory> queryWrapper = QueryGenerator.initQueryWrapper(sysCategory, req.getParameterMap());
        QueryWrapper<Category> queryWrapper = new QueryWrapper<Category>();
        queryWrapper.eq("pid", category.getPid());
        //--author:os_chengtgen---date:20190804 -----for: 分类字典页面显示错误,issues:377--------end

        Page<Category> page = new Page<Category>(pageNo, pageSize);
        IPage<Category> pageList = sysCategoryService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @GetMapping(value = "/childList")
    public Result<List<Category>> queryPageList(Category category, HttpServletRequest req) {
        Result<List<Category>> result = new Result<List<Category>>();
        QueryWrapper<Category> queryWrapper = QueryGenerator.initQueryWrapper(category, req.getParameterMap());
        List<Category> list = sysCategoryService.list(queryWrapper);
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }


    /**
     * 添加
     *
     * @param category
     * @return
     */
    @PostMapping(value = "/add")
    public Result<Category> add(@RequestBody Category category) {
        Result<Category> result = new Result<Category>();
        try {
            sysCategoryService.addSysCategory(category);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param category
     * @return
     */
    @PutMapping(value = "/edit")
    public Result<Category> edit(@RequestBody Category category) {
        Result<Category> result = new Result<Category>();
        Category categoryEntity = sysCategoryService.getById(category.getId());
        if (categoryEntity == null) {
            result.error500("未找到对应实体");
        } else {
            sysCategoryService.updateSysCategory(category);
            result.success("修改成功!");
        }
        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/delete")
    public Result<Category> delete(@RequestParam(name = "id", required = true) String id) {
        Result<Category> result = new Result<Category>();
        Category category = sysCategoryService.getById(id);
        if (category == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysCategoryService.removeById(id);
            if (ok) {
                result.success("删除成功!");
            }
        }

        return result;
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/deleteBatch")
    public Result<Category> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<Category> result = new Result<Category>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.sysCategoryService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/queryById")
    public Result<Category> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<Category> result = new Result<Category>();
        Category category = sysCategoryService.getById(id);
        if (category == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(category);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Category category) {
//     // Step.1 组装查询条件查询数据
//     QueryWrapper<Category> queryWrapper = QueryGenerator.initQueryWrapper(category, request.getParameterMap());
//     List<Category> pageList = sysCategoryService.list(queryWrapper);
//     // Step.2 AutoPoi 导出Excel
//     ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
//     // 过滤选中数据
//     String selections = request.getParameter("selections");
//     if(oConvertUtils.isEmpty(selections)) {
//         mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
//     }else {
//         List<String> selectionList = Arrays.asList(selections.split(","));
//         List<Category> exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
//         mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
//     }
//     //导出文件名称
//     mv.addObject(NormalExcelConstants.FILE_NAME, "分类字典列表");
//     mv.addObject(NormalExcelConstants.CLASS, Category.class);
//     LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//     mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("分类字典列表数据", "导出人:"+user.getRealname(), "导出信息"));
//     return mv;
        return null;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//     MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//     Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
//     for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
//         MultipartFile file = entity.getValue();// 获取上传文件对象
//         ImportParams params = new ImportParams();
//         params.setTitleRows(2);
//         params.setHeadRows(1);
//         params.setNeedSave(true);
//         try {
//             List<Category> listCategories = ExcelImportUtil.importExcel(file.getInputStream(), Category.class, params);
//            //按照编码长度排序
//             Collections.sort(listCategories);
//             log.info("排序后的list====>", listCategories);
//             for (Category categoryExcel : listCategories) {
//                 String code = categoryExcel.getCode();
//                 if(code.length()>3){
//                     String pCode = categoryExcel.getCode().substring(0,code.length()-3);
//                     log.info("pCode====>",pCode);
//                     String pId=sysCategoryService.queryIdByCode(pCode);
//                     log.info("pId====>",pId);
//                     if(StringUtils.isNotBlank(pId)){
//                         categoryExcel.setPid(pId);
//                     }
//                 }else{
//                     categoryExcel.setPid("0");
//                 }
//                 sysCategoryService.save(categoryExcel);
//             }
//             return Result.ok("文件导入成功！数据行数：" + listCategories.size());
//         } catch (Exception e) {
//             log.error(e.getMessage(), e);
//             return Result.error("文件导入失败："+e.getMessage());
//         } finally {
//             try {
//                 file.getInputStream().close();
//             } catch (IOException e) {
//                 e.printStackTrace();
//             }
//         }
//     }
        return Result.error("文件导入失败！");
    }


    /**
     * 加载单个数据 用于回显
     */
    @RequestMapping(value = "/loadOne", method = RequestMethod.GET)
    public Result<Category> loadOne(@RequestParam(name = "field") String field, @RequestParam(name = "val") String val) {
        Result<Category> result = new Result<Category>();
        try {

            QueryWrapper<Category> query = new QueryWrapper<Category>();
            query.eq(field, val);
            List<Category> ls = this.sysCategoryService.list(query);
            if (ls == null || ls.size() == 0) {
                result.setMessage("查询无果");
                result.setSuccess(false);
            } else if (ls.size() > 1) {
                result.setMessage("查询数据异常,[" + field + "]存在多个值:" + val);
                result.setSuccess(false);
            } else {
                result.setSuccess(true);
                result.setResult(ls.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 加载节点的子数据
     */
    @RequestMapping(value = "/loadTreeChildren", method = RequestMethod.GET)
    public Result<List<TreeSelectModel>> loadTreeChildren(@RequestParam(name = "pid") String pid) {
        Result<List<TreeSelectModel>> result = new Result<List<TreeSelectModel>>();
        try {
            List<TreeSelectModel> ls = this.sysCategoryService.queryListByPid(pid);
            result.setResult(ls);
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 加载一级节点/如果是同步 则所有数据
     */
    @RequestMapping(value = "/loadTreeRoot", method = RequestMethod.GET)
    public Result<List<TreeSelectModel>> loadTreeRoot(@RequestParam(name = "async") Boolean async, @RequestParam(name = "pcode") String pcode) {
        Result<List<TreeSelectModel>> result = new Result<List<TreeSelectModel>>();
        try {
            List<TreeSelectModel> ls = this.sysCategoryService.queryListByCode(pcode);
            if (!async) {
                loadAllCategoryChildren(ls);
            }
            result.setResult(ls);
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 递归求子节点 同步加载用到
     */
    private void loadAllCategoryChildren(List<TreeSelectModel> ls) {
        for (TreeSelectModel tsm : ls) {
            List<TreeSelectModel> temp = this.sysCategoryService.queryListByPid(tsm.getKey());
            if (temp != null && temp.size() > 0) {
                tsm.setChildren(temp);
                loadAllCategoryChildren(temp);
            }
        }
    }

    /**
     * 校验编码
     *
     * @param pid
     * @param code
     * @return
     */
    @GetMapping(value = "/checkCode")
    public Result<?> checkCode(@RequestParam(name = "pid", required = false) String pid, @RequestParam(name = "code", required = false) String code) {
        if (oConvertUtils.isEmpty(code)) {
            return Result.error("错误,类型编码为空!");
        }
        if (oConvertUtils.isEmpty(pid)) {
            return Result.ok();
        }
        Category parent = this.sysCategoryService.getById(pid);
        if (code.startsWith(parent.getCode())) {
            return Result.ok();
        } else {
            return Result.error("编码不符合规范,须以\"" + parent.getCode() + "\"开头!");
        }

    }


    /**
     * 分类字典树控件 加载节点
     *
     * @param pid
     * @param pcode
     * @param condition
     * @return
     */
    @RequestMapping(value = "/loadTreeData", method = RequestMethod.GET)
    public Result<List<TreeSelectModel>> loadDict(@RequestParam(name = "pid", required = false) String pid, @RequestParam(name = "pcode", required = false) String pcode, @RequestParam(name = "condition", required = false) String condition) {
        Result<List<TreeSelectModel>> result = new Result<List<TreeSelectModel>>();
        //pid如果传值了 就忽略pcode的作用
        if (oConvertUtils.isEmpty(pid)) {
            if (oConvertUtils.isEmpty(pcode)) {
                result.setSuccess(false);
                result.setMessage("加载分类字典树参数有误.[null]!");
                return result;
            } else {
                if (ISysCategoryService.ROOT_PID_VALUE.equals(pcode)) {
                    pid = ISysCategoryService.ROOT_PID_VALUE;
                } else {
                    pid = this.sysCategoryService.queryIdByCode(pcode);
                }
                if (oConvertUtils.isEmpty(pid)) {
                    result.setSuccess(false);
                    result.setMessage("加载分类字典树参数有误.[code]!");
                    return result;
                }
            }
        }
        Map<String, String> query = null;
        if (oConvertUtils.isNotEmpty(condition)) {
            query = JSON.parseObject(condition, Map.class);
        }
        List<TreeSelectModel> ls = sysCategoryService.queryListByPid(pid, query);
        result.setSuccess(true);
        result.setResult(ls);
        return result;
    }

    /**
     * 分类字典控件数据回显[表单页面]
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/loadDictItem", method = RequestMethod.GET)
    public Result<List<String>> loadDictItem(@RequestParam(name = "ids") String ids) {
        Result<List<String>> result = new Result<>();
        // 非空判断
        if (StringUtils.isBlank(ids)) {
            result.setSuccess(false);
            result.setMessage("ids 不能为空");
            return result;
        }
        String[] idArray = ids.split(",");
        LambdaQueryWrapper<Category> query = new LambdaQueryWrapper<>();
        query.in(Category::getId, Arrays.asList(idArray));
        // 查询数据
        List<Category> list = this.sysCategoryService.list(query);
        // 取出name并返回
        List<String> textList = list.stream().map(Category::getName).collect(Collectors.toList());
        result.setSuccess(true);
        result.setResult(textList);
        return result;
    }

    /**
     * [列表页面]加载分类字典数据 用于值的替换
     *
     * @param code
     * @return
     */
    @RequestMapping(value = "/loadAllData", method = RequestMethod.GET)
    public Result<List<DictModel>> loadAllData(@RequestParam(name = "code", required = true) String code) {
        Result<List<DictModel>> result = new Result<List<DictModel>>();
        LambdaQueryWrapper<Category> query = new LambdaQueryWrapper<Category>();
        if (oConvertUtils.isNotEmpty(code) && !"0".equals(code)) {
            query.likeRight(Category::getCode, code);
        }
        List<Category> list = this.sysCategoryService.list(query);
        if (list == null || list.size() == 0) {
            result.setMessage("无数据,参数有误.[code]");
            result.setSuccess(false);
            return result;
        }
        List<DictModel> rdList = new ArrayList<DictModel>();
        for (Category c : list) {
            rdList.add(new DictModel(c.getId(), c.getName()));
        }
        result.setSuccess(true);
        result.setResult(rdList);
        return result;
    }


}
