package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.dto;

import java.time.LocalDateTime;

/**
 * 레거시 주문 복합 조회 flat projection DTO.
 *
 * <p>orders + order_snapshot_product_group + external_order + interlocking_order +
 * payment_snapshot_shipping_address 조인 결과. option_values는 별도 쿼리로 조합됩니다.
 *
 * <p>QueryDSL Projections.constructor 사용으로 NumberPath&lt;Long&gt; 매핑을 위해
 * 숫자 필드는 래퍼 타입(Long, Integer)을 사용합니다. Mapper에서 null-safe 변환 처리합니다.
 *
 * @param legacyOrderId 레거시 주문 ID
 * @param legacyPaymentId 레거시 결제 ID
 * @param legacyProductId 레거시 상품 ID
 * @param legacySellerId 레거시 셀러 ID
 * @param legacyUserId 레거시 유저 ID
 * @param orderAmount 주문 금액
 * @param orderStatus 레거시 주문 상태
 * @param quantity 주문 수량
 * @param orderDate 주문 일시 (orders.INSERT_DATE)
 * @param productGroupId 상품그룹 ID
 * @param productGroupName 상품그룹명
 * @param brandId 브랜드 ID
 * @param categoryId 카테고리 ID
 * @param regularPrice 정가
 * @param currentPrice 판매가
 * @param commissionRate 수수료율
 * @param shareRatio 셀러 정산 비율
 * @param mainImageUrl 대표 이미지 URL (nullable, LEFT JOIN 결과)
 * @param externalOrderPkId 외부 주문 PK ID (nullable)
 * @param externalSiteId 외부 사이트 ID (nullable)
 * @param interlockingSiteName 연동 사이트명 (nullable)
 * @param receiverName 수령인 이름 (nullable)
 * @param receiverPhone 수령인 연락처 (nullable)
 * @param receiverZipCode 우편번호 (nullable)
 * @param receiverAddress 주소 (nullable)
 * @param receiverAddressDetail 상세주소 (nullable)
 * @param deliveryRequest 배송 요청사항 (nullable)
 */
public record LegacyOrderCompositeQueryDto(
        Long legacyOrderId,
        Long legacyPaymentId,
        Long legacyProductId,
        Long legacySellerId,
        Long legacyUserId,
        Long orderAmount,
        String orderStatus,
        Integer quantity,
        LocalDateTime orderDate,
        Long productGroupId,
        String productGroupName,
        Long brandId,
        Long categoryId,
        Long regularPrice,
        Long currentPrice,
        Long commissionRate,
        Long shareRatio,
        String mainImageUrl,
        String externalOrderPkId,
        Long externalSiteId,
        String interlockingSiteName,
        String receiverName,
        String receiverPhone,
        String receiverZipCode,
        String receiverAddress,
        String receiverAddressDetail,
        String deliveryRequest) {}
