package com.ryuqq.marketplace.domain.category.vo;

/**
 * Category Meta Value Object
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>displayName - 표시용 이름 (최대 255자)</li>
 *   <li>seoSlug - SEO용 슬러그 (최대 255자)</li>
 *   <li>iconUrl - 아이콘 URL (최대 500자)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record CategoryMeta(String displayName, String seoSlug, String iconUrl) {

    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_SLUG_LENGTH = 255;
    private static final int MAX_URL_LENGTH = 500;

    /**
     * Compact Constructor (검증 로직)
     */
    public CategoryMeta {
        if (displayName != null && displayName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Display name은 " + MAX_NAME_LENGTH + "자를 초과할 수 없습니다: " + displayName.length());
        }
        if (seoSlug != null && seoSlug.length() > MAX_SLUG_LENGTH) {
            throw new IllegalArgumentException("SEO slug는 " + MAX_SLUG_LENGTH + "자를 초과할 수 없습니다: " + seoSlug.length());
        }
        if (iconUrl != null && iconUrl.length() > MAX_URL_LENGTH) {
            throw new IllegalArgumentException("Icon URL은 " + MAX_URL_LENGTH + "자를 초과할 수 없습니다: " + iconUrl.length());
        }
    }

    /**
     * 값 기반 생성
     *
     * @param displayName 표시용 이름
     * @param seoSlug SEO 슬러그
     * @param iconUrl 아이콘 URL
     * @return CategoryMeta
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static CategoryMeta of(String displayName, String seoSlug, String iconUrl) {
        return new CategoryMeta(displayName, seoSlug, iconUrl);
    }

    /**
     * 빈 메타데이터 생성
     *
     * @return CategoryMeta (모든 필드 null)
     */
    public static CategoryMeta empty() {
        return new CategoryMeta(null, null, null);
    }
}
