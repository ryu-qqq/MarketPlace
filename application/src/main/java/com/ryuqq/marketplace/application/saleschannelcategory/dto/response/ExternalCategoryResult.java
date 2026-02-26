package com.ryuqq.marketplace.application.saleschannelcategory.dto.response;

/**
 * 외부 판매채널 API에서 조회된 카테고리 결과.
 *
 * @param externalCategoryCode 외부 카테고리 코드 (판매채널 카테고리 ID)
 * @param externalCategoryName 외부 카테고리 이름
 * @param displayPath 전체 카테고리 경로명
 * @param leaf 최하위 카테고리 여부
 */
public record ExternalCategoryResult(
        String externalCategoryCode,
        String externalCategoryName,
        String displayPath,
        boolean leaf) {}
