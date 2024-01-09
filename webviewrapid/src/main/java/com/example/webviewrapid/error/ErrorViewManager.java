package com.example.webviewrapid.error;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.webviewrapid.R;
import com.example.webviewrapid.base.BaseWebView;

public class ErrorViewManager {
    private View mErrorView;  //错误页面
    private final boolean useDefaultErrorView;
    private TextView tvErrorOrigin;
    private TextView tvErrorDescription;
    private TextView tvErrorCode;

    private final OnRefreshClickListener mOnRefreshClickListener;

    public ErrorViewManager(BaseWebView webView, int theErrorLayoutId, int theClickReloadViewId, OnRefreshClickListener onRefreshClickListener) {
        this.mOnRefreshClickListener = onRefreshClickListener;

        FrameLayout webViewParent = (FrameLayout) webView.getParent();

        //判断用户是否传了错误页面的id, 没有的话就用预设的
        useDefaultErrorView = theErrorLayoutId == 0;

        if (useDefaultErrorView) {  //如果是预设的错误页面, 则可以设置3个显示信息
            mErrorView = LayoutInflater.from(webViewParent.getContext()).inflate(R.layout.by_load_url_error, null);
            tvErrorOrigin = mErrorView.findViewById(R.id.wera_error_origin_tv);
            tvErrorDescription = mErrorView.findViewById(R.id.wera_error_description_tv);
            tvErrorCode = mErrorView.findViewById(R.id.wera_error_code_tv);
            mErrorView.setOnClickListener(v -> {  //预设的话就不用管theClickReloadViewId了, 也避免了一个业务bug
                if (mOnRefreshClickListener != null)
                    mOnRefreshClickListener.onClick();
            });

        } else {
            mErrorView = LayoutInflater.from(webViewParent.getContext()).inflate(theErrorLayoutId, null);
            //判断用户是否传了点击重试view的id, 没有的话就点击整体进行刷新
            View clickReloadView = theClickReloadViewId == 0 ? mErrorView : mErrorView.findViewById(theClickReloadViewId);
            clickReloadView
                .setOnClickListener(v -> {
                    if (mOnRefreshClickListener != null)
                        mOnRefreshClickListener.onClick();
                });
        }
        webViewParent.addView(mErrorView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setErrorInfo(String errorUrl, String errorDescription, int errorCode) {
        if (useDefaultErrorView) {
            tvErrorOrigin.setText("目标地址: " + errorUrl);
            tvErrorDescription.setText("错误原因: " + errorDescription);
            tvErrorCode.setText("错误码: " + errorCode);
        }
    }

    public void show() {
        mErrorView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mErrorView.setVisibility(View.INVISIBLE);
    }

    /**
     * 判断当前错误页面是否在隐藏着
     */
    public boolean isHide() {
        return mErrorView.getVisibility() == View.INVISIBLE;
    }

    public interface OnRefreshClickListener {
        void onClick();
    }
}
