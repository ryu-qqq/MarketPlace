package com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.query.SearchShippingPoliciesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.response.ShippingPolicyApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.shippingpolicy.dto.query.ShippingPolicySearchParams;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyPageResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ShippingPolicyQueryApiMapper - 배송정책 Query API 변환 매퍼.
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
public class ShippingPolicyQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    /**
     * SearchShippingPoliciesPageApiRequest -> ShippingPolicySearchParams 변환.
     *
     * <p>API-CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * <p>CommonSearchParams 내부에서 기본값 처리를 수행하므로 Mapper는 단순 변환만 담당합니다.
     *
     * @param sellerId 셀러 ID
     * @param request 조회 요청 DTO
     * @return ShippingPolicySearchParams 객체
     */
    public ShippingPolicySearchParams toSearchParams(
            Long sellerId, SearchShippingPoliciesPageApiRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;

        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        null, null, null, request.sortKey(), request.sortDirection(), page, size);

        return ShippingPolicySearchParams.of(sellerId, searchParams);
    }

    /**
     * 단일 ShippingPolicyResult -> ShippingPolicyApiResponse 변환.
     *
     * <p>API-DTO-005: Response DTO는 String 타입으로 날짜/시간 표현.
     *
     * @param result ShippingPolicyResult
     * @return ShippingPolicyApiResponse
     */
    public ShippingPolicyApiResponse toResponse(ShippingPolicyResult result) {
        return new ShippingPolicyApiResponse(
                result.policyId(),
                result.policyName(),
                result.defaultPolicy(),
                result.active(),
                result.shippingFeeType(),
                result.shippingFeeTypeDisplayName(),
                result.baseFee(),
                result.freeThreshold(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()));
    }

    /**
     * ShippingPolicyResult 목록 -> ShippingPolicyApiResponse 목록 변환.
     *
     * @param results ShippingPolicyResult 목록
     * @return ShippingPolicyApiResponse 목록
     */
    public List<ShippingPolicyApiResponse> toResponses(List<ShippingPolicyResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * ShippingPolicyPageResult -> PageApiResponse 변환.
     *
     * <p>API-CTR-011: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
     *
     * @param pageResult Application 페이지 결과 DTO
     * @return API 페이지 응답 DTO
     */
    public PageApiResponse<ShippingPolicyApiResponse> toPageResponse(
            ShippingPolicyPageResult pageResult) {
        List<ShippingPolicyApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
