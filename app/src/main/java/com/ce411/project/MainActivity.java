package com.ce411.project;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ce411.project.model.Config;
import com.ce411.project.model.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private SessionManager sessionManager;
    private String token;
    private static String url;
    ImageView img_user;
    TextView user_name;
    TextView user_email;


    RequestQueue queue;
    String filename = "main.txt";

    //Thư mục do mình đặt
    String filepath = "main";
    File myInternalFile;

    String TAG = "Main:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());
        if(!sessionManager.isUserLoggedIn()){
            Intent i = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(i);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        View headerView = navigationView.getHeaderView(0);
        user_name = headerView.findViewById(R.id.user_name);
        user_name.setText(sessionManager.getUsername());

        user_email = headerView.findViewById(R.id.textView_email);
        img_user = (ImageView)headerView.findViewById(R.id.imageView_user);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_information)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController,mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir(filepath, Context.MODE_PRIVATE);
        myInternalFile = new File(directory, filename);

        //----------------------------Firebase-------------------------------------------------//
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("Token", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        //sendRegistrationToServer(token);
                        Log.d("Token",token);
                        if(sessionManager.getToken().equals(token)){
                            Log.d("Token","no change");
                        }else{
                            sessionManager.setToken(token);
                            sendRegistrationToServer(token);
                        }
                    }
                });

        //---------------------------------------Firebase------------------------------------//
        if(isNetworkConnected()){
            url = new Config().getUrl() + "api/detail/" + sessionManager.getUsername();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String res = response.getString("status");
                        if(res.equals("success")){
                            JSONObject object = response.getJSONObject("data");
                            String tk = object.getString("token_id");
                            if(tk.matches(sessionManager.getToken())){
                                Log.d("token","no change");
                            }else{
                                sendRegistrationToServer(sessionManager.getToken());
                            }
                            user_email.setText(object.getString("email"));

                            try {
                                //Mở file
                                FileOutputStream fos = new FileOutputStream(myInternalFile);
                                //Ghi dữ liệu vào file
                                fos.write(object.toString().getBytes());
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
                    Toast.makeText(getApplicationContext(),"Error! Please check internet!",Toast.LENGTH_LONG).show();

                }
            });
            queue = Volley.newRequestQueue(this);
            queue.add(jsonObjectRequest);

        }else {
            Log.d("d",loadDatafromDisk());
        }
    }
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
    private String loadDatafromDisk(){
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
        return myData;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            sessionManager.logout();
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void sendRegistrationToServer(String token){
        try{
            JSONObject data = new JSONObject();
            data.put("token",token);
            String url = new Config().getUrl();
            String URL_TOKEN = url + "api/user/token/" + sessionManager.getUsername();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, URL_TOKEN, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String res = response.getString("status");
                        if(res.equals("success")){
                            Log.d("send Token","done");
                        }else Log.d("send Token","fail");
                        queue.getCache().clear();
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
                }
            });
            queue = Volley.newRequestQueue(this);
            queue.add(jsonObjectRequest);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
