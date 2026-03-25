package com.ryuqq.marketplace.adapter.in.rest.commoncode;

import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.command.ChangeActiveStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.command.RegisterCommonCodeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.command.UpdateCommonCodeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.query.SearchCommonCodesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.response.CommonCodeApiResponse;
import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodePageResult;
import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodeResult;
import java.time.Instant;
import java.util.List;

/**
 * CommonCode API 테스트 Fixtures.
 *
 * <p>CommonCode REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 */
public final class CommonCodeApiFixtures {

    private CommonCodeApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_COMMON_CODE_TYPE_ID = 1L;
    public static final String DEFAULT_CODE = "CREDIT_CARD";
    public static final String DEFAULT_DISPLAY_NAME = "신용카드";
    public static final int DEFAULT_DISPLAY_ORDER = 1;

    // ===== RegisterCommonCodeApiRequest =====

    public static RegisterCommonCodeApiRequest registerRequest() {
        return new RegisterCommonCodeApiRequest(
                DEFAULT_COMMON_CODE_TYPE_ID,
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER);
    }

    public static RegisterCommonCodeApiRequest registerRequest(
            Long commonCodeTypeId, String code, String displayName, int displayOrder) {
        return new RegisterCommonCodeApiRequest(commonCodeTypeId, code, displayName, displayOrder);
    }

    // ===== UpdateCommonCodeApiRequest =====

    public static UpdateCommonCodeApiRequest updateRequest() {
        return new UpdateCommonCodeApiRequest("수정된 표시명", 2);
    }

    public static UpdateCommonCodeApiRequest updateRequest(String displayName, int displayOrder) {
        return new UpdateCommonCodeApiRequest(displayName, displayOrder);
    }

    // ===== ChangeActiveStatusApiRequest =====

    public static ChangeActiveStatusApiRequest activateRequest(Long... ids) {
        return new ChangeActiveStatusApiRequest(List.of(ids), true);
    }

    public static ChangeActiveStatusApiRequest deactivateRequest(Long... ids) {
        return new ChangeActiveStatusApiRequest(List.of(ids), false);
    }

    // ===== SearchCommonCodesPageApiRequest =====

    public static SearchCommonCodesPageApiRequest searchRequest(String commonCodeTypeCode) {
        return new SearchCommonCodesPageApiRequest(commonCodeTypeCode, null, null, null, 0, 20);
    }

    public static SearchCommonCodesPageApiRequest searchRequest(
            String commonCodeTypeCode, Boolean active, int page, int size) {
        return new SearchCommonCodesPageApiRequest(
                commonCodeTypeCode, active, null, null, page, size);
    }

    // ===== CommonCodeResult (Application) =====

    public static CommonCodeResult commonCodeResult(Long id) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new CommonCodeResult(
                id,
                DEFAULT_COMMON_CODE_TYPE_ID,
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                true,
                now,
                now);
    }

    public static CommonCodeResult commonCodeResult(
            Long id, String code, String displayName, int displayOrder, boolean active) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new CommonCodeResult(
                id, DEFAULT_COMMON_CODE_TYPE_ID, code, displayName, displayOrder, active, now, now);
    }

    public static List<CommonCodeResult> commonCodeResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> commonCodeResult((long) i, "CODE_" + i, "표시명_" + i, i, true))
                .toList();
    }

    public static CommonCodePageResult pageResult(int count, int page, int size) {
        List<CommonCodeResult> results = commonCodeResults(count);
        return CommonCodePageResult.of(results, page, size, count);
    }

    public static CommonCodePageResult emptyPageResult() {
        return CommonCodePageResult.empty(20);
    }

    // ===== CommonCodeApiResponse =====

    public static CommonCodeApiResponse apiResponse(Long id) {
        return new CommonCodeApiResponse(
                id,
                DEFAULT_COMMON_CODE_TYPE_ID,
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                true,
                "2025-01-23 10:30:00",
                "2025-01-23 10:30:00");
    }
}
