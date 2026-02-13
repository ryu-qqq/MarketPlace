package com.ryuqq.marketplace.application.shipment.assembler;

import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult.ShipmentMethodResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentPageResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentSummaryResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethod;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Shipment Assembler.
 *
 * <p>Domain → Result 변환 및 PageResult 생성을 담당합니다.
 */
@Component
public class ShipmentAssembler {

    /**
     * Domain → ShipmentListResult 변환.
     *
     * @param shipment Shipment 도메인 객체
     * @return ShipmentListResult
     */
    public ShipmentListResult toListResult(Shipment shipment) {
        ShipmentMethod method = shipment.shipmentMethod();
        String courierName = method != null ? method.courierName() : null;

        return new ShipmentListResult(
                shipment.idValue(),
                shipment.shipmentNumberValue(),
                shipment.orderId(),
                shipment.orderNumber(),
                shipment.status().name(),
                shipment.trackingNumber(),
                courierName,
                shipment.shippedAt(),
                shipment.deliveredAt(),
                shipment.createdAt());
    }

    /**
     * Domain List → ShipmentListResult List 변환.
     *
     * @param shipments Shipment 도메인 객체 목록
     * @return ShipmentListResult 목록
     */
    public List<ShipmentListResult> toListResults(List<Shipment> shipments) {
        return shipments.stream().map(this::toListResult).toList();
    }

    /**
     * Domain → ShipmentDetailResult 변환.
     *
     * @param shipment Shipment 도메인 객체
     * @return ShipmentDetailResult
     */
    public ShipmentDetailResult toDetailResult(Shipment shipment) {
        ShipmentMethodResult methodResult = toMethodResult(shipment.shipmentMethod());

        return new ShipmentDetailResult(
                shipment.idValue(),
                shipment.shipmentNumberValue(),
                shipment.orderId(),
                shipment.orderNumber(),
                shipment.status().name(),
                methodResult,
                shipment.trackingNumber(),
                shipment.orderConfirmedAt(),
                shipment.shippedAt(),
                shipment.deliveredAt(),
                shipment.createdAt(),
                shipment.updatedAt());
    }

    /**
     * 페이지 결과 생성.
     *
     * @param shipments Shipment 도메인 객체 목록
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param totalCount 전체 개수
     * @return ShipmentPageResult
     */
    public ShipmentPageResult toPageResult(
            List<Shipment> shipments, int page, int size, long totalCount) {
        List<ShipmentListResult> results = toListResults(shipments);
        PageMeta pageMeta = PageMeta.of(page, size, totalCount);
        return new ShipmentPageResult(results, pageMeta);
    }

    /**
     * 상태별 카운트 → ShipmentSummaryResult 변환.
     *
     * @param statusCounts 상태별 카운트 맵
     * @return ShipmentSummaryResult
     */
    public ShipmentSummaryResult toSummaryResult(Map<ShipmentStatus, Long> statusCounts) {
        return new ShipmentSummaryResult(
                statusCounts.getOrDefault(ShipmentStatus.READY, 0L).intValue(),
                statusCounts.getOrDefault(ShipmentStatus.PREPARING, 0L).intValue(),
                statusCounts.getOrDefault(ShipmentStatus.SHIPPED, 0L).intValue(),
                statusCounts.getOrDefault(ShipmentStatus.IN_TRANSIT, 0L).intValue(),
                statusCounts.getOrDefault(ShipmentStatus.DELIVERED, 0L).intValue(),
                statusCounts.getOrDefault(ShipmentStatus.FAILED, 0L).intValue(),
                statusCounts.getOrDefault(ShipmentStatus.CANCELLED, 0L).intValue());
    }

    private ShipmentMethodResult toMethodResult(ShipmentMethod method) {
        if (method == null) {
            return null;
        }
        return new ShipmentMethodResult(
                method.type().name(), method.courierCode(), method.courierName());
    }
}
