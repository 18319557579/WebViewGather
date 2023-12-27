package com.example.webviewrapid.client;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.floatlayer.FloatLayoutManager;
import com.example.floatlayer.layer.FloatLayer;
import com.example.utilsgather.context.ApplicationGlobal;
import com.example.utilsgather.context.ContextUtil;
import com.example.utilsgather.jump.JumpActivityUtils;
import com.example.utilsgather.logcat.LogUtil;
import com.example.utilsgather.package_info.PackageInfoUtil;
import com.example.webviewrapid.R;
import com.example.webviewrapid.WebViewActivity;
import com.example.webviewrapid.floatlayer.JumpFloatLayerParams;

import java.io.InputStream;

public class RapidWebViewClient extends WebViewClient {
    /**
     * 我发现在webView.loadUrl()加载的url是不会回调到这里的，说白了这里就是在WebView中点击的链接才会在这里进行回调，给开发者一个控制的机会
     * 无论回调true/false，都将会把该url交给webview进行处理，而不是跳转到其他的浏览器（前提是setWebViewClient被调用）
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        LogUtil.d("shouldOverrideUrlLoading 获取跳转的url：" + url);
        LogUtil.d("RapidWebViewClient", "shouldOverrideUrlLoading 当前的线程信息：" + Thread.currentThread());

        if (url.startsWith("http://") || url.startsWith("https://")) {
            return false;
        }

//        WebViewActivity webViewActivity = (WebViewActivity) ContextUtil.getCurrentActivity();
        ViewGroup parent = (ViewGroup) view.getParent();

        FloatLayer floatLayer = new FloatLayer((FrameLayout) parent, R.layout.medi_tiny_message_bar);
        String appName = PackageInfoUtil.getAppNameByUrl(url);
        ((TextView) floatLayer.findView(R.id.flla_jump_title_tv)).setText(String.format("允许网站打开 %s 吗？", appName));
        ((TextView) floatLayer.findView(R.id.fila_jump_confirm_tv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpActivityUtils.startApp(view.getContext(), url);
                floatLayer.dismissMaybeAnim();  //这里不使用出场动画
            }
        });

        //todo 1.如果手机里没有该应用，应该怎么处理 2.还是遇到很多看起来是无效的url
        FloatLayoutManager.getInstance().show(JumpFloatLayerParams.getJumpConfig(), floatLayer, JumpFloatLayerParams.jumpLabel, 0);

        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        LogUtil.d("回调onPageStarted " + url);
        LogUtil.d("RapidWebViewClient", "onPageStarted 当前的线程信息：" + Thread.currentThread());
    }

    /**
     * 调用京东网页时，会出现一次onPageStarted()回调，而多次onPageFinished()回调的情况；还有onPageFinished()和onPageStarted()
     * url不完全一致的情况
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        LogUtil.d("回调onPageFinished " + url);
        LogUtil.d("RapidWebViewClient", "onPageFinished 当前的线程信息：" + Thread.currentThread());
    }

    /**
     * 重写此方法才能处理浏览器中的按键事件
     * todo 看看这个按键事件能带来什么用处
     */
    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        LogUtil.d("查看WebView中的按键事件：" + event);
        return super.shouldOverrideKeyEvent(view, event);
    }

    /**
     * 页面每一次请求资源之前都会调用这个方法。非UI线程调用，意思就是在该回调中时非UI线程
     * 此回调可用于各种URL方案(例如，http(s):、data:、file:等)，而不仅仅是那些通过网络发送请求的方案。javascript: URLs, blob: URLs，或者通过file:///android_asset/或file:// android_res/ URLs访问的资源都不会调用这个函数。
     */
    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        LogUtil.d("RapidWebViewClient", "打算拦截请求？ " + url);
        LogUtil.d("RapidWebViewClient", "shouldInterceptRequest 当前的线程信息：" + Thread.currentThread());

        /*//如果是百度标题那个Logo的Url，那么用本地的图片进行替换
        if (url.equals("https://www.baidu.com/img/flexible/logo/plus_logo_web_2.png")) {
            try {
                InputStream is = view.getContext().getResources().openRawResource(R.raw.webviewrapid_set_meal) ;
                WebResourceResponse response = new WebResourceResponse("image/png", "UTF-8", is);
                Thread.sleep(10000);  //这里延迟10秒。只有这里成功返回后onPageFinished才会进行回调
                return response;
            } catch (Resources.NotFoundException exception) {
                exception.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }*/


        return super.shouldInterceptRequest(view, request);
    }

    /**
     * 页面加载资源时调用，每加载一个资源（比如图片）就调用一次。（shouldInterceptRequest是请求，onLoadResource是加载）
     */
    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);

        LogUtil.d("RapidWebViewClient", "onLoadResource的url：" + url);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LogUtil.i( "RapidWebViewClient", "onReceivedError:" + error.getDescription() + " code:" + error.getErrorCode() + " failingUrl:" + request.getUrl().toString() + " getUrl:" + view.getUrl() + " getOriginalUrl:" + view.getOriginalUrl());
        }
        //用这种方式的会造成死循环，用户从错误页面返回后，由于出错，又会回到这个页面
//        view.loadUrl("file:///android_asset/webviewrapid_error_handle.html");


        //todo 这里存在一个bug，包括AgentWeb都存在该bug，就是长按后可以选择到地下的内容
        Activity activity = ContextUtil.getCurrentActivity();
        FrameLayout frameLayoutContainer = activity.findViewById(R.id.webviewrapid_rl_container);

        LayoutInflater mLayoutInflater = LayoutInflater.from(activity);
        View errorView = mLayoutInflater.inflate(R.layout.webviewrapid_error_layout, null, false);

        ViewStub mViewStub = (ViewStub) activity.findViewById(R.id.webviewrapid_id_error_viewstub);
        final int index = frameLayoutContainer.indexOfChild(mViewStub);
        frameLayoutContainer.removeViewInLayout(mViewStub);

        frameLayoutContainer.addView(errorView, index, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        errorView.setVisibility(View.VISIBLE);
        errorView.findViewById(R.id.middleagent_btn_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.reload();
            }
        });
    }

    /*void hideErrorLayout() {
        View mView = null;
        if ((mView = this.findViewById(R.id.mainframe_error_container_id)) != null) {
            mView.setVisibility(View.GONE);
        }
    }*/

    /**
     * SSL证书校验错误
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        LogUtil.d("SSL证书校验错误 " + error.getUrl() + " | " + error.getPrimaryError());
        new AlertDialog.Builder(view.getContext())
                .setTitle("提示")
                .setMessage("当前网站安全证书已过期或不可信\n是否继续浏览?")
                .setPositiveButton("继续浏览", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handler.proceed();
                    }
                })
                .setNegativeButton("返回上一页", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handler.cancel();
                    }
                })
                .create()
                .show();
    }

    /**
     * 我也发现这玩意的回调基本在onPageStarted之后，onPageFinished之前，如果onPageFinished被回调两次，那么它也会
     * 跟着回调两次相同的URL
     */
    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        super.doUpdateVisitedHistory(view, url, isReload);
        LogUtil.d("doUpdateVisitedHistory被回调，url：" + url + ", isReload: " + isReload);
    }
}
