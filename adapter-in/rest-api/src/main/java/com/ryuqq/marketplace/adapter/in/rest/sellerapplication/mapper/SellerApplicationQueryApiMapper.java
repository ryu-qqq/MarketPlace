package com.ryuqq.marketplace.adapter.in.rest.sellerapplication.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.query.SearchSellerApplicationsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.response.SellerApplicationApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.sellerapplication.dto.query.SellerApplicationSearchParams;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationPageResult;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationResult;
import com.ryuqq.marketplace.domain.sellerapplication.vo.ApplicationStatus;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * SellerApplicationQueryApiMapper - 셀러 입점 신청 Query API 변환 매퍼.
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
public class SellerApplicationQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final String DEFAULT_SORT_KEY = "appliedAt";
    private static final String DEFAULT_SORT_DIRECTION = "DESC";

    /**
     * SearchSellerApplicationsApiRequest -> SellerApplicationSearchParams 변환.
     *
     * @param request 검색 요청 DTO
     * @return SellerApplicationSearchParams 객체
     */
    public SellerApplicationSearchParams toSearchParams(
            SearchSellerApplicationsApiRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;
        String sortKey = request.sortKey() != null ? request.sortKey() : DEFAULT_SORT_KEY;
        String sortDirection =
                request.sortDirection() != null ? request.sortDirection() : DEFAULT_SORT_DIRECTION;

        CommonSearchParams searchParams =
                CommonSearchParams.of(false, null, null, sortKey, sortDirection, page, size);

        List<ApplicationStatus> status = parseStatusList(request.status());

        return SellerApplicationSearchParams.of(
                status, request.searchField(), request.searchWord(), searchParams);
    }

    /**
     * SellerApplicationPageResult -> PageApiResponse 변환.
     *
     * @param pageResult Application 페이지 결과 DTO
     * @return API 페이지 응답 DTO
     */
    public PageApiResponse<SellerApplicationApiResponse> toPageResponse(
            SellerApplicationPageResult pageResult) {
        List<SellerApplicationApiResponse> responses = toResponses(pageResult.content());
        return PageApiResponse.of(
                responses, pageResult.page(), pageResult.size(), pageResult.totalCount());
    }

    /**
     * SellerApplicationResult 목록 -> SellerApplicationApiResponse 목록 변환.
     *
     * @param results SellerApplicationResult 목록
     * @return SellerApplicationApiResponse 목록
     */
    public List<SellerApplicationApiResponse> toResponses(List<SellerApplicationResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * SellerApplicationResult -> SellerApplicationApiResponse 변환.
     *
     * @param result SellerApplicationResult
     * @return SellerApplicationApiResponse
     */
    public SellerApplicationApiResponse toResponse(SellerApplicationResult result) {
        return new SellerApplicationApiResponse(
                result.id(),
                toSellerInfo(result.sellerInfo()),
                toBusinessInfo(result.businessInfo()),
                toCsContactInfo(result.csContact()),
                toContactInfo(result.contactInfo()),
                toAgreementInfo(result.agreement()),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.appliedAt()),
                DateTimeFormatUtils.formatIso8601(result.processedAt()),
                result.processedBy(),
                result.rejectionReason(),
                result.approvedSellerId());
    }

    private SellerApplicationApiResponse.SellerInfo toSellerInfo(
            SellerApplicationResult.SellerInfoResult sellerInfo) {
        if (sellerInfo == null) {
            return null;
        }
        return new SellerApplicationApiResponse.SellerInfo(
                sellerInfo.sellerName(),
                sellerInfo.displayName(),
                sellerInfo.logoUrl(),
                sellerInfo.description());
    }

    private SellerApplicationApiResponse.BusinessInfo toBusinessInfo(
            SellerApplicationResult.BusinessInfoResult businessInfo) {
        if (businessInfo == null) {
            return null;
        }
        return new SellerApplicationApiResponse.BusinessInfo(
                businessInfo.registrationNumber(),
                businessInfo.companyName(),
                businessInfo.representative(),
                businessInfo.saleReportNumber(),
                toAddressDetail(businessInfo.businessAddress()));
    }

    private SellerApplicationApiResponse.CsContactInfo toCsContactInfo(
            SellerApplicationResult.CsContactResult csContact) {
        if (csContact == null) {
            return null;
        }
        return new SellerApplicationApiResponse.CsContactInfo(
                csContact.phone(), csContact.email(), csContact.mobile());
    }

    private SellerApplicationApiResponse.ContactInfo toContactInfo(
            SellerApplicationResult.ContactInfoResult contactInfo) {
        if (contactInfo == null) {
            return null;
        }
        return new SellerApplicationApiResponse.ContactInfo(
                contactInfo.name(), contactInfo.phone(), contactInfo.email());
    }

    private SellerApplicationApiResponse.AddressDetail toAddressDetail(
            SellerApplicationResult.AddressResult address) {
        if (address == null) {
            return null;
        }
        return new SellerApplicationApiResponse.AddressDetail(
                address.zipCode(), address.line1(), address.line2());
    }

    private SellerApplicationApiResponse.AgreementInfo toAgreementInfo(
            SellerApplicationResult.AgreementResult agreement) {
        if (agreement == null) {
            return null;
        }
        return new SellerApplicationApiResponse.AgreementInfo(
                DateTimeFormatUtils.formatIso8601(agreement.agreedAt()),
                agreement.termsAgreed(),
                agreement.privacyAgreed());
    }

    private List<ApplicationStatus> parseStatusList(List<String> statusList) {
        if (statusList == null || statusList.isEmpty()) {
            return List.of();
        }
        return statusList.stream().map(this::parseStatus).filter(Objects::nonNull).toList();
    }

    private ApplicationStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return ApplicationStatus.valueOf(status.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
