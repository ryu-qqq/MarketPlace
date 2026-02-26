package com.ryuqq.marketplace.adapter.in.rest.imagevariant.mapper;

import com.ryuqq.marketplace.adapter.in.rest.imagevariant.dto.command.RequestImageTransformApiRequest;
import com.ryuqq.marketplace.application.imagetransform.dto.command.RequestImageTransformCommand;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ImageVariantCommandApiMapper - 이미지 Variant Command API 변환 매퍼.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ImageVariantCommandApiMapper {

    /**
     * RequestImageTransformApiRequest -> RequestImageTransformCommand 변환.
     *
     * @param productGroupId 상품 그룹 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public RequestImageTransformCommand toCommand(
            Long productGroupId, RequestImageTransformApiRequest request) {
        if (request == null || request.variantTypes() == null || request.variantTypes().isEmpty()) {
            return RequestImageTransformCommand.allVariants(productGroupId);
        }

        List<ImageVariantType> variantTypes =
                request.variantTypes().stream().map(ImageVariantType::valueOf).toList();

        return RequestImageTransformCommand.of(productGroupId, variantTypes);
    }
}
