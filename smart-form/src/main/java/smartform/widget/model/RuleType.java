package smartform.widget.model;

/**
 * @ClassName: RuleType
 * @Description: 规则类型枚举
 * @author quhanlin
 * @date 2018年9月25日 下午10:20:28
 * 
 */
public enum RuleType {
	REQUIRED(1),
	LENGHT(2),
	REGEXP(3),
	SYMBOL(4),
	DECIMAL(5),
	SIZE(6),
	CUSTOM(7);

    /**
     * 枚举值
     */
    public final int value;
    //构造方法必须是private或者默认
    private RuleType(int value) {
        this.value = value;
    }
}
