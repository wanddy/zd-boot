package workflow.common.config;

import org.activiti.engine.impl.cfg.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** 
* @ClassName: UUIDGenerator 
* @Description: Activiti主键生成逻辑
* @author KaminanGTO
* @date 2018年9月11日 上午10:06:59 
*  
*/
@Component
public class UUIDGenerator implements IdGenerator{

	@Override
	public String getNextId() {
		return UUID.randomUUID().toString().replace("-", "");
	}

}
