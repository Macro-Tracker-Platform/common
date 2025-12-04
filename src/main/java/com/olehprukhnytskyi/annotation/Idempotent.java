package com.olehprukhnytskyi.annotation;

import static java.util.concurrent.TimeUnit.HOURS;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    long ttl() default 1;
    TimeUnit unit() default HOURS;
    boolean allowFallback() default true;
}
