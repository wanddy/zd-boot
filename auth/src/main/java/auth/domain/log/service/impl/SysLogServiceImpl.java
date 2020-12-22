package auth.domain.log.service.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import commons.system.api.ISysBaseAPI;
import auth.entity.Log;
import auth.domain.log.mapper.SysLogMapper;
import auth.domain.log.service.ISysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 系统日志 服务实现类
 */
@Slf4j
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, Log> implements ISysLogService {

	@Resource
	private SysLogMapper sysLogMapper;
	@Autowired
	private ISysBaseAPI sysBaseAPI;

	/**
	 * @功能：清空所有日志记录
	 */
	@Override
	public void removeAll() {
		sysLogMapper.removeAll();
	}

	@Override
	public Long findTotalVisitCount() {
		return sysLogMapper.findTotalVisitCount();
	}

	//update-begin--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
	@Override
	public Long findTodayVisitCount(Date dayStart, Date dayEnd) {
		return sysLogMapper.findTodayVisitCount(dayStart,dayEnd);
	}

	@Override
	public Long findTodayIp(Date dayStart, Date dayEnd) {
		return sysLogMapper.findTodayIp(dayStart,dayEnd);
	}
	//update-end--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数

	@Override
	public List<Map<String,Object>> findVisitCount(Date dayStart, Date dayEnd) {
		try {
			String dbType = sysBaseAPI.getDatabaseType();
			return sysLogMapper.findVisitCount(dayStart, dayEnd,dbType);
		} catch (SQLException e) {
		}
		return null;
	}
}
