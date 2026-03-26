package com.ryuqq.marketplace.adapter.in.rest.brand;

import com.ryuqq.marketplace.adapter.in.rest.brand.dto.query.SearchBrandsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandApiResponse;
import com.ryuqq.marketplace.application.brand.dto.response.BrandPageResult;
import com.ryuqq.marketplace.application.brand.dto.response.BrandResult;
import java.time.Instant;
import java.util.List;

/**
 * Brand API 테스트 Fixtures.
 *
 * <p>Brand REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class BrandApiFixtures {

    private BrandApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_CODE = "BR001";
    public static final String DEFAULT_NAME_KO = "테스트브랜드";
    public static final String DEFAULT_NAME_EN = "TestBrand";
    public static final String DEFAULT_SHORT_NAME = "테브";
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final String DEFAULT_LOGO_URL = "https://example.com/brand-logo.png";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");
    public static final String DEFAULT_FORMATTED_TIME = "2025-01-23 10:30:00";

    // ===== SearchBrandsApiRequest =====

    public static SearchBrandsApiRequest searchRequest() {
        return new SearchBrandsApiRequest(null, null, null, null, null, 0, 20);
    }

    public static SearchBrandsApiRequest searchRequest(
            List<String> statuses, String searchField, String searchWord, int page, int size) {
        return new SearchBrandsApiRequest(
                statuses, searchField, searchWord, "createdAt", "DESC", page, size);
    }

    // ===== BrandResult (Application) =====

    public static BrandResult brandResult(Long id) {
        return new BrandResult(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SHORT_NAME,
                DEFAULT_STATUS,
                DEFAULT_LOGO_URL,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static BrandResult brandResult(Long id, String nameKo, String status) {
        return new BrandResult(
                id,
                "BR" + String.format("%03d", id),
                nameKo,
                DEFAULT_NAME_EN,
                DEFAULT_SHORT_NAME,
                status,
                DEFAULT_LOGO_URL,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static List<BrandResult> brandResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> brandResult((long) i, "브랜드_" + i, DEFAULT_STATUS))
                .toList();
    }

    public static BrandPageResult pageResult(int count, int page, int size) {
        List<BrandResult> results = brandResults(count);
        return BrandPageResult.of(results, page, size, count);
    }

    public static BrandPageResult emptyPageResult() {
        return BrandPageResult.of(List.of(), 0, 20, 0);
    }

    // ===== BrandApiResponse =====

    public static BrandApiResponse apiResponse(Long id) {
        return new BrandApiResponse(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SHORT_NAME,
                DEFAULT_STATUS,
                DEFAULT_LOGO_URL,
                DEFAULT_FORMATTED_TIME,
                DEFAULT_FORMATTED_TIME);
    }
}
