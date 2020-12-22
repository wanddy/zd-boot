package smartform.widget.model.deserializer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import smartform.widget.model.RuleBase;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: RuleBaseListDeserializer
 * @Description: FastJson，序列化简单规则列表实现
 * @author quhanlin
 * @date 2018年9月29日 下午10:03:34
 * 
 */
public class RuleBaseListDeserializer implements ObjectDeserializer {

	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		Object value = parser.parse();
		List<RuleBase> dataList = null;
		if(value==null || !(value instanceof JSONArray))
			return null;
		JSONArray list = (JSONArray) value;

		// 解析FastJson给与的array
		if (list != null && list.size() > 0) {
			dataList = new ArrayList<RuleBase>();
			for (int i = 0; i < list.size(); i++) {
				Object obj = list.get(i);
				RuleBase widget = null;
				if (obj instanceof JSONObject) {
					JSONObject object = (JSONObject) obj;
					widget = RuleBaseDeserializer.deserialzeRuleBase(object);
				} else if (obj instanceof RuleBase) {
					widget = (RuleBase) obj;
				}
				if (widget != null)
					dataList.add(widget);
			}
		}
		return (T) dataList;
	}

	@Override
	public int getFastMatchToken() {
		// TODO Auto-generated method stub
		return 0;
	}

}
