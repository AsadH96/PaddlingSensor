package com.example.paddlingsensor.Model.PaddleStroke;

import com.example.paddlingsensor.Model.Queue.IMUQueue;
import com.example.paddlingsensor.Model.Queue.IMUQueueModel;

/**
 * Discover a trhoke on user paddle
 */
public class UserPaddleStroke implements Runnable {

    private StrokeHandler strokeHandler;
    private IMUQueue<IMUQueueModel> userQueue;
    private boolean strokeDiscovered;
    private boolean feedbackReceived;
    private boolean userPaddling;

    public UserPaddleStroke(StrokeHandler handler, IMUQueue<IMUQueueModel> userQueue){
        this.strokeHandler = handler;
        this.userQueue = userQueue;
        this.userQueue = userQueue;
        this.strokeDiscovered = this.feedbackReceived = false;
        this.userPaddling = false;
    }

    @Override
    public void run() {

        while(strokeHandler.isRunning()){
            IMUQueueModel userPaddleData = userQueue.peekLast();

            if(userPaddleData != null) {

                //if (userPaddleData.getAccY() <= (-0.5f) && userPaddleData.getGyrX() >= 100f && userPaddleData.getGyrY() >= (-30f) && !userPaddling) {
                if(userPaddleData.getEulerX() >= (25.0f) && !userPaddling){
                    //System.out.println("Ett paddeltag USER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + userPaddleData.getTimestamp());
                    userPaddling = true;
                    strokeHandler.userStrokeDiscovered(userPaddleData.getTimestamp());
                }

                //if (userPaddleData.getAccY() > (-0.5f) && userPaddleData.getGyrX() < 100f && userPaddleData.getGyrY() < (-30f) && userPaddling) {
                if(userPaddleData.getEulerX() < (25.0f) && userPaddling){
                    //System.out.println("Inte ett paddeltag USER---------------------------------------------------");
                    userPaddling = false;
                    strokeHandler.userStrokeDone();
                }
            }else{
                System.out.println("UserPaddleData is null");
            }
        }
    }

    public int getQueueSize(){
        return userQueue.size();
    }
}
