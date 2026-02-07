package com.ryuqq.marketplace.domain.seller.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/**
 * Seller 검색 필드.
 *
 * <p>셀러 목록 조회 시 검색 가능한 필드를 정의합니다.
 *
 * <p>DB 컬럼명 매핑은 Adapter 레이어에서 수행합니다.
 */
public enum SellerSearchField implements SearchField {

    /** 셀러명 */
    SELLER_NAME("sellerName"),

    /** 사업자등록번호 */
    REGISTRATION_NUMBER("registrationNumber"),

    /** 회사명 */
    COMPANY_NAME("companyName"),

    /** 대표자명 */
    REPRESENTATIVE_NAME("representativeName");

    private final String fieldName;

    SellerSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 SellerSearchField 변환.
     *
     * @param value 필드명 문자열
     * @return SellerSearchField (null이면 null 반환)
     */
    public static SellerSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (SellerSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
