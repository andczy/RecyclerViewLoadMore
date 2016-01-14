package com.example.czy.recyclerviewloadmore.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.czy.recyclerviewloadmore.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by czy on 2015/9/21.
 */
public class PullDownLayout extends LinearLayout {

    private static final int STATE_PULL = 0x100;
    private static final int STATE_PLAY = 0x101; //already play animation ;

    private View childView;
    private FrameLayout pullView;
    /***
     * 下拉动画view
     */
    private ImageView pullIconView;
    private int pullState;
    private int downY;
    /***
     * 下拉动画view的最大高度，大于这个值不再变大
     */
    private int maxIconHeight;
    /***
     * 下拉的高度大于此高度时，播放过场动画
     */
    private int iconChangeHeight;
    /***
     * 下拉起始图片
     */
    private Drawable startDrawable;
    /***
     * 下拉过场动画
     */
    private List<Drawable> changeDrawables;
    private int closePullSpeed ;
    private boolean isRefreshing ;
    private OnRefreshListener listener ;
    /***
     * 刷新中动画
     */
    private AnimationDrawable refreshAnimation ;
    private int downX;
    private boolean canPullDown = true ;
    public PullDownLayout(Context context) {
        this(context, null);
    }

    public PullDownLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullDownLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setCanPullDown(boolean canPullDown)
    {
        this.canPullDown = canPullDown ;
    }
    public boolean isCanPullDown(){
        return canPullDown ;
    }
    public void setOnRefreshListener(OnRefreshListener listener)
    {
        this.listener = listener ;
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://关闭下拉的高度
                    int height = msg.arg1 - closePullSpeed ;
                    height = height <0?0 :height ;
                    changePullHeaderHeight(height/2);
                    if(height > 1)
                    {
                        Message closeMsg = handler.obtainMessage() ;
                        closeMsg.what = 0 ;
                        closeMsg.arg1 = height ;
                        sendMessageDelayed(closeMsg , 50 ) ;
                    }
                    else{
                        pullState = 0 ;
                    }
                    break ;
                case STATE_PLAY: //播放下拉过场动画
                    int index = msg.arg1;
                    pullIconView.setImageDrawable(changeDrawables.get(index));
                    index++;

                    if (index < changeDrawables.size()) {
                        Message playMsg = obtainMessage();
                        playMsg.what = STATE_PLAY ;
                        playMsg.arg1 = index ;
                        sendMessageDelayed(playMsg, 100);
                    }
                    break;
                case STATE_PULL: //播放上移过场动画 ， 是case STATE_PLAY:播放动画的逆过程。
                    int position = msg.arg1;
                    if(position < 0 )
                        pullIconView.setImageDrawable(startDrawable);
                    else
                        pullIconView.setImageDrawable(changeDrawables.get(position));
                    if (position >= 0) {
                        position--;
                        Message pullMsg = obtainMessage() ;
                        pullMsg.what = STATE_PULL ;
                        pullMsg.arg1 = position;
                        sendMessageDelayed(pullMsg, 100);
                    }

                    break;
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PullDownLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    void init() {
        setOrientation(LinearLayout.VERTICAL);
        maxIconHeight = (int) (getResources().getDisplayMetrics().density * 60);
        iconChangeHeight = maxIconHeight;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(canPullDown)
            doEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        initChildView();
        super.onLayout(changed, l, t, r, b);
    }

    void initChildView() {
        if (childView == null) {
            childView = getChildAt(0);
        }
    }

    void initPullView() {
        if (pullView == null) {
            pullView = new FrameLayout(getContext());
            pullIconView = new ImageView(getContext());
            int pad = (int) (getResources().getDisplayMetrics().density * 6);
            pullIconView.setPadding(0 , pad , 0 , 0 );
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            pullView.addView(pullIconView, params);

            addView(pullView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            startDrawable = getDrawable(getContext(), R.mipmap.anim_pull);

            changeDrawables = new ArrayList<>();
            changeDrawables.add(getDrawable(getContext(),R.mipmap.anim_play1));
            Drawable drawable2 = getDrawable(getContext(),R.mipmap.anim_play2) ;
            changeDrawables.add(drawable2);
            changeDrawables.add(getDrawable(getContext(),R.mipmap.anim_play3));
        }
    }
    public static Drawable getDrawable(Context context , int res)
    {
        if(Build.VERSION.SDK_INT>20)
        {
            return context.getDrawable(res);
        }
        else{
            return context.getResources().getDrawable(res);
        }
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(canPullDown)
        {
            if (doEvent(event))
                return true;
        }
        return super.onInterceptTouchEvent(event);
    }

    boolean doEvent(MotionEvent event) {
        if(isRefreshing)
            return false;
        int y = (int) event.getY();
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = 0;
                pullState = 0;
                break;
            case MotionEvent.ACTION_MOVE:

                if ( pullState == 0 && needGetTouchEvent()) {
                    if (downY == 0) {
                        downY = y;
                        downX = x ;
                    } else {
                        int subX = x - downX ;
                        if( subX == 0 || Math.abs ( (y - downY ) / ( subX)) > 1.1f){
                            int height = (y - downY) / 2;
                            if (height > 4) {
                                initPullView();
                                changePullHeaderHeight(height);
                                if (pullState < STATE_PULL) {
                                    pullState = STATE_PULL;
                                    pullIconView.setImageDrawable(startDrawable);
                                    return true;
                                }
                            }
                        }
                        else{
                            return false ;
                        }
                    }
                }
                else if ( pullState == STATE_PULL || pullState == STATE_PLAY ) {
                    int height = (y - downY) / 2;
                    changePullHeaderHeight(height);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(pullState!=0&&downY!=0)
                {
                    int height = y - downY ;
                    if(pullView != null )
                    {
                        if(height /2 > maxIconHeight)
                        {
                            if(listener!=null)
                            {
                                isRefreshing = true ;
                                listener.refresh();
                                playRefresh();
                            }
                            else{
                                closePull(height);
                            }
                        }
                        else{
                            closePull(height);
                        }
                    }
                }
                break;
        }
        return false;
    }

    boolean needGetTouchEvent()
    {
        if(childView instanceof ListView)
        {
            if(((ListView)childView).getFirstVisiblePosition() == 0 ){
                View child = ((ListView) childView).getChildAt(0);
                if (child != null) {
                    int y = (int) child.getY();
                    if(y>-2 && y < 1)
                    {
                        return true ;
                    }
                }
            }
            return false ;
        }
        if(childView.getScrollY() < 3 )
        {
            return true ;
        }
        return false ;
    }
    /***
     * 播放刷新动画
     */
    private void playRefresh() {

        ViewGroup.LayoutParams parentParams = pullView.getLayoutParams();
        parentParams.height = maxIconHeight;
        pullView.setLayoutParams(parentParams);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) pullIconView.getLayoutParams();
        params.width = params.height = maxIconHeight;
        pullIconView.setLayoutParams(params);

        pullView.invalidate();
        pullIconView.invalidate();

        //播放刷新动画
        if(refreshAnimation == null )
            refreshAnimation = (AnimationDrawable)getDrawable(getContext(), R.anim.refresh_animation);
        pullIconView.setImageDrawable(refreshAnimation);
        refreshAnimation.start();
    }

    /***
     * 关闭下拉
     * @param height
     */
    void closePull(int height){
        closePullSpeed = height / 3 ;
        downY = 0;
        Message closeMsg = handler.obtainMessage() ;
        closeMsg.what = 0 ;
        closeMsg.arg1 = height ;
        handler.sendMessage(closeMsg);
    }

    /***
     * 刷新完成，结束刷新动画
     */
    public void refreshOver(){
        isRefreshing = false ;
        if(refreshAnimation!=null)
            refreshAnimation.stop();
        if(pullView != null && pullState != 0 ){
            pullState = 0 ;
            closePull( 2*iconChangeHeight);
            pullIconView.setImageDrawable(startDrawable);
        }
    }

    /***
     * 重绘下拉view的高度
     * @param height
     */
    private void changePullHeaderHeight(int height) {
        height = height < 0 ? 0 : height ;
        ViewGroup.LayoutParams parentParams = pullView.getLayoutParams();
        parentParams.height = height;
        pullView.setLayoutParams(parentParams);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) pullIconView.getLayoutParams();
        int iconHeight = height > maxIconHeight ? maxIconHeight : height;
        params.width = params.height = iconHeight;
        pullIconView.setLayoutParams(params);
        if (height > iconChangeHeight) {
            if (pullState == STATE_PULL) {
                pullState = STATE_PLAY;
                playChangeAnimation();
            }
        }
        else
        {
            if (pullState == STATE_PLAY) {
                pullState = STATE_PULL;
                playStartAnimation();
            }
        }
        pullView.invalidate();
        pullIconView.invalidate();
    }

    private void playStartAnimation() {
        if (changeDrawables.size() > 1) {
            handler.removeMessages(STATE_PLAY);
            Message msg = handler.obtainMessage();
            msg.what = STATE_PULL;
            msg.arg1 = changeDrawables.size() - 2;
            handler.sendMessage(msg);
        }
    }

    private void playChangeAnimation() {
        if (changeDrawables.size() > 1) {
            handler.removeMessages(STATE_PULL);
            Message msg = handler.obtainMessage();
            msg.what = STATE_PLAY;
            msg.arg1 = 0;
            handler.sendMessage(msg);
        }
    }
    public interface OnRefreshListener{
        public void refresh();
    }
}
