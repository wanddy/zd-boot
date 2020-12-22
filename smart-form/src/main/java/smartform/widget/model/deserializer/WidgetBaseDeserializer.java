package smartform.widget.model.deserializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import smartform.widget.model.*;

import java.lang.reflect.Type;

/**
 * @ClassName: WidgetBaseDeserializer
 * @Description: FastJson，序列化字段基类实现
 * @author quhanlin
 * @date 2018年9月17日 下午12:19:11
 * 
 */
public class WidgetBaseDeserializer implements ObjectDeserializer {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		Object value = parser.parse();
		JSONObject object = (JSONObject) value;
		return (T) deserialzeWidgetBase(object);
	}

	@Override
	public int getFastMatchToken() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/** 解析字段基类，fieldType 识别字段子类
	 * @param object
	 * @return
	 */
	public static WidgetBase deserialzeWidgetBase(JSONObject object){
		// 获取字段类型
		int dataType = object.getInteger("fieldType");
		WidgetBase widget = null;
		if (dataType == WidgetType.NUMBER.value)
			widget = toJavaBean(object, WidgetNumber.class);
		else if (dataType == WidgetType.INPUT.value)
			widget = toJavaBean(object, WidgetInput.class);
		else if (dataType == WidgetType.TEXTAREA.value)
			widget = toJavaBean(object, WidgetInput.class);
		else if (dataType == WidgetType.EDITOR.value)
			widget = toJavaBean(object, WidgetEditor.class);
		else if (dataType == WidgetType.RADIO.value)
			widget = toJavaBean(object, WidgeRadio.class);
		else if (dataType == WidgetType.CHECKBOX.value)
			widget = toJavaBean(object, WidgeSelect.class);
		else if (dataType == WidgetType.SELECT.value)
			widget = toJavaBean(object, WidgeSelect.class);
		else if (dataType == WidgetType.CASCADER.value)
			widget = toJavaBean(object, WidgeSelect.class);
		else if (dataType == WidgetType.DATE.value)
			widget = toJavaBean(object, WidgetDate.class);
		else if (dataType == WidgetType.DATESPAN.value)
			widget = toJavaBean(object, WidgetDate.class);
		else if (dataType == WidgetType.TIME.value)
			widget = toJavaBean(object, WidgetTime.class);
		else if (dataType == WidgetType.TIMESPAN.value)
			widget = toJavaBean(object, WidgetTime.class);
		else if (dataType == WidgetType.UPLOAD.value)
			widget = toJavaBean(object, WidgetUpload.class);
		else if (dataType == WidgetType.LABEL.value)
			widget = toJavaBean(object, WidgetLabel.class);
		
		return widget;
	}
	
	private static <T> WidgetBase toJavaBean(JSONObject object, Class<T> clazz){
		//JSON.toJavaObject(object, clazz); 
		return (WidgetBase) JSON.parseObject(object.toJSONString(), clazz);
	}
}
