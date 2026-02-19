package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.dto;

import java.util.List;

/**
 * ProductGroupImageCompositeDto - 이미지 + 아웃박스 Composite DTO.
 *
 * <p>productGroupId와 함께 이미지 목록, 아웃박스 목록을 포함.
 */
public record ProductGroupImageCompositeDto(
        Long productGroupId,
        List<ImageProjectionDto> images,
        List<ImageOutboxProjectionDto> outboxes) {}
