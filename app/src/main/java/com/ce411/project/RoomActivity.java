package com.ce411.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ce411.project.Class.RoomClass;
import com.ce411.project.adapter.RoomAdapter;
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

public class RoomActivity extends AppCompatActivity {
    private GridView gridView;
    List<RoomClass> roomClasses;
    RoomAdapter roomAdapter;
    Config config = new Config();
    String url = config.getUrl() + "api/room";
    SessionManager sessionManager;
    private RequestQueue queue;
    private String filename = "room.txt";

    //Thư mục do mình đặt
    private String filepath = "Room";
    File myInternalFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Rooms");
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        sessionManager = new SessionManager(getApplicationContext());

        url = url + "/" + sessionManager.getUsername();
        roomClasses = new ArrayList<>();
        roomAdapter = new RoomAdapter(this,1,roomClasses);

        gridView = (GridView) findViewById(R.id.id_room_grid_view);
        gridView.setAdapter(roomAdapter);
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir(filepath, Context.MODE_PRIVATE);
        myInternalFile = new File(directory, filename);
        if(!isNetworkConnected()){
            getDatafromDisk();
        }else{
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String res = response.getString("status");
                        if(res.equals("success")){
                            JSONArray array = response.getJSONArray("data");
                            for(int i = 0; i < array.length(); i++){
                                JSONObject rm = array.getJSONObject(i);
                                boolean b;
                                if(rm.getInt("permission")==1){
                                    b = true;
                                }else b = false;
                                roomClasses.add(new RoomClass(rm.getString("name"),b,rm.getString("description")));
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
                            roomAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
                        }
                        queue.getCache().clear();
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }}, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    getDatafromDisk();
                    Toast.makeText(getApplicationContext(),"Error! Please check internet!",Toast.LENGTH_LONG).show();
                }
            });
            queue = Volley.newRequestQueue(this);
            queue.add(jsonObjectRequest);
        }


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
                JSONObject rm = jsonArray.getJSONObject(i);
                boolean b;
                if(rm.getInt("permission")==1){
                    b = true;
                }else b = false;
                roomClasses.add(new RoomClass(rm.getString("name"),b,rm.getString("description")));
            }
            roomAdapter.notifyDataSetChanged();
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON:");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                sessionManager.logout();
                Intent i = new Intent(this,LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
            default:
                Log.d("Fail","d");
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
        super.onBackPressed();
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
