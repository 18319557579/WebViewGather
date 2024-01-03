package com.example.webviewgather;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.utilsgather.logcat.LogUtil;
import com.example.utilsgather.ui.screen.ScreenFunctionUtils;
import com.example.webviewgather.interaction.JSCallAndroidObject;
import com.example.webviewrapid.facade.RapidWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSWithAndroidActivity extends AppCompatActivity {

    public static final String TAG = "PASSED_URL";  //用于每个不同url之间的分隔
    RapidWebView rapidWebView;

    public static void start(Context context, String passedUrl) {
        Intent intent = new Intent(context, JSWithAndroidActivity.class);
        intent.putExtra(TAG, passedUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jswithandroid);

        ScreenFunctionUtils.hideActionBar(this);

        rapidWebView = RapidWebView.with(JSWithAndroidActivity.this)
                .setWebParent(findViewById(R.id.ll_out_container_js_with_android), new LinearLayout.LayoutParams(-1, -1))
                .loadUrl(getIntent().getStringExtra(TAG));

        rapidWebView.getRealWebView().addJavascriptInterface(new JSCallAndroidObject(), "jscallandroid");

        findViewById(R.id.btn_operation_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rapidWebView.loadUrl("javascript:calledBy_loadUrl('Hello JS, I am from loadUrl().')");
            }
        });
        findViewById(R.id.btn_operation_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //{"page": "pagefirst", "code": 6, "data":{"weigth": 50.5, "person": ["hsf", {"name": "hwt", "nickname": null}]}}
                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj.put("page", "pagefirst");
                    jsonObj.put("code", 6);

                    JSONObject jsonObjInner = new JSONObject();
                    jsonObjInner.put("weight", 50.5);

                    JSONArray jsonArrayInner = new JSONArray();
                    jsonArrayInner.put("hsf");
                    JSONObject jsonArrayInnerObj = new JSONObject();
                    jsonArrayInnerObj.put("name", "hwt");
                    jsonArrayInnerObj.put("nickname", JSONObject.NULL);
                    jsonArrayInner.put(jsonArrayInnerObj);

                    jsonObjInner.put("person", jsonArrayInner);

                    jsonObj.put("data", jsonObjInner);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("javascript:calledBy_evaluateJavascript('");
                stringBuffer.append(jsonObj.toString());
                stringBuffer.append("')");

                rapidWebView.evaluateJavascript(stringBuffer.toString(), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        LogUtil.d("Android得到了JS的返回值: " + value);
                    }
                });
            }
        });
        findViewById(R.id.btn_operation_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebBackForwardList webBackForwardList = rapidWebView.getRealWebView().copyBackForwardList();
                for (int i = 0; i < webBackForwardList.getSize(); i++) {
                    WebHistoryItem webHistoryItem = webBackForwardList.getItemAtIndex(i);
                    LogUtil.d("index：" + i + ", webHistoryItem:" + webHistoryItem.getUrl() + " | " +
                            webHistoryItem.getOriginalUrl() + " | " + webHistoryItem.getTitle() + " | " +
                            webHistoryItem.getFavicon());

                    String host = Uri.parse(webHistoryItem.getUrl()).getHost();
                    LogUtil.d("打印host：" + host);
                    if (TextUtils.isEmpty(host)) {
                        LogUtil.d("该页面是空的页面");
                    }

                }
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && rapidWebView.canGoBackReal()) {
            rapidWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}