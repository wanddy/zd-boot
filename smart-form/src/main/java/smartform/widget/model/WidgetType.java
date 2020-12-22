package smartform.widget.model;

/** 
* @ClassName: WidgetType 
* @Description: 字段类型枚举
* @author quhanlin
* @date 2018年9月17日 下午12:11:31 
*  
*/
public enum WidgetType {
	NUMBER(1),
	INPUT(2),
	TEXTAREA(3),
	EDITOR(4),
	RADIO(5),
	CHECKBOX(6),
	SELECT(7),
	CASCADER(8),
	DATE(9),
	DATESPAN(10),
	TIME(11),
	TIMESPAN(12),
	UPLOAD(13),
	LABEL(14);

    /**
     * 枚举值
     */
    public final int value;
    //构造方法必须是private或者默认
    private WidgetType(int value) {
        this.value = value;
    }
    
    public static WidgetType valueOf(int value)
    {
    	if(value < 1 || value > WidgetType.values().length)
    	{
    		return null;
    	}
    	return WidgetType.values()[value - 1];
    }
    
}
