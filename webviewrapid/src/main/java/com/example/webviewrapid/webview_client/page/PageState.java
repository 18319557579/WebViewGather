package com.example.webviewrapid.webview_client.page;

import android.view.View;

import com.example.utilsgather.logcat.LogUtil;
import com.example.webviewrapid.facade.RapidWebView;
import com.example.webviewrapid.webview_client.ErrorManager;

public class PageState {
    protected RapidWebView mRapidWebView;
    public ErrorManager theErrorManager;

    public MyState currentState = MyState.NORMAL;

    public PageState(RapidWebView mRapidWebView) {
        this.mRapidWebView = mRapidWebView;
    }

    public void handleState(MyState myState) {
        LogUtil.d("当前的状态: " + currentState);

        switch (myState) {
            case NORMAL:  //只有等待才会切为正常状态
                mRapidWebView.getRealWebView().setVisibility(View.VISIBLE);
                if (theErrorManager != null) {
                    theErrorManager.hide();
                }
                break;

            case ERROR:  //正常或等待都可能切为错误
                if (currentState.equals(MyState.NORMAL)) {  //如果是正常状态切为错误,那么还要将WebView设为不可见
                    mRapidWebView.getRealWebView().setVisibility(View.INVISIBLE);
                }

                makeErrorViewShow();
                theErrorManager.setErrorInfo(myState.errorUrl, myState.errorDescription, myState.errorCode);

                break;

            case WAITING:  //只有错误的状态才会切为等待
                if (! currentState.equals(MyState.ERROR)) {
                    LogUtil.d("当前是非错误状态, 不用切状态 (讲道理是正常状态了)");
                    return;
                }
                if (theErrorManager != null) {
                    theErrorManager.hide();
                }

                break;
        }
        currentState = myState;
        LogUtil.d("切换后的状态: " + currentState);
    }

    private void makeErrorViewShow() {
        if (theErrorManager == null) {
            theErrorManager = new ErrorManager(mRapidWebView.getRealWebView(), () -> mRapidWebView.reload());
        } else {
            theErrorManager.show();
        }
    }

    public enum MyState {
        NORMAL,  //WebView可见, errorView不可见或为null
        ERROR,  //WebView不可见, errorView可见
        WAITING;  //WebView不可见, errorView不可见

        private String errorUrl;
        private String errorDescription;
        private int errorCode;

        public MyState setErrorInfo(String errorUrl, String errorDescription, int errorCode) {
            this.errorUrl = errorUrl;
            this.errorDescription = errorDescription;
            this.errorCode = errorCode;
            return this;
        }

    }
}
