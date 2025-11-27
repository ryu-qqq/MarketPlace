package com.ryuqq.marketplace.domain.brand.vo;

import java.util.Set;

/**
 * Country Value Object
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>유효한 국가 코드만 허용</li>
 *   <li>대소문자 구분 없음 (자동 대문자 변환)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record Country(String code) {

    private static final Set<String> VALID_COUNTRY_CODES = Set.of(
        "KR", "US", "FR", "IT", "UK", "JP", "CN", "DE", "ES", "CA", "AU", "NL", "SE", "CH", "BE"
    );

    /**
     * Compact Constructor (검증 로직)
     */
    public Country {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Country code는 null이거나 빈 문자열일 수 없습니다.");
        }

        code = code.trim().toUpperCase();

        if (!VALID_COUNTRY_CODES.contains(code)) {
            throw new IllegalArgumentException(
                String.format("유효하지 않은 국가 코드입니다: %s (허용: %s)",
                    code,
                    String.join(", ", VALID_COUNTRY_CODES))
            );
        }
    }

    /**
     * 값 기반 생성
     *
     * @param code 국가 코드 (대소문자 무관)
     * @return Country
     * @throws IllegalArgumentException 유효하지 않은 국가 코드
     */
    public static Country of(String code) {
        return new Country(code);
    }

    /**
     * 국가명 반환
     *
     * @return 국가명
     */
    public String countryName() {
        return switch (code) {
            case "KR" -> "대한민국";
            case "US" -> "미국";
            case "FR" -> "프랑스";
            case "IT" -> "이탈리아";
            case "UK" -> "영국";
            case "JP" -> "일본";
            case "CN" -> "중국";
            case "DE" -> "독일";
            case "ES" -> "스페인";
            case "CA" -> "캐나다";
            case "AU" -> "호주";
            case "NL" -> "네덜란드";
            case "SE" -> "스웨덴";
            case "CH" -> "스위스";
            case "BE" -> "벨기에";
            default -> throw new IllegalStateException("매핑되지 않은 국가 코드: " + code);
        };
    }

    /**
     * 유효한 국가 코드 목록 반환
     *
     * @return 국가 코드 Set
     */
    public static Set<String> validCodes() {
        return VALID_COUNTRY_CODES;
    }
}
