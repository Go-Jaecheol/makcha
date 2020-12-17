package com.example.makcha;

public class GetLastBusInfo {
    private String lastBusNumer = "";
    private int lastBusRemainTime = 0;

    protected void getLastBusInfo(String starting_p, String finish_p){
        // 1. 서버에게 출발지와 목적지 전달
        // 2. 막차 버스 정보를 받는다

        // 3-1. 받은 정보 중 버스 번호 저장
        lastBusNumer = "937";
        // 3-2. 받은 정보 막차까지 남은 예상 분 저장
        lastBusRemainTime = 90;
    }

    protected String getLastBusAnnounceMsg() {
        int hour = lastBusRemainTime / 60;
        int min = lastBusRemainTime % 60;
        String msg = "막차 " + String.valueOf(hour) + "시간 " + String.valueOf(min) + "분 후 도착 예정";

        return msg;
    }

    public String getLastBusNumer() {
        return lastBusNumer;
    }

    public int getLastBusRemainTime() {
        return lastBusRemainTime;
    }
}
