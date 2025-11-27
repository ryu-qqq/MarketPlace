package com.ryuqq.marketplace.domain.brand.vo;

/**
 * Brand Meta Value Object
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>officialWebsite - 선택, URL 형식, 최대 500자</li>
 *   <li>logoUrl - 선택, URL 형식, 최대 500자</li>
 *   <li>description - 선택, 최대 2000자</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record BrandMeta(
    String officialWebsite,
    String logoUrl,
    String description
) {

    private static final int MAX_URL_LENGTH = 500;
    private static final int MAX_DESCRIPTION_LENGTH = 2000;
    private static final String URL_PATTERN = "^https?://[a-zA-Z0-9\\-._~:/?#\\[\\]@!$&'()*+,;=]+$";

    /**
     * Compact Constructor (검증 로직)
     */
    public BrandMeta {
        officialWebsite = officialWebsite != null ? officialWebsite.trim() : null;
        logoUrl = logoUrl != null ? logoUrl.trim() : null;
        description = description != null ? description.trim() : null;

        if (officialWebsite != null && !officialWebsite.isEmpty()) {
            if (officialWebsite.length() > MAX_URL_LENGTH) {
                throw new IllegalArgumentException(
                    String.format("officialWebsite는 %d자를 초과할 수 없습니다: %d",
                        MAX_URL_LENGTH, officialWebsite.length())
                );
            }
            if (!officialWebsite.matches(URL_PATTERN)) {
                throw new IllegalArgumentException("유효하지 않은 URL 형식입니다: " + officialWebsite);
            }
        }

        if (logoUrl != null && !logoUrl.isEmpty()) {
            if (logoUrl.length() > MAX_URL_LENGTH) {
                throw new IllegalArgumentException(
                    String.format("logoUrl은 %d자를 초과할 수 없습니다: %d",
                        MAX_URL_LENGTH, logoUrl.length())
                );
            }
            if (!logoUrl.matches(URL_PATTERN)) {
                throw new IllegalArgumentException("유효하지 않은 URL 형식입니다: " + logoUrl);
            }
        }

        if (description != null && !description.isEmpty() && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                String.format("description은 %d자를 초과할 수 없습니다: %d",
                    MAX_DESCRIPTION_LENGTH, description.length())
            );
        }
    }

    /**
     * 값 기반 생성
     *
     * @param officialWebsite 공식 웹사이트
     * @param logoUrl 로고 URL
     * @param description 설명
     * @return BrandMeta
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static BrandMeta of(String officialWebsite, String logoUrl, String description) {
        return new BrandMeta(officialWebsite, logoUrl, description);
    }

    /**
     * 빈 메타 정보 생성
     *
     * @return 모든 필드가 null인 BrandMeta
     */
    public static BrandMeta empty() {
        return new BrandMeta(null, null, null);
    }

    /**
     * 메타 정보 존재 여부 확인
     *
     * @return 모든 필드가 비어있으면 false
     */
    public boolean hasMetaInfo() {
        return (officialWebsite != null && !officialWebsite.isEmpty()) ||
               (logoUrl != null && !logoUrl.isEmpty()) ||
               (description != null && !description.isEmpty());
    }
}
