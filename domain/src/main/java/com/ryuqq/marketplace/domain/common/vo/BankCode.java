package com.ryuqq.marketplace.domain.common.vo;

/**
 * 은행 코드.
 *
 * <p>금융결제원(KFTC) 3자리 기관코드 기반. PG사(토스페이먼츠, 나이스페이, 포트원 등) 공통 표준.
 */
public enum BankCode {

    // 시중은행
    KB_KOOKMIN("004", "KB국민은행"),
    SHINHAN("088", "신한은행"),
    WOORI("020", "우리은행"),
    HANA("081", "하나은행"),
    SC("023", "SC제일은행"),
    CITI("027", "한국씨티은행"),

    // 특수은행
    IBK("003", "IBK기업은행"),
    NH("011", "NH농협은행"),
    NH_LOCAL("012", "단위농협"),
    SUHYUP("007", "Sh수협은행"),
    KDB("002", "한국산업은행"),
    EXIM("008", "수출입은행"),

    // 지방은행
    IM_BANK("031", "iM뱅크(대구)"),
    BUSAN("032", "부산은행"),
    GWANGJU("034", "광주은행"),
    JEJU("035", "제주은행"),
    JEONBUK("037", "전북은행"),
    GYEONGNAM("039", "경남은행"),

    // 인터넷전문은행
    K_BANK("089", "케이뱅크"),
    KAKAO_BANK("090", "카카오뱅크"),
    TOSS_BANK("092", "토스뱅크"),

    // 제2금융권
    SAEMAUL("045", "새마을금고"),
    SHINHYUP("048", "신협"),
    SAVINGS_BANK("050", "상호저축은행"),
    SANLIM("064", "산림조합"),
    POST_OFFICE("071", "우체국");

    private final String code;
    private final String displayName;

    BankCode(String code, String displayName) {
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
