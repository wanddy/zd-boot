package workflow.business.model;

import java.util.List;
import java.util.Map;

/** 
* @ClassName: UserReplace 
* @Description: 顶替用户数据
* @author KaminanGTO
* @date 2019年1月3日 上午9:48:57 
*  
*/
public class UserReplace {
	
	/** 
	* @Fields id : 顶替者id
	*/ 
	private String id;
	
	/** 
	* @Fields replaceInfos : 顶替数据 <流程定义id, 顶替数据列表> 
	*/ 
	private Map<String, List<UserReplaceInfo>> replaceInfos;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, List<UserReplaceInfo>> getReplaceInfos() {
		return replaceInfos;
	}

	public void setReplaceInfos(Map<String, List<UserReplaceInfo>> replaceInfos) {
		this.replaceInfos = replaceInfos;
	}

}
