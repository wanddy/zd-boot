package auth.domain.relation.user.agent.service.impl;

import lombok.extern.slf4j.Slf4j;
import auth.entity.UserAgent;
import auth.domain.relation.user.agent.mapper.SysUserAgentMapper;
import auth.domain.relation.user.agent.service.ISysUserAgentService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 用户代理人设置
 */
@Slf4j
@Service
public class SysUserAgentServiceImpl extends ServiceImpl<SysUserAgentMapper, UserAgent> implements ISysUserAgentService {

}
