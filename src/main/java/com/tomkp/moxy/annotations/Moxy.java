package com.tomkp.moxy.annotations;

import com.tomkp.moxy.FilenameGenerator;
import com.tomkp.moxy.NullFilenameGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Moxy {

    int port() default 9001;

    String proxy() default "";

    String[] contentType() default {};

    int[] statusCode() default {};

    String[] cookie() default {};

    String[] response() default {};

    String[] file() default {};

    boolean indexed() default false;

    String[] replace() default {};

    Class<? extends FilenameGenerator> filenameGenerator() default NullFilenameGenerator.class;
}
