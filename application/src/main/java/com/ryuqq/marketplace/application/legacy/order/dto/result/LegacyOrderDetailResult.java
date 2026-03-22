package com.ryuqq.marketplace.application.legacy.order.dto.result;

import java.time.Instant;
import java.util.List;

/**
 * 레거시 주문 단건 조회 결과.
 *
 * <p>기존 LegacyOrderCompositeResult 구조를 재사용하되, 주문 API 전용 Result로 분리.
 *
 * @param orderId 주문 ID
 * @param paymentId 결제 ID
 * @param productId 상품 ID
 * @param sellerId 셀러 ID
 * @param userId 유저 ID
 * @param orderAmount 주문 금액
 * @param orderStatus 주문 상태
 * @param quantity 수량
 * @param orderDate 주문 일시
 * @param productGroupId 상품그룹 ID
 * @param productGroupName 상품그룹명
 * @param brandId 브랜드 ID
 * @param brandName 브랜드명
 * @param categoryId 카테고리 ID
 * @param regularPrice 정가
 * @param currentPrice 판매가
 * @param commissionRate 수수료율
 * @param shareRatio 정산 비율
 * @param optionValues 옵션값 목록
 * @param mainImageUrl 대표 이미지 URL
 * @param receiverName 수령인 이름
 * @param receiverPhone 수령인 연락처
 * @param receiverZipCode 우편번호
 * @param receiverAddress 주소
 * @param receiverAddressDetail 상세주소
 * @param deliveryRequest 배송 요청사항
 */
public record LegacyOrderDetailResult(
        long orderId,
        long paymentId,
        long productId,
        long sellerId,
        long userId,
        long orderAmount,
        String orderStatus,
        int quantity,
        Instant orderDate,
        long productGroupId,
        String productGroupName,
        long brandId,
        String brandName,
        long categoryId,
        long regularPrice,
        long currentPrice,
        long commissionRate,
        long shareRatio,
        List<String> optionValues,
        String mainImageUrl,
        String receiverName,
        String receiverPhone,
        String receiverZipCode,
        String receiverAddress,
        String receiverAddressDetail,
        String deliveryRequest) {}
