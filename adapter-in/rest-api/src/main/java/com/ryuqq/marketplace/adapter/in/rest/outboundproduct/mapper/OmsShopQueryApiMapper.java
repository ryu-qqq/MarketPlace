package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchOmsShopsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsShopApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsShopSearchParams;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
import com.ryuqq.marketplace.application.shop.dto.response.ShopResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** OMS Shop Query API 매퍼. */
@Component
public class OmsShopQueryApiMapper {

    public OmsShopSearchParams toSearchParams(SearchOmsShopsApiRequest request) {
        CommonSearchParams commonParams =
                CommonSearchParams.of(
                        false,
                        null,
                        null,
                        request.sortKey(),
                        request.sortDirection(),
                        request.page(),
                        request.size());
        return OmsShopSearchParams.of(request.keyword(), commonParams);
    }

    public PageApiResponse<OmsShopApiResponse> toPageResponse(ShopPageResult pageResult) {
        List<OmsShopApiResponse> responses =
                pageResult.results().stream().map(this::toResponse).toList();
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }

    private OmsShopApiResponse toResponse(ShopResult r) {
        return new OmsShopApiResponse(
                r.id(), r.shopName(), r.salesChannelId(), r.accountId(), r.status());
    }
}
