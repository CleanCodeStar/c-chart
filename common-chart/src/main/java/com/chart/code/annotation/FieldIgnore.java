package com.chart.code.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段忽略
 * @author CleanCode
 */
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时保留
@Target(ElementType.FIELD) // 注解只能应用于字段
public @interface FieldIgnore {
}
