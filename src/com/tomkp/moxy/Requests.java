package com.tomkp.moxy;

import org.eclipse.jetty.util.MultiMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Requests {

    private static List<Map<String, String[]>> paramaters;

    public static void reset() {
        paramaters = new ArrayList<Map<String, String[]>>();
    }

    public static void add(Map<String, String[]> parameters) {
        paramaters.add(parameters);
    }

    public static List<Map<String, String[]>> getParameters() {
        return paramaters;
    }
}
