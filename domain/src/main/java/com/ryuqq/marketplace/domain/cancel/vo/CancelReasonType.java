package com.ryuqq.marketplace.domain.cancel.vo;

/** 취소 사유 유형. */
public enum CancelReasonType {
    CHANGE_OF_MIND,
    WRONG_ORDER,
    FOUND_CHEAPER,
    DELIVERY_TOO_SLOW,
    OUT_OF_STOCK,
    PRODUCT_DISCONTINUED,
    PRICE_ERROR,
    SHIPPING_UNAVAILABLE,
    PRODUCT_ISSUE,
    OTHER
}
