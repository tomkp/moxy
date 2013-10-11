package com.tomkp.moxy;

import javax.servlet.http.HttpServletRequest;

public class NullFilenameGenerator implements FilenameGenerator {

    @Override
    public String generate(HttpServletRequest request) {
        return null;
    }
}
