package com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand;

import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.command.RegisterSalesChannelBrandApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.query.SearchSalesChannelBrandsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.response.SalesChannelBrandApiResponse;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandPageResult;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandResult;
import java.time.Instant;
import java.util.List;

/**
 * SalesChannelBrand API 테스트 Fixtures.
 *
 * <p>SalesChannelBrand REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SalesChannelBrandApiFixtures {

    private SalesChannelBrandApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final String DEFAULT_EXTERNAL_BRAND_CODE = "BRD001";
    public static final String DEFAULT_EXTERNAL_BRAND_NAME = "나이키";
    public static final String DEFAULT_STATUS = "ACTIVE";

    // ===== RegisterSalesChannelBrandApiRequest =====

    public static RegisterSalesChannelBrandApiRequest registerRequest() {
        return new RegisterSalesChannelBrandApiRequest(
                DEFAULT_EXTERNAL_BRAND_CODE, DEFAULT_EXTERNAL_BRAND_NAME);
    }

    public static RegisterSalesChannelBrandApiRequest registerRequest(
            String externalBrandCode, String externalBrandName) {
        return new RegisterSalesChannelBrandApiRequest(externalBrandCode, externalBrandName);
    }

    // ===== SearchSalesChannelBrandsApiRequest =====

    public static SearchSalesChannelBrandsApiRequest searchRequest() {
        return new SearchSalesChannelBrandsApiRequest(null, null, null, null, null, 0, 20);
    }

    public static SearchSalesChannelBrandsApiRequest searchRequest(
            List<String> statuses, String searchField, String searchWord, int page, int size) {
        return new SearchSalesChannelBrandsApiRequest(
                statuses, searchField, searchWord, "createdAt", "DESC", page, size);
    }

    // ===== SalesChannelBrandResult (Application) =====

    public static SalesChannelBrandResult brandResult(Long id) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new SalesChannelBrandResult(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_STATUS,
                now,
                now);
    }

    public static SalesChannelBrandResult brandResult(
            Long id, Long salesChannelId, String externalBrandCode, String externalBrandName) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new SalesChannelBrandResult(
                id, salesChannelId, externalBrandCode, externalBrandName, DEFAULT_STATUS, now, now);
    }

    public static List<SalesChannelBrandResult> brandResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(
                        i ->
                                brandResult(
                                        (long) i,
                                        DEFAULT_SALES_CHANNEL_ID,
                                        "BRD00" + i,
                                        "브랜드_" + i))
                .toList();
    }

    public static SalesChannelBrandPageResult pageResult(int count, int page, int size) {
        List<SalesChannelBrandResult> results = brandResults(count);
        return SalesChannelBrandPageResult.of(results, page, size, count);
    }

    public static SalesChannelBrandPageResult emptyPageResult() {
        return SalesChannelBrandPageResult.of(List.of(), 0, 20, 0);
    }

    // ===== SalesChannelBrandApiResponse =====

    public static SalesChannelBrandApiResponse apiResponse(Long id) {
        return new SalesChannelBrandApiResponse(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_STATUS,
                "2025-01-23 10:30:00",
                "2025-01-23 10:30:00");
    }

    public static SalesChannelBrandApiResponse apiResponse(
            Long id, Long salesChannelId, String externalBrandCode, String externalBrandName) {
        return new SalesChannelBrandApiResponse(
                id,
                salesChannelId,
                externalBrandCode,
                externalBrandName,
                DEFAULT_STATUS,
                "2025-01-23 10:30:00",
                "2025-01-23 10:30:00");
    }
}
