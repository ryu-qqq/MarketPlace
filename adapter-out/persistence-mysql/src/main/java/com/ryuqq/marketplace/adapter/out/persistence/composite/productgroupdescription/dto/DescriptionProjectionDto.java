package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto;

/**
 * DescriptionProjectionDto - 상세설명 프로젝션 DTO.
 *
 * <p>product_group_descriptions 테이블 프로젝션 결과.
 */
public record DescriptionProjectionDto(Long descriptionId, String publishStatus, String cdnPath) {}
