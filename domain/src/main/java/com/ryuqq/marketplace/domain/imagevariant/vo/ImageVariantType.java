package com.ryuqq.marketplace.domain.imagevariant.vo;

/**
 * 이미지 Variant 타입.
 *
 * <p>멀티 사이즈 WEBP 변환 유형을 정의합니다.
 */
public enum ImageVariantType {
    SMALL_WEBP(300, 300, "webp", "RESIZE", 85, "소형 WEBP"),
    MEDIUM_WEBP(600, 600, "webp", "RESIZE", 85, "중형 WEBP"),
    LARGE_WEBP(1200, 1200, "webp", "RESIZE", 85, "대형 WEBP"),
    ORIGINAL_WEBP(null, null, "webp", "CONVERT", 90, "원본 WEBP 변환");

    private final Integer width;
    private final Integer height;
    private final String targetFormat;
    private final String transformType;
    private final int quality;
    private final String description;

    ImageVariantType(
            Integer width,
            Integer height,
            String targetFormat,
            String transformType,
            int quality,
            String description) {
        this.width = width;
        this.height = height;
        this.targetFormat = targetFormat;
        this.transformType = transformType;
        this.quality = quality;
        this.description = description;
    }

    public Integer width() {
        return width;
    }

    public Integer height() {
        return height;
    }

    public String targetFormat() {
        return targetFormat;
    }

    public String transformType() {
        return transformType;
    }

    public int quality() {
        return quality;
    }

    public String description() {
        return description;
    }

    /** 리사이즈 변환 유형인지 판별. */
    public boolean isResize() {
        return "RESIZE".equals(transformType);
    }

    /** 원본 포맷 변환 유형인지 판별. */
    public boolean isOriginalConversion() {
        return "CONVERT".equals(transformType);
    }

    /** 변환에 크기 지정이 필요한지 판별. */
    public boolean requiresDimensions() {
        return width != null && height != null;
    }

    /** 파일 접미사를 생성합니다. (예: "300x300.webp", "original.webp") */
    public String toFileSuffix() {
        String sizePrefix = requiresDimensions() ? width + "x" + height : "original";
        return sizePrefix + "." + targetFormat;
    }
}
