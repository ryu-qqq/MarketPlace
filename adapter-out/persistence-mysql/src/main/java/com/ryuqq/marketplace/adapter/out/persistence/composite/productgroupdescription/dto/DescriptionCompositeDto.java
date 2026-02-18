package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto;

import java.util.List;

/**
 * DescriptionCompositeDto - 상세설명 + 이미지 + 아웃박스 Composite DTO.
 *
 * <p>productGroupId와 함께 상세설명 정보, 이미지 목록, 아웃박스 목록을 포함.
 */
public record DescriptionCompositeDto(
        Long productGroupId,
        DescriptionProjectionDto description,
        List<DescriptionImageProjectionDto> images,
        List<DescriptionImageOutboxProjectionDto> outboxes) {}
