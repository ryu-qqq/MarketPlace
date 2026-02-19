package com.ryuqq.marketplace.application.imagetransform.dto.command;

import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.util.List;

/**
 * 수동 이미지 변환 요청 Command.
 *
 * @param productGroupId 상품 그룹 ID
 * @param variantTypes 변환 대상 Variant 타입 목록 (null이면 전체 타입 대상)
 */
public record RequestImageTransformCommand(
        Long productGroupId, List<ImageVariantType> variantTypes) {

    public static RequestImageTransformCommand of(
            Long productGroupId, List<ImageVariantType> variantTypes) {
        return new RequestImageTransformCommand(productGroupId, variantTypes);
    }

    public static RequestImageTransformCommand allVariants(Long productGroupId) {
        return new RequestImageTransformCommand(productGroupId, null);
    }

    /**
     * 변환 대상 Variant 타입 목록을 반환합니다.
     *
     * <p>null이면 전체 타입을 반환합니다.
     */
    public List<ImageVariantType> resolvedVariantTypes() {
        if (variantTypes == null || variantTypes.isEmpty()) {
            return List.of(ImageVariantType.values());
        }
        return variantTypes;
    }
}
