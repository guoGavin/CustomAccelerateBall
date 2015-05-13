# CustomAccelerateBall
accelerate ball,加速球效果，也就是圆形的ProgressBar的效果。

##效果图
录制效果不是很好
>
![Screenshot](https://github.com/guoGavin/CustomAccelerateBall/blob/master/pic.gif)

##功能
* 可以设置目标位置，让它自己加速到特定位置。
* 可以设置不适用加速的动画效果，直接设置到特定位置。
* 可以设置是否显示Percent。
* 可以设置刷新速度，RefreshSpeed。
* 可以设置加速动画涨幅，Speed。
* 可以设置加速过程监听，每次变化监听回调，加速结束回调，AccelerateBallUpdateListener。

##layout
```xml
<com.test.gavinguo.customaccelerateball.AccelerateBallView
        android:id="@+id/ball"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:layout_marginTop="50dp"
        android:layout_gravity="center_horizontal"
        accelerateball:initLevel="2000"
        accelerateball:galleryType="AnimationAndPercent"
        accelerateball:speed="superFast"
        accelerateball:refreshSpeed="superFast" />
```

##展示类型
```java
public enum GalleryType{
        NoneAnimationAndPercent,//没有动画和百分比文字
        NoneAnimationHavePercent,//没有动画但是有文本
        AnimationOnly,//只有动画
        AnimationAndPercent,//具有动画和百分比，default
    }
```

##动画涨幅
```java
public enum Speed{
        slow,//低速
        medium,//中速，default
        fast,//高速
        superFast,//超高速
        random,//随机速度
    }
```

##刷新速度
```java
public enum RefreshSpeed{
        slow,//低速
        medium,//中速，default
        fast,//高速
        superFast,//超高速
    }
```

##过程监听
```java
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
```
##设置参数以及启动
```java
  ball = (AccelerateBallView) findViewById(R.id.ball);
  ball.setSpeedType(AccelerateBallView.Speed.superFast);
  ball.setRefreshSpeedType(AccelerateBallView.RefreshSpeed.superFast);
  ball.setGalleryType(AccelerateBallView.GalleryType.AnimationAndPercent);
  ball.setAccelerateBallUpdateListener(new AccelerateBallView.AccelerateBallUpdateListener() {
      @Override
      public void updateLeveUp(int currentPercent) {
          //do nothing
      }

      @Override
      public void endLeveUp(int endPercent) {
          //do nothing
      }
  });
  start.setOnClickListener(this);
  ball.setTotalLevel(totalLevel);
```
Total可以不停的去设置，加速球最终会停止在最后设置的位置。



