package com.ryuqq.marketplace.application.saleschannel;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.saleschannel.dto.query.SalesChannelSearchParams;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelPageResult;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelResult;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import java.util.List;

/**
 * SalesChannel Query 테스트 Fixtures.
 *
 * <p>SalesChannel 관련 Query 파라미터 및 Result 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SalesChannelQueryFixtures {

    private SalesChannelQueryFixtures() {}

    // ===== SearchParams Fixtures =====

    public static SalesChannelSearchParams searchParams() {
        return SalesChannelSearchParams.of(null, null, null, defaultCommonSearchParams());
    }

    public static SalesChannelSearchParams searchParams(int page, int size) {
        return SalesChannelSearchParams.of(null, null, null, commonSearchParams(page, size));
    }

    public static SalesChannelSearchParams searchParams(List<String> statuses) {
        return SalesChannelSearchParams.of(statuses, null, null, defaultCommonSearchParams());
    }

    public static SalesChannelSearchParams searchParams(String searchField, String searchWord) {
        return SalesChannelSearchParams.of(
                null, searchField, searchWord, defaultCommonSearchParams());
    }

    public static SalesChannelSearchParams searchParams(
            List<String> statuses, String searchField, String searchWord, int page, int size) {
        return SalesChannelSearchParams.of(
                statuses, searchField, searchWord, commonSearchParams(page, size));
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
    }

    // ===== Result Fixtures =====

    public static SalesChannelResult salesChannelResult(Long id) {
        Instant now = CommonVoFixtures.now();
        return new SalesChannelResult(id, "테스트 판매채널", "ACTIVE", now, now);
    }

    public static SalesChannelResult salesChannelResult(Long id, String channelName) {
        Instant now = CommonVoFixtures.now();
        return new SalesChannelResult(id, channelName, "ACTIVE", now, now);
    }

    public static SalesChannelResult salesChannelResult(
            Long id, String channelName, String status) {
        Instant now = CommonVoFixtures.now();
        return new SalesChannelResult(id, channelName, status, now, now);
    }

    // ===== PageResult Fixtures =====

    public static SalesChannelPageResult salesChannelPageResult() {
        List<SalesChannelResult> results = List.of(salesChannelResult(1L), salesChannelResult(2L));
        return SalesChannelPageResult.of(results, 0, 20, 2L);
    }

    public static SalesChannelPageResult salesChannelPageResult(
            int page, int size, long totalCount) {
        List<SalesChannelResult> results = List.of(salesChannelResult(1L), salesChannelResult(2L));
        return SalesChannelPageResult.of(results, page, size, totalCount);
    }

    public static SalesChannelPageResult emptyPageResult() {
        return SalesChannelPageResult.of(List.of(), 0, 20, 0L);
    }
}
