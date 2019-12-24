package com.ce411.project.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ce411.project.Class.HistoryClass;
import com.ce411.project.R;

import java.util.Date;
import java.util.List;

public class HistoryAdapter extends ArrayAdapter<HistoryClass> {
    private Activity activity;
    public HistoryAdapter(Activity activity, int layoutID, List<HistoryClass> data){
        super(activity,layoutID,data);
        this.activity = activity;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_history,null,false);
        }
        HistoryClass historyClass = getItem(position);

        TextView name = (TextView) convertView.findViewById(R.id.id_item_history_name);
        TextView time = (TextView) convertView.findViewById(R.id.id_item_history_time);
        LinearLayout linearLayout = (LinearLayout)convertView.findViewById(R.id.id_item_history_parent);

//        if(historyClass.isStatus()){
//            //linearLayout.setBackgroundResource(R.color.history_item_status_color_in);
//            name.setText("Thời gian");
//        }else{
//            //linearLayout.setBackgroundResource(R.color.history_item_status_color_out);
//            name.setText("Thời gian");
//        }
        name.setText("Thời gian điểm danh");
        if(historyClass.getDate() != null){
            time.setText(historyClass.getDate());
        }
        else time.setText("error");
        return convertView;
    }
}
