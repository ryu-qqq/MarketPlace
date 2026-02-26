package com.ryuqq.marketplace.application.shippingpolicy.factory;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.ChangeShippingPolicyStatusCommand;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.LeadTimeCommand;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.RegisterShippingPolicyCommand;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.UpdateShippingPolicyCommand;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicyUpdateData;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.LeadTime;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.ShippingFeeType;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.ShippingPolicyName;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ShippingPolicyCommandFactory - 배송 정책 Command Factory
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ShippingPolicyCommandFactory {

    private final TimeProvider timeProvider;

    public ShippingPolicyCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * RegisterShippingPolicyCommand로부터 ShippingPolicy 생성
     *
     * @param command 등록 Command
     * @return ShippingPolicy 도메인 객체
     */
    public ShippingPolicy create(RegisterShippingPolicyCommand command) {
        Instant now = timeProvider.now();

        SellerId sellerId = SellerId.of(command.sellerId());
        ShippingPolicyName policyName = ShippingPolicyName.of(command.policyName());
        ShippingFeeType shippingFeeType = ShippingFeeType.valueOf(command.shippingFeeType());

        Money baseFee = toMoney(command.baseFee());
        Money freeThreshold = toMoney(command.freeThreshold());
        Money jejuExtraFee = toMoney(command.jejuExtraFee());
        Money islandExtraFee = toMoney(command.islandExtraFee());
        Money returnFee = toMoney(command.returnFee());
        Money exchangeFee = toMoney(command.exchangeFee());

        LeadTime leadTime = toLeadTime(command.leadTime());

        boolean defaultPolicy = Boolean.TRUE.equals(command.defaultPolicy());

        return ShippingPolicy.forNew(
                sellerId,
                policyName,
                defaultPolicy,
                shippingFeeType,
                baseFee,
                freeThreshold,
                jejuExtraFee,
                islandExtraFee,
                returnFee,
                exchangeFee,
                leadTime,
                now);
    }

    /**
     * UpdateShippingPolicyCommand로부터 UpdateContext 생성
     *
     * @param command 수정 Command
     * @return UpdateContext (ID, UpdateData, 변경 시간)
     */
    public UpdateContext<ShippingPolicyId, ShippingPolicyUpdateData> createUpdateContext(
            UpdateShippingPolicyCommand command) {
        Instant changedAt = timeProvider.now();

        ShippingPolicyId policyId = ShippingPolicyId.of(command.policyId());

        ShippingPolicyName policyName = ShippingPolicyName.of(command.policyName());
        ShippingFeeType shippingFeeType = ShippingFeeType.valueOf(command.shippingFeeType());

        Money baseFee = toMoney(command.baseFee());
        Money freeThreshold = toMoney(command.freeThreshold());
        Money jejuExtraFee = toMoney(command.jejuExtraFee());
        Money islandExtraFee = toMoney(command.islandExtraFee());
        Money returnFee = toMoney(command.returnFee());
        Money exchangeFee = toMoney(command.exchangeFee());

        LeadTime leadTime = toLeadTime(command.leadTime());

        ShippingPolicyUpdateData updateData =
                ShippingPolicyUpdateData.of(
                        policyName,
                        shippingFeeType,
                        baseFee,
                        freeThreshold,
                        jejuExtraFee,
                        islandExtraFee,
                        returnFee,
                        exchangeFee,
                        leadTime);

        return new UpdateContext<>(policyId, updateData, changedAt);
    }

    /**
     * ChangeShippingPolicyStatusCommand로부터 StatusChangeContext 목록 생성
     *
     * @param command 상태 변경 Command
     * @return StatusChangeContext 목록
     */
    public List<StatusChangeContext<ShippingPolicyId>> createStatusChangeContexts(
            ChangeShippingPolicyStatusCommand command) {
        Instant changedAt = timeProvider.now();
        return command.policyIds().stream()
                .map(id -> new StatusChangeContext<>(ShippingPolicyId.of(id), changedAt))
                .toList();
    }

    private Money toMoney(Long value) {
        if (value == null) {
            return Money.zero();
        }
        return Money.of(value.intValue());
    }

    private LeadTime toLeadTime(LeadTimeCommand command) {
        if (command == null) {
            return LeadTime.of(1, 3, LocalTime.of(14, 0));
        }
        LocalTime cutoffTime =
                command.cutoffTime() != null
                        ? LocalTime.parse(command.cutoffTime())
                        : LocalTime.of(14, 0);
        return LeadTime.of(command.minDays(), command.maxDays(), cutoffTime);
    }
}
