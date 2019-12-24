package com.ce411.project.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentController;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ce411.project.HistoryActivity;
import com.ce411.project.MainActivity;
import com.ce411.project.MapsActivity;
import com.ce411.project.R;
import com.ce411.project.RoomActivity;
import com.ce411.project.ScheduleActivity;
import com.ce411.project.adapter.HistoryAdapter;
import com.ce411.project.model.Config;
import com.ce411.project.model.SessionManager;
import com.ce411.project.ui.gallery.GalleryFragment;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Animation.AnimationListener animationListener;
    private TextView thu,ngay,gio;
    private static String url;
    SessionManager sessionManager;
    private RequestQueue queue;
    private String filename = "frag_home.txt";
    private String filepath = "frag_home";
    File myInternalFile;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        final TextView textHome = root.findViewById(R.id.id_home_title);
        final RelativeLayout btn1 = (RelativeLayout)root.findViewById(R.id.id_gridlayout_home_1);
        final RelativeLayout btn2 = (RelativeLayout)root.findViewById(R.id.id_gridlayout_home_2);
        final RelativeLayout btn3 = (RelativeLayout)root.findViewById(R.id.id_gridlayout_home_3);
        final RelativeLayout btn4 = (RelativeLayout)root.findViewById(R.id.id_gridlayout_home_4);
        thu = (TextView)root.findViewById(R.id.id_home_thu);
        ngay = (TextView)root.findViewById(R.id.id_home_ngay);
        gio = (TextView)root.findViewById(R.id.id_home_gio);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textHome.setText(s);
            }
        });

        sessionManager = new SessionManager(getContext());

        url = url + sessionManager.getUsername();
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File directory = contextWrapper.getDir(filepath, Context.MODE_PRIVATE);
        myInternalFile = new File(directory, filename);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleclickAnimation(btn1,R.anim.anim_blink);
                Intent i = new Intent(getActivity(),RoomActivity.class);
                startActivity(i);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleclickAnimation(btn2,R.anim.anim_blink);
                Intent i = new Intent(getActivity(), MapsActivity.class);
                startActivity(i);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleclickAnimation(btn3,R.anim.anim_blink);
                Intent i = new Intent(getActivity(), ScheduleActivity.class);
                startActivity(i);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleclickAnimation(btn4,R.anim.anim_blink);

                Intent i = new Intent(getActivity(),HistoryActivity.class);
                startActivity(i);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
            }
        });
        if(!isNetworkConnected()){
            getDatafromDisk();
        }else{
            url = new Config().getUrl() + "api/lastest/" + sessionManager.getUsername();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String res = response.getString("status");
                        if(res.equals("success")){
                            JSONObject ob = response.getJSONObject("data");
                            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try{
                                Date d1 = parser.parse(ob.getString("time"));
                                Log.d("data",ob.getString("time"));
                                setText(d1);
                            }catch (ParseException e){
                                e.printStackTrace();
                            }
                            try {
                                //Mở file
                                FileOutputStream fos = new FileOutputStream(myInternalFile);
                                //Ghi dữ liệu vào file
                                fos.write(ob.toString().getBytes());
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            thu.setText("Chưa có dữ liệu");
                        }
                        queue.getCache().clear();
                    }catch (JSONException e) {
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
            JSONObject jsonObject = new JSONObject(myData);
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try{
                Date d1 = parser.parse(jsonObject.getString("time"));
                setText(d1);
            }catch (ParseException e){
                e.printStackTrace();
            }
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON:");
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    private void setText(Date date){
        Log.d("time",Long.toString(date.getTime()));
        Log.d("time1",date.toString());
        int day = date.getDay();
        switch (day){
            case 1:
                thu.setText("Thứ 2");
                break;
            case 2:
                thu.setText("Thứ 3");
                break;
            case 3:
                thu.setText("Thứ 4");
                break;
            case 4:
                thu.setText("Thứ 5");
                break;
            case 5:
                thu.setText("Thứ 6");
                break;
            case 6:
                thu.setText("Thứ 7");
                break;
            case 7:
                thu.setText("Chủ nhật");
                break;
        }
        String n = "Ngày "+date.getDate()+" Tháng "+(date.getMonth() + 1)+" Năm "+(date.getYear()+1900);
        ngay.setText(n);
        String g = (date.getHours()+7)+" Giờ "+date.getMinutes()+" Phút "+date.getSeconds()+" Giây";
        gio.setText(g);
    }
    private void handleclickAnimation(RelativeLayout btn, int animId){
        final Animation animation = AnimationUtils.loadAnimation(getContext(),animId);
        animation.setAnimationListener(animationListener);
        btn.startAnimation(animation);
    }
    private void handleclickAnimation(TextView btn, int animId){
        final Animation animation = AnimationUtils.loadAnimation(getContext(),animId);
        animation.setAnimationListener(animationListener);
        btn.startAnimation(animation);
    }
}