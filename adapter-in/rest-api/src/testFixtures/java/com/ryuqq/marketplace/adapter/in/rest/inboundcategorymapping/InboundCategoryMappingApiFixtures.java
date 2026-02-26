package com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping;

import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.command.BatchRegisterInboundCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.command.RegisterInboundCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.command.UpdateInboundCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.query.SearchInboundCategoryMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.response.InboundCategoryMappingApiResponse;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.response.InboundCategoryMappingPageResult;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.response.InboundCategoryMappingResult;
import java.time.Instant;
import java.util.List;

/**
 * InboundCategoryMapping API 테스트 Fixtures.
 *
 * <p>InboundCategoryMapping REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class InboundCategoryMappingApiFixtures {

    private InboundCategoryMappingApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_EXTERNAL_SOURCE_ID = 1L;
    public static final String DEFAULT_EXTERNAL_CATEGORY_CODE = "NV_CAT_001";
    public static final String DEFAULT_EXTERNAL_CATEGORY_NAME = "남성의류";
    public static final Long DEFAULT_INTERNAL_CATEGORY_ID = 1L;
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");
    public static final String DEFAULT_FORMATTED_TIME = "2025-01-23T10:30:00+09:00";

    // ===== Command Requests =====

    public static RegisterInboundCategoryMappingApiRequest registerRequest() {
        return new RegisterInboundCategoryMappingApiRequest(
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_INTERNAL_CATEGORY_ID);
    }

    public static BatchRegisterInboundCategoryMappingApiRequest batchRegisterRequest() {
        return new BatchRegisterInboundCategoryMappingApiRequest(
                List.of(
                        new BatchRegisterInboundCategoryMappingApiRequest.MappingEntryRequest(
                                "NV_CAT_001", "남성의류", 1L),
                        new BatchRegisterInboundCategoryMappingApiRequest.MappingEntryRequest(
                                "NV_CAT_002", "여성의류", 2L)));
    }

    public static UpdateInboundCategoryMappingApiRequest updateRequest() {
        return new UpdateInboundCategoryMappingApiRequest(
                DEFAULT_EXTERNAL_CATEGORY_NAME, DEFAULT_INTERNAL_CATEGORY_ID, DEFAULT_STATUS);
    }

    // ===== Search Request =====

    public static SearchInboundCategoryMappingsApiRequest searchRequest() {
        return new SearchInboundCategoryMappingsApiRequest(null, null, null, null, 0, 20);
    }

    // ===== InboundCategoryMappingResult (Application) =====

    public static InboundCategoryMappingResult mappingResult(Long id) {
        return new InboundCategoryMappingResult(
                id,
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_INTERNAL_CATEGORY_ID,
                DEFAULT_STATUS,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static List<InboundCategoryMappingResult> mappingResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> mappingResult((long) i))
                .toList();
    }

    public static InboundCategoryMappingPageResult pageResult(int count, int page, int size) {
        List<InboundCategoryMappingResult> results = mappingResults(count);
        return InboundCategoryMappingPageResult.of(results, page, size, count);
    }

    public static InboundCategoryMappingPageResult emptyPageResult() {
        return InboundCategoryMappingPageResult.of(List.of(), 0, 20, 0);
    }

    // ===== InboundCategoryMappingApiResponse =====

    public static InboundCategoryMappingApiResponse apiResponse(Long id) {
        return new InboundCategoryMappingApiResponse(
                id,
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_INTERNAL_CATEGORY_ID,
                DEFAULT_STATUS,
                DEFAULT_FORMATTED_TIME,
                DEFAULT_FORMATTED_TIME);
    }
}
