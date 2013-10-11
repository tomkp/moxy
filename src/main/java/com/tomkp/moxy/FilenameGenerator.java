package com.tomkp.moxy;

import javax.servlet.http.HttpServletRequest;

public interface FilenameGenerator {

    public String generate(HttpServletRequest request);

}
