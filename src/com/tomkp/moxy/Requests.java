package com.tomkp.moxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Requests {

    private static List<Map<String, String[]>> parameters;
    private static List<Map<String, String>> headers;

    public static void reset() {
        parameters = new ArrayList<Map<String, String[]>>();
        headers = new ArrayList<Map<String, String>>();
    }

    public static void recordParameters(Map<String, String[]> parameters) {
        Requests.parameters.add(parameters);
    }

    public static void recordHeaders(Map<String, String> headers) {
        Requests.headers.add(headers);
    }

    public static List<Map<String, String[]>> getParameters() {
        return parameters;
    }

    public static List<Map<String, String>> getHeaders() {
        return headers;
    }
}
