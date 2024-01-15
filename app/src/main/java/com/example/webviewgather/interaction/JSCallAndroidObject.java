package com.example.webviewgather.interaction;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.example.utilsgather.logcat.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSCallAndroidObject extends Object {

    @JavascriptInterface
    public String executeGreet(String parameters) {
        LogUtil.d("Android得到JS的传值: " + parameters);

        try {
            JSONObject jsonObject = new JSONObject(parameters);
            LogUtil.d(jsonObject.getString("page"));
            LogUtil.d(String.valueOf(jsonObject.getInt("code")));

            JSONObject dataObj = jsonObject.getJSONObject("data");
            LogUtil.d(String.valueOf(dataObj.getDouble("weight")));

            JSONArray jsonArray = dataObj.getJSONArray("person");
            LogUtil.d(jsonArray.getString(0));
            JSONObject jsonArrayObj = jsonArray.getJSONObject(1);
            LogUtil.d(jsonArrayObj.getString("name"));
            LogUtil.d( String.valueOf(jsonArrayObj.get("nickname")));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return "Hello JS.";
    }


}
