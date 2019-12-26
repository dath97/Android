package com.ce411.project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ce411.project.Class.HistoryClass;
import com.ce411.project.adapter.HistoryAdapter;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    ListView listView;
    List<HistoryClass> historyClasses;
    HistoryAdapter historyAdapter;
    Config config = new Config();
    String url = config.getUrl() + "api/history/";
    private RequestQueue queue;
    SessionManager sessionManager;

    private String filename = "history.txt";

    //Thư mục do mình đặt
    private String filepath = "History";
    File myInternalFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("History");
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        sessionManager = new SessionManager(getApplicationContext());
        url = url + sessionManager.getUsername();
        historyClasses = new ArrayList<>();

        historyAdapter = new HistoryAdapter(this,1,historyClasses);

        listView = (ListView)findViewById(R.id.id_history_list_view);

        listView.setAdapter(historyAdapter);

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
                                JSONObject his = array.getJSONObject(i);
                                boolean b;
                                if(his.getInt("check_in")==1){
                                    b = true;
                                }else b = false;
                                historyClasses.add(new HistoryClass(getStringTime(his.getString("time")),b));
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
                            historyAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
                        }
                        queue.getCache().clear();
                    }catch (JSONException e){
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
                JSONObject his = jsonArray.getJSONObject(i);
                boolean b;
                if(his.getInt("check_in")==1){
                    b = true;
                }else b = false;
                historyClasses.add(new HistoryClass(getStringTime(his.getString("time")),b));
            }
            historyAdapter.notifyDataSetChanged();
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON:");
        }
    }
    private String getStringTime(String s){
        String str="";
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = parser.parse(s);
            int day = date.getDay();
            switch (day) {
                case 1:
                    str = "Thứ 2 ";
                    break;
                case 2:
                    str = "Thứ 3 ";
                    break;
                case 3:
                    str = "Thứ 4 ";
                    break;
                case 4:
                    str = "Thứ 5 ";
                    break;
                case 5:
                    str = "Thứ 6 ";
                    break;
                case 6:
                    str = "Thứ 7 ";
                    break;
                case 7:
                    str = "Chủ nhật ";
                    break;
            }
            str = str + date.getDate()+"-"+(date.getMonth() + 1)+"-"+(date.getYear()+1900)+" "+(date.getHours()+7)+":"+date.getMinutes()+":"+date.getSeconds();
        }catch (ParseException e){
            e.printStackTrace();
        }
        return str;
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
