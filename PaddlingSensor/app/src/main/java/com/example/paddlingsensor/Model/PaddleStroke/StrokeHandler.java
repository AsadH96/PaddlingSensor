package com.example.paddlingsensor.Model.PaddleStroke;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Looper;

import com.example.paddlingsensor.Model.PaddlingSensorModel;
import com.example.paddlingsensor.Model.Queue.IMUQueue;
import com.example.paddlingsensor.Model.Queue.IMUQueueModel;

import java.sql.SQLOutput;

/**
 * Created by Asad Hussain.
 */

public class StrokeHandler implements Runnable {

    private Context context;
    private FrontPaddleStroke frontPaddleStroke;
    private UserPaddleStroke userPaddleStroke;
    private boolean frontPaddling;
    private boolean userPaddling;
    private boolean feedbackReceived;
    private PaddlingSensorModel model;
    private boolean running;
    private long frontStrokeTimestamp;
    private long userStrokeTimestamp;
    private float syncLevelOne;
    private float syncLevelTwo;

    /**
     * The class that handles data from the queue and uses an algorithm to check if a paddle stroke
     * is being taken and how synchronised the strokes of the front paddler and user paddler are
     *
     * @param context          The activity showing to the user
     * @param frontPaddleQueue The queue of sensor data from front paddler
     * @param userPaddleQueue  The queue of sensor data from user paddler
     */
    public StrokeHandler(Context context, IMUQueue<IMUQueueModel> frontPaddleQueue, IMUQueue<IMUQueueModel> userPaddleQueue, PaddlingSensorModel model,
                         float syncLevelOne, float syncLevelTwo) {
        this.context = context;
        this.frontPaddleStroke = new FrontPaddleStroke(this, frontPaddleQueue);
        this.userPaddleStroke = new UserPaddleStroke(this, userPaddleQueue);
        this.frontPaddling = this.userPaddling = this.feedbackReceived = false;
        this.model = model;
        this.running = false;
        this.syncLevelOne = syncLevelOne;
        this.syncLevelTwo = syncLevelTwo;
    }

    /**
     * Returns true if the StrokeHandler thread is running
     *
     * @return The activity status of StrokeHandler thread
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Boolean value is set to true when a stroke is discovered on the front paddler
     *
     * @param timestamp The time at which the stroke was discovered
     */
    public void frontStrokeDiscovered(long timestamp) {
        this.frontStrokeTimestamp = timestamp;
        this.frontPaddling = true;
    }

    /**
     * Boolean value is set to true when a stroke is discovered on the user paddler
     *
     * @param timestamp The time at which the stroke was discovered
     */
    public void userStrokeDiscovered(long timestamp) {
        this.userStrokeTimestamp = timestamp;
        this.userPaddling = true;
    }

    /**
     * Sets boolean value to false when the execution of a stroke is done
     */
    public void frontStrokeDone() {
        this.frontPaddling = false;
    }

    /**
     * Sets boolean value to false when the execution of a stroke is done
     */
    public void userStrokeDone() {
        this.userPaddling = false;
    }

    /**
     * Gets values from the queue of each paddle, checks if a paddle stroke is being taken and
     * checks their timestamp to see how synchronised they are
     */
    @Override
    public void run() {

        Looper.prepare();

        while ((frontPaddleStroke.getQueueSize() == 0 || userPaddleStroke.getQueueSize() == 0) && model.getRunning()) {
//            System.out.println("Size in front and user queues: " + frontPaddleStroke.getQueueSize() + " and " + userPaddleStroke.getQueueSize());
        }

        this.running = true;

        CountDownTimer frontPaddleTimer = initialiseFrontPaddleStrokeTimer();
        CountDownTimer userPaddleTimer = initialiseUserPaddleStrokeTimer();

        Thread frontStrokeThread = new Thread(frontPaddleStroke);
        Thread userStrokeThread = new Thread(userPaddleStroke);
        frontStrokeThread.start();
        userStrokeThread.start();

        int loops;
        int syncLevel;
        long difference;

        while (model.getRunning()) {

            if (userPaddling && frontPaddling && !feedbackReceived) {

                if (userStrokeTimestamp > frontStrokeTimestamp) {
                    difference = userStrokeTimestamp - frontStrokeTimestamp;
                } else {
                    difference = frontStrokeTimestamp - userStrokeTimestamp;
                }

                loops = Integer.MIN_VALUE;

                if (difference <= syncLevelOne) {
                    loops = 0;
                } else if (difference > syncLevelOne && difference < syncLevelTwo) {
                    loops = 1;
                }  else if (difference >= syncLevelTwo) {
                    loops = 2;
                }

                syncLevel = loops + 1;
                model.playSound(loops);
                model.handleSynchronisationLevel(syncLevel);
                feedbackReceived = true;
            }

            if (userPaddling && !frontPaddling && !feedbackReceived) {
                frontPaddleTimer.start();
            } else if (!userPaddling && frontPaddling && !feedbackReceived) {
                userPaddleTimer.start();
            }

            if (!frontPaddling && !userPaddling) {
                feedbackReceived = false;
            }
        }

        this.running = false;
    }

    /**
     * A timer to wait 500 ms for front paddle stroke to be discovered. If it's not discovered,
     * the boolean that announced user paddle stroke is set to false.
     *
     * @return The CountDownTimer
     */
    private CountDownTimer initialiseFrontPaddleStrokeTimer() {

        CountDownTimer timerFront = new CountDownTimer(200, 20) {

            /**
             * Check each 10 ms if a stroke is discovered on front paddle
             *
             * @param millisecondsUntilFinished
             */
            @Override
            public void onTick(long millisecondsUntilFinished) {
                if (frontPaddling) {
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                userPaddling = false;
            }
        };

        return timerFront;
    }

    /**
     * A timer to wait 500 ms for user paddle stroke to be discovered. If it's not discovered,
     * the boolean that announced front paddle stroke is set to false.
     *
     * @return The CountDownTimer
     */
    private CountDownTimer initialiseUserPaddleStrokeTimer() {
        CountDownTimer timerUser = new CountDownTimer(200, 10) {

            /**
             * Check each 10 ms if a stroke is discovered on user paddle
             *
             * @param millisecondsUntilFinished
             */
            @Override
            public void onTick(long millisecondsUntilFinished) {
                if (userPaddling) {
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                frontPaddling = false;
            }
        };
        return timerUser;
    }
}
