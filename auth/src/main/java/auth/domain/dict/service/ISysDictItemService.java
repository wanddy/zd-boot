package auth.domain.dict.service;

import auth.entity.DictItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
public interface ISysDictItemService extends IService<DictItem> {
    public List<DictItem> selectItemsByMainId(String mainId);
}
