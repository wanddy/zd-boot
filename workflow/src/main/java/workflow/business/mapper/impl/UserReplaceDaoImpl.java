package workflow.business.mapper.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import workflow.business.mapper.UserReplaceDao;
import workflow.business.model.UserReplace;
import workflow.business.model.UserReplaceInfo;
import workflow.common.constant.ActivitiConstant;
import workflow.common.redis.JedisMgr_wf;

import java.util.*;
import java.util.Map.Entry;

//import org.springframework.data.redis.core.StringRedisTemplate;

/**
* @ClassName: UserReplaceDaoImpl
* @Description: 顶替用户数据处理
* @author KaminanGTO
* @date 2019年1月3日 上午9:48:32
*
*/
@Component
public class UserReplaceDaoImpl implements UserReplaceDao{

	private final String KEY = "replace";

	@Autowired
	private JedisMgr_wf jedisMgrWf;

	@Override
	public UserReplace get(String id) {
		String key = getKey(id);
		Map<String, String> hash = jedisMgrWf.getHash(key);
		if(hash == null || hash.isEmpty())
		{
			return null;
		}
		UserReplace userReplace = new UserReplace();
		userReplace.setId(id);
		Iterator<Entry<String, String>> iter = hash.entrySet().iterator();
		Map<String, List<UserReplaceInfo>> replaceInfos = new HashMap<String, List<UserReplaceInfo>>();
		while(iter.hasNext())
		{
			Entry<String, String> entry = iter.next();
			String[] keystrs = entry.getKey().split(ActivitiConstant.REDIS_HASHKEY_SPAN);
			String processId = keystrs[0];
			String replaceUserId = keystrs[1];
			if(!replaceInfos.containsKey(processId))
			{
				replaceInfos.put(processId, new ArrayList<UserReplaceInfo>());
			}
			UserReplaceInfo userReplaceInfo = JSONObject.parseObject(entry.getValue(), UserReplaceInfo.class);
			userReplaceInfo.setReplaceUser(replaceUserId);
			replaceInfos.get(processId).add(userReplaceInfo);
		}
		userReplace.setReplaceInfos(replaceInfos);
		return userReplace;
	}

	@Override
	public void deleteParams(String id, String paramkey) {
		jedisMgrWf.deleteHash(getKey(id), paramkey);
	}

	@Override
	public void deleteParams(String id, List<String> paramkeys) {
		jedisMgrWf.deleteHash(getKey(id), paramkeys.toArray(new String[paramkeys.size()]));
	}

	private String getKey(String id)
	{
		return JedisMgr_wf.KeyHead + KEY + JedisMgr_wf.KeySpan + id;
	}

}
