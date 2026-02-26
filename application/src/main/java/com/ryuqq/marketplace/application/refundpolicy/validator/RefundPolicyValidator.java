package com.ryuqq.marketplace.application.refundpolicy.validator;

import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.exception.LastActiveRefundPolicyCannotBeDeactivatedException;
import com.ryuqq.marketplace.domain.refundpolicy.exception.RefundPolicyException;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * RefundPolicyValidator - 환불 정책 검증기
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 *
 * @author ryu-qqq
 */
@Component
public class RefundPolicyValidator {

    private final RefundPolicyReadManager refundPolicyReadManager;

    public RefundPolicyValidator(RefundPolicyReadManager refundPolicyReadManager) {
        this.refundPolicyReadManager = refundPolicyReadManager;
    }

    /**
     * 환불 정책 존재 여부 검증 후 Domain 객체 반환
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id 환불 정책 ID
     * @return RefundPolicy 도메인 객체
     * @throws RefundPolicyException 존재하지 않는 경우
     */
    public RefundPolicy findExistingOrThrow(RefundPolicyId id) {
        return refundPolicyReadManager.getById(id);
    }

    /**
     * 셀러 ID와 정책 ID로 환불 정책 조회 및 검증
     *
     * <p>셀러가 소유한 정책인지 확인합니다.
     *
     * @param sellerId 셀러 ID
     * @param policyId 정책 ID
     * @return RefundPolicy 도메인 객체
     * @throws RefundPolicyException 존재하지 않는 경우
     */
    public RefundPolicy findExistingBySellerOrThrow(SellerId sellerId, RefundPolicyId policyId) {
        return refundPolicyReadManager
                .findBySellerIdAndId(sellerId, policyId)
                .orElseThrow(RefundPolicyException::policyNotFound);
    }

    /**
     * ID 목록으로 환불 정책 조회 및 검증.
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체 목록을 반환합니다.
     *
     * <p>최적화: Map 생성 후 단일 순회로 검증 + 순서 보장 반환을 동시 수행.
     *
     * @param ids 환불 정책 ID 목록
     * @return 환불 정책 목록 (ID 순서 보장)
     * @throws RefundPolicyException 존재하지 않는 ID가 있는 경우
     */
    public List<RefundPolicy> findAllExistingOrThrow(List<RefundPolicyId> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        Map<RefundPolicyId, RefundPolicy> foundMap =
                refundPolicyReadManager.getByIds(ids).stream()
                        .collect(Collectors.toMap(RefundPolicy::id, rp -> rp));

        return ids.stream()
                .map(
                        id -> {
                            RefundPolicy rp = foundMap.get(id);
                            if (rp == null) {
                                throw RefundPolicyException.policyNotFound();
                            }
                            return rp;
                        })
                .toList();
    }

    /**
     * 마지막 활성 정책 비활성화 검증.
     *
     * <p><b>POL-DEACT-002</b>: 마지막 활성 정책은 비활성화할 수 없습니다.
     *
     * @param sellerId 셀러 ID
     * @param policiesToDeactivate 비활성화할 정책 목록
     * @throws LastActiveRefundPolicyCannotBeDeactivatedException 마지막 활성 정책 비활성화 시도 시
     */
    public void validateNotLastActivePolicy(
            SellerId sellerId, List<RefundPolicy> policiesToDeactivate) {
        if (policiesToDeactivate.isEmpty()) {
            return;
        }

        long activePoliciesInRequest =
                policiesToDeactivate.stream().filter(RefundPolicy::isActive).count();

        if (activePoliciesInRequest == 0) {
            return;
        }

        long totalActiveCount = refundPolicyReadManager.countActiveBySellerId(sellerId);

        if (totalActiveCount <= activePoliciesInRequest) {
            throw new LastActiveRefundPolicyCannotBeDeactivatedException();
        }
    }
}
