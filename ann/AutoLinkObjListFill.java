package com.jdkhome.autoolk.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记该字段是一个由自定义对象组成的List,需要进一步查询
 * @author Link
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoLinkObjListFill {
	String sql();
	String parameters() default "";
}
