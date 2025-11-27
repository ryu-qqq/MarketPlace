package com.ryuqq.marketplace.domain.brand.vo;

/**
 * Alias Source Value Object
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>sourceType: 필수</li>
 *   <li>sellerId: 기본값 0L</li>
 *   <li>mallCode: 기본값 "GLOBAL"</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record AliasSource(
    AliasSourceType sourceType,
    Long sellerId,
    String mallCode
) {

    /**
     * Compact Constructor (검증 로직 + 기본값 설정)
     */
    public AliasSource {
        if (sourceType == null) {
            throw new IllegalArgumentException("sourceType은 null일 수 없습니다.");
        }

        if (sellerId == null) {
            sellerId = 0L;
        }

        if (mallCode == null || mallCode.isBlank()) {
            mallCode = "GLOBAL";
        } else {
            mallCode = mallCode.trim().toUpperCase();
        }
    }

    /**
     * 값 기반 생성
     *
     * @param sourceType 소스 타입
     * @param sellerId 셀러 ID (null 허용, 기본값 0L)
     * @param mallCode 몰 코드 (null 허용, 기본값 "GLOBAL")
     * @return AliasSource
     * @throws IllegalArgumentException sourceType이 null인 경우
     */
    public static AliasSource of(AliasSourceType sourceType, Long sellerId, String mallCode) {
        return new AliasSource(sourceType, sellerId, mallCode);
    }

    /**
     * 수동 입력 소스 생성
     *
     * @return MANUAL 타입, sellerId=0, mallCode="GLOBAL"
     */
    public static AliasSource manual() {
        return new AliasSource(AliasSourceType.MANUAL, 0L, "GLOBAL");
    }

    /**
     * 셀러 소스 생성
     *
     * @param sellerId 셀러 ID
     * @return SELLER 타입
     */
    public static AliasSource seller(Long sellerId) {
        return new AliasSource(AliasSourceType.SELLER, sellerId, "GLOBAL");
    }

    /**
     * 외부몰 소스 생성
     *
     * @param mallCode 몰 코드
     * @return EXTERNAL_MALL 타입
     */
    public static AliasSource externalMall(String mallCode) {
        return new AliasSource(AliasSourceType.EXTERNAL_MALL, 0L, mallCode);
    }

    /**
     * 시스템 소스 생성
     *
     * @return SYSTEM 타입
     */
    public static AliasSource system() {
        return new AliasSource(AliasSourceType.SYSTEM, 0L, "GLOBAL");
    }

    /**
     * 셀러 소스 여부 확인
     *
     * @return SELLER 타입이면 true
     */
    public boolean isSeller() {
        return sourceType == AliasSourceType.SELLER;
    }

    /**
     * 외부몰 소스 여부 확인
     *
     * @return EXTERNAL_MALL 타입이면 true
     */
    public boolean isExternalMall() {
        return sourceType == AliasSourceType.EXTERNAL_MALL;
    }
}
