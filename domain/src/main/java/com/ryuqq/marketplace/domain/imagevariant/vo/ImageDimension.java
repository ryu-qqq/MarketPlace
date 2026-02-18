package com.ryuqq.marketplace.domain.imagevariant.vo;

/**
 * 이미지 크기(너비/높이) Value Object.
 *
 * <p>너비와 높이를 함께 관리합니다. ORIGINAL_WEBP 변환의 경우 null이 허용됩니다.
 *
 * @param width 너비 (px)
 * @param height 높이 (px)
 */
public record ImageDimension(Integer width, Integer height) {

    public static ImageDimension of(Integer width, Integer height) {
        return new ImageDimension(width, height);
    }

    public boolean hasValues() {
        return width != null && height != null;
    }
}
