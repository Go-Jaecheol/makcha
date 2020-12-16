package com.example.makcha;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchResultActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Context context = this;

    private List<String> list;          // 데이터를 넣은 리스트변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

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
                    calendar.setTimeInMillis(System.currentTimeMillis()+value); //10초뒤 알람(milli)

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

        Intent it = getIntent();
        String starting_p = it.getStringExtra("starting_p");
        String finish_p = it.getStringExtra("finish_p");
       // ##여기서부터 자동완성 부분
        list = new ArrayList<String>(); // 리스트 생성
        settingList(); // 리스트에 검색될 데이터(단어)를 추가한다.
        final AutoCompleteTextView startingPointView = (AutoCompleteTextView) findViewById(R.id.starting_point);
        final AutoCompleteTextView finishPointView = (AutoCompleteTextView) findViewById(R.id.finish_point);
        // AutoCompleteTextView 에 아답터를 연결한다.
        startingPointView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,  list ));
        finishPointView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,  list ));
        // ##여기까지가 자동완성 부분
        startingPointView.setText(starting_p);
        finishPointView.setText(finish_p);

        ImageButton change_button = (ImageButton) findViewById(R.id.change_button);
        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable temp;
                temp = startingPointView.getText();
                startingPointView.setText(finishPointView.getText());
                finishPointView.setText(temp);
            }
        }); // 출발지, 도착지 Swap 기능

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

        ImageButton search_button = (ImageButton)findViewById(R.id.search_button);
        // search_button.setBackgroundResource(R.drawable.searchbtn);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("starting_p", startingPointView.getText().toString());
                intent.putExtra("finish_p", finishPointView.getText().toString());
                startActivity(intent);//액티비티 띄우기
                finish();
            }
        }); // 검색 기능


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

    private void settingList(){
        list.add("덕원고등학교앞2");
        list.add("욱수초등학교앞");
        list.add("시지중학교");
        list.add("신매태성아파트건너");
        list.add("신매역(4번출구)");
        list.add("대구농업마이스터고건너");
        list.add("고산역화성파크드림건너");
        list.add("고산2동행정복지센터앞");
        list.add("고산농협창고건너");
        list.add("월드컵삼거리앞");
        list.add("대공원역(1번출구)");
        list.add("연호동화훼단지2");
        list.add("방공포병학교건너");
        list.add("담티고개2");
        list.add("대륜중고등학교건너");
        list.add("도시철도담티역(1번출구)");
        list.add("수성대학교건너");
        list.add("구.남부정류장앞1");
        list.add("만촌2동행정복지센터");
        list.add("만촌태왕리더스앞2");
        list.add("만촌태왕리더스북편");
        list.add("청구시장앞");
        list.add("만촌1동행정복지센터");
        list.add("메트로팔레스1");
        list.add("팔공정보문화센터앞");
        list.add("경북아파트건너(신천동)");
        list.add("동대구역복합환승센터앞");
        list.add("동대구역");
        list.add("동대구맨션");
        list.add("평화시장(닭똥집골목)앞");
        list.add("공고네거리1");
        list.add("신암초등학교건너");
        list.add("경북대학교정문건너");
        list.add("경대아파트앞");
        list.add("경진초등학교건너");
        list.add("영진고등학교앞");
        list.add("영진전문대학교후문앞");
        list.add("복현오거리2");
        list.add("복현대백맨션앞");
        list.add("코스트코홀세일건너");
        list.add("복현화성타운앞");
        list.add("검단유성아파트(서편)");
        list.add("대구우편집중국앞");
        list.add("엑스코건너");
        list.add("전자관");
        list.add("전자상가");
        list.add("산격거평타운앞");
        list.add("무태네거리(시외방향)");
        list.add("유니버시아드선수촌2단지앞");
        list.add("동변주공그린빌건너");
        list.add("무태새마을금고건너");
        list.add("동서교회앞");
        list.add("무태조야동행정복지센터건너2");
        list.add("유니버시아드레포츠센터건너");
        list.add("북구구민운동장앞");
        list.add("칠곡그린빌5단지앞");
        list.add("강북경찰서건너");
        list.add("칠곡그린파크1");
        list.add("칠곡3차화성타운2");
        list.add("화성센트럴파크");
        list.add("칠곡2차영남타운앞");
        list.add("칠곡화성타운앞1");
        list.add("한국농어촌공사건너");
        list.add("칠곡운암역2");
        list.add("칠곡에덴타운건너");
        list.add("칠곡서한맨션앞");
        list.add("칠곡IC 1");
        list.add("한국도로공사건너");
        list.add("칠곡한라타운앞");
        list.add("관음변전소2");
        list.add("양지마을건너");
        list.add("칠곡우방타운건너(종점)");
        list.add("양지마을앞");
        list.add("관음변전소1");
    }
}