package com.ryuqq.marketplace.domain.brand.vo;

/**
 * Brand Alias ID Value Object
 *
 * <p><strong>생성 패턴</strong>:</p>
 * <ul>
 *   <li>{@code forNew()} - 신규 생성 (ID = null, Auto Increment 대비)</li>
 *   <li>{@code of(Long value)} - 값 기반 생성 (null 체크 필수)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record BrandAliasId(Long value) {

    /**
     * Compact Constructor (검증 로직)
     *
     * <p>주의: forNew()로 생성 시 null 허용</p>
     */
    public BrandAliasId {
        if (value != null && value <= 0) {
            throw new IllegalArgumentException("BrandAliasId 값은 양수여야 합니다: " + value);
        }
    }

    /**
     * 신규 생성 (Auto Increment 대비)
     *
     * <p>ID가 null인 상태로 생성. DB에서 Auto Increment로 할당될 예정.</p>
     *
     * @return BrandAliasId (value = null)
     */
    public static BrandAliasId forNew() {
        return new BrandAliasId(null);
    }

    /**
     * 값 기반 생성
     *
     * @param value ID 값 (null 불가)
     * @return BrandAliasId
     * @throws IllegalArgumentException value가 null이거나 음수인 경우
     */
    public static BrandAliasId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("BrandAliasId 값은 null일 수 없습니다.");
        }
        return new BrandAliasId(value);
    }

    /**
     * null 여부 확인
     *
     * @return ID가 null이면 true
     */
    public boolean isNew() {
        return value == null;
    }
}
