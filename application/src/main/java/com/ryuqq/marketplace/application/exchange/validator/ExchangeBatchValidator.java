package com.ryuqq.marketplace.application.exchange.validator;

import com.ryuqq.marketplace.application.exchange.manager.ExchangeReadManager;
import com.ryuqq.marketplace.application.refund.manager.RefundReadManager;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.exception.ExchangeOwnershipMismatchException;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 교환 배치 요청 검증기.
 *
 * <p>소유권(sellerId) 검증 + 중복 클레임 방지를 수행합니다.
 */
@Component
public class ExchangeBatchValidator {

    private final ExchangeReadManager exchangeReadManager;
    private final RefundReadManager refundReadManager;

    public ExchangeBatchValidator(
            ExchangeReadManager exchangeReadManager, RefundReadManager refundReadManager) {
        this.exchangeReadManager = exchangeReadManager;
        this.refundReadManager = refundReadManager;
    }

    public List<ExchangeClaim> validateAndGet(List<String> exchangeClaimIds, Long sellerId) {
        List<ExchangeClaim> foundClaims =
                exchangeReadManager.findByIdIn(exchangeClaimIds, sellerId);

        if (foundClaims.size() != exchangeClaimIds.size()) {
            List<String> foundIds = foundClaims.stream().map(ExchangeClaim::idValue).toList();
            List<String> missingIds =
                    exchangeClaimIds.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new ExchangeOwnershipMismatchException(missingIds);
        }

        return foundClaims;
    }

    /** 해당 OrderItem에 진행 중인 Refund/Exchange 클레임이 있는지 확인. */
    public boolean hasActiveClaim(Long orderItemId) {
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
