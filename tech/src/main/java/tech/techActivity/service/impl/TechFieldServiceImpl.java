package tech.techActivity.service.impl;

import org.springframework.transaction.annotation.Transactional;
import tech.techActivity.entity.TechField;
import tech.techActivity.mapper.TechFieldMapper;
import tech.techActivity.service.ITechFieldService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 报名表单配置
 * @Author: zd-boot
 * @Date:   2020-12-02
 * @Version: V1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TechFieldServiceImpl extends ServiceImpl<TechFieldMapper, TechField> implements ITechFieldService {

	@Autowired
	private TechFieldMapper techFieldMapper;

	@Override
	public List<TechField> selectByMainId(String mainId) {
		return techFieldMapper.selectByMainId(mainId);
	}
}
