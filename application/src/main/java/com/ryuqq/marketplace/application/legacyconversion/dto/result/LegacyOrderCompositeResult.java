package com.ryuqq.marketplace.application.legacyconversion.dto.result;

import java.time.Instant;
import java.util.List;

/**
 * 레거시 주문 복합 조회 결과 DTO.
 *
 * <p>orders + order_snapshot_product_group + external_order + interlocking_order +
 * payment_snapshot_shipping_address + shipment + order_snapshot_option_detail + orders_history 조합
 * 결과.
 *
 * @param legacyOrderId 레거시 주문 ID
 * @param legacyPaymentId 레거시 결제 ID
 * @param legacyProductId 레거시 상품 ID
 * @param legacySellerId 레거시 셀러 ID
 * @param legacyUserId 레거시 유저 ID
 * @param orderAmount 주문 금액
 * @param orderStatus 레거시 주문 상태 (ORDER_STATUS 원본값)
 * @param quantity 주문 수량
 * @param orderDate 주문 일시 (orders.INSERT_DATE)
 * @param productGroupId 상품그룹 ID (order_snapshot_product_group)
 * @param productGroupName 상품그룹명
 * @param brandId 브랜드 ID
 * @param brandName 브랜드명
 * @param categoryId 카테고리 ID
 * @param regularPrice 정가
 * @param currentPrice 판매가
 * @param commissionRate 수수료율
 * @param shareRatio 셀러 정산 비율
 * @param optionValues 선택 옵션값 목록 (order_snapshot_option_detail.OPTION_VALUE)
 * @param mainImageUrl 대표 이미지 URL (order_snapshot_product_group_image 첫번째)
 * @param externalOrderPkId 외부 주문 PK ID (external_order, nullable)
 * @param externalSiteId 외부 사이트 ID (external_order, nullable)
 * @param interlockingSiteName 연동 사이트명 (interlocking_order, nullable)
 * @param receiverName 수령인 이름 (payment_snapshot_shipping_address)
 * @param receiverPhone 수령인 연락처
 * @param receiverZipCode 우편번호
 * @param receiverAddress 주소
 * @param receiverAddressDetail 상세주소
 * @param deliveryRequest 배송 요청사항
 * @param invoiceNo 운송장번호 (shipment 테이블, nullable)
 * @param companyCode 택배사 코드 (shipment 테이블, nullable)
 * @param shipmentCreatedAt 배송 레코드 생성 시각 (shipment.INSERT_DATE, nullable)
 * @param histories 주문 상태 변경 이력 (orders_history 테이블)
 */
public record LegacyOrderCompositeResult(
        long legacyOrderId,
        long legacyPaymentId,
        long legacyProductId,
        long legacySellerId,
        long legacyUserId,
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
        String externalOrderPkId,
        Long externalSiteId,
        String interlockingSiteName,
        String receiverName,
        String receiverPhone,
        String receiverZipCode,
        String receiverAddress,
        String receiverAddressDetail,
        String deliveryRequest,
        String invoiceNo,
        String companyCode,
        Instant shipmentCreatedAt,
        List<LegacyOrderHistoryEntry> histories) {}
