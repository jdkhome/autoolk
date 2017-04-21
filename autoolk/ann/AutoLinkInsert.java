package com.jdkhome.autoolk.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记该字段与表中字段映射
 * 如果value=""则自动取成员名称进行映射
 * Created by Link on 2017/4/19.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoLinkInsert {
    String value() default "";
    boolean primarykey() default false;
}
