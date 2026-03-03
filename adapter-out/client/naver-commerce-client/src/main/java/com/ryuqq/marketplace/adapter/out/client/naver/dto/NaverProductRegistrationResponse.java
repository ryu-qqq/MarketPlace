package com.ryuqq.marketplace.adapter.out.client.naver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 네이버 커머스 상품 등록 응답 DTO.
 *
 * <p>POST /v2/products 응답에서 필요한 필드만 추출합니다.
 *
 * @param originProductNo 원상품 번호
 * @param smartstoreChannelProductNo 스마트스토어 채널상품 번호
 */
public record NaverProductRegistrationResponse(
        @JsonProperty("originProductNo") Long originProductNo,
        @JsonProperty("smartstoreChannelProductNo") Long smartstoreChannelProductNo) {}
