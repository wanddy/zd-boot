package tech.techActivity.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import commons.auth.vo.LoginUser;
import tech.techActivity.entity.TechActivity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import tech.wxUser.entity.WxUser;

import java.util.List;

/**
 * @Description: 活动表
 * @Author: zd-boot
 * @Date:   2020-12-02
 * @Version: V1.0
 */
public interface TechActivityMapper extends BaseMapper<TechActivity> {


    IPage<TechActivity> getPage(Page<TechActivity> page, TechActivity techActivity, LoginUser sysUser);

    List<TechActivity> appList(String headline, String place, String startTime,String status);

}
