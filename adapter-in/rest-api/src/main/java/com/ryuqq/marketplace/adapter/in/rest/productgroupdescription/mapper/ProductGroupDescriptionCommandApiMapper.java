package com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.mapper;

import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.dto.command.UpdateProductGroupDescriptionApiRequest;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import org.springframework.stereotype.Component;

/**
 * ProductGroupDescriptionCommandApiMapper - 상품 그룹 상세 설명 Command API 변환 매퍼.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ProductGroupDescriptionCommandApiMapper {

    /**
     * UpdateProductGroupDescriptionApiRequest -> UpdateProductGroupDescriptionCommand 변환.
     *
     * @param productGroupId 상품 그룹 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateProductGroupDescriptionCommand toCommand(
            Long productGroupId, UpdateProductGroupDescriptionApiRequest request) {
        return new UpdateProductGroupDescriptionCommand(productGroupId, request.content());
    }
}
