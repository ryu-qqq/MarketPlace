package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 배송지 주소 정보.
 *
 * <p>productOrder.shippingAddress 필드에 매핑.
 *
 * @param name 수령인명
 * @param tel1 수령인 연락처1
 * @param tel2 수령인 연락처2
 * @param zipCode 우편번호
 * @param baseAddress 기본주소
 * @param detailedAddress 상세주소
 * @param addressType 주소 유형
 * @param isRoadNameAddress 도로명 주소 여부
 */
public record NaverShippingAddress(
        String name,
        String tel1,
        String tel2,
        String zipCode,
        String baseAddress,
        String detailedAddress,
        String addressType,
        Boolean isRoadNameAddress) {}
