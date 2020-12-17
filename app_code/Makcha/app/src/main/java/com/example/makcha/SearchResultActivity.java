package com.example.makcha;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchResultActivity extends MainActivity {

    private DrawerLayout mDrawerLayout;
    private final Context context = this;
    private final BusStationLoading BusStationLoading = new BusStationLoading();
    private final StartFinishInputControl StartFinishInputControl = new StartFinishInputControl();
    private final GetLastBusInfo GetLastBusInfo = new GetLastBusInfo();
    private final GetBusBoard GetBusBoard = new GetBusBoard();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        // ##여기서부터 자동완성 부분
        final AutoCompleteTextView startingPointView = (AutoCompleteTextView) findViewById(R.id.starting_point);
        final AutoCompleteTextView finishPointView = (AutoCompleteTextView) findViewById(R.id.finish_point);

        setLastBusInfo(startingPointView, finishPointView);
        setAlarm();

        Intent it = getIntent();
        String starting_p = it.getStringExtra("starting_p");
        String finish_p = it.getStringExtra("finish_p");

        // Bustation new value check
        SharedPreferences bus_station_info = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(CheckAppFirstExecute())
            BusStationLoading.settingList(bus_station_info);
        BusStationLoading.checkingChanges(bus_station_info);

        super.autoInputComplete(startingPointView, finishPointView);
        startingPointView.setText(starting_p);
        finishPointView.setText(finish_p);

        super.setSwapButton(startingPointView, finishPointView);

        super.setSearchButton(startingPointView, finishPointView);

        setBackButton();
        setBookmarkButton(starting_p, finish_p);

        setBusBoard(startingPointView);

        // 여기부터 네비게이션 뷰 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 메뉴 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.view_menu_icon); //메뉴 버튼 이미지 지정

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.setting:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        // 여기까지 네비게이션 뷰 설정
    }

    private void setBookmarkButton(String starting_p, String finish_p){
        ImageButton bookmarkbutton = (ImageButton)findViewById(R.id.bookmarkbutton);
        bookmarkbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences book = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = book.edit();
                editor.putString("start", starting_p);
                editor.putString("finish", finish_p);
                editor.commit();
                Toast.makeText(getApplicationContext(), "즐겨찾기가 설정 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }); // 즐겨찾기 설정 기능
    }

    private void setBackButton(){
        ImageButton back_button = (ImageButton) findViewById(R.id.back_button);
        //back_button.setBackgroundResource(R.drawable.backbtn);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);//액티비티 띄우기
                finish();
            }
        }); // 뒤로가기 기능
    }

    private void setAlarm(){
        int arrivalTime;
        arrivalTime = GetLastBusInfo.getLastBusRemainTime() * 60000;

        // ##여기부터 알람설정
        Switch switchbutton = (Switch) findViewById(R.id.alarm_switch);
        switchbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true)
                {
                    SharedPreferences test = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    int value = test.getInt("milli", 600000);

                    // 현재 지정된 시간으로 알람 시간 설정
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis()+arrivalTime-value); //버스 도착 value분 전 알람(milli)

                    // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
                    if (calendar.before(Calendar.getInstance())) {
                        calendar.add(Calendar.DATE, 1);
                    }

                    Date currentDateTime = calendar.getTime();
                    String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
                    Toast.makeText(getApplicationContext(),date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();

                    //  Preference에 설정한 값 저장
                    SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
                    editor.putLong("nextNotifyTime", (long)calendar.getTimeInMillis());
                    editor.apply();
                    diaryNotification(calendar);
                }
            }
        });
        // ##여기까지 알람설정
    }

    void diaryNotification(Calendar calendar)
    {
        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = this.getPackageManager();
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {
            if (alarmManager != null) {

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
        }
    }

    private void setLastBusInfo(AutoCompleteTextView startingPointView, AutoCompleteTextView finishPointView){
        /* View Part */
        //set busNumberVier
        GetLastBusInfo.getLastBusInfo(startingPointView.getText().toString(), finishPointView.getText().toString());
        String busNumber = "";
        TextView busNumberView = (TextView) findViewById(R.id.busNumber);
        busNumber = GetLastBusInfo.getLastBusNumer();
        busNumberView.setText(busNumber);
        //busArrivalTime View
        String busArrivalMsg = "";
        TextView busArrivalTimeView = (TextView) findViewById(R.id.busArribalTime);
        busArrivalMsg = GetLastBusInfo.getLastBusAnnounceMsg();
        busArrivalTimeView.setText(busArrivalMsg);
    }

    private void setBusBoard(AutoCompleteTextView startingPointView){
        /* View Part */
        //board Bus number view
        GetBusBoard.getBusBoardInfo(startingPointView.getText().toString());
        String boardBusNumber1 = "";
        TextView boardBusNumber1View = (TextView) findViewById(R.id.first_bus_name);
        boardBusNumber1 = GetBusBoard.getBoardBusNumber1();
        boardBusNumber1View.setText(boardBusNumber1);
        String boardBusNumber2 = "";
        TextView boardBusNumber2View = (TextView) findViewById(R.id.second_bus_name);
        boardBusNumber2 = GetBusBoard.getBoardBusNumber2();
        boardBusNumber2View.setText(boardBusNumber2);
        String boardBusNumber3 = "";
        TextView boardBusNumber3View = (TextView) findViewById(R.id.third_bus_name);
        boardBusNumber3 = GetBusBoard.getBoardBusNumber3();
        boardBusNumber3View.setText(boardBusNumber3);
        //board Bus arrival time view
        String boardBusArrivalTime1 = "";
        TextView boardBusArrivalTime1View = (TextView) findViewById(R.id.first_bus_time);
        boardBusArrivalTime1 = GetBusBoard.getBoardBusArrivalTime1();
        boardBusArrivalTime1View.setText(boardBusArrivalTime1);
        String boardBusArrivalTime2 = "";
        TextView boardBusArrivalTime2View = (TextView) findViewById(R.id.second_bus_time);
        boardBusArrivalTime2 = GetBusBoard.getBoardBusArrivalTime2();
        boardBusArrivalTime2View.setText(boardBusArrivalTime2);
        String boardBusArrivalTime3 = "";
        TextView boardBusArrivalTime3View = (TextView) findViewById(R.id.third_bus_time);
        boardBusArrivalTime3 = GetBusBoard.getBoardBusArrivalTime3();
        boardBusArrivalTime3View.setText(boardBusArrivalTime3);
        //board bus location view
        String boardBusLocation1 = "";
        TextView boardBusLocation1View = (TextView) findViewById(R.id.first_bus_location);
        boardBusLocation1 = GetBusBoard.getBoardBusLocation1();
        boardBusLocation1View.setText(boardBusLocation1);
        String boardBusLocation2 = "";
        TextView boardBusLocation2View = (TextView) findViewById(R.id.second_bus_location);
        boardBusLocation2 = GetBusBoard.getBoardBusLocation2();
        boardBusLocation2View.setText(boardBusLocation2);
        String boardBusLocation3 = "";
        TextView boardBusLocation3View = (TextView) findViewById(R.id.third_bus_location);
        boardBusLocation3 = GetBusBoard.getBoardBusLocation3();
        boardBusLocation3View.setText(boardBusLocation3);
    }

    protected boolean CheckAppFirstExecute(){
        SharedPreferences pref = getSharedPreferences("IsFirst" , Activity.MODE_PRIVATE);
        boolean isFirst = pref.getBoolean("isFirst", false);
        if(!isFirst){ //최초 실행시 true 저장
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", true);
            editor.commit();
        }

        return !isFirst;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}