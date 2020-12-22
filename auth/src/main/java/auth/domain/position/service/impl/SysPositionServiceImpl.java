package auth.domain.position.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import auth.entity.Position;
import auth.domain.position.mapper.SysPositionMapper;
import auth.domain.position.service.ISysPositionService;
import org.springframework.stereotype.Service;

/**
 * 职务
 */
@Slf4j
@Service
public class SysPositionServiceImpl extends ServiceImpl<SysPositionMapper, Position> implements ISysPositionService {

}
