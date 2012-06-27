package com.tomkp.moxy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Moxy {

    // start server on this port
    int port() default 9001;

    String proxy() default "";

    String[] contentType() default {};

    int[] statusCode() default {};

    String[] cookie() default {};

    String[] response() default {};

    String[] file() default {};

}
