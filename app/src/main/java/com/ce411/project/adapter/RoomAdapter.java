package com.ce411.project.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ce411.project.Class.RoomClass;
import com.ce411.project.R;

import java.util.List;

public class RoomAdapter extends ArrayAdapter<RoomClass> {
    private Activity activity;
    public RoomAdapter(Activity activity, int layoutID, List<RoomClass> data){
        super(activity,layoutID,data);
        this.activity = activity;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_room,null,false);
        }
        final RoomClass roomClass = getItem(position);

        Button textView = (Button) convertView.findViewById(R.id.id_item_room);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),roomClass.getDescription(),Toast.LENGTH_SHORT).show();

            }
        });
        if(roomClass.getName() != null){
            textView.setText(roomClass.getName());
        }else textView.setText(roomClass.getName());
        if(roomClass.getAllow()){
            textView.setBackgroundResource(R.color.item_room_allow);
        }else textView.setBackgroundResource(R.color.item_room_unallow);
        return convertView;
    }
}
