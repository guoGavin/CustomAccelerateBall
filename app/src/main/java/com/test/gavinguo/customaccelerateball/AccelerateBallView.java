package com.test.gavinguo.customaccelerateball;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by gavinguo on 5/11/2015.
 */
public class AccelerateBallView extends RelativeLayout {

    /** 上下文 **/
    private Context mContext;
    /** 加载的View **/
    private View contentView;
    /** 加速球的透明遮罩层 **/
    private ImageView maskView;
    /** 加速球的level **/
    private ImageView levelView;
    /** 显示百分比的文字控件 **/
    private TextView percentView;
    /** 目标水平 **/
    private int totalLevel;
    /** 当前位置 **/
    private int currentLevel;

    /** 最大水平不能超过10000 **/
    private final int MAX_LEVEL = 10000;
    private final int slowLength = 5;
    private final int mediumLength = 10;
    private final int fastLength = 15;
    private final int superFastLength = 50;
    private final int slowRefresh = 15;
    private final int mediumRefresh = 10;
    private final int fastRefresh = 5;
    private final int superFastRefresh = 1;

    /** 变化速度 **/
    private int levelUpSpeed = slowLength;
    /** 刷新速度 **/
    private int refreshSpeed = fastRefresh;

    /** 控制上涨动画 **/
    private Handler mHandler = null;
    private Runnable levelUpRunnable = null;

    /** 标记当前是否正在运行中，处理多次设置totalLevel问题 **/
    private boolean isRuning = false;

    /** 变化监听接口 **/
    private AccelerateBallUpdateListener accelerateBallUpdateListener;

    /** 标记展示类型，默认是具有动画的百分比文字 **/
    private GalleryType galleryType = GalleryType.AnimationAndPercent;
    /**
     * 提供展示类型
     */
    public enum GalleryType{
        NoneAnimationAndPercent,//没有动画和百分比文字
        NoneAnimationHavePercent,//没有动画但是有文本
        AnimationOnly,//只有动画
        AnimationAndPercent,//具有动画和百分比,默认
    }

    /** 标记上涨速度类型 **/
    private Speed speedType = Speed.medium;
    /**
     * 提供上涨速度
     */
    public enum Speed{
        slow,//低速
        medium,//中速
        fast,//高速
        superFast,//超高速
        random,//随机速度
    }

    /** 标记刷新类型 **/
    private RefreshSpeed refreshSpeedType = RefreshSpeed.medium;
    /**
     * 刷新速度
     */
    public enum RefreshSpeed{
        slow,//低速
        medium,//中速
        fast,//高速
        superFast,//超高速
    }

    /**
     * 加速球变化监听
     */
    public interface AccelerateBallUpdateListener{
        /**
         * 每次变化
         * @param currentPercent 当前百分比
         */
        void updateLeveUp(int currentPercent);

        /**
         * 上涨结束调用
         * @param endPercent 结束时候的百分比
         */
        void endLeveUp(int endPercent);
    }

