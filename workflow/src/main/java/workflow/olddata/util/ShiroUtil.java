package workflow.olddata.util;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ShiroUtil 存放当前用户信息
 *
 */
public class ShiroUtil {
	private static TransmittableThreadLocal<ConcurrentHashMap<String,String>> idLocal= new TransmittableThreadLocal<>();

	/**
	 * 获取当前用户ID
	 * @return
	 */
	public static String getUserId() {
		return idLocal.get() == null ? null : idLocal.get().get("userId");
	}
	
	/**
	 * 设置当前用户ID
	 * @param userId
	 */
	// public static void setUserId(String userId){
	// 	idLocal.set(userId);
	// }

	/**
	 * 设置token解析的用户信息
	 * @param map
	 */
	public static void set(ConcurrentHashMap<String,String> map){
		idLocal.set(map);
	}

	public static ConcurrentHashMap getMap(){
		return idLocal.get();
	}

	/**
	 * 获取当前用户服务系统标识
	 * @return
	 */
	public static String getUserClientType() {
		return idLocal.get() == null ? null : idLocal.get().get("userClientType");
	}

}
