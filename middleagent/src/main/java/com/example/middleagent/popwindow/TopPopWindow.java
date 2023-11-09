package com.example.middleagent.popwindow;

import android.animation.Animator;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.example.middleagent.R;
import com.example.utilsgather.logcat.LogUtil;
import com.example.utilsgather.ui.SizeTransferUtil;

public class TopPopWindow extends PopupWindow {
    private final Activity mActivity;

    private final OnTopItemClickListener mItemClickListener;

    private AnimUtil mAnimUtil;

    private static final long DURATION = 500;
    private static final float START_ALPHA = 0.5f;
    private static final float END_ALPHA = 1f;

    private boolean bright = false;  //要转换的明亮状态

    public TopPopWindow(Activity mActivity, OnTopItemClickListener mItemClickListener) {
        this.mActivity = mActivity;
        this.mItemClickListener = mItemClickListener;

        View popWindowView = LayoutInflater.from(mActivity).inflate(R.layout.layout_top_add, null);
        setContentView(popWindowView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        popWindowView.findViewById(R.id.tv_01).setOnClickListener(clickListener);
        popWindowView.findViewById(R.id.tv_02).setOnClickListener(clickListener);
        popWindowView.findViewById(R.id.tv_03).setOnClickListener(clickListener);

        getContentView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                LogUtil.d("PopupWindow的焦点发生变化：" + hasFocus);
            }
        });

        //当PopupWindow被关闭了
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                LogUtil.d("PopupWindow消失了");
                mAnimUtil.startAnimator();
            }
        });

        setFocusable(true);

        setAnimation();
    }

    private void setAnimation() {
        mAnimUtil = new AnimUtil();
        mAnimUtil.setValueAnimator(START_ALPHA, END_ALPHA, DURATION);
        mAnimUtil.addUpdateListener(new AnimUtil.UpdateListener() {
            @Override
            public void progress(float progress) {
                float bgAlpha = bright ? progress : (START_ALPHA + END_ALPHA - progress);
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = bgAlpha;
                mActivity.getWindow().setAttributes(lp);
            }
        });
        mAnimUtil.addEndListener(new AnimUtil.EndListener() {
            @Override
            public void endUpdate(Animator animator) {
                bright = !bright;
            }
        });
    }

    public void showAaDropDownView(View view) {
        //view代表基准的位置，后面两个参数是相对基准的位置之偏移。
        //如果不填gravity，那么默认PopupWindow的左上角在参照物的左下角；现在设置了right，代表PopupWindow的右上角在参照物的左下角
        //PopupWindow的宽度为110dp，再往左10dp，就会给右边留出10dp的空隙
        showAsDropDown(view, SizeTransferUtil.dp2px(110 + 10, mActivity) * -1,
                SizeTransferUtil.dp2px(5, mActivity) * -1, Gravity.RIGHT);
        mAnimUtil.startAnimator();
    }

    public interface OnTopItemClickListener {
        void onItemClick(View v);
    }
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v);
            }
            dismiss();
        }
    };
}
