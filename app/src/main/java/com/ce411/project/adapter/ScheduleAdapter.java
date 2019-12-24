package com.ce411.project.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ce411.project.Class.Schedule;
import com.ce411.project.R;
import java.util.List;

public class ScheduleAdapter extends ArrayAdapter<Schedule> {
    private Activity activity;
    public ScheduleAdapter(Activity activity, int layoutID, List<Schedule> data){
        super(activity,layoutID,data);
        this.activity = activity;
    }
    @Override
    public View getView(final int position, View convertview, ViewGroup parent){
        if(convertview == null){
            convertview = LayoutInflater.from(activity).inflate(R.layout.item_schedule,null,false);
        }
        Schedule schedule = getItem(position);
        TextView time = (TextView)convertview.findViewById(R.id.id_item_schedule_time);
        TextView room = (TextView)convertview.findViewById(R.id.id_item_schedule_room);
        if (schedule.getTime() != null){
            time.setText(schedule.getTime());
        }
        if (schedule.getRoom() != null){
            room.setText(schedule.getRoom());
        }
        return convertview;
    }
}
