package com.ryuqq.marketplace.application.shippingpolicy;

import com.ryuqq.marketplace.application.shippingpolicy.dto.command.ChangeShippingPolicyStatusCommand;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.LeadTimeCommand;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.RegisterShippingPolicyCommand;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.UpdateShippingPolicyCommand;
import java.util.List;

/**
 * ShippingPolicy Command 테스트 Fixtures.
 *
 * <p>ShippingPolicy 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 */
public final class ShippingPolicyCommandFixtures {

    private ShippingPolicyCommandFixtures() {}

    // ===== RegisterShippingPolicyCommand =====

    public static RegisterShippingPolicyCommand registerCommand(Long sellerId) {
        return new RegisterShippingPolicyCommand(
                sellerId,
                "기본 배송 정책",
                true,
                "CONDITIONAL_FREE",
                3000L,
                50000L,
                3000L,
                5000L,
                3000L,
                6000L,
                leadTimeCommand());
    }

    public static RegisterShippingPolicyCommand freeShippingRegisterCommand(Long sellerId) {
        return new RegisterShippingPolicyCommand(
                sellerId,
                "무료 배송 정책",
                false,
                "FREE",
                0L,
                null,
                3000L,
                5000L,
                3000L,
                6000L,
                leadTimeCommand());
    }

    public static RegisterShippingPolicyCommand paidShippingRegisterCommand(Long sellerId) {
        return new RegisterShippingPolicyCommand(
                sellerId,
                "유료 배송 정책",
                false,
                "PAID",
                3000L,
                null,
                3000L,
                5000L,
                3000L,
                6000L,
                leadTimeCommand());
    }

    public static LeadTimeCommand leadTimeCommand() {
        return new LeadTimeCommand(1, 3, "14:00");
    }

    public static LeadTimeCommand sameDayLeadTimeCommand() {
        return new LeadTimeCommand(0, 0, "12:00");
    }

    // ===== UpdateShippingPolicyCommand =====

    public static UpdateShippingPolicyCommand updateCommand(Long sellerId, Long policyId) {
        return new UpdateShippingPolicyCommand(
                sellerId,
                policyId,
                "수정된 배송 정책",
                true,
                "CONDITIONAL_FREE",
                3500L,
                60000L,
                3500L,
                5500L,
                3500L,
                7000L,
                leadTimeCommand());
    }

    public static UpdateShippingPolicyCommand updateCommandWithDefaultPolicyChange(
            Long sellerId, Long policyId, Boolean newDefaultPolicy) {
        return new UpdateShippingPolicyCommand(
                sellerId,
                policyId,
                "수정된 배송 정책",
                newDefaultPolicy,
                "CONDITIONAL_FREE",
                3500L,
                60000L,
                3500L,
                5500L,
                3500L,
                7000L,
                leadTimeCommand());
    }

    // ===== ChangeShippingPolicyStatusCommand =====

    public static ChangeShippingPolicyStatusCommand activateCommand(
            Long sellerId, Long... policyIds) {
        return new ChangeShippingPolicyStatusCommand(sellerId, List.of(policyIds), true);
    }

    public static ChangeShippingPolicyStatusCommand deactivateCommand(
            Long sellerId, Long... policyIds) {
        return new ChangeShippingPolicyStatusCommand(sellerId, List.of(policyIds), false);
    }
}
