package com.example.webviewrapid.utils;

import java.util.regex.Pattern;

public class WebUtil {
    /**
     * 判断是否为有效的url
     */
    public boolean isValidUrl(String inputUrl) {


        if (inputUrl.startsWith("http:") || inputUrl.startsWith("https:")) {
            return true;
        }

        Pattern pattern = Pattern.compile(
                ".(com|net|org|cc|edu|gov|io|cn|info|biz|mil|name|int|tech|health|travel|museum|jobs|mobi|xxx|adult|sex|porn)$");
        if (pattern.matcher(inputUrl).find()) {
            return true;
        }

        pattern = Pattern.compile(
                "[^0-9a-zA-Z$\\-_.+!()?/:@&%]");
        if (pattern.matcher(inputUrl).find()) {
            return false;
        }


        return false;


    }


}
