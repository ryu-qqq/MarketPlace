package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchOmsPartnersApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsPartnerApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsPartnerSearchParams;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** OMS Partner Query API 매퍼. */
@Component
public class OmsPartnerQueryApiMapper {

    public OmsPartnerSearchParams toSearchParams(SearchOmsPartnersApiRequest request) {
        CommonSearchParams commonParams =
                CommonSearchParams.of(
                        false,
                        null,
                        null,
                        request.sortKey(),
                        request.sortDirection(),
                        request.page(),
                        request.size());
        return OmsPartnerSearchParams.of(request.keyword(), commonParams);
    }

    public PageApiResponse<OmsPartnerApiResponse> toPageResponse(SellerPageResult pageResult) {
        List<OmsPartnerApiResponse> responses =
                pageResult.content().stream().map(this::toResponse).toList();
        return PageApiResponse.of(
                responses, pageResult.page(), pageResult.size(), pageResult.totalCount());
    }

    private OmsPartnerApiResponse toResponse(SellerResult r) {
        String status = r.active() ? "ACTIVE" : "INACTIVE";
        return new OmsPartnerApiResponse(r.id(), r.displayName(), r.sellerName(), status);
    }
}
