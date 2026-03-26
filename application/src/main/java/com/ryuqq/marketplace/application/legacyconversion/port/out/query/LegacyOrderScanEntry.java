package com.ryuqq.marketplace.application.legacyconversion.port.out.query;

/**
 * 레거시 주문 스캔 엔트리.
 *
 * <p>커서 기반 스캔 시 orderId와 paymentId를 함께 전달하기 위한 DTO.
 *
 * @param orderId 레거시 주문 ID
 * @param paymentId 레거시 결제 ID
 */
public record LegacyOrderScanEntry(long orderId, long paymentId) {}
