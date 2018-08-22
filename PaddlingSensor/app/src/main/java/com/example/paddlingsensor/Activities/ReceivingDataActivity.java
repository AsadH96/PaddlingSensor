package com.example.paddlingsensor.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.paddlingsensor.Model.PaddlingSensorModel;
import com.example.paddlingsensor.R;

/**
 * Created by Asad Hussain.
 */

public class ReceivingDataActivity extends AppCompatActivity {

    private PaddlingSensorModel model;
    private TextView userNodeAccX, userNodeAccY, userNodeAccZ, userNodeGyrX, userNodeGyrY, userNodeGyrZ;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_data);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        constraintLayout = (ConstraintLayout) findViewById(R.id.screen);
        //constraintLayout.setBackgroundColor(-16711936);

        userNodeAccX = (TextView) findViewById(R.id.userNodeAccX);
        userNodeAccY = (TextView) findViewById(R.id.userNodeAccY);
        userNodeAccZ = (TextView) findViewById(R.id.userNodeAccZ);

        userNodeGyrX = (TextView) findViewById(R.id.userNodeGyrX);
        userNodeGyrY = (TextView) findViewById(R.id.userNodeGyrY);
        userNodeGyrZ = (TextView) findViewById(R.id.userNodeGyrZ);

        model = PaddlingSensorModel.getInstance();
        model.setContext(this);
        model.connectNodes();
        model.initialiseNodes();

        model.startHandlingData();

        //model.testSound();
        initialiseFrontPaddleStrokeTimer();
    }

    /**
     * Initialise sound file in model
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        model.initSound();
        model.testSound();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                System.out.println("Tesssttt");
                model.testSound();
        }
        return true;
    }

    /**
     * Set TextView for accelerometer's X-axis
     *
     * @param text The text to be shown
     */
    public void setUserNodeAccX(String text) {
        this.userNodeAccX.setText(text);
    }

    /**
     * Set TextView for accelerometer's Y-axis
     *
     * @param text The text to be shown
     */
    public void setUserNodeAccY(String text) {
        this.userNodeAccY.setText(text);
    }

    /**
     * Set TextView for accelerometer's X-axis
     *
     * @param text The text to be shown
     */
    public void setUserNodeAccZ(String text) {
        this.userNodeAccZ.setText(text);
    }

    /**
     * Set TextView for gyroscope's X-axis
     *
     * @param text The text to be shown
     */
    public void setUserNodeGyrX(String text) {
        this.userNodeGyrX.setText(text);
    }

    /**
     * Set TextView for gyroscope's Y-axis
     *
     * @param text The text to be shown
     */
    public void setUserNodeGyrY(String text) {
        this.userNodeGyrY.setText(text);
    }

    /**
     * Set TextView for gyroscope's Z-axis
     *
     * @param text The text to be shown
     */
    public void setUserNodeGyrZ(String text) {
        this.userNodeGyrZ.setText(text);
    }

    /**
     * Update the background of the activity to give a visual feedback of the synchronisation level.
     * Level 1 means full synchronisation, level 2 implies a little off from synchronisation and
     * level three signifies very far from synchronisation.
     *
     * @param syncLevel The level of synchronisation
     */
    public void giveVisualFeedback(int syncLevel){

        switch (syncLevel){
            case 1:
                //Green background
                constraintLayout.setBackgroundColor(-16711936);
                break;
            case 2:
                //Yellow background
                constraintLayout.setBackgroundColor(-256);
                break;
            case 3:
                //Red background
                constraintLayout.setBackgroundColor(-65536);
                break;
            default:
                break;

        }
    }

    private CountDownTimer initialiseFrontPaddleStrokeTimer() {

        CountDownTimer timer = new CountDownTimer(200, 20) {

            /**
             * Check each 10 ms if a stroke is discovered on front paddle
             *
             * @param millisecondsUntilFinished
             */
            @Override
            public void onTick(long millisecondsUntilFinished) {
                System.out.println("In timerrr tick");
                if (true) {
                    System.out.println("Front reccceived in timer");
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                System.out.println("Finished frrront");
            }
        }.start();
        return null;
    }

    /**
     * Disconnect the connected IMU-sensors
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        model.reset();
    }
}
