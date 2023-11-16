package com.example.middleagent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.middleagent.config.DefaultConfig;
import com.example.middleagent.config.WebUtilsConfig;
import com.example.middleagent.indicator.CoolIndicatorLayout;
import com.example.middleagent.popwindow.TopPopWindow;
import com.example.utilsgather.logcat.LogUtil;
import com.example.utilsgather.ui.StatusBarUtil;
import com.example.utilsgather.version.VersionCodeUtil;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

import java.util.Map;


public class OpenWebActivity extends AppCompatActivity {
    private AgentWeb mAgentWeb;

    private String currentUrl;
    private WebUtilsConfig webConfig;

    public static final String TAG = "OpenWebActivity";  //用于每个不同url之间的分隔
    public static final String TAG_SPLIT = "OpenWebActivity_Split";  //用于url和cookie之间的分隔

    private TextView mTitleTextView;  //标题
    private TextView mTitleBack;  //返回文字
    private ImageView ivMore;  //展开更多
    private LinearLayout llWebTitle;  //标题Layout
    private View titleLine;  //标题分割线

    /**
     * 只传url
     */
    public static void openWebView(Context context, String url) {
        openWebView(context, url, null, null);
    }

    /**
     * 传url和config
     */
    public static void openWebView(Context context, String url, WebUtilsConfig config) {
        openWebView(context, url, config, null);
    }

    /**
     * 传url和cookies
     */
    public static void openWebView(Context context, String url, Map<String, String> cookies) {
        openWebView(context, url, null, cookies);
    }

    /**
     * 这是总的方法，包括传url，config，cookies
     */
    public static void openWebView(Context context, String url, @Nullable WebUtilsConfig config, @Nullable Map<String, String> cookies) {
        Intent intent = new Intent(context, OpenWebActivity.class);
        intent.putExtra("url", url);
        if (url == null) {
            Toast.makeText(context, "链接不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (config != null) {
            intent.putExtra("config", config);
        }

        if (cookies != null && cookies.size() != 0) {
            StringBuilder cookieStr = new StringBuilder();
            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                cookieStr.append(entry.getKey()).append(TAG_SPLIT).append(entry.getValue()).append(TAG);
            }
            intent.putExtra("cookie", cookieStr.substring(0, cookieStr.length() - TAG.length()));
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_web);

        findView();
        addViewListener();

        cleanCookies();
        parseExtra();
        statusBarAndTitle();
        doConfig();
        goUrl();
    }

    private void findView() {
        mTitleTextView = findViewById(R.id.middleagent_tv_title_info);
        mTitleBack = findViewById(R.id.middleagent_tv_back);
        ivMore = findViewById(R.id.middleagent_more_web);
        llWebTitle = findViewById(R.id.middleagent_ll_web_title);
        titleLine = findViewById(R.id.middleagent_line_divider);
    }

