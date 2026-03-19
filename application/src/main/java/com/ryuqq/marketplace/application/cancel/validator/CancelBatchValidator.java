package com.ryuqq.marketplace.application.cancel.validator;

import com.ryuqq.marketplace.application.cancel.manager.CancelReadManager;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.exception.CancelOwnershipMismatchException;
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

    public CancelBatchValidator(CancelReadManager cancelReadManager) {
        this.cancelReadManager = cancelReadManager;
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
}
