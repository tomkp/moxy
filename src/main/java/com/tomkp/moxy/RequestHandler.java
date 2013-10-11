package com.tomkp.moxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestHandler {

    public void process(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);


}
