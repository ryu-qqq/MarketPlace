package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 변경 상품주문 상태.
 *
 * <p>last-changed-statuses API 응답의 개별 항목.
 *
 * @param orderId 주문번호
 * @param productOrderId 상품주문번호
 * @param lastChangedType 최종 변경 구분 (PAYED, DISPATCHED 등)
 * @param paymentDate 결제 일시
 * @param lastChangedDate 최종 변경 일시
 * @param productOrderStatus 상품주문 상태 (PAYED, DELIVERING 등)
 * @param claimType 클레임 구분 (CANCEL, RETURN, EXCHANGE 등)
 * @param claimStatus 클레임 상태
 * @param receiverAddressChanged 배송지 정보 변경 여부
 * @param giftReceivingStatus 선물 수락 상태 구분
 */
public record NaverLastChangedStatus(
        String orderId,
        String productOrderId,
        String lastChangedType,
        String paymentDate,
        String lastChangedDate,
        String productOrderStatus,
        String claimType,
        String claimStatus,
        Boolean receiverAddressChanged,
        String giftReceivingStatus) {}
