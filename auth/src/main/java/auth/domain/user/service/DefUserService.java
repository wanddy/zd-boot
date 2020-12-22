package auth.domain.user.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import auth.domain.user.mapper.UserMapper;
import auth.entity.User;

@Service
public class DefUserService extends ServiceImpl<UserMapper, User> {
}
