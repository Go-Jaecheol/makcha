package com.example.makcha;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        TimePicker picker=(TimePicker)findViewById(R.id.timepicker);
        picker.setIs24HourView(true);

        Button button = (Button) findViewById(R.id.set_time_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int hour_24, minute, milli = 0;

                if (Build.VERSION.SDK_INT >= 23) {
                    hour_24 = picker.getHour();
                    minute = picker.getMinute();
                }
                else {
                    hour_24 = picker.getCurrentHour();
                    minute = picker.getCurrentMinute();
                }
                milli = milli + hour_24*60;
                milli = milli + minute;
                Toast.makeText(getApplicationContext(), milli+"분 전으로 알람이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                milli = milli * 60000;
                SharedPreferences test = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = test.edit();
                editor.putInt("milli", milli);
                editor.commit();
            }
        });

        // 여기부터 네비게이션 뷰 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.back_icon); //뒤로가기 버튼 이미지 지정
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
