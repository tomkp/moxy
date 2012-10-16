package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;

import java.util.List;

public class MoxyData {

    private List<Moxy> moxies;


    public MoxyData(List<Moxy> moxies) {
        this.moxies = moxies;
    }

    public String[] getResponses() {
        String[] responses = null;
        for (Moxy moxy : moxies) {
            responses = moxy.response();
            if (responses != null && responses.length > 0) {
                break;
            }
        }
        return responses;
    }

    public String[] getFiles() {
        String[] files = null;
        for (Moxy moxy : moxies) {
            files = moxy.file();
            if (files != null && files.length > 0) {
                break;
            }
        }
        return files;
    }

    public String[] getCookies() {
        String[] cookies = null;
        for (Moxy moxy : moxies) {
            cookies = moxy.cookie();
            if (cookies != null && cookies.length > 0) {
                break;
            }
        }
        return cookies;
    }

    public String[] getContentTypes() {
        String[] contentTypes = null;
        for (Moxy moxy : moxies) {
            contentTypes = moxy.contentType();
            if (contentTypes != null && contentTypes.length > 0) {
                break;
            }
        }
        return contentTypes;
    }

    public int[] getStatusCodes() {
        int[] statusCodes = null;
        for (Moxy moxy : moxies) {
            statusCodes = moxy.statusCode();
            if (statusCodes != null && statusCodes.length > 0) {
                break;
            }
        }
        return statusCodes;
    }

    public String getProxy() {
        String proxy = null;
        for (Moxy moxy : moxies) {
            proxy = moxy.proxy();
            if (proxy != null) {
                break;
            }
        }
        return proxy;
    }


}
