package com.jdkhome.autoolk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记该基础字段与sql映射
 * 如果value=""则自动取成员名称进行映射
 * @author Link
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoLinkFill {
	String value() default "";
}
