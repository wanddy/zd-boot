package smartform.widget.model.deserializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import smartform.widget.model.*;

import java.lang.reflect.Type;

/** 
* @ClassName: RuleBaseDeserializer 
* @Description: FastJson，序列化简单规则实现
* @author quhanlin
* @date 2018年9月30日 上午11:03:10
*  
*/
public class RuleBaseDeserializer  implements ObjectDeserializer {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		Object value = parser.parse();
		JSONObject object = (JSONObject) value;
		return (T) deserialzeRuleBase(object);
	}

	@Override
	public int getFastMatchToken() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/** 解析规则基类，type 识别规则子类
	 * @param object
	 * @return
	 */
	public static RuleBase deserialzeRuleBase(JSONObject object){
		// 获取字段类型
		int dataType = object.getInteger("type");
		RuleBase widget = null;
		if (dataType == RuleType.REQUIRED.value)
			widget = toJavaBean(object, RuleRequired.class);
		else if (dataType == RuleType.LENGHT.value)
			widget = toJavaBean(object, RuleLenght.class);
		else if (dataType == RuleType.REGEXP.value)
			widget = toJavaBean(object, RuleRegexp.class);
		else if (dataType == RuleType.SYMBOL.value)
			widget = toJavaBean(object, RuleSymbol.class);
		else if (dataType == RuleType.DECIMAL.value)
			widget = toJavaBean(object, RuleDecimal.class);
		else if (dataType == RuleType.SIZE.value)
			widget = toJavaBean(object, RuleSize.class);
		else if (dataType == RuleType.CUSTOM.value)
			widget = toJavaBean(object, RuleCustom.class);
		
		return widget;
	}
	
	private static <T> RuleBase toJavaBean(JSONObject object, Class<T> clazz){
		return (RuleBase) JSON.parseObject(object.toJSONString(), clazz);
	}
}
