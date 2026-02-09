package com.ryuqq.marketplace.adapter.in.rest.shop.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.query.SearchShopsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.response.ShopApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.shop.dto.query.ShopSearchParams;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
import com.ryuqq.marketplace.application.shop.dto.response.ShopResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** Shop Query API Mapper. */
@Component
public class ShopQueryApiMapper {

    public ShopSearchParams toSearchParams(SearchShopsApiRequest request) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        false,
                        null,
                        null,
                        request.sortKey() != null ? request.sortKey() : "createdAt",
                        request.sortDirection() != null ? request.sortDirection() : "DESC",
                        request.page() != null ? request.page() : 0,
                        request.size() != null ? request.size() : 20);

        return ShopSearchParams.of(
                request.statuses(), request.searchField(), request.searchWord(), searchParams);
    }

    public ShopApiResponse toResponse(ShopResult result) {
        return new ShopApiResponse(
                result.id(),
                result.shopName(),
                result.accountId(),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    public List<ShopApiResponse> toResponses(List<ShopResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<ShopApiResponse> toPageResponse(ShopPageResult pageResult) {
        List<ShopApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
