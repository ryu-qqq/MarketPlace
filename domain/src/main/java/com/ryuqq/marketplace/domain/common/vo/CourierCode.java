package com.ryuqq.marketplace.domain.common.vo;

/**
 * 택배사 코드.
 *
 * <p>Sweet Tracker(스마트택배) 숫자 코드 체계 기반. 레거시 {@code ShipmentCompanyCode}에서 SHIP 접두사를 제거한 형태.
 */
public enum CourierCode {
    KOREA_POST("01", "우체국택배"),
    CJ_LOGISTICS("04", "CJ대한통운"),
    HANJIN("05", "한진택배"),
    LOGEN("06", "로젠택배"),
    LOTTE("08", "롯데택배"),
    ILYANG("11", "일양로지스"),
    HANJIN_LOVE("16", "한의사랑택배"),
    CHUNIL("17", "천일택배"),
    KUNYOUNG("18", "건영택배"),
    HANDEX("20", "한덱스"),
    DAESIN("22", "대신택배"),
    KYUNGDONG("23", "경동택배"),
    GS_POSTBOX("24", "GS Postbox 택배"),
    HAPDONG("32", "합동택배"),
    GOODTOLUCK("40", "굿투럭"),
    ANYTRACK("43", "애니트랙"),
    SLX("44", "SLX택배"),
    WOORI("45", "우리택배"),
    CU_POST("46", "CU 편의점택배"),
    WOORI_HANBANG("47", "우리한방택배"),
    NONGHYUP("53", "농협택배"),
    HOMEPICK("54", "홈픽택배"),
    IK_LOGISTICS("71", "IK물류"),
    SUNGHUN("72", "성훈물류"),
    YONGMA("74", "용마로지스"),
    WONDERS_QUICK("75", "원더스퀵"),
    LOGISVALLEY("79", "로지스밸리택배"),
    KURLY("82", "컬리로지스"),
    FULL_AT_HOME("85", "풀앳홈"),
    SAMSUNG("86", "삼성전자물류"),
    CURUN("88", "큐런택배"),
    DOOBAL_HERO("89", "두발히어로"),
    WINIADIMCHAE("90", "위니아딤채"),
    GENIEGO("92", "지니고 당일배송"),
    TODAYS_PICKUP("94", "오늘의픽업"),
    LOGISVALLEY_SAME("96", "로지스밸리"),
    HANSEM("101", "한샘서비스원 택배"),
    NDEX_KOREA("103", "NDEX KOREA"),
    DODOFLEX("104", "도도플렉스"),
    LG_PANTOS("107", "LG전자(판토스)"),
    VROONG("110", "부릉"),
    HOME_1004("112", "1004홈"),
    THUNDER_HERO("113", "썬더히어로"),
    TEAMFRESH("116", "팀프레시"),
    LOTTE_CHILSUNG("118", "롯데칠성"),
    PINGPONG("119", "핑퐁"),
    VALLEX("120", "발렉스 특수물류"),
    NTLPS("123", "엔티엘피스"),
    GTS_LOGIS("125", "GTS로지스"),
    LOGISPOT("127", "로지스팟"),
    HOMEPICK_TODAY("129", "홈픽 오늘도착"),
    UFO_LOGIS("130", "UFO로지스"),
    DELI_RABBIT("131", "딜리래빗"),
    GEOPI("132", "지오피"),
    HK_HOLDINGS("134", "에이치케이홀딩스"),
    HTNS("135", "HTNS"),
    KJT("136", "케이제이티"),
    THE_BAO("137", "더바오"),
    LAST_MILE("138", "라스트마일"),
    ONEULHOE_RUSH("139", "오늘회 러쉬"),
    TANGO_AND_GO("142", "탱고앤고"),
    TODAY("143", "투데이"),
    MANUAL("999", "수동처리(퀵, 방문수령 등)");

    private final String code;
    private final String displayName;

    CourierCode(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String code() {
        return code;
    }

    public String displayName() {
        return displayName;
    }
}
