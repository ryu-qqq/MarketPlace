package com.ryuqq.marketplace.adapter.in.rest.externalsource;

import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.command.RegisterExternalSourceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.command.UpdateExternalSourceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.query.SearchExternalSourcesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.response.ExternalSourceApiResponse;
import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourcePageResult;
import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourceResult;
import java.time.Instant;
import java.util.List;

/**
 * ExternalSource API 테스트 Fixtures.
 *
 * <p>ExternalSource REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ExternalSourceApiFixtures {

    private ExternalSourceApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_CODE = "NAVER_COMMERCE";
    public static final String DEFAULT_NAME = "네이버 커머스";
    public static final String DEFAULT_TYPE = "SALES_CHANNEL";
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final String DEFAULT_DESCRIPTION = "네이버 커머스 연동";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");
    public static final String DEFAULT_FORMATTED_TIME = "2025-01-23T10:30:00+09:00";

    // ===== Command Requests =====

    public static RegisterExternalSourceApiRequest registerRequest() {
        return new RegisterExternalSourceApiRequest(
                DEFAULT_CODE, DEFAULT_NAME, DEFAULT_TYPE, DEFAULT_DESCRIPTION);
    }

    public static UpdateExternalSourceApiRequest updateRequest() {
        return new UpdateExternalSourceApiRequest(
                DEFAULT_NAME, DEFAULT_STATUS, DEFAULT_DESCRIPTION);
    }

    // ===== Search Request =====

    public static SearchExternalSourcesApiRequest searchRequest() {
        return new SearchExternalSourcesApiRequest(null, null, null, null, null, null, 0, 20);
    }

    // ===== ExternalSourceResult (Application) =====

    public static ExternalSourceResult sourceResult(Long id) {
        return new ExternalSourceResult(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_TYPE,
                DEFAULT_STATUS,
                DEFAULT_DESCRIPTION,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static List<ExternalSourceResult> sourceResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> sourceResult((long) i))
                .toList();
    }

    public static ExternalSourcePageResult pageResult(int count, int page, int size) {
        List<ExternalSourceResult> results = sourceResults(count);
        return ExternalSourcePageResult.of(results, page, size, count);
    }

    public static ExternalSourcePageResult emptyPageResult() {
        return ExternalSourcePageResult.of(List.of(), 0, 20, 0);
    }

    // ===== ExternalSourceApiResponse =====

    public static ExternalSourceApiResponse apiResponse(Long id) {
        return new ExternalSourceApiResponse(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_TYPE,
                DEFAULT_STATUS,
                DEFAULT_DESCRIPTION,
                DEFAULT_FORMATTED_TIME,
                DEFAULT_FORMATTED_TIME);
    }
}
