package com.ryuqq.marketplace.application.refundpolicy.factory;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.ChangeRefundPolicyStatusCommand;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.RegisterRefundPolicyCommand;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.UpdateRefundPolicyCommand;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicyUpdateData;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.refundpolicy.vo.NonReturnableCondition;
import com.ryuqq.marketplace.domain.refundpolicy.vo.RefundPolicyName;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RefundPolicyCommandFactory - 환불 정책 Command Factory
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 *
 * @author ryu-qqq
 */
@Component
public class RefundPolicyCommandFactory {

    private final TimeProvider timeProvider;

    public RefundPolicyCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * RegisterRefundPolicyCommand로부터 RefundPolicy 생성
     *
     * @param command 등록 Command
     * @return RefundPolicy 도메인 객체
     */
    public RefundPolicy create(RegisterRefundPolicyCommand command) {
        Instant now = timeProvider.now();

        SellerId sellerId = SellerId.of(command.sellerId());
        RefundPolicyName policyName = RefundPolicyName.of(command.policyName());
        boolean defaultPolicy = Boolean.TRUE.equals(command.defaultPolicy());

        List<NonReturnableCondition> conditions =
                toNonReturnableConditions(command.nonReturnableConditions());

        boolean partialRefundEnabled = Boolean.TRUE.equals(command.partialRefundEnabled());
        boolean inspectionRequired = Boolean.TRUE.equals(command.inspectionRequired());
        int inspectionPeriodDays =
                command.inspectionPeriodDays() != null ? command.inspectionPeriodDays() : 0;

        return RefundPolicy.forNew(
                sellerId,
                policyName,
                defaultPolicy,
                command.returnPeriodDays(),
                command.exchangePeriodDays(),
                conditions,
                partialRefundEnabled,
                inspectionRequired,
                inspectionPeriodDays,
                command.additionalInfo(),
                now);
    }

    /**
     * UpdateRefundPolicyCommand로부터 UpdateContext 생성
     *
     * @param command 수정 Command
     * @return UpdateContext (ID, UpdateData, 변경 시간)
     */
    public UpdateContext<RefundPolicyId, RefundPolicyUpdateData> createUpdateContext(
            UpdateRefundPolicyCommand command) {
        Instant changedAt = timeProvider.now();

        RefundPolicyId policyId = RefundPolicyId.of(command.policyId());
        RefundPolicyName policyName = RefundPolicyName.of(command.policyName());

        List<NonReturnableCondition> conditions =
                toNonReturnableConditions(command.nonReturnableConditions());

        boolean partialRefundEnabled = Boolean.TRUE.equals(command.partialRefundEnabled());
        boolean inspectionRequired = Boolean.TRUE.equals(command.inspectionRequired());
        int inspectionPeriodDays =
                command.inspectionPeriodDays() != null ? command.inspectionPeriodDays() : 0;

        RefundPolicyUpdateData updateData =
                RefundPolicyUpdateData.of(
                        policyName,
                        command.returnPeriodDays(),
                        command.exchangePeriodDays(),
                        conditions,
                        partialRefundEnabled,
                        inspectionRequired,
                        inspectionPeriodDays,
                        command.additionalInfo());

        return new UpdateContext<>(policyId, updateData, changedAt);
    }

    /**
     * ChangeRefundPolicyStatusCommand로부터 StatusChangeContext 목록 생성
     *
     * @param command 상태 변경 Command
     * @return StatusChangeContext 목록
     */
    public List<StatusChangeContext<RefundPolicyId>> createStatusChangeContexts(
            ChangeRefundPolicyStatusCommand command) {
        Instant changedAt = timeProvider.now();
        return command.policyIds().stream()
                .map(id -> new StatusChangeContext<>(RefundPolicyId.of(id), changedAt))
                .toList();
    }

    private List<NonReturnableCondition> toNonReturnableConditions(List<String> conditionCodes) {
        if (conditionCodes == null || conditionCodes.isEmpty()) {
            return List.of();
        }
        return conditionCodes.stream().map(NonReturnableCondition::valueOf).toList();
    }
}
