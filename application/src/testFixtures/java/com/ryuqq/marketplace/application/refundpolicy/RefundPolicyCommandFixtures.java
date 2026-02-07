package com.ryuqq.marketplace.application.refundpolicy;

import com.ryuqq.marketplace.application.refundpolicy.dto.command.ChangeRefundPolicyStatusCommand;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.RegisterRefundPolicyCommand;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.UpdateRefundPolicyCommand;
import java.util.List;

/**
 * RefundPolicy Command 테스트 Fixtures.
 *
 * <p>RefundPolicy 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 */
public final class RefundPolicyCommandFixtures {

    private RefundPolicyCommandFixtures() {}

    // ===== RegisterRefundPolicyCommand =====

    public static RegisterRefundPolicyCommand registerCommand(Long sellerId) {
        return new RegisterRefundPolicyCommand(
                sellerId,
                "기본 환불 정책",
                true,
                7,
                14,
                List.of("OPENED_PACKAGING", "USED_PRODUCT"),
                true,
                true,
                3,
                "추가 안내 문구입니다.");
    }

    public static RegisterRefundPolicyCommand simpleRegisterCommand(Long sellerId) {
        return new RegisterRefundPolicyCommand(
                sellerId, "간편 환불 정책", false, 7, 14, List.of(), false, false, 0, null);
    }

    // ===== UpdateRefundPolicyCommand =====

    public static UpdateRefundPolicyCommand updateCommand(Long sellerId, Long policyId) {
        return new UpdateRefundPolicyCommand(
                sellerId,
                policyId,
                "수정된 환불 정책",
                true,
                14,
                21,
                List.of("OPENED_PACKAGING", "USED_PRODUCT", "MISSING_TAG"),
                true,
                true,
                5,
                "수정된 추가 안내 문구입니다.");
    }

    public static UpdateRefundPolicyCommand updateCommandWithDefaultPolicyChange(
            Long sellerId, Long policyId, Boolean newDefaultPolicy) {
        return new UpdateRefundPolicyCommand(
                sellerId,
                policyId,
                "수정된 환불 정책",
                newDefaultPolicy,
                14,
                21,
                List.of("OPENED_PACKAGING"),
                true,
                false,
                0,
                null);
    }

    // ===== ChangeRefundPolicyStatusCommand =====

    public static ChangeRefundPolicyStatusCommand activateCommand(
            Long sellerId, Long... policyIds) {
        return new ChangeRefundPolicyStatusCommand(sellerId, List.of(policyIds), true);
    }

    public static ChangeRefundPolicyStatusCommand deactivateCommand(
            Long sellerId, Long... policyIds) {
        return new ChangeRefundPolicyStatusCommand(sellerId, List.of(policyIds), false);
    }
}
