package com.ryuqq.marketplace.adapter.in.rest.refundpolicy.mapper;

import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command.ChangeRefundPolicyStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command.RegisterRefundPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command.UpdateRefundPolicyApiRequest;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.ChangeRefundPolicyStatusCommand;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.RegisterRefundPolicyCommand;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.UpdateRefundPolicyCommand;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RefundPolicyCommandApiMapper - 환불정책 Command API 변환 매퍼.
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-002: 양방향 변환 지원.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * <p>CQRS 분리: Command 전용 Mapper (QueryApiMapper와 분리).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class RefundPolicyCommandApiMapper {

    /**
     * RegisterRefundPolicyApiRequest -> RegisterRefundPolicyCommand 변환.
     *
     * @param sellerId 셀러 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public RegisterRefundPolicyCommand toCommand(
            Long sellerId, RegisterRefundPolicyApiRequest request) {
        List<String> conditions =
                request.nonReturnableConditions() != null
                        ? request.nonReturnableConditions()
                        : List.of();

        return new RegisterRefundPolicyCommand(
                sellerId,
                request.policyName(),
                request.defaultPolicy(),
                request.returnPeriodDays(),
                request.exchangePeriodDays(),
                conditions,
                request.partialRefundEnabled(),
                request.inspectionRequired(),
                request.inspectionPeriodDays(),
                request.additionalInfo());
    }

    /**
     * UpdateRefundPolicyApiRequest + PathVariable IDs -> UpdateRefundPolicyCommand 변환.
     *
     * <p>API-DTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param sellerId 셀러 ID (PathVariable)
     * @param policyId 정책 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateRefundPolicyCommand toCommand(
            Long sellerId, Long policyId, UpdateRefundPolicyApiRequest request) {
        List<String> conditions =
                request.nonReturnableConditions() != null
                        ? request.nonReturnableConditions()
                        : List.of();

        return new UpdateRefundPolicyCommand(
                sellerId,
                policyId,
                request.policyName(),
                request.defaultPolicy(),
                request.returnPeriodDays(),
                request.exchangePeriodDays(),
                conditions,
                request.partialRefundEnabled(),
                request.inspectionRequired(),
                request.inspectionPeriodDays(),
                request.additionalInfo());
    }

    /**
     * ChangeRefundPolicyStatusApiRequest -> ChangeRefundPolicyStatusCommand 변환.
     *
     * @param sellerId 셀러 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public ChangeRefundPolicyStatusCommand toCommand(
            Long sellerId, ChangeRefundPolicyStatusApiRequest request) {
        return new ChangeRefundPolicyStatusCommand(sellerId, request.policyIds(), request.active());
    }
}
