package com.ryuqq.marketplace.adapter.in.rest.seller.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.query.SearchSellersApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.response.SellerApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.response.SellerDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.response.SellerPublicProfileApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerFullCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.query.SellerSearchParams;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPublicProfileResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerQueryApiMapper - 셀러 Query API 변환 매퍼.
 *
 * <p>API Request/Response와 Application Query/Result 간 변환을 담당합니다.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-002: 양방향 변환 지원.
 *
 * <p>API-MAP-003: Application Result -> API Response 변환.
 *
 * <p>API-MAP-004: Slice/Page 변환 지원.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * <p>API-DTO-005: 날짜 String 변환 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class SellerQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final String DEFAULT_SORT_KEY = "createdAt";
    private static final String DEFAULT_SORT_DIRECTION = "DESC";

    /**
     * SearchSellersApiRequest -> SellerSearchParams 변환.
     *
     * <p>API-CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * <p>API-MAP-006: 기본값 처리 담당.
     *
     * @param request 검색 요청 DTO
     * @return SellerSearchParams 객체
     */
    public SellerSearchParams toSearchParams(SearchSellersApiRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;
        String sortKey = request.sortKey() != null ? request.sortKey() : DEFAULT_SORT_KEY;
        String sortDirection =
                request.sortDirection() != null ? request.sortDirection() : DEFAULT_SORT_DIRECTION;

        CommonSearchParams searchParams =
                CommonSearchParams.of(false, null, null, sortKey, sortDirection, page, size);

        return SellerSearchParams.of(
                request.active(), request.searchField(), request.searchWord(), searchParams);
    }

    /**
     * SellerResult -> SellerApiResponse 변환.
     *
     * <p>API-DTO-005: Response DTO는 String 타입으로 날짜/시간 표현.
     *
     * @param result SellerResult
     * @return SellerApiResponse
     */
    public SellerApiResponse toResponse(SellerResult result) {
        return new SellerApiResponse(
                result.id(),
                result.sellerName(),
                result.displayName(),
                result.logoUrl(),
                result.description(),
                result.active(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * SellerResult 목록 -> SellerApiResponse 목록 변환.
     *
     * @param results SellerResult 목록
     * @return SellerApiResponse 목록
     */
    public List<SellerApiResponse> toResponses(List<SellerResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * SellerPageResult -> PageApiResponse 변환.
     *
     * <p>API-CTR-011: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
     *
     * @param pageResult Application 페이지 결과 DTO
     * @return API 페이지 응답 DTO
     */
    public PageApiResponse<SellerApiResponse> toPageResponse(SellerPageResult pageResult) {
        List<SellerApiResponse> responses = toResponses(pageResult.content());
        return PageApiResponse.of(
                responses, pageResult.page(), pageResult.size(), pageResult.totalCount());
    }

    /**
     * SellerFullCompositeResult -> SellerDetailApiResponse 변환.
     *
     * <p>상세 조회 결과를 API 응답으로 변환합니다.
     *
     * @param result SellerFullCompositeResult
     * @return SellerDetailApiResponse
     */
    public SellerDetailApiResponse toDetailResponse(SellerFullCompositeResult result) {
        SellerCompositeResult composite = result.sellerComposite();

        SellerDetailApiResponse.SellerInfo sellerInfo = toSellerInfo(composite.seller());
        SellerDetailApiResponse.BusinessInfo businessInfo =
                toBusinessInfo(composite.businessInfo());
        SellerDetailApiResponse.CsInfo csInfo = toCsInfo(composite.csInfo());
        SellerDetailApiResponse.ContractInfo contractInfo = toContractInfo(result.contractInfo());
        SellerDetailApiResponse.SettlementInfo settlementInfo =
                toSettlementInfo(result.settlementInfo());

        return new SellerDetailApiResponse(
                sellerInfo, businessInfo, csInfo, contractInfo, settlementInfo);
    }

    private SellerDetailApiResponse.SellerInfo toSellerInfo(
            SellerCompositeResult.SellerInfo seller) {
        return new SellerDetailApiResponse.SellerInfo(
                seller.id(),
                seller.sellerName(),
                seller.displayName(),
                seller.logoUrl(),
                seller.description(),
                seller.active(),
                DateTimeFormatUtils.formatIso8601(seller.createdAt()),
                DateTimeFormatUtils.formatIso8601(seller.updatedAt()));
    }

    private SellerDetailApiResponse.BusinessInfo toBusinessInfo(
            SellerCompositeResult.BusinessInfo business) {
        if (business == null) {
            return null;
        }
        return new SellerDetailApiResponse.BusinessInfo(
                business.id(),
                business.registrationNumber(),
                business.companyName(),
                business.representative(),
                business.saleReportNumber(),
                business.businessZipcode(),
                business.businessAddress(),
                business.businessAddressDetail());
    }

    private SellerDetailApiResponse.CsInfo toCsInfo(SellerCompositeResult.CsInfo cs) {
        if (cs == null) {
            return null;
        }
        return new SellerDetailApiResponse.CsInfo(
                cs.id(),
                cs.csPhone(),
                cs.csMobile(),
                cs.csEmail(),
                cs.operatingStartTime(),
                cs.operatingEndTime(),
                cs.operatingDays(),
                cs.kakaoChannelUrl());
    }

    private SellerDetailApiResponse.ContractInfo toContractInfo(
            SellerFullCompositeResult.ContractInfo contract) {
        if (contract == null) {
            return null;
        }
        return new SellerDetailApiResponse.ContractInfo(
                contract.id(),
                contract.commissionRate() != null ? contract.commissionRate().toString() : null,
                contract.contractStartDate() != null
                        ? contract.contractStartDate().toString()
                        : null,
                contract.contractEndDate() != null ? contract.contractEndDate().toString() : null,
                contract.status(),
                contract.specialTerms(),
                DateTimeFormatUtils.formatIso8601(contract.createdAt()),
                DateTimeFormatUtils.formatIso8601(contract.updatedAt()));
    }

    private SellerDetailApiResponse.SettlementInfo toSettlementInfo(
            SellerFullCompositeResult.SettlementInfo settlement) {
        if (settlement == null) {
            return null;
        }
        return new SellerDetailApiResponse.SettlementInfo(
                settlement.id(),
                settlement.bankCode(),
                settlement.bankName(),
                settlement.accountNumber(),
                settlement.accountHolderName(),
                settlement.settlementCycle(),
                settlement.settlementDay(),
                settlement.verified(),
                DateTimeFormatUtils.formatIso8601(settlement.verifiedAt()),
                DateTimeFormatUtils.formatIso8601(settlement.createdAt()),
                DateTimeFormatUtils.formatIso8601(settlement.updatedAt()));
    }

    /**
     * SellerPublicProfileResult -> SellerPublicProfileApiResponse 변환.
     *
     * @param result SellerPublicProfileResult
     * @return SellerPublicProfileApiResponse
     */
    public SellerPublicProfileApiResponse toPublicProfileResponse(
            SellerPublicProfileResult result) {
        return new SellerPublicProfileApiResponse(
                result.sellerName(),
                result.displayName(),
                result.companyName(),
                result.representative());
    }
}
