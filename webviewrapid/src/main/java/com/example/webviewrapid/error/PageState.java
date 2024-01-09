package com.example.webviewrapid.error;

import android.view.View;

import com.example.utilsgather.logcat.LogUtil;
import com.example.webviewrapid.facade.RapidWebView;

public class PageState {
    protected RapidWebView mRapidWebView;
    private ErrorViewManager theErrorViewManager;

    public MyState currentState = MyState.NORMAL;  //保存当前的状态

    public PageState(RapidWebView mRapidWebView) {
        this.mRapidWebView = mRapidWebView;
    }

    public void handleState(MyState myState) {
        LogUtil.d("当前的状态: " + currentState);

        switch (myState) {
            case NORMAL:  //只有等待才会切为正常状态
                mRapidWebView.getRealWebView().setVisibility(View.VISIBLE);
                if (theErrorViewManager != null) {
                    theErrorViewManager.hide();
                }
                break;

            case ERROR:  //正常或等待都可能切为错误
                if (currentState.equals(MyState.NORMAL)) {  //如果是正常状态切为错误,那么还要将WebView设为不可见
                    mRapidWebView.getRealWebView().setVisibility(View.INVISIBLE);
                }

                makeErrorViewShow();
                theErrorViewManager.setErrorInfo(myState.errorUrl, myState.errorDescription, myState.errorCode);

                break;

            case WAITING:  //只有错误的状态才会切为等待
                if (theErrorViewManager != null) {
                    theErrorViewManager.hide();
                }

                break;
        }
        currentState = myState;
        LogUtil.d("切换后的状态: " + currentState);
    }

    private void makeErrorViewShow() {
        if (theErrorViewManager == null) {
            theErrorViewManager = new ErrorViewManager(
                mRapidWebView.getRealWebView(),
                mRapidWebView.theErrorLayoutId,
                mRapidWebView.theClickReloadViewId,
                () -> mRapidWebView.reload()
            );
        } else {
            theErrorViewManager.show();
        }
    }

    public ErrorViewManager getErrorViewManager() {
        return theErrorViewManager;
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
