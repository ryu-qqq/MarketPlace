package com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping;

import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.command.BatchRegisterInboundBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.command.RegisterInboundBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.command.UpdateInboundBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.query.SearchInboundBrandMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.response.InboundBrandMappingApiResponse;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.response.InboundBrandMappingPageResult;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.response.InboundBrandMappingResult;
import java.time.Instant;
import java.util.List;

/**
 * InboundBrandMapping API 테스트 Fixtures.
 *
 * <p>InboundBrandMapping REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class InboundBrandMappingApiFixtures {

    private InboundBrandMappingApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_EXTERNAL_SOURCE_ID = 1L;
    public static final String DEFAULT_EXTERNAL_BRAND_CODE = "NV_BRAND_001";
    public static final String DEFAULT_EXTERNAL_BRAND_NAME = "나이키";
    public static final Long DEFAULT_INTERNAL_BRAND_ID = 1L;
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");
    public static final String DEFAULT_FORMATTED_TIME = "2025-01-23T10:30:00+09:00";

    // ===== Command Requests =====

    public static RegisterInboundBrandMappingApiRequest registerRequest() {
        return new RegisterInboundBrandMappingApiRequest(
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_INTERNAL_BRAND_ID);
    }

    public static BatchRegisterInboundBrandMappingApiRequest batchRegisterRequest() {
        return new BatchRegisterInboundBrandMappingApiRequest(
                List.of(
                        new BatchRegisterInboundBrandMappingApiRequest.MappingEntryRequest(
                                "NV_BRAND_001", "나이키", 1L),
                        new BatchRegisterInboundBrandMappingApiRequest.MappingEntryRequest(
                                "NV_BRAND_002", "아디다스", 2L)));
    }

    public static UpdateInboundBrandMappingApiRequest updateRequest() {
        return new UpdateInboundBrandMappingApiRequest(
                DEFAULT_EXTERNAL_BRAND_NAME, DEFAULT_INTERNAL_BRAND_ID, DEFAULT_STATUS);
    }

    // ===== Search Request =====

    public static SearchInboundBrandMappingsApiRequest searchRequest() {
        return new SearchInboundBrandMappingsApiRequest(null, null, null, null, 0, 20);
    }

    // ===== InboundBrandMappingResult (Application) =====

    public static InboundBrandMappingResult mappingResult(Long id) {
        return new InboundBrandMappingResult(
                id,
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_INTERNAL_BRAND_ID,
                DEFAULT_STATUS,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static List<InboundBrandMappingResult> mappingResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> mappingResult((long) i))
                .toList();
    }

    public static InboundBrandMappingPageResult pageResult(int count, int page, int size) {
        List<InboundBrandMappingResult> results = mappingResults(count);
        return InboundBrandMappingPageResult.of(results, page, size, count);
    }

    public static InboundBrandMappingPageResult emptyPageResult() {
        return InboundBrandMappingPageResult.of(List.of(), 0, 20, 0);
    }

    // ===== InboundBrandMappingApiResponse =====

    public static InboundBrandMappingApiResponse apiResponse(Long id) {
        return new InboundBrandMappingApiResponse(
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
