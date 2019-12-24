package com.ce411.project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ce411.project.model.*;

import org.json.JSONException;
import org.json.JSONObject;
public class LoginActivity extends AppCompatActivity {
    private EditText username_login;
    private EditText password_login;
    private TextView info;
    private Button button_login;
    int attempt_counter = 5;
    private SessionManager sessionManager;

    Config config = new Config();
    private String url= config.getUrl() + "api/user/login";
    String u,p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());
        if(sessionManager.isUserLoggedIn()){
            Intent i = new Intent(LoginActivity.this,MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        }
        setContentView(R.layout.activity_login);
        username_login = findViewById(R.id.user_name_login);
        password_login = findViewById(R.id.pass_word_login);
        info = findViewById(R.id.info);
        button_login = findViewById(R.id.button_login);
        final RequestQueue queue = Volley.newRequestQueue(this);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                u = username_login.getText().toString();
                p = password_login.getText().toString();
               // Toast.makeText(getApplicationContext(),"button",Toast.LENGTH_LONG).show();
               // new RequestAsync().execute();
                try {
                    JSONObject postDataParams = new JSONObject();
                    postDataParams.put("UserName",u);
                    postDataParams.put("PassWord",p);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postDataParams, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String res = response.getString("status");
                                if(res.equals("success")){
                                    sessionManager.setUsername(u);
                                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(i);
                                }else{
                                    Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
                                    //info.setText("Tài khoản hoặc mật khẩu sai!");
                                    attempt_counter--;
                                    if(attempt_counter == 0){
                                        info.setText("Bạn đã sai quá nhiều lần!");
                                        button_login.setEnabled(false);
                                    }
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),"error1",Toast.LENGTH_LONG).show();
                        }
                    });
                    queue.add(jsonObjectRequest);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //---------------------------------------------



    //---------------------------------------------------
}