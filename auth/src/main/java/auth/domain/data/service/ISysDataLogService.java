package auth.domain.data.service;

import auth.entity.DataLog;

import com.baomidou.mybatisplus.extension.service.IService;

public interface ISysDataLogService extends IService<DataLog> {

	/**
	 * 添加数据日志
	 * @param tableName
	 * @param dataId
	 * @param dataContent
	 */
	public void addDataLog(String tableName, String dataId, String dataContent);

}
