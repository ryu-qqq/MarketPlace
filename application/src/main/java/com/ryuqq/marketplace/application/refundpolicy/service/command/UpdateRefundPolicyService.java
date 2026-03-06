package com.ryuqq.marketplace.application.refundpolicy.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.UpdateRefundPolicyCommand;
import com.ryuqq.marketplace.application.refundpolicy.factory.RefundPolicyCommandFactory;
import com.ryuqq.marketplace.application.refundpolicy.internal.DefaultRefundPolicyResolver;
import com.ryuqq.marketplace.application.refundpolicy.internal.RefundPolicyOutboundFacade;
import com.ryuqq.marketplace.application.refundpolicy.port.in.command.UpdateRefundPolicyUseCase;
import com.ryuqq.marketplace.application.refundpolicy.validator.RefundPolicyValidator;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerOperationType;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicyUpdateData;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UpdateRefundPolicyService - 환불 정책 수정 Service
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 *
 * <p>APP-VAL-001: 검증 + Domain 조회는 Validator.findExistingOrThrow()로 처리
 *
 * <p>비즈니스 규칙:
 *
 * <ul>
 *   <li>기본 정책이 아닌 정책을 기본 정책으로 변경 시 기존 기본 정책 해제
 *   <li>유일한 기본 정책은 해제 불가
 * </ul>
 *
 * @author ryu-qqq
 */
@Service
public class UpdateRefundPolicyService implements UpdateRefundPolicyUseCase {

    private final RefundPolicyCommandFactory commandFactory;
    private final RefundPolicyValidator validator;
    private final DefaultRefundPolicyResolver defaultPolicyResolver;
    private final RefundPolicyOutboundFacade outboundFacade;

    public UpdateRefundPolicyService(
            RefundPolicyCommandFactory commandFactory,
            RefundPolicyValidator validator,
            DefaultRefundPolicyResolver defaultPolicyResolver,
            RefundPolicyOutboundFacade outboundFacade) {
        this.commandFactory = commandFactory;
        this.validator = validator;
        this.defaultPolicyResolver = defaultPolicyResolver;
        this.outboundFacade = outboundFacade;
    }

    @Override
    @Transactional
    public void execute(UpdateRefundPolicyCommand command) {
        UpdateContext<RefundPolicyId, RefundPolicyUpdateData> context =
                commandFactory.createUpdateContext(command);

        SellerId sellerId = SellerId.of(command.sellerId());
        RefundPolicy refundPolicy = validator.findExistingBySellerOrThrow(sellerId, context.id());

        // 기본 정책 변경 처리
        defaultPolicyResolver.resolveForUpdate(
                sellerId, refundPolicy, command.defaultPolicy(), context.changedAt());

        // 정책 정보 업데이트
        refundPolicy.update(context.updateData(), context.changedAt());

        outboundFacade.persistWithSync(
                refundPolicy, OutboundSellerOperationType.UPDATE, context.changedAt());
    }
}
