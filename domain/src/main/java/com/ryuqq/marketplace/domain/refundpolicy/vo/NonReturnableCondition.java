package com.ryuqq.marketplace.domain.refundpolicy.vo;

/** 반품 불가 조건 Enum. */
public enum NonReturnableCondition {
    OPENED_PACKAGING("포장 개봉", "고객 변심으로 인한 포장 개봉 시"),
    USED_PRODUCT("사용 흔적", "사용 흔적이 있는 경우"),
    TIME_EXPIRED("시간 경과", "시간 경과로 재판매가 불가한 경우"),
    DIGITAL_CONTENT("디지털 콘텐츠", "복제 가능한 디지털 콘텐츠"),
    CUSTOM_MADE("주문 제작", "고객 요청에 따른 주문 제작 상품"),
    HYGIENE_PRODUCT("위생 상품", "식품/화장품 등 위생상 문제가 있는 상품"),
    PARTIAL_SET("세트 일부", "세트 상품의 일부만 반품하는 경우"),
    MISSING_TAG("택/라벨 제거", "상품 택이나 라벨이 제거된 경우"),
    DAMAGED_BY_CUSTOMER("고객 과실 파손", "고객 과실로 인한 상품 파손");

    private final String displayName;
    private final String description;

    NonReturnableCondition(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String displayName() {
        return displayName;
    }

    public String description() {
        return description;
    }
}
