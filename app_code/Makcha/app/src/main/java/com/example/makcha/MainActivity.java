package com.example.makcha;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toast toast;
    private BackPressHandler backPressHandler = new BackPressHandler(this);
    private DrawerLayout mDrawerLayout; // 네비게이터 Drawer
    private Context context = this;
    public BusStationLoading BusStationLoading = new BusStationLoading();
    private StartFinishInputControl StartFinishInputControl = new StartFinishInputControl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bustation setting
        BusStationLoading.settingList();
        // ##여기서부터 자동완성 부분
        final AutoCompleteTextView startingPointView = (AutoCompleteTextView) findViewById(R.id.starting_point);
        final AutoCompleteTextView finishPointView = (AutoCompleteTextView) findViewById(R.id.finish_point);
        // AutoCompleteTextView 에 아답터를 연결한다.
        startingPointView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,  BusStationLoading.BusStationList ));
        finishPointView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,  BusStationLoading.BusStationList ));
        // ##여기까지가 자동완성 부분

        ImageButton change_button = (ImageButton) findViewById(R.id.change_button);
        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartFinishInputControl.swap_starting_and_ending(startingPointView, finishPointView);
            }
        }); // 출발지, 도착지 Swap 기능

        ImageButton search_button = (ImageButton)findViewById(R.id.search_button);
       // search_button.setBackgroundResource(R.drawable.searchbtn);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startingPointView.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "출발지를 입력하세요.", Toast.LENGTH_SHORT).show();
                else if(finishPointView.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "도착지를 입력하세요.", Toast.LENGTH_SHORT).show();
                else {
                    if(!BusStationLoading.BusStationList.contains(startingPointView.getText().toString()))
                        Toast.makeText(getApplicationContext(), "존재하지 않는 출발지입니다.", Toast.LENGTH_SHORT).show();
                    else if(!BusStationLoading.BusStationList.contains(finishPointView.getText().toString()))
                        Toast.makeText(getApplicationContext(), "존재하지 않는 도착지입니다.", Toast.LENGTH_SHORT).show();
                    else{
                        Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                        intent.putExtra("starting_p", startingPointView.getText().toString());
                        intent.putExtra("finish_p", finishPointView.getText().toString());
                        startActivity(intent);//액티비티 띄우기
                        finish();
                    }
                }
            }
        }); // 검색 기능

        // 여기부터 네비게이터 뷰 설정
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
        // 여기까지 네비게이터 뷰 설정
    }

    @Override
    public void onBackPressed() {
        backPressHandler.onBackPressed("뒤로가기 버튼 한번 더 누르면 앱 종료");
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