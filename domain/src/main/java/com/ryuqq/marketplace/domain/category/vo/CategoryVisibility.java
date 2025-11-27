package com.ryuqq.marketplace.domain.category.vo;

/**
 * Category Visibility Value Object
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>isVisible - 카테고리 표시 여부</li>
 *   <li>isListable - 상품 등록 가능 여부</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record CategoryVisibility(boolean isVisible, boolean isListable) {

    /**
     * 표시 가능한 카테고리 생성
     *
     * @return CategoryVisibility (visible=true, listable=true)
     */
    public static CategoryVisibility visible() {
        return new CategoryVisibility(true, true);
    }

    /**
     * 숨김 카테고리 생성
     *
     * @return CategoryVisibility (visible=false, listable=false)
     */
    public static CategoryVisibility hidden() {
        return new CategoryVisibility(false, false);
    }

    /**
     * 값 기반 생성
     *
     * @param isVisible 표시 여부
     * @param isListable 상품 등록 가능 여부
     * @return CategoryVisibility
     */
    public static CategoryVisibility of(boolean isVisible, boolean isListable) {
        return new CategoryVisibility(isVisible, isListable);
    }

    /**
     * 카테고리 표시 가능 여부
     *
     * @return 표시 가능하면 true
     */
    public boolean canDisplay() {
        return isVisible;
    }

    /**
     * 상품 등록 가능 여부
     *
     * @return 등록 가능하면 true
     */
    public boolean canListProducts() {
        return isListable;
    }
}
