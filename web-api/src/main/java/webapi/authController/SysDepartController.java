package webapi.authController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import auth.discard.model.DepartIdModel;
import auth.discard.model.SysDepartTreeModel;
import auth.domain.depart.service.ISysDepartService;
import auth.domain.user.service.DefUserService;
import auth.domain.user.service.UserService;
import auth.entity.Depart;
import auth.entity.User;
import commons.api.vo.Result;
import commons.auth.vo.LoginUser;
import commons.constant.CacheConstant;
import commons.constant.CommonConstant;
import commons.auth.utils.JwtUtil;
import commons.util.oConvertUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 部门
 */
@RestController
@RequestMapping("/sys/sysDepart")
@Slf4j
public class SysDepartController {

    private final ISysDepartService sysDepartService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final UserService userService;

    private final DefUserService defUserService;

    @Autowired
    public SysDepartController(ISysDepartService sysDepartService, RedisTemplate<String, Object> redisTemplate, UserService userService, DefUserService defUserService) {
        this.sysDepartService = sysDepartService;
        this.redisTemplate = redisTemplate;
        this.userService = userService;
        this.defUserService = defUserService;
    }

    /**
     * 查询数据 查出我的部门,并以树结构数据格式响应给前端
     */
    @RequestMapping(value = "/queryMyDeptTreeList", method = RequestMethod.GET)
    public Result<List<SysDepartTreeModel>> queryMyDeptTreeList() {
        Result<List<SysDepartTreeModel>> result = new Result<>();
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        try {
            if (oConvertUtils.isNotEmpty(user.getUserIdentity()) && user.getUserIdentity().equals(CommonConstant.USER_IDENTITY_2)) {
                List<SysDepartTreeModel> list = sysDepartService.queryMyDeptTreeList(user.getDepartIds());
                result.setResult(list);
                result.setMessage(CommonConstant.USER_IDENTITY_2.toString());
            } else {
                result.setMessage(CommonConstant.USER_IDENTITY_1.toString());
            }
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 查询数据 查出所有部门,并以树结构数据格式响应给前端
     */
    @RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
    public Result<List<SysDepartTreeModel>> queryTreeList() {
        Result<List<SysDepartTreeModel>> result = new Result<>();
        try {
            // 从内存中读取
//			List<SysDepartTreeModel> list =FindsDepartsChildrenUtil.getSysDepartTreeList();
//			if (CollectionUtils.isEmpty(list)) {
//				list = sysDepartService.queryTreeList();
//			}
            List<SysDepartTreeModel> list = sysDepartService.queryTreeList();
            result.setResult(list);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 添加新数据 添加用户新建的部门对象数据,并保存到数据库
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result<Depart> add(@RequestBody Depart Depart, HttpServletRequest request) {
        Result<Depart> result = new Result<>();
        String username = JwtUtil.getUserNameByToken(request);
        try {
//			Depart.setCreateBy(username);
            sysDepartService.saveDepartData(Depart, username);
            //清除部门树内存
            // FindsDepartsChildrenUtil.clearSysDepartTreeList();
            // FindsDepartsChildrenUtil.clearDepartIdModel();
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑数据 编辑部门的部分数据,并保存到数据库
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result<Depart> edit(@RequestBody Depart Depart, HttpServletRequest request) {
        String username = JwtUtil.getUserNameByToken(request);
//		Depart.setupdateBy(username);
        Result<Depart> result = new Result<>();
        Depart sysDepartEntity = sysDepartService.getById(Depart.getId());
        if (sysDepartEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysDepartService.updateDepartDataById(Depart, username);
            // TODO 返回false说明什么？
            if (ok) {
                //清除部门树内存
                //FindsDepartsChildrenUtil.clearSysDepartTreeList();
                //FindsDepartsChildrenUtil.clearDepartIdModel();
                result.success("修改成功!");
            }
        }
        return result;
    }

    /**
     * 通过id删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result<Depart> delete(@RequestParam(name = "id") String id) {

        Result<Depart> result = new Result<>();
        Depart Depart = sysDepartService.getById(id);
        if (Depart == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysDepartService.delete(id);
            if (ok) {
                //清除部门树内存
                //FindsDepartsChildrenUtil.clearSysDepartTreeList();
                // FindsDepartsChildrenUtil.clearDepartIdModel();
                result.success("删除成功!");
            }
        }
        return result;
    }


    /**
     * 批量删除 根据前端请求的多个ID,对数据库执行删除相关部门数据的操作
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result<Depart> deleteBatch(@RequestParam(name = "ids") String ids) {

        Result<Depart> result = new Result<>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.sysDepartService.deleteBatchWithChildren(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 查询数据 添加或编辑页面对该方法发起请求,以树结构形式加载所有部门的名称,方便用户的操作
     */
    @RequestMapping(value = "/queryIdTree", method = RequestMethod.GET)
    public Result<List<DepartIdModel>> queryIdTree() {
//		Result<List<DepartIdModel>> result = new Result<List<DepartIdModel>>();
//		List<DepartIdModel> idList;
//		try {
//			idList = FindsDepartsChildrenUtil.wrapDepartIdModel();
//			if (idList != null && idList.size() > 0) {
//				result.setResult(idList);
//				result.setSuccess(true);
//			} else {
//				sysDepartService.queryTreeList();
//				idList = FindsDepartsChildrenUtil.wrapDepartIdModel();
//				result.setResult(idList);
//				result.setSuccess(true);
//			}
//			return result;
//		} catch (Exception EModel) {
//			log.error(EModel.getMessage(),EModel);
//			result.setSuccess(false);
//			return result;
//		}
        Result<List<DepartIdModel>> result = new Result<>();
        try {
            List<DepartIdModel> list = sysDepartService.queryDepartIdTreeList();
            result.setResult(list);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 部门搜索功能方法,根据关键字模糊搜索相关部门
     */
    @RequestMapping(value = "/searchBy", method = RequestMethod.GET)
    public Result<List<SysDepartTreeModel>> searchBy(@RequestParam(name = "keyWord") String keyWord, @RequestParam(name = "myDeptSearch", required = false) String myDeptSearch) {
        Result<List<SysDepartTreeModel>> result = new Result<>();
        //部门查询，myDeptSearch为1时为我的部门查询，登录用户为上级时查只查负责部门下数据
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String departIds = null;
        if (oConvertUtils.isNotEmpty(user.getUserIdentity()) && user.getUserIdentity().equals(CommonConstant.USER_IDENTITY_2)) {
            departIds = user.getDepartIds();
        }
        List<SysDepartTreeModel> treeList = this.sysDepartService.searhBy(keyWord, myDeptSearch, departIds);
        if (treeList == null || treeList.size() == 0) {
            result.setSuccess(false);
            result.setMessage("未查询匹配数据！");
            return result;
        }
        result.setResult(treeList);
        return result;
    }



    /**
     * 查询所有部门信息
     */
    @GetMapping("listAll")
    public Result<List<Depart>> listAll(@RequestParam(name = "id", required = false) String id) {
        Result<List<Depart>> result = new Result<>();
        LambdaQueryWrapper<Depart> query = new LambdaQueryWrapper<>();
        query.orderByAsc(Depart::getOrgCode);
        if (oConvertUtils.isNotEmpty(id)) {
            String[] arr = id.split(",");
            query.in(Depart::getId, (Object) arr);
        }
        List<Depart> ls = this.sysDepartService.list(query);
        result.setSuccess(true);
        result.setResult(ls);
        return result;
    }

    /**
     * 查询数据 查出所有部门,并以树结构数据格式响应给前端
     */
    @RequestMapping(value = "/queryTreeByKeyWord", method = RequestMethod.GET)
    public Result<Map<String, Object>> queryTreeByKeyWord(@RequestParam(name = "keyWord", required = false) String keyWord) {
        Result<Map<String, Object>> result = new Result<>();
        try {
            Map<String, Object> map = new HashMap<>();
            List<SysDepartTreeModel> list = sysDepartService.queryTreeByKeyWord(keyWord);
            //根据keyWord获取用户信息
            LambdaQueryWrapper<User> queryUser = new LambdaQueryWrapper<>();
            queryUser.eq(User::getDelFlag, 0);
            queryUser.and(i -> i.like(User::getUsername, keyWord).or().like(User::getRealname, keyWord));
            List<User> sysUsers = defUserService.list(queryUser);
            map.put("userList", sysUsers);
            map.put("departList", list);
            result.setResult(map);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 根据部门编码获取部门信息
     */
    @GetMapping("/getDepartName")
    public Result<Depart> getDepartName(@RequestParam(name = "orgCode") String orgCode) {
        Result<Depart> result = new Result<>();
        LambdaQueryWrapper<Depart> query = new LambdaQueryWrapper<>();
        query.eq(Depart::getOrgCode, orgCode);
        Depart Depart = sysDepartService.getOne(query);
        result.setSuccess(true);
        result.setResult(Depart);
        return result;
    }
}
