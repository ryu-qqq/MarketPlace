package com.ryuqq.marketplace.adapter.in.rest.commoncodetype;

import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command.ChangeActiveStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command.RegisterCommonCodeTypeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command.UpdateCommonCodeTypeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.query.SearchCommonCodeTypesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.response.CommonCodeTypeApiResponse;
import com.ryuqq.marketplace.application.commoncodetype.dto.response.CommonCodeTypePageResult;
import com.ryuqq.marketplace.application.commoncodetype.dto.response.CommonCodeTypeResult;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * CommonCodeType API 테스트 Fixtures.
 *
 * <p>CommonCodeType REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class CommonCodeTypeApiFixtures {

    private CommonCodeTypeApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_CODE = "PAYMENT_METHOD";
    public static final String DEFAULT_NAME = "결제수단";
    public static final String DEFAULT_DESCRIPTION = "결제 시 사용 가능한 결제수단 목록";
    public static final int DEFAULT_DISPLAY_ORDER = 1;
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");

    // ===== RegisterCommonCodeTypeApiRequest =====

    public static RegisterCommonCodeTypeApiRequest registerRequest() {
        return new RegisterCommonCodeTypeApiRequest(
                DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_DISPLAY_ORDER);
    }

    public static RegisterCommonCodeTypeApiRequest registerRequest(
            String code, String name, String description, int displayOrder) {
        return new RegisterCommonCodeTypeApiRequest(code, name, description, displayOrder);
    }

    // ===== UpdateCommonCodeTypeApiRequest =====

    public static UpdateCommonCodeTypeApiRequest updateRequest() {
        return new UpdateCommonCodeTypeApiRequest("수정된 결제수단", "수정된 설명입니다.", 2);
    }

    // ===== ChangeActiveStatusApiRequest =====

    public static ChangeActiveStatusApiRequest changeActiveStatusRequest() {
        return new ChangeActiveStatusApiRequest(List.of(1L, 2L, 3L), false);
    }

    // ===== SearchCommonCodeTypesPageApiRequest =====

    public static SearchCommonCodeTypesPageApiRequest searchRequest() {
        return new SearchCommonCodeTypesPageApiRequest(
                null, null, null, null, null, null, null, null);
    }

    public static SearchCommonCodeTypesPageApiRequest searchRequest(
            Boolean active,
            String searchField,
            String searchWord,
            String type,
            String sortKey,
            String sortDirection,
            Integer page,
            Integer size) {
        return new SearchCommonCodeTypesPageApiRequest(
                active, searchField, searchWord, type, sortKey, sortDirection, page, size);
    }

    // ===== CommonCodeTypeResult (Application) =====

    public static CommonCodeTypeResult codeTypeResult(Long id) {
        return new CommonCodeTypeResult(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_DISPLAY_ORDER,
                true,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static CommonCodeTypeResult codeTypeResult(
            Long id, String code, String name, boolean active) {
        return new CommonCodeTypeResult(
                id,
                code,
                name,
                DEFAULT_DESCRIPTION,
                DEFAULT_DISPLAY_ORDER,
                active,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static List<CommonCodeTypeResult> codeTypeResults(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> codeTypeResult((long) i, "CODE_" + i, "이름_" + i, true))
                .toList();
    }

    public static CommonCodeTypePageResult pageResult(int count, int page, int size) {
        List<CommonCodeTypeResult> results = codeTypeResults(count);
        return CommonCodeTypePageResult.of(results, page, size, count);
    }

    public static CommonCodeTypePageResult emptyPageResult() {
        return CommonCodeTypePageResult.empty(20);
    }

    // ===== CommonCodeTypeApiResponse =====

    public static CommonCodeTypeApiResponse apiResponse(Long id) {
        return new CommonCodeTypeApiResponse(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_DISPLAY_ORDER,
                true,
                "2025-01-23 10:30:00",
                "2025-01-23 10:30:00");
    }

    public static List<CommonCodeTypeApiResponse> apiResponses(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> apiResponse((long) i)).toList();
    }
}
