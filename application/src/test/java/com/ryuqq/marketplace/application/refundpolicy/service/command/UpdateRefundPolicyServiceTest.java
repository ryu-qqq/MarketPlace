package com.ryuqq.marketplace.application.refundpolicy.service.command;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.refundpolicy.RefundPolicyCommandFixtures;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.UpdateRefundPolicyCommand;
import com.ryuqq.marketplace.application.refundpolicy.factory.RefundPolicyCommandFactory;
import com.ryuqq.marketplace.application.refundpolicy.internal.DefaultRefundPolicyResolver;
import com.ryuqq.marketplace.application.refundpolicy.internal.RefundPolicyOutboundFacade;
import com.ryuqq.marketplace.application.refundpolicy.validator.RefundPolicyValidator;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerOperationType;
import com.ryuqq.marketplace.domain.refundpolicy.RefundPolicyFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicyUpdateData;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.refundpolicy.vo.NonReturnableCondition;
import com.ryuqq.marketplace.domain.refundpolicy.vo.RefundPolicyName;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateRefundPolicyService 단위 테스트")
class UpdateRefundPolicyServiceTest {

    @InjectMocks private UpdateRefundPolicyService sut;

    @Mock private RefundPolicyCommandFactory commandFactory;
    @Mock private RefundPolicyValidator validator;
    @Mock private DefaultRefundPolicyResolver defaultPolicyResolver;
    @Mock private RefundPolicyOutboundFacade outboundFacade;

    @Nested
    @DisplayName("execute() - 환불 정책 수정")
    class ExecuteTest {

        @Test
        @DisplayName("환불 정책을 수정한다")
        void execute_UpdatesPolicy() {
            // given
            Long sellerId = 1L;
            Long policyId = 100L;
            UpdateRefundPolicyCommand command =
                    RefundPolicyCommandFixtures.updateCommand(sellerId, policyId);
            RefundPolicy refundPolicy =
                    RefundPolicyFixtures.activeRefundPolicy(policyId, SellerId.of(sellerId));

            Instant changedAt = Instant.now();
            RefundPolicyUpdateData updateData =
                    RefundPolicyUpdateData.of(
                            RefundPolicyName.of("수정된 정책"),
                            14,
                            21,
                            List.of(NonReturnableCondition.OPENED_PACKAGING),
                            true,
                            true,
                            5,
                            "추가 안내");
            UpdateContext<RefundPolicyId, RefundPolicyUpdateData> context =
                    new UpdateContext<>(RefundPolicyId.of(policyId), updateData, changedAt);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingBySellerOrThrow(SellerId.of(sellerId), context.id()))
                    .willReturn(refundPolicy);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateContext(command);
            then(validator)
                    .should()
                    .findExistingBySellerOrThrow(SellerId.of(sellerId), context.id());
            then(defaultPolicyResolver)
                    .should()
                    .resolveForUpdate(
                            eq(SellerId.of(sellerId)),
                            eq(refundPolicy),
                            eq(command.defaultPolicy()),
                            eq(context.changedAt()));
            then(outboundFacade)
                    .should()
                    .persistWithSync(
                            eq(refundPolicy),
                            eq(OutboundSellerOperationType.UPDATE),
                            eq(context.changedAt()));
        }
    }
}
