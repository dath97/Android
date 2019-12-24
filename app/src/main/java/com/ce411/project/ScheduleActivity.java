package com.ce411.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ce411.project.Class.Schedule;
import com.ce411.project.adapter.ScheduleAdapter;
import com.ce411.project.model.Config;
import com.ce411.project.model.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private ListView listView;
    private ScheduleAdapter scheduleAdapter;
    private List<Schedule> schedules;
    private Config config = new Config();
    private String url = config.getUrl() + "api/schedule/";
    private RequestQueue queue;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Schedule");
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        sessionManager = new SessionManager(this);
        url = url + sessionManager.getUsername();

        calendarView = (CalendarView)findViewById(R.id.id_schedule_calendar_view);
        listView = (ListView)findViewById(R.id.id_schedule_list_task);

        schedules = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(this,1,schedules);
        listView.setAdapter(scheduleAdapter);

        calendarView.setShowWeekNumber(false);
        calendarView.setDate(Calendar.getInstance().getTimeInMillis());
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String url_temp = url + "?day=" + Integer.toString(dayOfMonth) + "&month=" + Integer.toString(month+1) + "&year=" + Integer.toString(year);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url_temp,null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        schedules.clear();
                        try {
                            String res = response.getString("status");
                            if(res.equals("success")){
                                Log.d("d",response.toString());
                                JSONObject array = response.getJSONObject("data");
                                schedules.add(new Schedule(array.getString("time"),array.getString("location")));
                            }else if(res.equals("none")){

                            }else{

                                Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
                            }
                            scheduleAdapter.notifyDataSetChanged();
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        queue.getCache().clear();
                    }}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Error! Please check internet!",Toast.LENGTH_LONG).show();
                        schedules.clear();
                        scheduleAdapter.notifyDataSetChanged();
                    }
                });
                queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(jsonObjectRequest);

            }
        });
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
}

