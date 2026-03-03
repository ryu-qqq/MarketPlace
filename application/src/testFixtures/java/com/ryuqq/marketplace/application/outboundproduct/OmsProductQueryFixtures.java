package com.ryuqq.marketplace.application.outboundproduct;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsPartnerSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsProductSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsShopSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.SyncHistorySearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductDetailResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductListResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductPageResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryListResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryPageResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncSummaryResult;
import java.time.Instant;
import java.util.List;

/**
 * OmsProduct Application Query 테스트 Fixtures.
 *
 * <p>outboundproduct 관련 Query 파라미터 및 결과 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class OmsProductQueryFixtures {

    private OmsProductQueryFixtures() {}

    // ===== CommonSearchParams =====

    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
    }

    // ===== OmsProductSearchParams =====

    public static OmsProductSearchParams omsProductSearchParams() {
        return new OmsProductSearchParams(
                null,
                List.of(),
                List.of(),
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                defaultCommonSearchParams());
    }

    public static OmsProductSearchParams omsProductSearchParams(int page, int size) {
        return new OmsProductSearchParams(
                null,
                List.of(),
                List.of(),
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                commonSearchParams(page, size));
    }

    public static OmsProductSearchParams omsProductSearchParams(List<String> statuses) {
        return new OmsProductSearchParams(
                null,
                statuses,
                List.of(),
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                defaultCommonSearchParams());
    }

    public static OmsProductSearchParams omsProductSearchParams(
            List<String> statuses, List<String> syncStatuses) {
        return new OmsProductSearchParams(
                null,
                statuses,
                syncStatuses,
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                defaultCommonSearchParams());
    }

    public static OmsProductSearchParams omsProductSearchParamsWithSearch(
            String searchField, String searchWord) {
        return new OmsProductSearchParams(
                null,
                List.of(),
                List.of(),
                searchField,
                searchWord,
                List.of(),
                List.of(),
                List.of(),
                defaultCommonSearchParams());
    }

    public static OmsProductSearchParams omsProductSearchParamsWithShops(List<Long> shopIds) {
        return new OmsProductSearchParams(
                null,
                List.of(),
                List.of(),
                null,
                null,
                shopIds,
                List.of(),
                List.of(),
                defaultCommonSearchParams());
    }

    // ===== SyncHistorySearchParams =====

    public static SyncHistorySearchParams syncHistorySearchParams(long productGroupId) {
        return new SyncHistorySearchParams(productGroupId, null, defaultCommonSearchParams());
    }

    public static SyncHistorySearchParams syncHistorySearchParams(
            long productGroupId, String status) {
        return new SyncHistorySearchParams(productGroupId, status, defaultCommonSearchParams());
    }

    public static SyncHistorySearchParams syncHistorySearchParams(
            long productGroupId, int page, int size) {
        return new SyncHistorySearchParams(productGroupId, null, commonSearchParams(page, size));
    }

    // ===== OmsPartnerSearchParams =====

    public static OmsPartnerSearchParams omsPartnerSearchParams() {
        return OmsPartnerSearchParams.of(null, defaultCommonSearchParams());
    }

    public static OmsPartnerSearchParams omsPartnerSearchParams(String keyword) {
        return OmsPartnerSearchParams.of(keyword, defaultCommonSearchParams());
    }

    public static OmsPartnerSearchParams omsPartnerSearchParams(int page, int size) {
        return OmsPartnerSearchParams.of(null, commonSearchParams(page, size));
    }

    // ===== OmsShopSearchParams =====

    public static OmsShopSearchParams omsShopSearchParams() {
        return OmsShopSearchParams.of(null, defaultCommonSearchParams());
    }

    public static OmsShopSearchParams omsShopSearchParams(String keyword) {
        return OmsShopSearchParams.of(keyword, defaultCommonSearchParams());
    }

    public static OmsShopSearchParams omsShopSearchParams(int page, int size) {
        return OmsShopSearchParams.of(null, commonSearchParams(page, size));
    }

    // ===== OmsProductListResult =====

    public static OmsProductListResult omsProductListResult(long id) {
        Instant now = Instant.now();
        return new OmsProductListResult(
                id,
                "PG-" + id,
                "테스트 상품 " + id,
                "https://cdn.example.com/product/" + id + ".jpg",
                10000,
                100,
                "ACTIVE",
                "판매중",
                "테스트 파트너",
                now,
                "COMPLETED",
                "완료",
                now);
    }

    public static OmsProductListResult omsProductListResult(
            long id, String syncStatus, String status) {
        Instant now = Instant.now();
        return new OmsProductListResult(
                id,
                "PG-" + id,
                "테스트 상품 " + id,
                "https://cdn.example.com/product/" + id + ".jpg",
                15000,
                50,
                status,
                status.equals("ACTIVE") ? "판매중" : "품절",
                "테스트 파트너",
                now,
                syncStatus,
                syncStatus.equals("COMPLETED") ? "완료" : "실패",
                now);
    }

    // ===== OmsProductPageResult =====

    public static OmsProductPageResult omsProductPageResult() {
        List<OmsProductListResult> results =
                List.of(omsProductListResult(1L), omsProductListResult(2L));
        return OmsProductPageResult.of(results, 0, 20, 2L);
    }

    public static OmsProductPageResult omsProductPageResult(int page, int size, long total) {
        List<OmsProductListResult> results = List.of(omsProductListResult(1L));
        return OmsProductPageResult.of(results, page, size, total);
    }

    public static OmsProductPageResult emptyOmsProductPageResult() {
        return OmsProductPageResult.empty(20);
    }

    // ===== SyncHistoryListResult =====

    public static SyncHistoryListResult syncHistoryListResult(long id) {
        Instant now = Instant.now();
        return new SyncHistoryListResult(
                id,
                "테스트 쇼핑몰",
                "test-account-" + id,
                null,
                "COMPLETED",
                "완료",
                0,
                null,
                "EXT-PROD-" + id,
                now,
                now);
    }

    public static SyncHistoryListResult syncHistoryListResult(long id, String status) {
        Instant now = Instant.now();
        return new SyncHistoryListResult(
                id,
                "테스트 쇼핑몰",
                "test-account-" + id,
                null,
                status,
                status.equals("COMPLETED") ? "완료" : "실패",
                status.equals("FAILED") ? 3 : 0,
                status.equals("FAILED") ? "외부 API 오류" : null,
                status.equals("COMPLETED") ? "EXT-PROD-" + id : null,
                now,
                status.equals("COMPLETED") ? now : null);
    }

    // ===== SyncHistoryPageResult =====

    public static SyncHistoryPageResult syncHistoryPageResult(long productGroupId) {
        List<SyncHistoryListResult> results =
                List.of(syncHistoryListResult(1L), syncHistoryListResult(2L));
        return SyncHistoryPageResult.of(results, 0, 20, 2L);
    }

    public static SyncHistoryPageResult emptySyncHistoryPageResult() {
        return SyncHistoryPageResult.empty(20);
    }

    // ===== SyncSummaryResult =====

    public static SyncSummaryResult syncSummaryResult() {
        return new SyncSummaryResult(10L, 8L, 1L, 1L, Instant.now());
    }

    public static SyncSummaryResult syncSummaryResult(
            long total, long success, long fail, long pending) {
        return new SyncSummaryResult(total, success, fail, pending, Instant.now());
    }

    public static SyncSummaryResult emptySyncSummaryResult() {
        return new SyncSummaryResult(0L, 0L, 0L, 0L, null);
    }

    // ===== OmsProductDetailResult =====

    public static OmsProductDetailResult omsProductDetailResult() {
        return new OmsProductDetailResult(null, syncSummaryResult());
    }
}
