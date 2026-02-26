package com.ryuqq.marketplace.application.shippingpolicy.internal;

import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyCommandManager;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.CannotUnmarkOnlyDefaultShippingPolicyException;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * DefaultShippingPolicyResolver - 배송 정책 기본 정책 설정 해결자
 *
 * <p>기본 정책 관련 비즈니스 규칙을 처리합니다:
 *
 * <ul>
 *   <li><b>POL-DEF-001</b>: 셀러당 기본 정책은 정확히 1개만 존재해야 함
 *   <li><b>POL-DEF-002</b>: 기본 정책은 활성화 상태여야 함
 * </ul>
 *
 * @author ryu-qqq
 */
@Component
public class DefaultShippingPolicyResolver {

    private final ShippingPolicyReadManager readManager;
    private final ShippingPolicyCommandManager commandManager;

    public DefaultShippingPolicyResolver(
            ShippingPolicyReadManager readManager, ShippingPolicyCommandManager commandManager) {
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    /**
     * 새 정책 등록 시 기본 정책 설정을 해결합니다.
     *
     * <p>1. 새 정책이 기본 정책으로 설정되면 기존 기본 정책 해제
     *
     * <p>2. 셀러의 첫 번째 정책이면 자동으로 기본 정책으로 설정
     *
     * @param sellerId 셀러 ID
     * @param newPolicy 새로 등록할 정책
     * @param timestamp 변경 시간
     */
    public void resolveForRegistration(
            SellerId sellerId, ShippingPolicy newPolicy, Instant timestamp) {
        Optional<ShippingPolicy> existingDefault = readManager.findDefaultBySellerId(sellerId);

        if (newPolicy.isDefaultPolicy() && existingDefault.isPresent()) {
            unmarkExistingDefault(existingDefault.get(), timestamp);
        } else if (existingDefault.isEmpty()) {
            newPolicy.markAsDefault(timestamp);
        }
    }

    /**
     * 정책 수정 시 기본 정책 설정을 해결합니다.
     *
     * <p>1. null이면 변경하지 않음
     *
     * <p>2. 기본 정책이 아니었는데 → 기본 정책으로 변경: 기존 기본 정책 해제
     *
     * <p>3. 기본 정책이었는데 → 기본 정책 해제: 유일한 기본 정책이면 예외 발생
     *
     * @param sellerId 셀러 ID
     * @param policy 수정 대상 정책
     * @param newDefaultPolicy 새로운 기본 정책 설정 값 (null이면 변경하지 않음)
     * @param timestamp 변경 시간
     * @throws CannotUnmarkOnlyDefaultShippingPolicyException 유일한 기본 정책을 해제하려는 경우
     */
    public void resolveForUpdate(
            SellerId sellerId, ShippingPolicy policy, Boolean newDefaultPolicy, Instant timestamp) {
        if (newDefaultPolicy == null) {
            return;
        }

        boolean wasDefault = policy.isDefaultPolicy();

        if (wasDefault == newDefaultPolicy) {
            return;
        }

        if (newDefaultPolicy) {
            handleMarkAsDefault(sellerId, policy, timestamp);
        } else {
            handleUnmarkDefault(policy, timestamp);
        }
    }

    private void handleMarkAsDefault(SellerId sellerId, ShippingPolicy policy, Instant timestamp) {
        Optional<ShippingPolicy> existingDefault = readManager.findDefaultBySellerId(sellerId);

        if (existingDefault.isPresent() && !existingDefault.get().id().equals(policy.id())) {
            unmarkExistingDefault(existingDefault.get(), timestamp);
        }

        policy.markAsDefault(timestamp);
    }

    private void handleUnmarkDefault(ShippingPolicy policy, Instant timestamp) {
        throw new CannotUnmarkOnlyDefaultShippingPolicyException();
    }

    private void unmarkExistingDefault(ShippingPolicy oldDefault, Instant timestamp) {
        oldDefault.unmarkDefault(timestamp);
        commandManager.persist(oldDefault);
    }
}
