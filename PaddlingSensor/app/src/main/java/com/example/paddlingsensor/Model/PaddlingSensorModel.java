package com.example.paddlingsensor.Model;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.paddlingsensor.Activities.ReceivingDataActivity;
import com.example.paddlingsensor.Model.Nodes.FrontPaddleNode;
import com.example.paddlingsensor.Model.Nodes.UserPaddleNode;
import com.example.paddlingsensor.Model.IMUSensor.LpmsB2;
import com.example.paddlingsensor.Model.PaddleStroke.StrokeHandler;
import com.example.paddlingsensor.Model.Queue.IMUQueue;
import com.example.paddlingsensor.Model.Queue.IMUQueueModel;
import com.example.paddlingsensor.Model.Sound.FeedbackSound;

/**
 * Created by Asad Hussain.
 */

public class PaddlingSensorModel {

    private static final String TAG = "PaddlingSensorModel";

    private FrontPaddleNode fpn;
    private UserPaddleNode upn;
    private static PaddlingSensorModel model = null;
    private LpmsB2 userNode;
    private LpmsB2 frontNode;
    private Context context;
    private SensorFilter filter;
    private ReceivingDataActivity receivingDataActivity;
    private Handler mainHandler;
    private FeedbackSound sound;
    private IMUQueue<IMUQueueModel> frontQueue;
    private IMUQueue<IMUQueueModel> userQueue;
    private StrokeHandler strokeHandler;
    private Thread strokeHandlerThread;
    private boolean running;
    private String appLanguage;

    private int samplingFrequency;
    private float syncLevelOne;
    private float syncLevelTwo;

    public PaddlingSensorModel() {
        filter = new SensorFilter(0.95F, 0F);
        running = false;
    }

    /**
     * Get instance of this singleton model class
     *
     * @return The instance of the class
     */
    public static PaddlingSensorModel getInstance() {
        if (model == null) {
            model = new PaddlingSensorModel();
        }
        return model;
    }

    /**
     * Update the context referenced in the model
     *
     * @param context The context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Initialise main looper to update activity by accessing the activity thread from
     * another thread
     */
    private void initialiseMainLooper() {
        receivingDataActivity = (ReceivingDataActivity) context;
        mainHandler = new Handler(context.getMainLooper());
    }

    /**
     * Get front paddle node
     *
     * @return Front paddle node
     */
    public FrontPaddleNode getFpn() {
        return fpn;
    }

    /**
     * Get user paddle node
     *
     * @return User paddle node
     */
    public UserPaddleNode getUpn() {
        return upn;
    }

    /**
     * Set front paddle node
     *
     * @param fpn Front paddle node
     */
    public void setFpn(FrontPaddleNode fpn) {
        this.fpn = fpn;
    }

    /**
     * Set user paddle node
     *
     * @param upn User paddle node
     */
    public void setUpn(UserPaddleNode upn) {
        this.upn = upn;
    }

    /**
     * Get connected LPMS-B2 sensor on front paddle
     *
     * @return The connected LPMS-B2 sensor on front paddle
     */
    public LpmsB2 getFrontNode() {
        return frontNode;
    }

    /**
     * Get connected LPMS-B2 sensor on user paddle
     *
     * @return The connected LPMS-B2 sensor on user paddle
     */
    public LpmsB2 getUserNode() {
        return userNode;
    }

    /**
     * Connect the nodes
     */
    public void connectNodes() {
        connectFPN();
        connectUPN();
    }

    /**
     * Initialise the nodes
     */
    public void initialiseNodes() {
        resetClocks();

        if (frontNode.getStreamFrequency() != samplingFrequency) {
            setFPNFrequency(samplingFrequency);
        }

        if (userNode.getStreamFrequency() != samplingFrequency) {
            setUPNFrequency(samplingFrequency);
        }
    }

    /**
     * Connect to the LPMS-B2 sensor on front paddle
     */
    private void connectFPN() {
        frontQueue = new IMUQueue<>(Integer.MAX_VALUE);
        frontNode = new LpmsB2("Front", context, this, frontQueue);
        frontNode.connect(fpn.getMacAddress());
        initialiseMainLooper();
    }

    /**
     * Connect to the LPMS-B2 sensor on user paddle
     */
    private void connectUPN() {
        userQueue = new IMUQueue<>(Integer.MAX_VALUE);
        userNode = new LpmsB2("User", context, this, userQueue);
        userNode.connect(upn.getMacAddress());
    }

    /**
     * Disconnect from the LPMS-B2 sensor on the front paddle
     */
    private void disconnectFPN() {
        frontNode.disconnect();
        frontNode = null;
    }

    /**
     * Disconect from the LPMS-B2 sensor on the user paddle
     */
    private void disconnectUPN() {
        userNode.disconnect();
        frontNode = null;
    }

    /**
     * Resets the timestamp on both nodes to make them as close to each other as possible
     */
    private void resetClocks() {
        frontNode.resetTimestamp();
        userNode.resetTimestamp();
    }

    public void setSamplingFrequency(int samplingFreq) {
        samplingFrequency = samplingFreq;
    }

