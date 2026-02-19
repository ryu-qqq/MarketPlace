package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.dto;

/**
 * ImageProjectionDto - 상품 그룹 이미지 프로젝션 DTO.
 *
 * <p>product_group_images 테이블 프로젝션 결과.
 */
public record ImageProjectionDto(
        Long imageId, String imageType, String originUrl, String uploadedUrl) {}