    private void addViewListener() {
        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View ivMoreView) {
                TopPopWindow topPopWindow = new TopPopWindow(OpenWebActivity.this, new TopPopWindow.OnTopItemClickListener() {
                    @Override
                    public void onItemClick(View v) {
                        if (v.getId() == R.id.tv_01) {
                            LogUtil.d("按钮1被点击");
                        } else if (v.getId() == R.id.tv_02) {
                            LogUtil.d("按钮2被点击");
                        } else if (v.getId() == R.id.tv_03) {
                            LogUtil.d("按钮3被点击");
                        }
                    }
                });
                topPopWindow.showAaDropDownView(ivMoreView);
            }
        });
        mTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void cleanCookies() {
        //清除所有cookies
        AgentWebConfig.removeExpiredCookies();
        AgentWebConfig.removeSessionCookies();
        AgentWebConfig.removeAllCookies();
    }

    private void parseExtra() {
        Intent obtainedIntent = getIntent();
        currentUrl = obtainedIntent.getStringExtra("url");
        webConfig = getIntent().getParcelableExtra("config");
        if (webConfig == null) {
            webConfig = DefaultConfig.getDefaultConfig(this);
        }

        String cookie = obtainedIntent.getStringExtra("cookie");
        if (!TextUtils.isEmpty(cookie)) {
            String[] cookieTemp = cookie.split(TAG);  //先分隔每一个不同url的cookie
            for (String singleCookie : cookieTemp ) {
                AgentWebConfig.syncCookie(singleCookie.split(TAG_SPLIT)[0], singleCookie.split(TAG_SPLIT)[1]);  //分别拿到url和cookie
            }
        }
    }

    private void statusBarAndTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (VersionCodeUtil.versionReached(Build.VERSION_CODES.KITKAT)) {
            //这个是让内容能能够嵌入到状态栏中
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //让标题栏的上边距和标题栏的高度一致。如果标题栏可见性为GONE，那么上边距的这个效果也会消失，那么内容就会跑到标题栏中了
            llWebTitle.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
        }
    }

    private void doConfig() {
        if (webConfig.isShowBackText()) {
            mTitleBack.setText("~".equals(webConfig.getBackText()) ? "返回" : webConfig.getBackText());
        } else {
            mTitleBack.setText("");
        }

        if (webConfig.getTitleBackgroundRes() != -1) {
            llWebTitle.setBackgroundResource(webConfig.getTitleBackgroundRes());
        }

        if (webConfig.getTitleBackgroundColor() != -1) {
            llWebTitle.setBackgroundColor(getResources().getColor(webConfig.getTitleBackgroundColor()));
        }

        if (webConfig.getBackBtnRes() != -1) {
            mTitleBack.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(webConfig.getBackBtnRes()), null, null, null);
        }

        if (webConfig.getMoreBtnRes() != -1) {
            ivMore.setImageResource(webConfig.getMoreBtnRes());
        }
        if (webConfig.isShowMoreBtn()) {
            ivMore.setVisibility(View.VISIBLE);
        } else {
            ivMore.setVisibility(View.GONE);
        }

        if (webConfig.isShowTitleLine()) {
            titleLine.setVisibility(View.VISIBLE);
        } else {
            titleLine.setVisibility(View.GONE);
        }
        if (webConfig.getTitleLineColor() != -1) {
            titleLine.setBackgroundColor(webConfig.getTitleLineColor());
        }

        if (webConfig.isShowTitleView()) {
            llWebTitle.setVisibility(View.VISIBLE);
        } else {
            llWebTitle.setVisibility(View.GONE);
        }
        if (webConfig.getTitleTextColor() != -1) {
            mTitleTextView.setTextColor(getResources().getColor(webConfig.getTitleTextColor()));
        }

        if (webConfig.getBackTextColor() != -1) {
            mTitleTextView.setTextColor(getResources().getColor(webConfig.getBackTextColor()));
        }

        if (VersionCodeUtil.versionReached(Build.VERSION_CODES.M)) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            if (webConfig.isStateBarTextColorDark()) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }

    private void goUrl() {
        AgentWeb.IndicatorBuilder indicatorBuilder = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout)findViewById(R.id.middleagent_ll_web_root), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        AgentWeb.CommonBuilder commonBuilder;
        if (webConfig.isUseCoolIndicator()) {
            commonBuilder = indicatorBuilder.setCustomIndicator(new CoolIndicatorLayout(OpenWebActivity.this));
        } else if (webConfig.getIndicatorColor() != -1){
            commonBuilder = indicatorBuilder.useDefaultIndicator(webConfig.getIndicatorColor());
        } else {
            commonBuilder = indicatorBuilder.useDefaultIndicator();
        }

        mAgentWeb = commonBuilder.setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .setMainFrameErrorView(webConfig.getErrorLayoutId(), -1)
                .createAgentWeb()
                .ready()
                .go(currentUrl);
    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (mTitleTextView != null) {
                mTitleTextView.setText(title);  //设置标题
            }
        }
    };

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtil.d("url变成了：" + url);
            currentUrl = url;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb != null && mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        super.onDestroy();
    }
}