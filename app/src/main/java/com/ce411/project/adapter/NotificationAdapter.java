package com.ce411.project.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ce411.project.Class.HistoryClass;
import com.ce411.project.Class.NotificationClass;
import com.ce411.project.R;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<NotificationClass> {
    private Activity activity;
    public NotificationAdapter(Activity activity, int layoutID, List<NotificationClass> data){
        super(activity,layoutID,data);
        this.activity = activity;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_notification,null,false);
        }
        NotificationClass notificationClass = getItem(position);
        TextView title = (TextView)convertView.findViewById(R.id.id_item_notification_title);
        TextView description = (TextView)convertView.findViewById(R.id.id_item_notification_description);
        title.setText(notificationClass.getTitle());
        description.setText(notificationClass.getDescription());
        return convertView;
    }
}
