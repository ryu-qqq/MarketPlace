package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 배송/수령인 정보.
 *
 * <p>상품주문 상세의 delivery 필드에 매핑.
 *
 * @param recipientName 수령인명
 * @param recipientTelNo1 수령인 연락처
 * @param zipCode 우편번호
 * @param baseAddress 기본주소
 * @param detailedAddress 상세주소
 * @param deliveryMemo 배송 요청사항
 */
public record NaverProductOrderShipping(
        String recipientName,
        String recipientTelNo1,
        String zipCode,
        String baseAddress,
        String detailedAddress,
        String deliveryMemo) {}
