package smartform.form.redis;

import smartform.common.service.RedisListService;
import smartform.form.model.FormFieldBase;

/**
* @ClassName: ContentFieldListService
* @Description: 表单内容数据字段列表操作类，用于表单内容具体字段的快速索引
* @author quhanlin
* @date 2018年10月6日 下午8:04:09
*
*/
public interface ContentFieldListService extends RedisListService<FormFieldBase> {

}
