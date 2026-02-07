package com.ryuqq.marketplace.domain.commoncode.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/**
 * CommonCode 정렬 키.
 *
 * <p>공통 코드 목록 조회 시 사용 가능한 정렬 필드를 정의합니다.
 */
public enum CommonCodeSortKey implements SortKey {

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt"),

    /** 표시 순서 */
    DISPLAY_ORDER("displayOrder"),

    /** 코드값 알파벳 순 */
    CODE("code");

    private final String fieldName;

    CommonCodeSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static CommonCodeSortKey defaultKey() {
        return CREATED_AT;
    }
}
