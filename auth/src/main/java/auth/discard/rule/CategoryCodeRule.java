package auth.discard.rule;

import auth.domain.category.mapper.SysCategoryMapper;
import auth.entity.Category;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import commons.system.api.IFillRuleHandler;
import commons.util.SpringContextUtils;
import commons.util.YouBianCodeUtil;
import commons.util.oConvertUtils;

import java.util.List;

/**
 * @Author scott
 * @Date 2019/12/9 11:32
 * @Description: 分类字典编码生成规则
 */
public class CategoryCodeRule implements IFillRuleHandler {

    public static final String ROOT_PID_VALUE = "0";

    @Override
    public Object execute(JSONObject params, JSONObject formData) {

        String categoryPid = ROOT_PID_VALUE;
        String categoryCode = null;

        if (formData != null && formData.size() > 0) {
            Object obj = formData.get("pid");
            if (oConvertUtils.isNotEmpty(obj)) categoryPid = obj.toString();
        } else {
            if (params != null) {
                Object obj = params.get("pid");
                if (oConvertUtils.isNotEmpty(obj)) categoryPid = obj.toString();
            }
        }

        /*
         * 分成三种情况
         * 1.数据库无数据 调用YouBianCodeUtil.getNextYouBianCode(null);
         * 2.添加子节点，无兄弟元素 YouBianCodeUtil.getSubYouBianCode(parentCode,null);
         * 3.添加子节点有兄弟元素 YouBianCodeUtil.getNextYouBianCode(lastCode);
         * */
        //找同类 确定上一个最大的code值
        LambdaQueryWrapper<Category> query = new LambdaQueryWrapper<Category>().eq(Category::getPid, categoryPid).isNotNull(Category::getCode).orderByDesc(Category::getCode);
        SysCategoryMapper baseMapper = (SysCategoryMapper) SpringContextUtils.getBean("sysCategoryMapper");
        List<Category> list = baseMapper.selectList(query);
        if (list == null || list.size() == 0) {
            if (ROOT_PID_VALUE.equals(categoryPid)) {
                //情况1
                categoryCode = YouBianCodeUtil.getNextYouBianCode(null);
            } else {
                //情况2
                Category parent = (Category) baseMapper.selectById(categoryPid);
                categoryCode = YouBianCodeUtil.getSubYouBianCode(parent.getCode(), null);
            }
        } else {
            //情况3
            categoryCode = YouBianCodeUtil.getNextYouBianCode(list.get(0).getCode());
        }
        return categoryCode;
    }
}
