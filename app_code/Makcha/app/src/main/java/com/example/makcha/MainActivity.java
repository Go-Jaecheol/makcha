package com.example.makcha;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BackPressHandler backPressHandler = new BackPressHandler(this);
    private DrawerLayout mDrawerLayout; // 네비게이터 Drawer
    private Context context = this;

    private List<String> list;          // 데이터를 넣은 리스트변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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