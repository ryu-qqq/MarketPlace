package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto;

/**
 * DescriptionImageProjectionDto - 상세설명 이미지 프로젝션 DTO.
 *
 * <p>description_images 테이블 프로젝션 결과.
 */
public record DescriptionImageProjectionDto(Long imageId, String originUrl, String uploadedUrl) {}
