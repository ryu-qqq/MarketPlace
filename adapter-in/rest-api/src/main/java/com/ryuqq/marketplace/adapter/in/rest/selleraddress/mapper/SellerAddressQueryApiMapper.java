package com.ryuqq.marketplace.adapter.in.rest.selleraddress.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.query.SearchSellerAddressesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.response.SellerAddressApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.response.SellerOperationMetadataApiResponse;
import com.ryuqq.marketplace.application.selleraddress.dto.query.SellerAddressSearchParams;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressPageResult;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressResult;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerOperationMetadataResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerAddressQueryApiMapper - 셀러 주소 Query API 변환 매퍼.
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
public class SellerAddressQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    /**
     * SearchSellerAddressesApiRequest -> SellerAddressSearchParams 변환.
     *
     * <p>page/size 기본값은 Mapper에서 적용.
     *
     * @param request 조회 요청 DTO
     * @return SellerAddressSearchParams 객체
     */
    public SellerAddressSearchParams toSearchParams(SearchSellerAddressesApiRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;
        List<Long> sellerIds = request.sellerIds() != null ? request.sellerIds() : List.of();
        return new SellerAddressSearchParams(
                sellerIds,
                request.addressTypes(),
                request.defaultAddress(),
                request.searchField(),
                request.searchWord(),
                page,
                size);
    }

    /**
     * 단일 SellerAddressResult -> SellerAddressApiResponse 변환.
     *
     * <p>API-DTO-005: Response DTO는 String 타입으로 날짜/시간 표현.
     *
     * @param result SellerAddressResult
     * @return SellerAddressApiResponse
     */
    public SellerAddressApiResponse toResponse(SellerAddressResult result) {
        return new SellerAddressApiResponse(
                result.id(),
                result.sellerId(),
                result.addressType(),
                result.addressName(),
                new SellerAddressApiResponse.AddressResponse(
                        result.address().zipCode(),
                        result.address().line1(),
                        result.address().line2()),
                result.defaultAddress(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * SellerAddressResult 목록 -> SellerAddressApiResponse 목록 변환.
     *
     * @param results SellerAddressResult 목록
     * @return SellerAddressApiResponse 목록
     */
    public List<SellerAddressApiResponse> toResponses(List<SellerAddressResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * SellerAddressPageResult -> PageApiResponse 변환.
     *
     * <p>API-CTR-011: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
     *
     * @param pageResult Application 페이지 결과 DTO
     * @return API 페이지 응답 DTO
     */
    public PageApiResponse<SellerAddressApiResponse> toPageResponse(
            SellerAddressPageResult pageResult) {
        List<SellerAddressApiResponse> responses = toResponses(pageResult.content());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }

    /**
     * SellerOperationMetadataResult -> SellerOperationMetadataApiResponse 변환.
     *
     * @param result Application 운영 메타데이터 결과 DTO
     * @return API 운영 메타데이터 응답 DTO
     */
    public SellerOperationMetadataApiResponse toMetadataResponse(
            SellerOperationMetadataResult result) {
        return new SellerOperationMetadataApiResponse(
                result.totalCount(),
                result.shippingCount(),
                result.returnCount(),
                result.hasDefaultShipping(),
                result.hasDefaultReturn(),
                result.shippingPolicyCount(),
                result.refundPolicyCount(),
                result.hasDefaultShippingPolicy(),
                result.hasDefaultRefundPolicy());
    }
}
