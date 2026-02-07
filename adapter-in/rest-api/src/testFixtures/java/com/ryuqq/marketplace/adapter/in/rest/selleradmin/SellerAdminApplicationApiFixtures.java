package com.ryuqq.marketplace.adapter.in.rest.selleradmin;

import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.ApplySellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.BulkApproveSellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.BulkRejectSellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.ChangeSellerAdminPasswordApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.ApplySellerAdminApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.ApproveSellerAdminApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.BulkApproveSellerAdminApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.BulkApproveSellerAdminApiResponse.ItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import java.util.List;

/**
 * SellerAdminApplication API 테스트 Fixtures.
 *
 * <p>SellerAdminApplication REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 */
public final class SellerAdminApplicationApiFixtures {

    private SellerAdminApplicationApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_LOGIN_ID = "admin@example.com";
    public static final String DEFAULT_NAME = "홍길동";
    public static final String DEFAULT_PHONE_NUMBER = "010-1234-5678";
    public static final String DEFAULT_PASSWORD = "Password123!";
    public static final String DEFAULT_SELLER_ADMIN_ID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";
    public static final String DEFAULT_SELLER_ADMIN_ID_2 = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f61";
    public static final String DEFAULT_SELLER_ADMIN_ID_3 = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f62";

    // ===== ApplySellerAdminApiRequest =====

    public static ApplySellerAdminApiRequest applyRequest() {
        return new ApplySellerAdminApiRequest(
                DEFAULT_SELLER_ID,
                DEFAULT_LOGIN_ID,
                DEFAULT_NAME,
                DEFAULT_PHONE_NUMBER,
                DEFAULT_PASSWORD);
    }

    public static ApplySellerAdminApiRequest applyRequest(
            Long sellerId, String loginId, String name, String phoneNumber, String password) {
        return new ApplySellerAdminApiRequest(sellerId, loginId, name, phoneNumber, password);
    }

    // ===== BulkApproveSellerAdminApiRequest =====

    public static BulkApproveSellerAdminApiRequest bulkApproveRequest(String... sellerAdminIds) {
        return new BulkApproveSellerAdminApiRequest(List.of(sellerAdminIds));
    }

    public static BulkApproveSellerAdminApiRequest bulkApproveRequest() {
        return bulkApproveRequest(DEFAULT_SELLER_ADMIN_ID, DEFAULT_SELLER_ADMIN_ID_2);
    }

    // ===== BulkRejectSellerAdminApiRequest =====

    public static BulkRejectSellerAdminApiRequest bulkRejectRequest(String... sellerAdminIds) {
        return new BulkRejectSellerAdminApiRequest(List.of(sellerAdminIds));
    }

    public static BulkRejectSellerAdminApiRequest bulkRejectRequest() {
        return bulkRejectRequest(DEFAULT_SELLER_ADMIN_ID, DEFAULT_SELLER_ADMIN_ID_2);
    }

    // ===== ChangeSellerAdminPasswordApiRequest =====

    public static ChangeSellerAdminPasswordApiRequest changePasswordRequest() {
        return new ChangeSellerAdminPasswordApiRequest("NewPass123!");
    }

    public static ChangeSellerAdminPasswordApiRequest changePasswordRequest(String newPassword) {
        return new ChangeSellerAdminPasswordApiRequest(newPassword);
    }

    // ===== BatchProcessingResult (Application) =====

    public static BatchProcessingResult<String> allSuccessBatchResult(String... ids) {
        List<BatchItemResult<String>> items =
                List.of(ids).stream().map(BatchItemResult::success).toList();
        return BatchProcessingResult.from(items);
    }

    public static BatchProcessingResult<String> partialFailureBatchResult(
            String successId, String failureId) {
        List<BatchItemResult<String>> items =
                List.of(
                        BatchItemResult.success(successId),
                        BatchItemResult.failure(failureId, "SELADM-003", "이미 처리된 신청입니다"));
        return BatchProcessingResult.from(items);
    }

    // ===== ApplySellerAdminApiResponse =====

    public static ApplySellerAdminApiResponse applyResponse() {
        return new ApplySellerAdminApiResponse(DEFAULT_SELLER_ADMIN_ID);
    }

    public static ApplySellerAdminApiResponse applyResponse(String sellerAdminId) {
        return new ApplySellerAdminApiResponse(sellerAdminId);
    }

