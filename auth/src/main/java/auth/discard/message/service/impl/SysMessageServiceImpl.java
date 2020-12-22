package auth.discard.message.service.impl;

import commons.system.base.service.impl.JeecgServiceImpl;
import auth.discard.message.entity.SysMessage;
import auth.discard.message.mapper.SysMessageMapper;
import auth.discard.message.service.ISysMessageService;
import org.springframework.stereotype.Service;

/**
 * @Description: 消息
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Service
public class SysMessageServiceImpl extends JeecgServiceImpl<SysMessageMapper, SysMessage> implements ISysMessageService {

}
