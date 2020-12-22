package smartform.widget.model.deserializer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import smartform.widget.model.WidgetBase;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: WidgetBaseListDeserializer
 * @Description: FastJson，序列化字段基类列表实现
 * @author quhanlin
 * @date 2018年9月17日 下午12:19:11
 * 
 */
public class WidgetBaseListDeserializer implements ObjectDeserializer {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		Object value = parser.parse();
		List<WidgetBase> dataList = null;
		if(value==null || !(value instanceof JSONArray))
			return null;
		JSONArray list = (JSONArray) value;

		// 解析FastJson给与的array
		if (list != null && list.size() > 0) {
			dataList = new ArrayList<WidgetBase>();
			for (int i = 0; i < list.size(); i++) {
				Object obj = list.get(i);
				WidgetBase widget = null;
				if (obj instanceof JSONObject) {
					JSONObject object = (JSONObject) obj;
					widget = WidgetBaseDeserializer.deserialzeWidgetBase(object);
				} else if (obj instanceof WidgetBase) {
					widget = (WidgetBase) obj;
				}
				if(widget!=null)
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
