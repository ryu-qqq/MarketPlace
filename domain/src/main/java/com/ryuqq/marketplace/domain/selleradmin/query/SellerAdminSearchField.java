package com.ryuqq.marketplace.domain.selleradmin.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/**
 * SellerAdmin 검색 필드.
 *
 * <p>셀러 관리자 목록 조회 시 검색 가능한 필드를 정의합니다.
 *
 * <p>DB 컬럼명 매핑은 Adapter 레이어에서 수행합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public enum SellerAdminSearchField implements SearchField {

    /** 로그인 ID */
    LOGIN_ID("loginId"),

    /** 이름 */
    NAME("name");

    private final String fieldName;

    SellerAdminSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 SellerAdminSearchField 변환.
     *
     * @param value 필드명 문자열
     * @return SellerAdminSearchField (null이면 null 반환)
     */
    public static SellerAdminSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (SellerAdminSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