    public boolean setSyncLevelOne(float syncLevelOne) {
        if (syncLevelTwo != 0) {
            if (syncLevelOne < syncLevelTwo) {
                this.syncLevelOne = syncLevelOne;
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    public String getSyncLevelOne() {
        return Float.toString(syncLevelOne);
    }

    public boolean setSyncLevelTwo(float syncLevelTwo) {
        if (syncLevelTwo > syncLevelOne) {
            this.syncLevelTwo = syncLevelTwo;
            return true;
        } else {
            return false;
        }
    }

    public String getSyncLevelTwo() {
        return Float.toString(syncLevelTwo);
    }

    /**
     * Set frequency of the front node to 5, 10, 25, 50, 100, 200 or 400 Hz
     */
    private void setFPNFrequency(int frequency) {
        if (frequency == 5 || frequency == 10 || frequency == 25 || frequency == 50 ||
                frequency == 100 || frequency == 200 || frequency == 400) {
            frontNode.setStreamFrequency(frequency);
        }
    }

    /**
     * Set frequency of the user node to 5, 10, 25, 50, 100, 200 or 400 Hz
     */
    private void setUPNFrequency(int frequency) {
        if (frequency == 5 || frequency == 10 || frequency == 25 || frequency == 50 ||
                frequency == 100 || frequency == 200 || frequency == 400) {
            userNode.setStreamFrequency(frequency);
        }
    }


    /**
     * Initialise the sound file to SoundPool
     */
    public void initSound() {
        sound = new FeedbackSound(context);
    }

    /**
     * Test the initialised sound by playing it 3 times
     */
    public void testSound() {
        sound.play(2);
    }

    /**
     * Loops the sound from the SoundPool the amount of times mentioned in integer variable loops.
     * 0 loops will play the sound once, 1 loop will play it twice and 2 loops will play the
     * sound thrice.
     *
     * @param loops The amount of times to loop the sound
     */
    public void playSound(int loops) {
        sound.play(loops);
    }

    /**
     * Update the activity with new data from the LPMS-B2 sensor
     *
     * @param data     The new sensor data
     * @param valIndex Value index to separate sensor data, e.g. separate
     *                 accelerometer X-axis from Y-axis
     */
    public synchronized void handleSensorData(final float data, int valIndex) {

        switch (valIndex) {
            //Handle Accelerometer X-axis
            case 1:
                Runnable accXRunnable = new Runnable() {
                    @Override
                    public void run() {
                        receivingDataActivity.setUserNodeAccX(Float.toString(filter.filterDataAccX(data)));
                    }
                };
                mainHandler.post(accXRunnable);
                break;
            //Handle Accelerometer Y-axis
            case 2:
                Runnable accYRunnable = new Runnable() {
                    @Override
                    public void run() {
                        receivingDataActivity.setUserNodeAccY(Float.toString(filter.filterDataAccY(data)));
                    }
                };
                mainHandler.post(accYRunnable);
                break;
            //Handle Accelerometer Z-axis
            case 3:
                Runnable accZRunnable = new Runnable() {
                    @Override
                    public void run() {
                        receivingDataActivity.setUserNodeAccZ(Float.toString(filter.filterDataAccZ(data)));
                    }
                };
                mainHandler.post(accZRunnable);
                break;
            //Handle Gyroscope X-axis
            case 4:
                Runnable gyrXRunnable = new Runnable() {
                    @Override
                    public void run() {
                        receivingDataActivity.setUserNodeGyrX(Float.toString(filter.filterDataGyrX(data)));
                    }
                };
                mainHandler.post(gyrXRunnable);
                break;
            //Handle Gyroscope Y-axis
            case 5:
                Runnable gyrYRunnable = new Runnable() {
                    @Override
                    public void run() {
                        receivingDataActivity.setUserNodeGyrY(Float.toString(filter.filterDataGyrY(data)));
                    }
                };
                mainHandler.post(gyrYRunnable);
                break;
            //Handle Gyroscope Z-axis
            case 6:
                Runnable gyrZRunnable = new Runnable() {
                    @Override
                    public void run() {
                        receivingDataActivity.setUserNodeGyrZ(Float.toString(filter.filterDataGyrZ(data)));
                    }
                };
                mainHandler.post(gyrZRunnable);
                break;
            default:
        }
    }

    /**
     * Handle visual feedback of the synchronisation level.
     * Level 1 means full synchronisation, level 2 implies a little off from synchronisation and
     * level three signifies very far from synchronisation.
     *
     * @param syncLevel The level of synchronisation
     */
    public synchronized void handleSynchronisationLevel(final int syncLevel) {

        if (syncLevel < 1 || syncLevel > 3) {
            return;
        }
        Runnable synchronisationLevel1 = new Runnable() {
            @Override
            public void run() {
                receivingDataActivity.giveVisualFeedback(syncLevel);
            }
        };
        mainHandler.post(synchronisationLevel1);
    }

    /**
     * Create StrokeHandler class and start thread to handle sensor data
     */
    public void startHandlingData() {
        strokeHandler = new StrokeHandler(context, frontQueue, userQueue, this, syncLevelOne, syncLevelTwo);
        strokeHandlerThread = new Thread(strokeHandler);
        running = true;
        System.out.println("Front frequency: " + frontNode.getStreamFrequency() + " and user frequency: " + frontNode.getStreamFrequency());
        strokeHandlerThread.start();
    }

    /**
     * Stop StrokeHandler thread
     */
    private void stopHandlingData() {
        running = false;
        try {
            strokeHandlerThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, "Error joining StrokeHandler thread");
            e.printStackTrace();
        }
    }

    /**
     * Clean up method
     */
    public void reset() {
        disconnectFPN();
        disconnectUPN();
        stopHandlingData();
        sound.destroySound();
        mainHandler = null;
        fpn = null;
        upn = null;
        frontQueue.clear();
        frontQueue = null;
        userQueue.clear();
        userQueue = null;
        running = false;
    }

    /**
     * Get boolean value of running to see if the application is running or not
     *
     * @return The boolean value of running
     */
    public boolean getRunning() {
        return running;
    }
}
