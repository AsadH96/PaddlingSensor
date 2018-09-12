package com.example.paddlingsensor.Model.IMUSensor;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

/**
 * Created by Asad Hussain.
 */

public class IMUDataLogger {

    private String filename;
    private String node;
    private long timestamp;
    private FileWriter fw;
    private Context context;

    public IMUDataLogger(String node, Context context) {
        filename = "";
        timestamp = 0;
        fw = null;
        this.node = node;
        this.context = context;
    }

    /**
     * Create a folder on the phone (if such doesn't exist) and a file in that folder
     * which will later be concatenated with sensor data
     *
     * @return If creating the folder and file went successful
     */
    public boolean CreateFile() { //create a file at specified "path" with specified "name"

        int file_number = 1; // added to filename if it already exists
        if (node.equals("Front")) {
            filename = "IMUSensorData_FrontNode_" + getCurrentDate() + "_" + getCurrentTime();
        } else if (node.equals("User")) {
            filename = "IMUSensorData_UserNode_" + getCurrentDate() + "_" + getCurrentTime();
        }

        //String path = context.getFilesDir().getPath() + "/" + filename;
        String path = Environment.getExternalStorageDirectory() + "/Paddle Sensor/";

        File tempFile = new File(path);

        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }

        String filetype = filename + ".txt";
        boolean file_created = false;

        try {
            while (!file_created) {
                File file = new File(path + filetype);

                if (!file.exists()) { // if file does not exist

                    if (file.createNewFile()) {
                        fw = new FileWriter(file); // create file writer (used to write to file)
                        file_created = true;
                    } else {
                        return false;
                    }
                } else { // if file already exists
                    file_number++;
                    filename = filename + "_" + file_number + ".txt"; // make filename look like this
                }
            }
        } catch (IOException e) {
            System.out.println("Exception caught!");
            System.out.println(e.getCause());
            System.out.println(e.toString());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            System.out.println(sStackTrace);

            return false;
        }
        return true;
    }

    /**
     * Concatenate sensor data to the file created
     *
     * @param txt The data to be concatenated to the file
     * @return If the concatenation went successful
     */
    public boolean LogNewLine(String txt) { // add "txt" as new line to the file
        try {
            if (fw != null) {
                fw.append(txt + "\r\n");
                fw.flush();
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Get the current date in YYYY-MM-DD format
     *
     * @return The date
     */
    private String getCurrentDate() {
        String currentDate;

        long millis = Calendar.getInstance().getTimeInMillis();
        currentDate = String.format("%tF", millis);
        return currentDate;
    }

    /**
     * Get the current time in hhmmss format
     *
     * @return The time
     */
    public String getCurrentTime() {
        String currentTime;

        long millis = Calendar.getInstance().getTimeInMillis();
        currentTime = String.format("%tH", millis) + String.format("%tM", millis) + String.format("%tS", millis);
        return currentTime;
    }
}
