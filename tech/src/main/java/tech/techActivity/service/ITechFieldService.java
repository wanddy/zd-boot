package tech.techActivity.service;

import tech.techActivity.entity.TechField;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 报名表单配置
 * @Author: zd-boot
 * @Date:   2020-12-02
 * @Version: V1.0
 */
public interface ITechFieldService extends IService<TechField> {

	public List<TechField> selectByMainId(String mainId);
}
