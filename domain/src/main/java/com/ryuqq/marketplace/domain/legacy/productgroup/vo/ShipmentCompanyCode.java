package com.ryuqq.marketplace.domain.legacy.productgroup.vo;

/** 레거시(세토프) 택배사 코드 enum. */
public enum ShipmentCompanyCode {
    SHIP04("04", "CJ대한통운"),
    SHIP05("05", "한진택배"),
    SHIP08("08", "롯데택배"),
    SHIP01("01", "우체국택배"),
    SHIP06("06", "로젠택배"),
    SHIP11("11", "일양로지스"),
    SHIP20("20", "한덱스"),
    SHIP22("22", "대신택배"),
    SHIP23("23", "경동택배"),
    SHIP32("32", "합동택배"),
    SHIP46("46", "CU 편의점택배"),
    SHIP24("24", "GS Postbox 택배"),
    SHIP16("16", "한의사랑택배"),
    SHIP17("17", "천일택배"),
    SHIP18("18", "건영택배"),
    SHIP40("40", "굿투럭"),
    SHIP43("43", "애니트랙"),
    SHIP44("44", "SLX택배"),
    SHIP45("45", "우리택배(구호남택배)"),
    SHIP47("47", "우리한방택배"),
    SHIP53("53", "농협택배"),
    SHIP54("54", "홈픽택배"),
    SHIP71("71", "IK물류"),
    SHIP72("72", "성훈물류"),
    SHIP74("74", "용마로지스"),
    SHIP75("75", "원더스퀵"),
    SHIP79("79", "로지스밸리택배"),
    SHIP82("82", "컬리로지스"),
    SHIP85("85", "풀앳홈"),
    SHIP86("86", "삼성전자물류"),
    SHIP88("88", "큐런택배"),
    SHIP89("89", "두발히어로"),
    SHIP90("90", "위니아딤채"),
    SHIP92("92", "지니고 당일배송"),
    SHIP94("94", "오늘의픽업"),
    SHIP96("96", "로지스밸리"),
    SHIP101("101", "한샘서비스원 택배"),
    SHIP103("103", "NDEX KOREA"),
    SHIP104("104", "도도플렉스(dodoflex)"),
    SHIP107("107", "LG전자(판토스)"),
    SHIP110("110", "부릉"),
    SHIP112("112", "1004홈"),
    SHIP113("113", "썬더히어로"),
    SHIP116("116", "(주)팀프레시"),
    SHIP118("118", "롯데칠성"),
    SHIP119("119", "핑퐁"),
    SHIP120("120", "발렉스 특수물류"),
    SHIP123("123", "엔티엘피스"),
    SHIP125("125", "GTS로지스"),
    SHIP127("127", "로지스팟"),
    SHIP129("129", "홈픽 오늘도착"),
    SHIP130("130", "UFO로지스"),
    SHIP131("131", "딜리래빗"),
    SHIP132("132", "지오피"),
    SHIP134("134", "에이치케이홀딩스"),
    SHIP135("135", "HTNS"),
    SHIP136("136", "케이제이티"),
    SHIP137("137", "더바오"),
    SHIP138("138", "라스트마일"),
    SHIP139("139", "오늘회 러쉬"),
    SHIP142("142", "탱고앤고"),
    SHIP143("143", "투데이"),
    SHIP144("144", "발렉스 특수물류"),
    SHIP145("145", "지정없음 (구매자자율)"),
    SHIP146("08", "롯데택배(구 현대택배)"),
    SHIP999("MENUAL", "수동처리(퀵, 방문수령 등)"),
    REFER_DETAIL("REFER_DETAIL", "상세 정보 참고");

    private final String code;
    private final String description;

    ShipmentCompanyCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String code() {
        return code;
    }

    public String description() {
        return description;
    }
}
