package workflow.olddata.util;

import java.util.UUID;

/**
 * ID生成工具类
 *
 */
public class IDGeneratorUtil {
	
	/**
	 * 生成32位UUID
	 * @return
	 */
	public static String generatorId(){
		
		String s = UUID.randomUUID().toString(); 
        //去掉“-”符号 
        return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24); 
	}
}
