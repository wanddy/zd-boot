package auth.domain.tenant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import auth.entity.Tenant;
import auth.domain.tenant.mapper.SysTenantMapper;
import auth.domain.tenant.service.ISysTenantService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, Tenant> implements ISysTenantService {


}