    // ===== ApproveSellerAdminApiResponse =====

    public static ApproveSellerAdminApiResponse approveResponse() {
        return new ApproveSellerAdminApiResponse(DEFAULT_SELLER_ADMIN_ID);
    }

    public static ApproveSellerAdminApiResponse approveResponse(String sellerAdminId) {
        return new ApproveSellerAdminApiResponse(sellerAdminId);
    }

    // ===== BulkApproveSellerAdminApiResponse =====

    public static BulkApproveSellerAdminApiResponse bulkApproveAllSuccessResponse(String... ids) {
        List<ItemResult> items =
                List.of(ids).stream().map(id -> new ItemResult(id, true, null, null)).toList();
        return new BulkApproveSellerAdminApiResponse(ids.length, ids.length, 0, items);
    }

    public static BulkApproveSellerAdminApiResponse bulkApprovePartialFailureResponse(
            String successId, String failureId) {
        List<ItemResult> items =
                List.of(
                        new ItemResult(successId, true, null, null),
                        new ItemResult(failureId, false, "SELADM-003", "이미 처리된 신청입니다"));
        return new BulkApproveSellerAdminApiResponse(2, 1, 1, items);
    }

    // ===== SellerAdminApplicationResult (Application) =====

    public static com.ryuqq.marketplace.application.selleradmin.dto.response
                    .SellerAdminApplicationResult
            applicationResult(String sellerAdminId) {
        java.time.Instant now = java.time.Instant.parse("2025-02-05T01:30:00Z");
        return new com.ryuqq.marketplace.application.selleradmin.dto.response
                .SellerAdminApplicationResult(
                sellerAdminId,
                DEFAULT_SELLER_ID,
                DEFAULT_LOGIN_ID,
                DEFAULT_NAME,
                DEFAULT_PHONE_NUMBER,
                "PENDING_APPROVAL",
                null,
                now,
                now);
    }

    public static com.ryuqq.marketplace.application.selleradmin.dto.response
                    .SellerAdminApplicationResult
            applicationResult(String sellerAdminId, String status) {
        java.time.Instant now = java.time.Instant.parse("2025-02-05T01:30:00Z");
        return new com.ryuqq.marketplace.application.selleradmin.dto.response
                .SellerAdminApplicationResult(
                sellerAdminId,
                DEFAULT_SELLER_ID,
                DEFAULT_LOGIN_ID,
                DEFAULT_NAME,
                DEFAULT_PHONE_NUMBER,
                status,
                null,
                now,
                now);
    }

    public static List<
                    com.ryuqq.marketplace.application.selleradmin.dto.response
                            .SellerAdminApplicationResult>
            applicationResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(
                        i ->
                                applicationResult(
                                        String.format(
                                                "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f%02d", 60 + i),
                                        "PENDING_APPROVAL"))
                .toList();
    }

    public static com.ryuqq.marketplace.application.selleradmin.dto.response
                    .SellerAdminApplicationPageResult
            pageResult(int count, int page, int size) {
        List<
                        com.ryuqq.marketplace.application.selleradmin.dto.response
                                .SellerAdminApplicationResult>
                results = applicationResults(count);
        return com.ryuqq.marketplace.application.selleradmin.dto.response
                .SellerAdminApplicationPageResult.of(results, page, size, count);
    }

    public static com.ryuqq.marketplace.application.selleradmin.dto.response
                    .SellerAdminApplicationPageResult
            emptyPageResult() {
        return com.ryuqq.marketplace.application.selleradmin.dto.response
                .SellerAdminApplicationPageResult.of(List.of(), 0, 20, 0);
    }

    // ===== SellerAdminApplicationApiResponse (API) =====

    public static com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response
                    .SellerAdminApplicationApiResponse
            apiApplicationResponse(String sellerAdminId) {
        java.time.Instant now = java.time.Instant.parse("2025-02-05T01:30:00Z");
        return new com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response
                .SellerAdminApplicationApiResponse(
                sellerAdminId,
                DEFAULT_SELLER_ID,
                DEFAULT_LOGIN_ID,
                DEFAULT_NAME,
                DEFAULT_PHONE_NUMBER,
                "PENDING_APPROVAL",
                null,
                now,
                now);
    }
}
