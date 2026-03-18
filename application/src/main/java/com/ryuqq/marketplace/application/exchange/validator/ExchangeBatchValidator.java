package com.ryuqq.marketplace.application.exchange.validator;

import com.ryuqq.marketplace.application.exchange.manager.ExchangeReadManager;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.exception.ExchangeOwnershipMismatchException;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 교환 배치 요청 검증기.
 *
 * <p>exchangeClaimIds를 IN절로 일괄 조회하고 소유권(sellerId) 검증을 수행합니다.
 */
@Component
public class ExchangeBatchValidator {

    private final ExchangeReadManager exchangeReadManager;

    public ExchangeBatchValidator(ExchangeReadManager exchangeReadManager) {
        this.exchangeReadManager = exchangeReadManager;
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
}
