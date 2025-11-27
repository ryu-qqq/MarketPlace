package com.ryuqq.marketplace.domain.category.vo;

/**
 * Product Group Enum
 *
 * <p><strong>상품 그룹</strong>:</p>
 * <ul>
 *   <li>CLOTHING - 의류</li>
 *   <li>SHOES - 신발</li>
 *   <li>BAGS - 가방</li>
 *   <li>ACCESSORIES - 액세서리</li>
 *   <li>JEWELRY - 주얼리</li>
 *   <li>BEAUTY - 뷰티</li>
 *   <li>HOME - 홈/리빙</li>
 *   <li>ELECTRONICS - 전자기기</li>
 *   <li>ETC - 기타</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum ProductGroup {
    CLOTHING("의류"),
    SHOES("신발"),
    BAGS("가방"),
    ACCESSORIES("액세서리"),
    JEWELRY("주얼리"),
    BEAUTY("뷰티"),
    HOME("홈/리빙"),
    ELECTRONICS("전자기기"),
    ETC("기타");

    private final String displayName;

    ProductGroup(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 표시용 이름 반환
     *
     * @return 한글 표시명
     */
    public String displayName() {
        return displayName;
    }

    /**
     * 문자열로부터 ProductGroup 찾기
     *
     * @param value 문자열 값
     * @return ProductGroup
     * @throws IllegalArgumentException 일치하는 ProductGroup이 없는 경우
     */
    public static ProductGroup fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ProductGroup 값은 null이거나 빈 문자열일 수 없습니다.");
        }

        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 ProductGroup 값입니다: " + value);
        }
    }
}