    /**
     * 构造方法
     * @param context
     * @param attrs
     */
    public AccelerateBallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.custome_accelerate, this);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        maskView = (ImageView) contentView.findViewById(R.id.image_mask);
        levelView = (ImageView) contentView.findViewById(R.id.image_level);
        percentView = (TextView) contentView.findViewById(R.id.percent);
        ClipDrawable clip = (ClipDrawable) levelView.getDrawable();
        clip.setLevel(0);
        percentView.setText("0%");
    }

    /**
     * 设置目标位置
     * @param totalLevelParam
     */
    public void setTotalLevel(int totalLevelParam) {
        if(totalLevelParam > MAX_LEVEL){
            throw new IllegalArgumentException("totalLevel can't more than " + MAX_LEVEL);
        }
        this.totalLevel = totalLevelParam;
        if(galleryType == GalleryType.AnimationAndPercent || galleryType == GalleryType.AnimationOnly){
            if(mHandler == null && levelUpRunnable == null){
                mHandler = new Handler();
                levelUpRunnable = new Runnable(){

                    @Override
                    public void run() {

                        //是否需要随机生成上涨速度
                        if(speedType == Speed.random){
                            levelUpSpeed = (int)(Math.random()*fastLength);
                        }

                        currentLevel = currentLevel+levelUpSpeed;

                        if(currentLevel > totalLevel){
                            currentLevel = totalLevel;
                        }

                        ClipDrawable clip = (ClipDrawable) levelView.getDrawable();
                        clip.setLevel(currentLevel);

                        int percentValue = (int)(currentLevel * 100 / MAX_LEVEL);

                        //调用接口返回变化监听
                        if(accelerateBallUpdateListener != null){
                            accelerateBallUpdateListener.updateLeveUp(percentValue);
                        }

                        //是否需要显示
                        if(galleryType == GalleryType.AnimationAndPercent){
                            percentView.setVisibility(View.VISIBLE);
                            percentView.setText(percentValue+"%");
                        }else{
                            percentView.setVisibility(View.GONE);
                        }

                        //到达目标位置则暂停
                        if(currentLevel != totalLevel){
                            //刷新频率为15ms每次
                            mHandler.postDelayed(levelUpRunnable,refreshSpeed);
                        }else{
                            isRuning = false;
                            pauseLevelUp();
                            //调用接口返回结束监听
                            if(accelerateBallUpdateListener != null){
                                accelerateBallUpdateListener.endLeveUp(percentValue);
                            }
                        }

                    }
                };
            }
            //不是false的话，说明正在运行，那么就不需要重新启动，修改了totalLevel就可以了。
            if(!isRuning){
                isRuning = true;
                startLevelUp();
            }
        }else{
            ClipDrawable clip = (ClipDrawable) levelView.getDrawable();
            clip.setLevel(totalLevel);
            if(galleryType == GalleryType.NoneAnimationHavePercent){
                int percentValue = (int)(totalLevel * 100 / MAX_LEVEL);
                percentView.setText(percentValue+"%");
            }
        }
    }

    public GalleryType getGalleryType() {
        return galleryType;
    }

    /**
     *暂停上涨
     */
    public void pauseLevelUp(){
        mHandler.removeCallbacks(levelUpRunnable);
    }

    /**
     * 开始上涨
     */
    public void startLevelUp(){
        mHandler.post(levelUpRunnable);
    }

    /**
     * 结束上涨，最好在不适用该控件的地方调用这个方法，对于暂时，请调用@pauseLevelUp()，否则，会出现异常
     */
    public void stopLevelUp(){
        mHandler.removeCallbacks(levelUpRunnable);
        mHandler = null;
        levelUpRunnable = null;
    }

    /**
     * 设置展示类型
     * @param galleryType
     */
    public void setGalleryType(GalleryType galleryType) {
        this.galleryType = galleryType;
    }

    public Speed getSpeedType() {
        return speedType;
    }

    /**
     * 设置展示速度
     */
    public void setSpeedType(Speed speedType) {
        this.speedType = speedType;
        switch (speedType){
            case slow:
                levelUpSpeed = slowLength;
                break;
            case medium:
                levelUpSpeed = mediumLength;
                break;
            case fast:
                levelUpSpeed = fastLength;
                break;
            case superFast:
                levelUpSpeed = superFastLength;
                break;
            case random:
                //该类型在每次上涨的时候具体生成
                break;
        }
    }

    public RefreshSpeed getRefreshSpeedType() {
        return refreshSpeedType;
    }

    /**
     * 设置刷新速度
     * @param refreshSpeedType
     */
    public void setRefreshSpeedType(RefreshSpeed refreshSpeedType) {
        this.refreshSpeedType = refreshSpeedType;
        switch (refreshSpeedType){
            case slow:
                refreshSpeed = slowRefresh;
                break;
            case medium:
                refreshSpeed = mediumRefresh;
                break;
            case fast:
                refreshSpeed = fastRefresh;
                break;
            case superFast:
                refreshSpeed = superFastRefresh;
                break;
        }
    }

    public int getTotalLevel() {
        return totalLevel;
    }

    public AccelerateBallUpdateListener getAccelerateBallUpdateListener() {
        return accelerateBallUpdateListener;
    }

    public void setAccelerateBallUpdateListener(AccelerateBallUpdateListener accelerateBallUpdateListener) {
        this.accelerateBallUpdateListener = accelerateBallUpdateListener;
    }

}
