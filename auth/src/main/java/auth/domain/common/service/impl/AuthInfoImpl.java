package auth.domain.common.service.impl;

import auth.discard.model.SysDepartTreeModel;
import auth.discard.util.FindsDepartsChildrenUtil;
import auth.domain.common.dto.MdmDto;
import auth.domain.common.dto.UserDepartDto;
import auth.domain.common.service.AuthInfo;
import auth.domain.depart.mapper.SysDepartMapper;
import auth.domain.user.mapper.UserMapper;
import auth.domain.user.model.UserDepartModel;
import auth.entity.Depart;
import auth.entity.User;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import commons.auth.vo.LoginUser;
import commons.constant.CacheConstant;
import commons.constant.CommonConstant;
import commons.exception.FilterFailureReason;
import commons.exception.ZdException;
import commons.auth.utils.JwtUtil;
import commons.system.HttpContextUtils;
import commons.util.RedisUtil;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthInfoImpl implements AuthInfo {

    private final UserMapper userMapper;

    private final SysDepartMapper sysDepartMapper;

    private final RedisUtil redisUtil;

    @Autowired
    public AuthInfoImpl(UserMapper userMapper, SysDepartMapper sysDepartMapper, RedisUtil redisUtil) {
        this.userMapper = userMapper;
        this.sysDepartMapper = sysDepartMapper;
        this.redisUtil = redisUtil;
    }

    private String getToken() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        return request.getHeader("X-Access-Token");
    }

    @Override
    public UserDepartDto getUserInfo() {
        String username = JwtUtil.getUsername(getToken());
        User user = userMapper.getUserByName(username);
        return UserDepartDto.builder()
                .id(user.getId())
                .name(user.getRealname())
                .build();
    }

    @Override
    public List<MdmDto> getUserDepartSub(String userId) {
        return userMapper.selectList(Wrappers
                .<User>lambdaQuery()
                .eq(User::getId, userId)
                .eq(User::getStatus, 1)
                .eq(User::getDelFlag, 0))
                .stream()
                .map(x -> {
                    if (StringUtils.isEmpty(x.getDepartIds())) {
                        return MdmDto.builder()
                                .id(x.getId())
                                .name(x.getRealname())
                                .build();
                    }
                    List<String> arr = Arrays.asList(x.getDepartIds().split(","));
                    val departs = sysDepartMapper.selectBatchIds(arr);
                    return MdmDto.builder()
                            .id(x.getId())
                            .name(x.getRealname())
                            .mdms(departs.stream()
                                    .map(d -> MdmDto.builder()
                                            .id(d.getId())
                                            .name(d.getDepartName())
                                            .build())
                                    .collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SysDepartTreeModel> getDeparts() {
        val list = sysDepartMapper.selectList(Wrappers
                .<Depart>lambdaQuery()
                .eq(Depart::getDelFlag, CommonConstant.DEL_FLAG_0.toString())
                .orderByAsc(Depart::getDepartOrder));

        return FindsDepartsChildrenUtil.wrapTreeDataToTreeList(list);
    }

    @Override
    public List<MdmDto> getUsers() {
        return userMapper.selectList(Wrappers
                .<User>lambdaQuery()
                .eq(User::getStatus, 1)
                .eq(User::getDelFlag, 0)).stream()
                .map(x -> MdmDto.builder()
                        .id(x.getId())
                        .name(x.getRealname())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDepartDto> getUserById(String id) {
        val user = userMapper.selectById(id);

        if (StringUtils.isEmpty(user.getDepartIds())) {
            return Collections.singletonList(
                    UserDepartDto.builder()
                            .id(user.getId())
                            .name(user.getRealname())
                            .build());
        }

        List<String> arr = Arrays.asList(user.getDepartIds().split(","));
        val departs = sysDepartMapper.selectBatchIds(arr);

        if (departs.size() > 0) {
            return departs.stream()
                    .map(x -> UserDepartDto.builder()
                            .id(user.getId())
                            .name(user.getRealname())
                            .departId(x.getId())
                            .departName(x.getDepartName())
                            .build())
                    .collect(Collectors.toList());
        } else {
            return Collections.singletonList(
                    UserDepartDto.builder()
                            .id(user.getId())
                            .name(user.getRealname())
                            .build());
        }
    }

    @Override
    public List<UserDepartDto> getUserByName(String name) {

        val users = userMapper.selectList(Wrappers
                .<User>lambdaQuery()
                .like(User::getRealname, name)
                .eq(User::getStatus, 1)
                .eq(User::getDelFlag, 0));

        List<UserDepartDto> userDeparts = new ArrayList<>();

        users.forEach(x -> {
            if (StringUtils.isEmpty(x.getDepartIds())) {
                userDeparts.add(UserDepartDto.builder()
                        .id(x.getId())
                        .name((x.getRealname()))
                        .build());
            } else {
                List<String> arr = Arrays.asList(x.getDepartIds().split(","));
                val departs = sysDepartMapper.selectBatchIds(arr);

                userDeparts.addAll(
                        departs.stream()
                                .map(m -> UserDepartDto.builder()
                                        .id(x.getId())
                                        .name(x.getRealname())
                                        .departId(m.getId())
                                        .departName(m.getDepartName())
                                        .build())
                                .collect(Collectors.toList())
                );
            }
        });

        return userDeparts;
    }
}
