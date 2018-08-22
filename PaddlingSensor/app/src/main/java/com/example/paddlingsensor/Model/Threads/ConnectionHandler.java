package com.example.paddlingsensor.Model.Threads;

import android.content.Context;
import android.os.Handler;

import com.example.paddlingsensor.Activities.ConnectUserNodeActivity;

public class ConnectionHandler extends Thread {
    private int mode;
    private Handler h;
    private ConnectUserNodeActivity ft;
    private Context c;

    public ConnectionHandler(ConnectUserNodeActivity ft, Context c, Handler h) { // contructor
        super();
        this.ft = ft;
        this.c = c;
        this.h = h;
        this.mode = 1; //Mode = "free training"
        //super.default_settings = new DefaultSettings(c);
    }
}
