package com.example.makcha;

public class GetBusBoard {
    private String boardBusNumber1 = "";
    private String boardBusNumber2 = "";
    private String boardBusNumber3 = "";
    private String boardBusArrivalTime1 = "";
    private String boardBusArrivalTime2 = "";
    private String boardBusArrivalTime3 = "";
    private String boardBusLocation1 = "";
    private String boardBusLocation2 = "";
    private String boardBusLocation3 = "";



    protected void getBusBoardInfo(String starting_p){
        // 1. 서버에게 조회할 정류장 정보 전달
        // 2. 정류장의 버스 전광판 정보를 받는다

        // 3. 받은 정보 setter에게 전달
        setBusBoardInfo();
    }

    private void setBusBoardInfo() {
        // 1. 도착 소요 예정 시간이 이른 순으로 정렬

        // 2. 선언한 변수에 데이터 저장
        boardBusNumber1 = "937";
        boardBusNumber2 = "503";
        boardBusNumber3 = "북구2";

        boardBusArrivalTime1 = "5분전";
        boardBusArrivalTime2 = "6분전";
        boardBusArrivalTime3 = "15분전";

        boardBusLocation1 = "동대구멘션";
        boardBusLocation2 = "동침산네거리";
        boardBusLocation3 = "수창초등학교";
    }

    protected String getBoardBusArrivalTime1() {
        return boardBusArrivalTime1;
    }

    protected String getBoardBusArrivalTime2() {
        return boardBusArrivalTime2;
    }

    protected String getBoardBusArrivalTime3() {
        return boardBusArrivalTime3;
    }

    protected String getBoardBusLocation1() {
        return boardBusLocation1;
    }

    protected String getBoardBusLocation2() {
        return boardBusLocation2;
    }

    protected String getBoardBusLocation3() {
        return boardBusLocation3;
    }

    protected String getBoardBusNumber1() {
        return boardBusNumber1;
    }

    protected String getBoardBusNumber2() {
        return boardBusNumber2;
    }

    protected String getBoardBusNumber3() {
        return boardBusNumber3;
    }
}
