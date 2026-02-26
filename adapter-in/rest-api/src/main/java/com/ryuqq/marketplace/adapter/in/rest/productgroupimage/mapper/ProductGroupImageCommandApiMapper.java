package com.ryuqq.marketplace.adapter.in.rest.productgroupimage.mapper;

import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.dto.command.UpdateProductGroupImagesApiRequest;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import org.springframework.stereotype.Component;

/**
 * ProductGroupImageCommandApiMapper - 상품 그룹 이미지 Command API 변환 매퍼.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ProductGroupImageCommandApiMapper {

    /**
     * UpdateProductGroupImagesApiRequest -> UpdateProductGroupImagesCommand 변환.
     *
     * @param productGroupId 상품 그룹 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateProductGroupImagesCommand toCommand(
            Long productGroupId, UpdateProductGroupImagesApiRequest request) {
        return new UpdateProductGroupImagesCommand(
                productGroupId,
                request.images().stream()
                        .map(
                                img ->
                                        new UpdateProductGroupImagesCommand.ImageCommand(
                                                img.imageType(), img.originUrl(), img.sortOrder()))
                        .toList());
    }
}
