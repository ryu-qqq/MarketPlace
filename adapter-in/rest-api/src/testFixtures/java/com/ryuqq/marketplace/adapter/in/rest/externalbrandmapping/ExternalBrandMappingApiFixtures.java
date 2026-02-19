package com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping;

import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.command.BatchRegisterExternalBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.command.RegisterExternalBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.command.UpdateExternalBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.query.SearchExternalBrandMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.response.ExternalBrandMappingApiResponse;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.response.ExternalBrandMappingPageResult;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.response.ExternalBrandMappingResult;
import java.time.Instant;
import java.util.List;

/**
 * ExternalBrandMapping API 테스트 Fixtures.
 *
 * <p>ExternalBrandMapping REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ExternalBrandMappingApiFixtures {

    private ExternalBrandMappingApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_EXTERNAL_SOURCE_ID = 1L;
    public static final String DEFAULT_EXTERNAL_BRAND_CODE = "NV_BRAND_001";
    public static final String DEFAULT_EXTERNAL_BRAND_NAME = "나이키";
    public static final Long DEFAULT_INTERNAL_BRAND_ID = 1L;
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");
    public static final String DEFAULT_FORMATTED_TIME = "2025-01-23T10:30:00+09:00";

    // ===== Command Requests =====

    public static RegisterExternalBrandMappingApiRequest registerRequest() {
        return new RegisterExternalBrandMappingApiRequest(
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_INTERNAL_BRAND_ID);
    }

    public static BatchRegisterExternalBrandMappingApiRequest batchRegisterRequest() {
        return new BatchRegisterExternalBrandMappingApiRequest(
                List.of(
                        new BatchRegisterExternalBrandMappingApiRequest.MappingEntryRequest(
                                "NV_BRAND_001", "나이키", 1L),
                        new BatchRegisterExternalBrandMappingApiRequest.MappingEntryRequest(
                                "NV_BRAND_002", "아디다스", 2L)));
    }

    public static UpdateExternalBrandMappingApiRequest updateRequest() {
        return new UpdateExternalBrandMappingApiRequest(
                DEFAULT_EXTERNAL_BRAND_NAME, DEFAULT_INTERNAL_BRAND_ID, DEFAULT_STATUS);
    }

    // ===== Search Request =====

    public static SearchExternalBrandMappingsApiRequest searchRequest() {
        return new SearchExternalBrandMappingsApiRequest(null, null, null, null, 0, 20);
    }

    // ===== ExternalBrandMappingResult (Application) =====

    public static ExternalBrandMappingResult mappingResult(Long id) {
        return new ExternalBrandMappingResult(
                id,
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_INTERNAL_BRAND_ID,
                DEFAULT_STATUS,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static List<ExternalBrandMappingResult> mappingResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> mappingResult((long) i))
                .toList();
    }

    public static ExternalBrandMappingPageResult pageResult(int count, int page, int size) {
        List<ExternalBrandMappingResult> results = mappingResults(count);
        return ExternalBrandMappingPageResult.of(results, page, size, count);
    }

    public static ExternalBrandMappingPageResult emptyPageResult() {
        return ExternalBrandMappingPageResult.of(List.of(), 0, 20, 0);
    }

    // ===== ExternalBrandMappingApiResponse =====

    public static ExternalBrandMappingApiResponse apiResponse(Long id) {
        return new ExternalBrandMappingApiResponse(
                id,
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_INTERNAL_BRAND_ID,
                DEFAULT_STATUS,
                DEFAULT_FORMATTED_TIME,
                DEFAULT_FORMATTED_TIME);
    }
}
