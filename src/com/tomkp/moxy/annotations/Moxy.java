package com.tomkp.moxy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Moxy {

    int port() default 9001;

    String contentType() default "text/xml";

    int[] statusCode() default {};

    String[] responses() default {};

    String[] files() default {};

}
