package auth.discard.demo.test.service.impl;

import auth.discard.demo.test.entity.JoaDemo;
import auth.discard.demo.test.mapper.JoaDemoMapper;
import auth.discard.demo.test.service.IJoaDemoService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 流程测试
 * @Author: jeecg-boot
 * @Date:   2019-05-14
 * @Version: V1.0
 */
@Service
public class JoaDemoServiceImpl extends ServiceImpl<JoaDemoMapper, JoaDemo> implements IJoaDemoService {

}
