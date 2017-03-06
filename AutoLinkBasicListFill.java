package com.jdkhome.autoolk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记该字段是一个由基础类型(String/ingteger等)组成的List
 * 将自动解析由逗号分隔开(可使用Mysql的group_concat函数)的数据生成List
 * 如果value=""则自动取成员名称进行映射
 * @author Link
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoLinkBasicListFill {
	String value() default"";
}
