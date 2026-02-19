package com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping;

import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.command.BatchRegisterExternalCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.command.RegisterExternalCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.command.UpdateExternalCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.query.SearchExternalCategoryMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.response.ExternalCategoryMappingApiResponse;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.response.ExternalCategoryMappingPageResult;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.response.ExternalCategoryMappingResult;
import java.time.Instant;
import java.util.List;

/**
 * ExternalCategoryMapping API 테스트 Fixtures.
 *
 * <p>ExternalCategoryMapping REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ExternalCategoryMappingApiFixtures {

    private ExternalCategoryMappingApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_EXTERNAL_SOURCE_ID = 1L;
    public static final String DEFAULT_EXTERNAL_CATEGORY_CODE = "NV_CAT_001";
    public static final String DEFAULT_EXTERNAL_CATEGORY_NAME = "남성의류";
    public static final Long DEFAULT_INTERNAL_CATEGORY_ID = 1L;
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");
    public static final String DEFAULT_FORMATTED_TIME = "2025-01-23T10:30:00+09:00";

    // ===== Command Requests =====

    public static RegisterExternalCategoryMappingApiRequest registerRequest() {
        return new RegisterExternalCategoryMappingApiRequest(
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_INTERNAL_CATEGORY_ID);
    }

    public static BatchRegisterExternalCategoryMappingApiRequest batchRegisterRequest() {
        return new BatchRegisterExternalCategoryMappingApiRequest(
                List.of(
                        new BatchRegisterExternalCategoryMappingApiRequest.MappingEntryRequest(
                                "NV_CAT_001", "남성의류", 1L),
                        new BatchRegisterExternalCategoryMappingApiRequest.MappingEntryRequest(
                                "NV_CAT_002", "여성의류", 2L)));
    }

    public static UpdateExternalCategoryMappingApiRequest updateRequest() {
        return new UpdateExternalCategoryMappingApiRequest(
                DEFAULT_EXTERNAL_CATEGORY_NAME, DEFAULT_INTERNAL_CATEGORY_ID, DEFAULT_STATUS);
    }

    // ===== Search Request =====

    public static SearchExternalCategoryMappingsApiRequest searchRequest() {
        return new SearchExternalCategoryMappingsApiRequest(null, null, null, null, 0, 20);
    }

    // ===== ExternalCategoryMappingResult (Application) =====

    public static ExternalCategoryMappingResult mappingResult(Long id) {
        return new ExternalCategoryMappingResult(
                id,
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_INTERNAL_CATEGORY_ID,
                DEFAULT_STATUS,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static List<ExternalCategoryMappingResult> mappingResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> mappingResult((long) i))
                .toList();
    }

    public static ExternalCategoryMappingPageResult pageResult(int count, int page, int size) {
        List<ExternalCategoryMappingResult> results = mappingResults(count);
        return ExternalCategoryMappingPageResult.of(results, page, size, count);
    }

    public static ExternalCategoryMappingPageResult emptyPageResult() {
        return ExternalCategoryMappingPageResult.of(List.of(), 0, 20, 0);
    }

    // ===== ExternalCategoryMappingApiResponse =====

    public static ExternalCategoryMappingApiResponse apiResponse(Long id) {
        return new ExternalCategoryMappingApiResponse(
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
