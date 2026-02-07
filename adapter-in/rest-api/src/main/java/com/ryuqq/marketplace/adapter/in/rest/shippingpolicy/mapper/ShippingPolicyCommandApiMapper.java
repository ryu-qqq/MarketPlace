package com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.mapper;

import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command.ChangeShippingPolicyStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command.RegisterShippingPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command.UpdateShippingPolicyApiRequest;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.ChangeShippingPolicyStatusCommand;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.LeadTimeCommand;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.RegisterShippingPolicyCommand;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.UpdateShippingPolicyCommand;
import org.springframework.stereotype.Component;

/**
 * ShippingPolicyCommandApiMapper - 배송정책 Command API 변환 매퍼.
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
public class ShippingPolicyCommandApiMapper {

    /**
     * RegisterShippingPolicyApiRequest -> RegisterShippingPolicyCommand 변환.
     *
     * @param sellerId 셀러 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public RegisterShippingPolicyCommand toCommand(
            Long sellerId, RegisterShippingPolicyApiRequest request) {
        LeadTimeCommand leadTimeCommand = toLeadTimeCommand(request.leadTime());

        return new RegisterShippingPolicyCommand(
                sellerId,
                request.policyName(),
                request.defaultPolicy(),
                request.shippingFeeType(),
                request.baseFee(),
                request.freeThreshold(),
                request.jejuExtraFee(),
                request.islandExtraFee(),
                request.returnFee(),
                request.exchangeFee(),
                leadTimeCommand);
    }

    /**
     * UpdateShippingPolicyApiRequest + PathVariable IDs -> UpdateShippingPolicyCommand 변환.
     *
     * <p>API-DTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param sellerId 셀러 ID (PathVariable)
     * @param policyId 정책 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateShippingPolicyCommand toCommand(
            Long sellerId, Long policyId, UpdateShippingPolicyApiRequest request) {
        LeadTimeCommand leadTimeCommand = toLeadTimeCommand(request.leadTime());

        return new UpdateShippingPolicyCommand(
                sellerId,
                policyId,
                request.policyName(),
                request.defaultPolicy(),
                request.shippingFeeType(),
                request.baseFee(),
                request.freeThreshold(),
                request.jejuExtraFee(),
                request.islandExtraFee(),
                request.returnFee(),
                request.exchangeFee(),
                leadTimeCommand);
    }

    /**
     * ChangeShippingPolicyStatusApiRequest -> ChangeShippingPolicyStatusCommand 변환.
     *
     * @param sellerId 셀러 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public ChangeShippingPolicyStatusCommand toCommand(
            Long sellerId, ChangeShippingPolicyStatusApiRequest request) {
        return new ChangeShippingPolicyStatusCommand(
                sellerId, request.policyIds(), request.active());
    }

    private LeadTimeCommand toLeadTimeCommand(
            RegisterShippingPolicyApiRequest.LeadTimeApiRequest leadTime) {
        if (leadTime == null) {
            return null;
        }
        return new LeadTimeCommand(leadTime.minDays(), leadTime.maxDays(), leadTime.cutoffTime());
    }
}
