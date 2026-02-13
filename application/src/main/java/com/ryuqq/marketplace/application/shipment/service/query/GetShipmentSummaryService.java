package com.ryuqq.marketplace.application.shipment.service.query;

import com.ryuqq.marketplace.application.shipment.assembler.ShipmentAssembler;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentSummaryResult;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.application.shipment.port.in.query.GetShipmentSummaryUseCase;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.Map;
import org.springframework.stereotype.Service;

/** 배송 상태별 요약 조회 Service. */
@Service
public class GetShipmentSummaryService implements GetShipmentSummaryUseCase {

    private final ShipmentReadManager readManager;
    private final ShipmentAssembler assembler;

    public GetShipmentSummaryService(ShipmentReadManager readManager, ShipmentAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public ShipmentSummaryResult execute() {
        Map<ShipmentStatus, Long> statusCounts = readManager.countByStatus();
        return assembler.toSummaryResult(statusCounts);
    }
}
