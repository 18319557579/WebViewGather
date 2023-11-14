package com.example.webviewrapid.base;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.example.utilsgather.logcat.LogUtil;

public class BaseWebView extends WebView {
    private final String canonicalName = getClass().getCanonicalName();

    public BaseWebView(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(canonicalName + " 回调onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d(canonicalName + " 回调onPause");
    }

    /**
     * 判断是否真的可以返回，因此一个WebView被recycle回收后，会用一个空页面进行占位。这样第二次使用该WebView时，就会
     * 出现该空页面在栈底的情况
     */
    public boolean canGoBackReal() {
        if (!this.canGoBack()) {
            return false;
        }
        WebBackForwardList webBackForwardList = this.copyBackForwardList();
        if (webBackForwardList.getCurrentIndex() == 1) {
            WebHistoryItem webHistoryItem = webBackForwardList.getItemAtIndex(0);
            String host = Uri.parse(webHistoryItem.getUrl()).getHost();
            if (TextUtils.isEmpty(host)) {
                return false;
            }
        }
        return true;
    }
}
