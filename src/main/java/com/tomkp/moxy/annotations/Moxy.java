package com.tomkp.moxy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Moxy {

    int port() default 0;

    String proxy() default "";

    String[] contentType() default {};

    int[] statusCode() default {};

    String[] cookie() default {};

    String[] response() default {};

    String[] file() default {};

    boolean indexed() default false;

    String[] replace() default {};
}
