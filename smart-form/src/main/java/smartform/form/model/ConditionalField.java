package smartform.form.model;

import com.alibaba.fastjson.annotation.JSONField;
import smartform.widget.model.RuleBase;
import smartform.widget.model.deserializer.RuleBaseListDeserializer;

import java.io.Serializable;
import java.util.List;

/** 
* @ClassName: ConditionalField 
* @Description: 条件处理需要设置的字段
* @author quhanlin
* @date 2018年12月1日 下午6:05:13 
*  
*/
public class ConditionalField implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 字段ID
	 */
	private String id;

	/**
	 * 执行Sql语句
	 */
	private String sqlStr;

	/**
	 * 默认值
	 */
	private String defValue;

	/**
	 * 规则列表
	 */
	@JSONField(deserializeUsing = RuleBaseListDeserializer.class)
	private List<RuleBase> rules;

	@Override
	public String toString() {
		return "ConditionalField [id=" + id + ", sqlStr=" + sqlStr + ", defValue=" + defValue + ", rules=" + rules + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSqlStr() {
		return sqlStr;
	}

	public void setSqlStr(String sqlStr) {
		this.sqlStr = sqlStr;
	}

	public String getDefValue() {
		return defValue;
	}

	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}

	public List<RuleBase> getRules() {
		return rules;
	}

	public void setRules(List<RuleBase> rules) {
		this.rules = rules;
	}

}
