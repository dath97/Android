package com.ce411.project.ui.information;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.ce411.project.R;
import com.ce411.project.model.Config;
import com.ce411.project.model.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class InformationFragment extends Fragment {

    private InformationViewModel informationViewModel;
    private String username = "";
    int gender = 0;
    Button btn_save;
    RadioGroup rg;
    RadioButton rbtn_m,rbtn_wm;
    EditText name, phone, email,pass,cpass;
    TextView user;
    private RequestQueue queue;
    SessionManager sessionManager;
    private String filename = "information.txt";
    private String url;
    //Thư mục do mình đặt
    private String filepath = "information";
    File myInternalFile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        informationViewModel =
                ViewModelProviders.of(this).get(InformationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_information, container, false);
        final TextView textView = root.findViewById(R.id.id_information_title);
        informationViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        sessionManager = new SessionManager(getContext());
        username = sessionManager.getUsername();


        name = (EditText) root.findViewById(R.id.id_user_editText_name);
        user = (TextView) root.findViewById(R.id.id_information_user_username_value);
        phone = (EditText) root.findViewById(R.id.id_user_editText_phone);
        email = (EditText) root.findViewById(R.id.id_user_editText_email);
        pass = (EditText) root.findViewById(R.id.id_user_editText_pass);
        cpass = (EditText) root.findViewById(R.id.id_user_editText_cpass);

        btn_save = (Button)root.findViewById(R.id.id_user_btn_save);

        rbtn_m = (RadioButton)root.findViewById(R.id.id_user_man);
        rbtn_wm = (RadioButton)root.findViewById(R.id.id_user_woman);

        rg = (RadioGroup)root.findViewById(R.id.id_user_rg);
        rg.clearCheck();

        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File directory = contextWrapper.getDir(filepath, Context.MODE_PRIVATE);
        myInternalFile = new File(directory, filename);

        if(!isNetworkConnected()){
            getDatafromDisk();
        }else{
            url = new Config().getUrl() + "api/detail/" + username;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String res = response.getString("status");
                        if(res.equals("success")){
                            JSONObject ob = response.getJSONObject("data");
                            setTest(ob);
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
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValue()&&checkPass()){
                    String url_up = new Config().getUrl() + "api/user/update/" + sessionManager.getUsername();
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url_up,getValue(), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String res = response.getString("status");
                                if(res.equals("success")){
                                    //sessionManager.setUsername(username);
                                    Toast.makeText(getContext(),"Cập nhật thành công",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getContext(),"Cập nhật thất bại",Toast.LENGTH_SHORT).show();
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
                }else{
                    if(!checkPass()){
                        Toast.makeText(getContext(),"Mã xác nhận không đúng",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(),"Vui lòng nhập đầy đủ",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.id_user_man:
                        gender = 1;
                        break;
                    case R.id.id_user_woman:
                        gender = 0;
                        break;
                }
            }
        });

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
            JSONObject ob = new JSONObject(myData);
            setTest(ob);
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON:");
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void setTest(JSONObject ob){
        try{
            if(!ob.isNull("full_name")){
                name.setText(ob.getString("full_name"));
            }
            if(!ob.isNull("user_name")){
                user.setText(username);
            }
            if(!ob.isNull("phone")){
                phone.setText(ob.getString("phone"));
            }
            if(!ob.isNull("email")){
                email.setText(ob.getString("email"));
            }
            if(ob.getInt("gender")==0){
                rbtn_wm.setChecked(true);
                rbtn_m.setChecked(false);
            }
            else{
                rbtn_m.setChecked(true);
                rbtn_wm.setChecked(false);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    private boolean checkValue(){
        if(name.getText().toString().matches("")||email.getText().toString().matches("")|| pass.getText().toString().matches("")
                || phone.getText().toString().matches("")||cpass.getText().toString().matches("")){
            return false;
        }else return true;
    }
    private boolean checkPass(){
        if(pass.getText().toString().matches(cpass.getText().toString())) return true;
        else return false;
    }
    private JSONObject getValue(){
        JSONObject ob = new JSONObject();
        try {
            ob.put("fullname",name.getText().toString());
            ob.put("username",username);
            ob.put("password",pass.getText().toString());
            ob.put("phone",phone.getText().toString());
            ob.put("email",email.getText().toString());
            ob.put("gender",gender);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return ob;
    }
}