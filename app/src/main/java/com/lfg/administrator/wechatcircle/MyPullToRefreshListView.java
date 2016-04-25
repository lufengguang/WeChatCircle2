package com.lfg.administrator.wechatcircle;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by FengguangLu on 2016/4/16.
 */
public class MyPullToRefreshListView extends ListView implements AbsListView.OnScrollListener {
    private RelativeLayout headView;
    private ImageView scro_iv;
    private OnPullDownRefreshListener mPullDownRefreshListener;
    private int firstVisibilityItem;
    private boolean isComplete;   //下拉刷新完成状态标记
    private boolean isBegin;       //开始下拉标记
    private int headViewHeight;
    private Animation animation;    //持续旋转动画
    private RotateAnimation rotateAnimation; //动态旋转tween动画
    private TranslateAnimation translateAnimation;
    private AnimationSet animationSet;
    private int startY;   //开始下滑Y坐标
    private int offsetY;  //总下滑高度
    private int remainY;  //headView 向上偏移剩下的高度，即旋转图标离顶顶部的距离和自身高度的和
    private int now_Y;    //现在向上偏移的值，(offsetY-remain)
    private int old_Y;    //上一次偏移的值,记录上一次旋转角度用到。
    private Context context;
    final private int HIDE_HEADVIEW = 1;
    final private int CANCEL_SCRO = 2;
    final private int QUICK_SMOOTH_SCRO = 3;
    private int remain_count;
    private int scro_distanceY;

    public MyPullToRefreshListView(Context context) {
        super(context);
        init(context);
    }

    public MyPullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public MyPullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    /**
     * 初始化
     * @param context
     */
    private void init(Context context) {
        this.context = context;
        headView = (RelativeLayout) View.inflate(context, R.layout.pulltorefresh_headview, null);
        scro_iv = (ImageView) headView.findViewById(R.id.scro_iv);
        measureView(headView);
        addHeaderView(headView);
        headViewHeight = headView.getMeasuredHeight();
        headViewHeight -= DisplayUtils.dp2px(context, 160);
        headView.setPadding(0, -headViewHeight, 0, 0);
        animation = AnimationUtils.loadAnimation(context, R.anim.head_view_scroll_view_rotate_anim);
        remainY = DisplayUtils.dp2px(context, 64);
        isBegin = false;
        isComplete = true;
        old_Y = 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (ifAtTop() && !isBegin && isComplete) {
                    startY = (int) ev.getRawY();
                    isBegin = true;
                    isComplete = false;
                }
                if (isBegin) {
                    offsetY = (int) ev.getRawY() - startY;
                    headView.setPadding(0, offsetY - headViewHeight, 0, 0);
                    now_Y = offsetY - remainY;
                    rotateAnimation = new RotateAnimation(old_Y % 360, now_Y % 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setFillAfter(true);
                    scro_iv.startAnimation(rotateAnimation);
                    if (now_Y >= 0) {
                        setMarginBottom(-now_Y);
                    }
                    old_Y = now_Y;
                    if (offsetY > 0) return true;   //如果滑动事件未结束，ListView不执行滑动
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isBegin) {
                    scro_iv.startAnimation(animation);
                    if (offsetY > remainY) {
                        scro_distanceY=offsetY;
                        final Timer timer=new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if(scro_distanceY>0) {
                                    scro_distanceY-=5;
                                    Message msg = new Message();
                                    msg.what = QUICK_SMOOTH_SCRO;
                                    msg.arg1=scro_distanceY;
                                    handler.sendMessage(msg);
                                }else {
                                    timer.cancel();
                                }
                            }
                        },1,1);
                        mPullDownRefreshListener.onRefresh();
                    } else {
                        headView.setPadding(0, -headViewHeight, 0, 0);
                        setMarginBottom(0);
                        isComplete = true;
                    }
                    isBegin = false;
                    offsetY = 0;
                    old_Y = 0;
                }
                break;

        }
        return super.onTouchEvent(ev);
    }

    public void setPullDownRefreshListener(OnPullDownRefreshListener pullDownRefreshListener) {
        this.mPullDownRefreshListener = pullDownRefreshListener;
    }

    public void pullDownRefreshCompleted() {
        remain_count = remainY;
       final Timer  mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = HIDE_HEADVIEW;
                message.arg1 = remain_count--;
                if (remain_count >= 0) handler.sendMessage(message);
                else {
                    message.what = CANCEL_SCRO;
                    mTimer.cancel();
                    isComplete = true;
                }
            }

        }, 2, 2);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibilityItem = firstVisibleItem;
    }

    /**
     * 测量View
     *
     * @param child
     */
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * 判断listView是否在最顶部
     *
     * @return
     */
    private boolean ifAtTop() {
        return firstVisibilityItem == 0 && this.getChildAt(0).getTop() == 0;
    }

    private void setMarginBottom(int offset) {
        scro_iv.bringToFront();
        LinearLayout.LayoutParams rp = (LinearLayout.LayoutParams) scro_iv.getLayoutParams();
        //MarginLayoutParams p=scro_iv.g
        rp.setMargins(DisplayUtils.dp2px(context, 16), 0, 0, DisplayUtils.dp2px(context, 160) - offset);
        scro_iv.setLayoutParams(rp);
        if (android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }
    public interface OnPullDownRefreshListener {
        public void onRefresh();

    }
    final Handler handler = new Handler() {

        public void handleMessage(Message msg) {         // handle message
            switch (msg.what) {
                case HIDE_HEADVIEW:
                    setMarginBottom(msg.arg1);
                    break;
                case CANCEL_SCRO:
                    scro_iv.clearAnimation();
                    break;
                case QUICK_SMOOTH_SCRO:
                    setMarginBottom(remainY-msg.arg1);
                    headView.setPadding(0,msg.arg1-headViewHeight,0,0);
                    break;
            }
            super.handleMessage(msg);
        }

    };
}
