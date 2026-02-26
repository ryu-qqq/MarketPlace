package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.dto;

/**
 * ImageOutboxProjectionDto - 이미지 업로드 아웃박스 프로젝션 DTO.
 *
 * <p>image_upload_outboxes 테이블 프로젝션 결과.
 */
public record ImageOutboxProjectionDto(
        Long sourceId, String status, int retryCount, String errorMessage) {}
