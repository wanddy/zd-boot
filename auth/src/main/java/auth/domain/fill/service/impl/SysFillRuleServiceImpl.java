package auth.domain.fill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import auth.entity.FillRule;
import auth.domain.fill.mapper.SysFillRuleMapper;
import auth.domain.fill.service.ISysFillRuleService;
import org.springframework.stereotype.Service;

/**
 * 填值规则
 */
@Slf4j
@Service
public class SysFillRuleServiceImpl extends ServiceImpl<SysFillRuleMapper, FillRule> implements ISysFillRuleService {

}
