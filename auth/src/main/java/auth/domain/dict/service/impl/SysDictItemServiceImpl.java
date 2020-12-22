package auth.domain.dict.service.impl;

import lombok.extern.slf4j.Slf4j;
import auth.entity.DictItem;
import auth.domain.dict.mapper.SysDictItemMapper;
import auth.domain.dict.service.ISysDictItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务实现类
 */
@Slf4j
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, DictItem> implements ISysDictItemService {

    @Autowired
    private SysDictItemMapper sysDictItemMapper;

    @Override
    public List<DictItem> selectItemsByMainId(String mainId) {
        return sysDictItemMapper.selectItemsByMainId(mainId);
    }
}
