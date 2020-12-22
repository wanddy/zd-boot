package auth.domain.category.service.impl;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import commons.constant.FillRuleConstant;
import commons.exception.ZdException;
import commons.util.FillRuleUtil;
import commons.util.oConvertUtils;
import auth.entity.Category;
import auth.domain.category.mapper.SysCategoryMapper;
import auth.discard.model.TreeSelectModel;
import auth.domain.category.service.ISysCategoryService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 分类字典
 */
@Slf4j
@Service
public class SysCategoryServiceImpl extends ServiceImpl<SysCategoryMapper, Category> implements ISysCategoryService {

    @Override
    public void addSysCategory(Category category) {
        String categoryCode = "";
        String categoryPid = ISysCategoryService.ROOT_PID_VALUE;
        String parentCode = null;
        if (oConvertUtils.isNotEmpty(category.getPid())) {
            categoryPid = category.getPid();

            //PID 不是根节点 说明需要设置父节点 hasChild 为1
            if (!ISysCategoryService.ROOT_PID_VALUE.equals(categoryPid)) {
                Category parent = baseMapper.selectById(categoryPid);
                parentCode = parent.getCode();
                if (!"1".equals(parent.getHasChild())) {
                    parent.setHasChild("1");
                    baseMapper.updateById(parent);
                }
            }
        }
        //update-begin--Author:baihailong  Date:20191209 for：分类字典编码规则生成器做成公用配置
        JSONObject formData = new JSONObject();
        formData.put("pid", categoryPid);
        categoryCode = (String) FillRuleUtil.executeRule(FillRuleConstant.CATEGORY, formData);
        //update-end--Author:baihailong  Date:20191209 for：分类字典编码规则生成器做成公用配置
        category.setCode(categoryCode);
        category.setPid(categoryPid);
        baseMapper.insert(category);
    }

    @Override
    public void updateSysCategory(Category category) {
        if (oConvertUtils.isEmpty(category.getPid())) {
            category.setPid(ISysCategoryService.ROOT_PID_VALUE);
        } else {
            //如果当前节点父ID不为空 则设置父节点的hasChild 为1
            Category parent = baseMapper.selectById(category.getPid());
            if (parent != null && !"1".equals(parent.getHasChild())) {
                parent.setHasChild("1");
                baseMapper.updateById(parent);
            }
        }
        baseMapper.updateById(category);
    }

    @Override
    public List<TreeSelectModel> queryListByCode(String pcode) throws ZdException {
        String pid = ROOT_PID_VALUE;
        if (oConvertUtils.isNotEmpty(pcode)) {
            List<Category> list = baseMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getCode, pcode));
            if (list == null || list.size() == 0) {
                throw new ZdException("该编码【" + pcode + "】不存在，请核实!");
            }
            if (list.size() > 1) {
                throw new ZdException("该编码【" + pcode + "】存在多个，请核实!");
            }
            pid = list.get(0).getId();
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public List<TreeSelectModel> queryListByPid(String pid) {
        if (oConvertUtils.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public List<TreeSelectModel> queryListByPid(String pid, Map<String, String> condition) {
        if (oConvertUtils.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByPid(pid, condition);
    }

    @Override
    public String queryIdByCode(String code) {
        return baseMapper.queryIdByCode(code);
    }

    @Override
    public List<Category> getByPId(String id) {
        return baseMapper.selectList(new QueryWrapper<Category>().eq("pid",id));
    }

}
