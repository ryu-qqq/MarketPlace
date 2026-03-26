package com.ryuqq.marketplace.application.refund.validator;

import com.ryuqq.marketplace.application.exchange.manager.ExchangeReadManager;
import com.ryuqq.marketplace.application.refund.manager.RefundReadManager;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.exception.RefundOwnershipMismatchException;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 환불 배치 요청 검증기.
 *
 * <p>소유권(sellerId) 검증 + 중복 클레임 방지를 수행합니다.
 */
@Component
public class RefundBatchValidator {

    private final RefundReadManager refundReadManager;
    private final ExchangeReadManager exchangeReadManager;

    public RefundBatchValidator(
            RefundReadManager refundReadManager, ExchangeReadManager exchangeReadManager) {
        this.refundReadManager = refundReadManager;
        this.exchangeReadManager = exchangeReadManager;
    }

    public List<RefundClaim> validateAndGet(List<String> refundClaimIds, Long sellerId) {
        List<RefundClaim> foundClaims = refundReadManager.findByIdIn(refundClaimIds, sellerId);

        if (foundClaims.size() != refundClaimIds.size()) {
            List<String> foundIds = foundClaims.stream().map(RefundClaim::idValue).toList();
            List<String> missingIds =
                    refundClaimIds.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new RefundOwnershipMismatchException(missingIds);
        }

        return foundClaims;
    }

    /** 해당 OrderItem에 진행 중인 Refund/Exchange 클레임이 있는지 확인. */
    public boolean hasActiveClaim(String orderItemId) {
        boolean hasActiveRefund =
                refundReadManager
                        .findByOrderItemId(orderItemId)
                        .filter(r -> r.status().isActive())
                        .isPresent();
        if (hasActiveRefund) {
            return true;
        }
        return exchangeReadManager
                .findByOrderItemId(OrderItemId.of(orderItemId))
                .filter(e -> e.status().isActive())
                .isPresent();
    }
}
