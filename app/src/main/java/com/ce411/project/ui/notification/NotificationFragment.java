package com.ce411.project.ui.notification;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ce411.project.Class.HistoryClass;
import com.ce411.project.Class.NotificationClass;
import com.ce411.project.R;
import com.ce411.project.adapter.HistoryAdapter;
import com.ce411.project.adapter.NotificationAdapter;
import com.ce411.project.model.Config;
import com.ce411.project.model.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private NotificationViewModel notificationViewModel;
    ListView listView;
    List<NotificationClass> notificationClasses;
    NotificationClass notificationClass;
    NotificationAdapter notificationAdapter;
    String url;
    private RequestQueue queue;
    SessionManager sessionManager;

    private String filename = "notification.txt";

    //Thư mục do mình đặt
    private String filepath = "notification";
    File myInternalFile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationViewModel =
                ViewModelProviders.of(this).get(NotificationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notification, container, false);
        final TextView textView = root.findViewById(R.id.text_notification);
        notificationViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        sessionManager = new SessionManager(getContext());
        notificationClasses = new ArrayList<>();

        notificationAdapter = new NotificationAdapter(getActivity(),1,notificationClasses);

        listView = (ListView)root.findViewById(R.id.id_notification_list_view);

        listView.setAdapter(notificationAdapter);

        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File directory = contextWrapper.getDir(filepath, Context.MODE_PRIVATE);
        myInternalFile = new File(directory, filename);

        if(!isNetworkConnected()){
            getDatafromDisk();
        }else{
            url = new Config().getUrl() + "api/notifications" + sessionManager.getUsername();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String res = response.getString("status");
                        if(res.equals("success")){
                            JSONArray array = response.getJSONArray("data");
                            for(int i = 0; i < array.length(); i++){
                                JSONObject noti = array.getJSONObject(i);
                                String title = noti.getString("title");
                                String desc = noti.getString("description");
                                String cont = noti.getString("content");
                                notificationClasses.add(new NotificationClass(title,desc,cont));
                            }
                            try {
                                //Mở file
                                FileOutputStream fos = new FileOutputStream(myInternalFile);
                                //Ghi dữ liệu vào file
                                fos.write(array.toString().getBytes());
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            notificationAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getContext(),res,Toast.LENGTH_LONG).show();
                        }
                        queue.getCache().clear();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }}, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    getDatafromDisk();
                    Toast.makeText(getContext(),"Error! Please check internet!",Toast.LENGTH_LONG).show();

                }
            });
            queue = Volley.newRequestQueue(getContext());
            queue.add(jsonObjectRequest);
        }
        return root;
    }
    private void getDatafromDisk(){
        String myData="";
        try {
            //Đọc file
            FileInputStream fis = new FileInputStream(myInternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(in));
            String strLine;
            //Đọc từng dòng
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONArray jsonArray = new JSONArray(myData);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                String title = object.getString("title");
                String desc = object.getString("description");
                String cont = object.getString("content");
                notificationClasses.add(new NotificationClass(title,desc,cont));
            }
            notificationAdapter.notifyDataSetChanged();
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON:");
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
