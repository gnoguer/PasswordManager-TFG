package com.example.passwordmanager.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;

import com.example.passwordmanager.AlarmReceiver;
import com.example.passwordmanager.R;
import com.example.passwordmanager.user.SharedPrefManager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;

public class LeaksSettingsActivity extends AppCompatActivity {

    Switch leaksSwitch;
    boolean checkerSet = false;
    TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaks_settings);

        Toolbar toolbar = findViewById(R.id.saveSettingsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        leaksSwitch = findViewById(R.id.leaksSwitch);

        timePicker = findViewById(R.id.leaksTimePicker);
        timePicker.setIs24HourView(true);

        try {
            checkerSet = SharedPrefManager.getInstance(getApplicationContext()).getLeakCheckerFlag();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        leaksSwitch.setChecked(checkerSet);
        setVisibilities();

        leaksSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setVisibilities();
            }
        });



    }

    private void setVisibilities(){
        if(!leaksSwitch.isChecked()){
            timePicker.setVisibility(View.GONE);
        }else{
            timePicker.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            if(checkerSet){
                cancelPreviousAlarm();
            }
            if(leaksSwitch.isChecked()){
                startLeakCheckerAlarm(timePicker.getHour(), timePicker.getMinute());
                try {
                    SharedPrefManager.getInstance(getApplicationContext()).setLeakCheckerFlag(true);
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    SharedPrefManager.getInstance(getApplicationContext()).setLeakCheckerFlag(false);
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void cancelPreviousAlarm(){

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("code",0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
    public void startLeakCheckerAlarm(int hour, int minute){

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour); // For 1 PM or 2 PM
        calendar.set(Calendar.MINUTE, minute);

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("code",0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}