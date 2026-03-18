package com.ryuqq.marketplace.application.refund.validator;

import com.ryuqq.marketplace.application.refund.manager.RefundReadManager;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.exception.RefundOwnershipMismatchException;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 환불 배치 요청 검증기.
 *
 * <p>refundClaimIds를 IN절로 일괄 조회하고 소유권(sellerId) 검증을 수행합니다.
 */
@Component
public class RefundBatchValidator {

    private final RefundReadManager refundReadManager;

    public RefundBatchValidator(RefundReadManager refundReadManager) {
        this.refundReadManager = refundReadManager;
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
}
