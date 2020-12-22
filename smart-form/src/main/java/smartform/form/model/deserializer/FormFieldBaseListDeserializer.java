package smartform.form.model.deserializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import smartform.form.model.FormFieldBase;
import smartform.form.model.FormFieldType;
import smartform.form.model.Group;
import smartform.widget.model.WidgetBase;
import smartform.widget.model.deserializer.WidgetBaseDeserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FormFieldBaseListDeserializer implements ObjectDeserializer {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		Object value = parser.parse();
		if(value==null || !(value instanceof JSONArray))
			return null;
		List<FormFieldBase> dataList = null;
		JSONArray list = (JSONArray) value;

		// 解析FastJson给与的array
		if (list != null && list.size() > 0) {
			dataList = new ArrayList<FormFieldBase>();
			for (int i = 0; i < list.size(); i++) {
				Object obj = list.get(i);
				FormFieldBase widget = null;
				if (obj instanceof JSONObject) {
					JSONObject object = (JSONObject) obj;
					// 获取字段类型
					int dataType = object.getInteger("type");
					if (dataType == FormFieldType.GROUP.value)
						widget = JSON.parseObject(object.toJSONString(), Group.class);
					else if (dataType == FormFieldType.WIDGET.value)
						widget = WidgetBaseDeserializer.deserialzeWidgetBase(object);
				} else if (obj instanceof WidgetBase) {
					widget = (WidgetBase) obj;
				} else if (obj instanceof Group) {
					widget = (Group) obj;
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
