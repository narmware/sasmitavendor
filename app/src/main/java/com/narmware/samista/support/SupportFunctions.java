package com.narmware.samista.support;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by savvy on 3/14/2018.
 */

public class SupportFunctions {



    public static String appendParam(String url, HashMap<String, String> params){

        StringBuilder sbParams;
        String api_url;
        String charset = "UTF-8";

        sbParams = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            try {
                if (i != 0){
                    sbParams.append("&");
                }
                sbParams.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), charset));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }
        api_url=url+"?"+sbParams;
        return api_url;
    }

    public static String splitString(String str)
    {
        String currentString = str;
        String[] separated = currentString.split(",");
        String oneStr=separated[0];
        String twoStr=separated[1];

        return currentString;
    }


}
