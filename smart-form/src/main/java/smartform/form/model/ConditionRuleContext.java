package smartform.form.model;

import smartform.widget.model.WidgetBase;

/**
 * 条件规则上下文，查询时使用
 * @ClassName: ConditionalWidget
 * @Description: 条件处理需要设置的组件实体
 * @author quhanlin
 * @date 2018年12月1日 下午6:05:13
 * 
 */
public class ConditionRuleContext {
	
	/**
	 * 复杂规则
	 */
	private ConditionalRule rule;
	
	/**
	 * 复杂规则判定字段
	 */
	private WidgetBase widget;

	public ConditionalRule getRule() {
		return rule;
	}

	public void setRule(ConditionalRule rule) {
		this.rule = rule;
	}

	public WidgetBase getWidget() {
		return widget;
	}

	public void setWidget(WidgetBase widget) {
		this.widget = widget;
	}

	@Override
	public String toString() {
		return "ConditionRuleContext [rule=" + rule + ", widget=" + widget + "]";
	}

}
