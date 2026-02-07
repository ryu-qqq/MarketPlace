package com.ryuqq.marketplace.adapter.in.rest.refundpolicy.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.query.SearchRefundPoliciesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response.NonReturnableConditionApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response.RefundPolicyApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.refundpolicy.dto.query.RefundPolicySearchParams;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.NonReturnableConditionResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyPageResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RefundPolicyQueryApiMapper - 환불정책 Query API 변환 매퍼.
 *
 * <p>API Request/Response와 Application Query/Result 간 변환을 담당합니다.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-003: Application Result -> API Response 변환.
 *
 * <p>API-MAP-004: Slice/Page 변환 지원.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * <p>API-DTO-005: 날짜 String 변환 필수.
 *
 * <p>CQRS 분리: Query 전용 Mapper (CommandApiMapper와 분리).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class RefundPolicyQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    /**
     * SearchRefundPoliciesPageApiRequest -> RefundPolicySearchParams 변환.
     *
     * <p>API-CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * <p>CommonSearchParams 내부에서 기본값 처리를 수행하므로 Mapper는 단순 변환만 담당합니다.
     *
     * @param sellerId 셀러 ID
     * @param request 조회 요청 DTO
     * @return RefundPolicySearchParams 객체
     */
    public RefundPolicySearchParams toSearchParams(
            Long sellerId, SearchRefundPoliciesPageApiRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;

        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        null, null, null, request.sortKey(), request.sortDirection(), page, size);

        return RefundPolicySearchParams.of(sellerId, searchParams);
    }

    /**
     * 단일 RefundPolicyResult -> RefundPolicyApiResponse 변환.
     *
     * <p>API-DTO-005: Response DTO는 String 타입으로 날짜/시간 표현.
     *
     * @param result RefundPolicyResult
     * @return RefundPolicyApiResponse
     */
    public RefundPolicyApiResponse toResponse(RefundPolicyResult result) {
        List<NonReturnableConditionApiResponse> conditions =
                toConditionResponses(result.nonReturnableConditions());

        return new RefundPolicyApiResponse(
                result.policyId(),
                result.policyName(),
                result.defaultPolicy(),
                result.active(),
                result.returnPeriodDays(),
                result.exchangePeriodDays(),
                conditions,
                DateTimeFormatUtils.formatIso8601(result.createdAt()));
    }

    /**
     * NonReturnableConditionResult 목록 -> NonReturnableConditionApiResponse 목록 변환.
     *
     * @param conditions NonReturnableConditionResult 목록
     * @return NonReturnableConditionApiResponse 목록
     */
    public List<NonReturnableConditionApiResponse> toConditionResponses(
            List<NonReturnableConditionResult> conditions) {
        if (conditions == null) {
            return List.of();
        }
        return conditions.stream()
                .map(c -> new NonReturnableConditionApiResponse(c.code(), c.displayName()))
                .toList();
    }

    /**
     * RefundPolicyResult 목록 -> RefundPolicyApiResponse 목록 변환.
     *
     * @param results RefundPolicyResult 목록
     * @return RefundPolicyApiResponse 목록
     */
    public List<RefundPolicyApiResponse> toResponses(List<RefundPolicyResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * RefundPolicyPageResult -> PageApiResponse 변환.
     *
     * <p>API-CTR-011: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
     *
     * @param pageResult Application 페이지 결과 DTO
     * @return API 페이지 응답 DTO
     */
    public PageApiResponse<RefundPolicyApiResponse> toPageResponse(
            RefundPolicyPageResult pageResult) {
        List<RefundPolicyApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
