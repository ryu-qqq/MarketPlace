package com.ryuqq.marketplace.adapter.out.client.naver.dto;

/**
 * Naver Commerce 카테고리 응답.
 *
 * @param wholeCategoryName 전체 카테고리 경로명
 * @param id 카테고리 ID
 * @param name 카테고리 이름
 * @param last 최하위 카테고리 여부
 */
public record NaverCommerceCategory(
        String wholeCategoryName, String id, String name, boolean last) {}
