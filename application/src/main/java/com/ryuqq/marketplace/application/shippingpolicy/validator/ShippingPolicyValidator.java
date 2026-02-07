package com.ryuqq.marketplace.application.shippingpolicy.validator;

import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.LastActiveShippingPolicyCannotBeDeactivatedException;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.ShippingPolicyException;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ShippingPolicyValidator - 배송 정책 검증기
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 *
 * @author ryu-qqq
 */
@Component
public class ShippingPolicyValidator {

    private final ShippingPolicyReadManager shippingPolicyReadManager;

    public ShippingPolicyValidator(ShippingPolicyReadManager shippingPolicyReadManager) {
        this.shippingPolicyReadManager = shippingPolicyReadManager;
    }

    /**
     * 배송 정책 존재 여부 검증 후 Domain 객체 반환
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id 배송 정책 ID
     * @return ShippingPolicy 도메인 객체
     * @throws ShippingPolicyException 존재하지 않는 경우
     */
    public ShippingPolicy findExistingOrThrow(ShippingPolicyId id) {
        return shippingPolicyReadManager.getById(id);
    }

    /**
     * 셀러 ID와 정책 ID로 배송 정책 조회 및 검증
     *
     * <p>셀러가 소유한 정책인지 확인합니다.
     *
     * @param sellerId 셀러 ID
     * @param policyId 정책 ID
     * @return ShippingPolicy 도메인 객체
     * @throws ShippingPolicyException 존재하지 않는 경우
     */
    public ShippingPolicy findExistingBySellerOrThrow(
            SellerId sellerId, ShippingPolicyId policyId) {
        return shippingPolicyReadManager
                .findBySellerIdAndId(sellerId, policyId)
                .orElseThrow(ShippingPolicyException::policyNotFound);
    }

    /**
     * ID 목록으로 배송 정책 조회 및 검증.
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체 목록을 반환합니다.
     *
     * <p>최적화: Map 생성 후 단일 순회로 검증 + 순서 보장 반환을 동시 수행.
     *
     * @param ids 배송 정책 ID 목록
     * @return 배송 정책 목록 (ID 순서 보장)
     * @throws ShippingPolicyException 존재하지 않는 ID가 있는 경우
     */
    public List<ShippingPolicy> findAllExistingOrThrow(List<ShippingPolicyId> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        Map<ShippingPolicyId, ShippingPolicy> foundMap =
                shippingPolicyReadManager.getByIds(ids).stream()
                        .collect(Collectors.toMap(ShippingPolicy::id, sp -> sp));

        return ids.stream()
                .map(
                        id -> {
                            ShippingPolicy sp = foundMap.get(id);
                            if (sp == null) {
                                throw ShippingPolicyException.policyNotFound();
                            }
                            return sp;
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
     * @throws LastActiveShippingPolicyCannotBeDeactivatedException 마지막 활성 정책 비활성화 시도 시
     */
    public void validateNotLastActivePolicy(
            SellerId sellerId, List<ShippingPolicy> policiesToDeactivate) {
        if (policiesToDeactivate.isEmpty()) {
            return;
        }

        long activePoliciesInRequest =
                policiesToDeactivate.stream().filter(ShippingPolicy::isActive).count();

        if (activePoliciesInRequest == 0) {
            return;
        }

        long totalActiveCount = shippingPolicyReadManager.countActiveBySellerId(sellerId);

        if (totalActiveCount <= activePoliciesInRequest) {
            throw new LastActiveShippingPolicyCannotBeDeactivatedException();
        }
    }
}
