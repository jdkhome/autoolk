package com.jdkhome.autoolk.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记该类与对应的表映射
 * 如果value=""则自动取类名进行映射
 * Created by Link on 2017/4/19.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoLinkPojo {
    String value() default "";
}
