package workflow.olddata.core.annotation;

import java.lang.annotation.*;

/**
 * 注解，当@Access加在类上时，当前类中的所有方法都要经过权限过滤。当加在方法上时，同时类上没有加时，只对该方法进行权限过滤。
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Access {
	String value() default "";
}
