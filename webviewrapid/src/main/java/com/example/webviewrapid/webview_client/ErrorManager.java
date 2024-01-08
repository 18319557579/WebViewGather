package com.example.webviewrapid.webview_client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.webviewrapid.R;
import com.example.webviewrapid.base.BaseWebView;

public class ErrorManager {
    private final View mErrorView;  //错误页面
    private TextView tvErrorOrigin;
    private TextView tvErrorDescription;
    private TextView tvErrorCode;

    private final OnRefreshClickListener mOnRefreshClickListener;

    public ErrorManager(BaseWebView webView, OnRefreshClickListener onRefreshClickListener) {
        this.mOnRefreshClickListener = onRefreshClickListener;

        FrameLayout webViewParent = (FrameLayout) webView.getParent();
        mErrorView = LayoutInflater.from(webViewParent.getContext()).inflate(R.layout.by_load_url_error, null);
        tvErrorOrigin = mErrorView.findViewById(R.id.wera_error_origin_tv);
        tvErrorDescription = mErrorView.findViewById(R.id.wera_error_description_tv);
        tvErrorCode = mErrorView.findViewById(R.id.wera_error_code_tv);
        mErrorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRefreshClickListener != null)
                    mOnRefreshClickListener.onClick();
            }
        });

        webViewParent.addView(mErrorView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setErrorInfo(String errorUrl, String errorDescription, int errorCode) {
        tvErrorOrigin.setText("目标地址: " + errorUrl);
        tvErrorDescription.setText("错误原因: " + errorDescription);
        tvErrorCode.setText("错误码: " + errorCode);
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
