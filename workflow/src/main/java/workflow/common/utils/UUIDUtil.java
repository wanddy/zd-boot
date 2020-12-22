package workflow.common.utils;

import java.util.UUID;

/** 
* @ClassName: UUIDUtil 
* @Description: UUID获取
* @author hou
* @date 2018年9月12日 上午11:06:44 
*  
*/
public class UUIDUtil {
	public static String getNextId() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
