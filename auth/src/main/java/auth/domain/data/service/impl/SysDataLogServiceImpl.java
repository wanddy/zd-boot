package auth.domain.data.service.impl;

import lombok.extern.slf4j.Slf4j;
import auth.entity.DataLog;
import auth.domain.data.mapper.SysDataLogMapper;
import auth.domain.data.service.ISysDataLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Slf4j
@Service
public class SysDataLogServiceImpl extends ServiceImpl<SysDataLogMapper, DataLog> implements ISysDataLogService {


	private final SysDataLogMapper logMapper;

	@Autowired
	public SysDataLogServiceImpl(SysDataLogMapper logMapper) {
		this.logMapper = logMapper;
	}

	/**
	 * 添加数据日志
	 */
	@Override
	public void addDataLog(String tableName, String dataId, String dataContent) {
		String versionNumber = "0";
		String dataVersion = logMapper.queryMaxDataVer(tableName, dataId);
		if(dataVersion != null ) {
			versionNumber = String.valueOf(Integer.parseInt(dataVersion)+1);
		}
		DataLog log = new DataLog();
		log.setDataTable(tableName);
		log.setDataId(dataId);
		log.setDataContent(dataContent);
		log.setDataVersion(versionNumber);
		this.save(log);
	}

}
