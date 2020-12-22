package auth.domain.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import auth.entity.DataSource;
import auth.domain.data.mapper.SysDataSourceMapper;
import auth.domain.data.service.ISysDataSourceService;
import org.springframework.stereotype.Service;

/**
 * 多数据源管理
 */
@Slf4j
@Service
public class SysDataSourceServiceImpl extends ServiceImpl<SysDataSourceMapper, DataSource> implements ISysDataSourceService {

}
