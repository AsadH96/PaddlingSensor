package com.example.paddlingsensor.Model.PaddleStroke;

import com.example.paddlingsensor.Model.Queue.IMUQueue;
import com.example.paddlingsensor.Model.Queue.IMUQueueModel;

/**
 * Discover a stroke on front paddle
 */
public class FrontPaddleStroke implements Runnable {

    private StrokeHandler strokeHandler;
    private IMUQueue<IMUQueueModel> frontQueue;
    private boolean strokeDiscovered;
    private boolean feedbackReceived;
    private boolean frontPaddling;

    public FrontPaddleStroke(StrokeHandler handler, IMUQueue<IMUQueueModel> frontQueue) {
        this.strokeHandler = handler;
        this.frontQueue = frontQueue;
        this.strokeDiscovered = this.feedbackReceived = false;
        this.frontPaddling = false;
    }

    @Override
    public void run() {

        while (strokeHandler.isRunning()) {
            IMUQueueModel frontPaddleData = frontQueue.peekLast();

            if(frontPaddleData != null) {
                //if (frontPaddleData.getAccY() <= (-1.2f) && frontPaddleData.getGyrX() >= 120f && frontPaddleData.getGyrY() >= (-30f) && !frontPaddling) {
                if(frontPaddleData.getEulerX() >= (25.0f) && !frontPaddling){
                    //System.out.println("Ett paddeltag FRONT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + frontPaddleData.getTimestamp());
                    frontPaddling = true;
                    strokeHandler.frontStrokeDiscovered(frontPaddleData.getTimestamp());
                }

                //if (frontPaddleData.getAccY() > (-1.2f) && frontPaddleData.getGyrX() < 120f && frontPaddleData.getGyrY() < (-30f) && frontPaddling) {
                if(frontPaddleData.getEulerX() < (25.0f) && frontPaddling){
                    //System.out.println("Inte ett paddeltag FRONT---------------------------------------------------");
                    frontPaddling = false;
                    strokeHandler.frontStrokeDone();
                }
            }else{
                System.out.println("FrontPaddleData is null");
            }
        }
    }

    public int getQueueSize(){
        return frontQueue.size();
    }
}
