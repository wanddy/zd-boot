package commons.poi.excel.annatotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * excel 导出是用于标记id的
 *
 * @author JEECG
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ExcelTarget {
	/**
	 * 定义excel导出ID 来限定导出字段
	 */
	public String value();
}
