package com.ryuqq.marketplace.application.shippingpolicy.service.command;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.shippingpolicy.ShippingPolicyCommandFixtures;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.UpdateShippingPolicyCommand;
import com.ryuqq.marketplace.application.shippingpolicy.factory.ShippingPolicyCommandFactory;
import com.ryuqq.marketplace.application.shippingpolicy.internal.DefaultShippingPolicyResolver;
import com.ryuqq.marketplace.application.shippingpolicy.internal.ShippingPolicyOutboundFacade;
import com.ryuqq.marketplace.application.shippingpolicy.validator.ShippingPolicyValidator;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerOperationType;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.ShippingPolicyFixtures;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicyUpdateData;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.time.Instant;
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
@DisplayName("UpdateShippingPolicyService 단위 테스트")
class UpdateShippingPolicyServiceTest {

    @InjectMocks private UpdateShippingPolicyService sut;

    @Mock private ShippingPolicyCommandFactory commandFactory;
    @Mock private ShippingPolicyValidator validator;
    @Mock private DefaultShippingPolicyResolver defaultPolicyResolver;
    @Mock private ShippingPolicyOutboundFacade outboundFacade;

    @Nested
    @DisplayName("execute() - 배송 정책 수정")
    class ExecuteTest {

        @Test
        @DisplayName("배송 정책을 수정한다")
        void execute_UpdatesPolicy() {
            // given
            Long sellerId = 1L;
            Long policyId = 100L;
            UpdateShippingPolicyCommand command =
                    ShippingPolicyCommandFixtures.updateCommand(sellerId, policyId);
            ShippingPolicy shippingPolicy =
                    ShippingPolicyFixtures.activeShippingPolicy(policyId, SellerId.of(sellerId));

            Instant changedAt = Instant.now();
            ShippingPolicyUpdateData updateData =
                    ShippingPolicyUpdateData.of(
                            ShippingPolicyFixtures.defaultPolicyName(),
                            shippingPolicy.shippingFeeType(),
                            CommonVoFixtures.defaultBaseFee(),
                            CommonVoFixtures.defaultFreeThreshold(),
                            CommonVoFixtures.defaultExtraFee(),
                            CommonVoFixtures.defaultExtraFee(),
                            CommonVoFixtures.defaultReturnFee(),
                            CommonVoFixtures.defaultExchangeFee(),
                            ShippingPolicyFixtures.defaultLeadTime());
            UpdateContext<ShippingPolicyId, ShippingPolicyUpdateData> context =
                    new UpdateContext<>(ShippingPolicyId.of(policyId), updateData, changedAt);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingBySellerOrThrow(SellerId.of(sellerId), context.id()))
                    .willReturn(shippingPolicy);

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
                            eq(shippingPolicy),
                            eq(command.defaultPolicy()),
                            eq(context.changedAt()));
            then(outboundFacade)
                    .should()
                    .persistWithSync(
                            eq(shippingPolicy),
                            eq(OutboundSellerOperationType.UPDATE),
                            eq(context.changedAt()));
        }
    }
}
