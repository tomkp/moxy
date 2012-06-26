package com.tomkp.moxy;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class Requests {

    private static List<HttpServletRequest> requests = new ArrayList<HttpServletRequest>();


    public static List<HttpServletRequest> getRequests() {
        return requests;
    }

    public static void setRequests(List<HttpServletRequest> requests) {
        Requests.requests = requests;
    }
}
