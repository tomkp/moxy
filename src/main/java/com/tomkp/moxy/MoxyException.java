package com.tomkp.moxy;

public class MoxyException extends RuntimeException {


    public MoxyException(String s) {
        super(s);
    }


    public MoxyException(String s, Throwable throwable) {
        super(s, throwable);
    }

}
