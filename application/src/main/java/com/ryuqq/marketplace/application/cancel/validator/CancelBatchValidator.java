package com.ryuqq.marketplace.application.cancel.validator;

import com.ryuqq.marketplace.application.cancel.manager.CancelReadManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.exception.CancelOwnershipMismatchException;
import com.ryuqq.marketplace.domain.shipment.exception.ShipmentNotShippedException;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 취소 배치 요청 검증기.
 *
 * <p>cancelIds를 IN절로 일괄 조회하고 소유권(sellerId) 검증을 수행합니다. sellerId가 null이면 슈퍼어드민으로 간주하여 전체 접근을 허용합니다.
 * 요청한 ID와 조회된 ID가 불일치하면 소유권 위반 또는 존재하지 않는 건으로 판단하여 예외를 던집니다.
 */
@Component
public class CancelBatchValidator {

    private final CancelReadManager cancelReadManager;
    private final ShipmentReadManager shipmentReadManager;

    public CancelBatchValidator(
            CancelReadManager cancelReadManager, ShipmentReadManager shipmentReadManager) {
        this.cancelReadManager = cancelReadManager;
        this.shipmentReadManager = shipmentReadManager;
    }

    /**
     * cancelIds를 일괄 조회하고 소유권을 검증합니다.
     *
     * @param cancelIds 요청한 취소 ID 목록
     * @param sellerId 요청 셀러 ID (null이면 슈퍼어드민)
     * @return 검증 통과한 Cancel 목록
     * @throws CancelOwnershipMismatchException 불일치 시
     */
    public List<Cancel> validateAndGet(List<String> cancelIds, Long sellerId) {
        List<Cancel> foundCancels = cancelReadManager.findByIdIn(cancelIds, sellerId);

        if (foundCancels.size() != cancelIds.size()) {
            List<String> foundIds = foundCancels.stream().map(Cancel::idValue).toList();
            List<String> missingIds =
                    cancelIds.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new CancelOwnershipMismatchException(missingIds);
        }

        return foundCancels;
    }

    /**
     * 취소 거부(reject) 전용 검증. 소유권 검증 + 운송장 등록 여부 확인.
     *
     * <p>네이버 정책: 운송장이 등록된(SHIPPED 이상) 상태여야 취소 거부가 가능합니다. 운송장 미등록 시 먼저 운송장을 등록한 후
     * 거부해야 합니다.
     *
     * @param cancelIds 요청한 취소 ID 목록
     * @param sellerId 요청 셀러 ID (null이면 슈퍼어드민)
     * @return 검증 통과한 Cancel 목록
     */
    public List<Cancel> validateForReject(List<String> cancelIds, Long sellerId) {
        List<Cancel> cancels = validateAndGet(cancelIds, sellerId);

        for (Cancel cancel : cancels) {
            boolean hasShipped =
                    shipmentReadManager
                            .findByOrderItemId(cancel.orderItemId())
                            .filter(s -> s.status() == ShipmentStatus.SHIPPED
                                    || s.status() == ShipmentStatus.IN_TRANSIT
                                    || s.status() == ShipmentStatus.DELIVERED)
                            .isPresent();

            if (!hasShipped) {
                throw new ShipmentNotShippedException(cancel.idValue());
            }
        }

        return cancels;
    }
}
