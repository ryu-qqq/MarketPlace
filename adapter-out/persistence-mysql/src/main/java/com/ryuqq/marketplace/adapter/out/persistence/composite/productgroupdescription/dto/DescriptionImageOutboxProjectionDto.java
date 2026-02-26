package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto;

/**
 * DescriptionImageOutboxProjectionDto - 상세설명 이미지 업로드 아웃박스 프로젝션 DTO.
 *
 * <p>image_upload_outboxes 테이블 프로젝션 결과 (sourceType = DESCRIPTION_IMAGE).
 */
public record DescriptionImageOutboxProjectionDto(
        Long sourceId, String status, int retryCount, String errorMessage) {}
