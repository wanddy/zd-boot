package smartcode.form.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import smartcode.form.entity.OnlCgreportItem;
import smartcode.form.mapper.OnlCgreportItemMapper;
import smartcode.form.service.OnlCgreportItemService;

import java.util.List;
import java.util.Map;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/31 18:00
 * @Description: zdit.zdboot.smartcode.online.service.impl
 **/

@Service("onlCgreportItemServiceImpl")
public class OnlCgreportItemServiceImpl extends ServiceImpl<OnlCgreportItemMapper, OnlCgreportItem> implements OnlCgreportItemService {

    @Override
    public List<Map<String, String>> getAutoListQueryInfo(String var1) {
        return null;
    }
}
