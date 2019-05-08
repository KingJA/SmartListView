package com.kingja.smartlistview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Description:TODO
 * Create Time:2019/4/14 14:36
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class SmartListView extends ListView implements AbsListView.OnScrollListener {

    private static final String TAG = "SmartListView";
    private View headView;
    private int firstVisibleItem;
    private boolean isRemark;
    private int startY;
    private int state;
    private final int NONE = 0;
    private final int PULL = 1;
    private final int RELESE = 2;
    private final int REFRASHING = 3;
    private int headHeight;
    private int scrollState;
    private OnRefreshListener onRefreshListener;
    private int moveY;
    private float speed;

    public SmartListView(Context context) {
        this(context, null);
    }

    public SmartListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmartListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSmartListView(context, attrs);
    }

    private void initSmartListView(Context context, AttributeSet attrs) {
        headView = View.inflate(context, R.layout.refresh_head, null);
        measureView(headView);
        headHeight = headView.getMeasuredHeight();
        Log.e(TAG, "headHeight: " + headHeight);
        topPadding(-headHeight);
        addHeaderView(headView);
        setOnScrollListener(this);
        speed = headHeight / 150f;
    }

    private void topPadding(int paddingTop) {
        headView.setPadding(headView.getPaddingLeft(), paddingTop, headView.getPaddingRight(), headView
                .getPaddingBottom());
    }

    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        this.scrollState = scrollState;
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int i1, int i2) {
        this.firstVisibleItem = firstVisibleItem;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (firstVisibleItem == 0) {
                    isRemark = true;
                    startY = (int) ev.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                int endY = (int) ev.getY();
                moveY = endY - startY;
                if (state == RELESE) {
                    state = REFRASHING;
                    refreshViewByState();
                    if (onRefreshListener != null) {
                        onRefreshListener.onRefresh();
                        smoothScrollHeadView(-headHeight+moveY,0);
                    }
                } else if (state == PULL) {
                    state = NONE;
                    isRemark = false;
                    refreshViewByState();
                    smoothScrollHeadView(-headHeight+moveY,-headHeight);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void onMove(MotionEvent ev) {
        if (!isRemark) {
            return;
        }
        int tempY = (int) ev.getY();
        int space = tempY - startY;
        int topPadding = space - headHeight;
        switch (state) {
            case NONE:
                if (space > 0) {
                    state = PULL;
                    refreshViewByState();
                }
                break;
            case PULL:
                topPadding(topPadding);
                if (space > headHeight + 30 && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    state = RELESE;
                    refreshViewByState();
                }
                break;
            case RELESE:
                topPadding(topPadding);
                if (space < headHeight + 30) {
                    state = PULL;
                    refreshViewByState();
                } else if (space <= 0) {
                    state = NONE;
                    isRemark = false;
                    refreshViewByState();
                }
                break;
            case REFRASHING:
                break;
        }
    }

    private void refreshViewByState() {
        TextView tvTip = headView.findViewById(R.id.tv_tip);
        ImageView ivArrow = headView.findViewById(R.id.iv_arrow);
        ProgressBar pb = headView.findViewById(R.id.pb);
        RotateAnimation upRotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation
                .RELATIVE_TO_SELF, 0.5f);
        upRotate.setDuration(500);
        upRotate.setFillAfter(true);
        RotateAnimation downRotate = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation
                .RELATIVE_TO_SELF, 0.5f);
        downRotate.setDuration(500);
        downRotate.setFillAfter(true);
        switch (state) {
            case NONE:
                ivArrow.clearAnimation();
//                topPadding(-headHeight);

                break;
            case PULL:
                ivArrow.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
                tvTip.setText(R.string.pulltorefresh);
                ivArrow.clearAnimation();
                ivArrow.setAnimation(downRotate);
                break;
            case RELESE:
                ivArrow.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
                tvTip.setText(R.string.releserefresh);
                ivArrow.clearAnimation();
                ivArrow.setAnimation(upRotate);
                break;
            case REFRASHING:
//                topPadding(0);
                ivArrow.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
                tvTip.setText(R.string.refreshing);
                ivArrow.clearAnimation();
                break;
        }
    }

    public void refreshComplete() {
        state = NONE;
        isRemark = false;
        refreshViewByState();
        TextView tvLastRefreshTime = headView.findViewById(R.id.tv_lastRefreshTime);
        SimpleDateFormat format = new SimpleDateFormat(getContext().getString(R.string.dateformat));
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        tvLastRefreshTime.setText(time);
        smoothScrollHeadView(0,-headHeight);
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    private void smoothScrollHeadView(int startMoveY,int endMoveY) {
        Log.e(TAG, "moveY: " + startMoveY);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startMoveY, endMoveY)
                .setDuration((long) ((headHeight + startMoveY) / speed));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int paddingTop = (int) animation.getAnimatedValue();
                Log.e(TAG, "paddingTop: " + paddingTop);
                topPadding(paddingTop);
            }
        });
        valueAnimator.start();
    }

}
