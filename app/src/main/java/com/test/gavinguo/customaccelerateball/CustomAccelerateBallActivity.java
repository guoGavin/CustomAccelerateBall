package com.test.gavinguo.customaccelerateball;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by gavinguo on 5/6/2015.
 */
public class CustomAccelerateBallActivity extends Activity implements View.OnClickListener{

    private EditText percent;
    private Button start;
    private AccelerateBallView ball;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_accelerate_ball_activity);
        percent = (EditText) findViewById(R.id.percent_content);
        start = (Button) findViewById(R.id.start);
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
    }

    @Override
    public void onClick(View v) {
        int totalLevel = Integer.parseInt(percent.getText().toString());
        ball.setTotalLevel(totalLevel);
    }
}
