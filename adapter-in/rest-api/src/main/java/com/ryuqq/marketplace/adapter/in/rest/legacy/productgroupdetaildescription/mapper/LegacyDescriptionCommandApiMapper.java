package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.dto.request.LegacyUpdateProductDescriptionRequest;
import com.ryuqq.marketplace.application.legacy.description.dto.command.LegacyUpdateDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import org.springframework.stereotype.Component;

/** 레거시 상품 상세설명 Request → Command 변환 매퍼. */
@Component
public class LegacyDescriptionCommandApiMapper {

    /** productGroupId + Request → LegacyUpdateDescriptionCommand. */
    public LegacyUpdateDescriptionCommand toLegacyUpdateDescriptionCommand(
            long productGroupId, LegacyUpdateProductDescriptionRequest request) {
        return new LegacyUpdateDescriptionCommand(productGroupId, request.detailDescription());
    }

    /** LegacyUpdateProductDescriptionRequest → UpdateProductGroupDescriptionCommand. */
    public UpdateProductGroupDescriptionCommand toDescriptionCommand(
            long productGroupId, LegacyUpdateProductDescriptionRequest request) {
        return new UpdateProductGroupDescriptionCommand(
                productGroupId, request.detailDescription());
    }
}
