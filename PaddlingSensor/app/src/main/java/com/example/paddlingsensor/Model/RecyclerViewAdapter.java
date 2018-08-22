package com.example.paddlingsensor.Model;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.paddlingsensor.Activities.ConnectFrontNodeActivity;
import com.example.paddlingsensor.Activities.ConnectUserNodeActivity;
import com.example.paddlingsensor.Model.Nodes.FrontPaddleNode;
import com.example.paddlingsensor.Model.Nodes.PaddleNode;
import com.example.paddlingsensor.Model.Nodes.UserPaddleNode;
import com.example.paddlingsensor.R;

import java.util.ArrayList;

/**
 * Created by Asad Hussain.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<BluetoothDevice> dataSet;
    private PaddlingSensorModel model;
    private Context context;
    private String connector;
    private ItemClickListener clickListener;

    public RecyclerViewAdapter(ArrayList<BluetoothDevice> dataSet, String connector, PaddlingSensorModel model, Context context) {
        this.dataSet = dataSet;
        this.connector = connector;
        this.model = model;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.textView.setText(dataSet.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView textView;

        public ViewHolder(View v) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int index = getAdapterPosition();
                    Log.d("Testing", "Element " + index + " clicked. Name is: " + dataSet.get(index).getName());

                    if (connector.equals("UserNodeConnector")) {
                        //Add dataset device to UserPaddleNode
                        UserPaddleNode upn = new UserPaddleNode(dataSet.get(index));
                        model.setUpn(upn);

                        //Call method in context that creates ConnectFrontNodeActivity
                        ConnectUserNodeActivity activity = (ConnectUserNodeActivity) context;
                        activity.userNodeConnected();
                    } else if (connector.equals("FrontNodeConnector")) {
                        //Add dataset device to FrontPaddleNode
                        FrontPaddleNode fpn = new FrontPaddleNode(dataSet.get(index));
                        model.setFpn(fpn);

                        //Call method in context that creates ReceivingDataActivity
                        ConnectFrontNodeActivity activity = (ConnectFrontNodeActivity) context;
                        activity.frontNodeConnected();
                    }
                }
            });

            textView = (TextView) v.findViewById(R.id.deviceMacAddress);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
}
