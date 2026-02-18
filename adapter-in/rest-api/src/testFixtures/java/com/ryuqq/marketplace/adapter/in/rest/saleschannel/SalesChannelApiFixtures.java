package com.ryuqq.marketplace.adapter.in.rest.saleschannel;

import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.command.RegisterSalesChannelApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.command.UpdateSalesChannelApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.query.SearchSalesChannelsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.response.SalesChannelApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.response.SalesChannelIdApiResponse;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelPageResult;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelResult;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * SalesChannel API 테스트 Fixtures.
 *
 * <p>SalesChannel REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SalesChannelApiFixtures {

    private SalesChannelApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final String DEFAULT_CHANNEL_NAME = "쿠팡";
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");
    public static final String DEFAULT_ISO_DATE = "2025-01-23T10:30:00+09:00";

    // ===== RegisterSalesChannelApiRequest =====

    public static RegisterSalesChannelApiRequest registerRequest() {
        return new RegisterSalesChannelApiRequest(DEFAULT_CHANNEL_NAME);
    }

    public static RegisterSalesChannelApiRequest registerRequest(String channelName) {
        return new RegisterSalesChannelApiRequest(channelName);
    }

    // ===== UpdateSalesChannelApiRequest =====

    public static UpdateSalesChannelApiRequest updateRequest() {
        return new UpdateSalesChannelApiRequest(DEFAULT_CHANNEL_NAME, DEFAULT_STATUS);
    }

    public static UpdateSalesChannelApiRequest updateRequest(String channelName, String status) {
        return new UpdateSalesChannelApiRequest(channelName, status);
    }

    // ===== SearchSalesChannelsApiRequest =====

    public static SearchSalesChannelsApiRequest searchRequest() {
        return new SearchSalesChannelsApiRequest(null, null, null, null, null, 0, 20);
    }

    public static SearchSalesChannelsApiRequest searchRequest(
            List<String> statuses, String searchField, String searchWord, int page, int size) {
        return new SearchSalesChannelsApiRequest(
                statuses, searchField, searchWord, "createdAt", "DESC", page, size);
    }

    // ===== SalesChannelResult (Application) =====

    public static SalesChannelResult channelResult(Long id) {
        return new SalesChannelResult(
                id, DEFAULT_CHANNEL_NAME, DEFAULT_STATUS, DEFAULT_INSTANT, DEFAULT_INSTANT);
    }

    public static SalesChannelResult channelResult(Long id, String channelName, String status) {
        return new SalesChannelResult(id, channelName, status, DEFAULT_INSTANT, DEFAULT_INSTANT);
    }

    public static List<SalesChannelResult> channelResults(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> channelResult((long) i, "채널_" + i, DEFAULT_STATUS))
                .toList();
    }

    public static SalesChannelPageResult pageResult(int count, int page, int size) {
        List<SalesChannelResult> results = channelResults(count);
        return SalesChannelPageResult.of(results, page, size, count);
    }

    public static SalesChannelPageResult emptyPageResult() {
        return SalesChannelPageResult.of(List.of(), 0, 20, 0);
    }

    // ===== SalesChannelApiResponse =====

    public static SalesChannelApiResponse apiResponse(Long id) {
        return new SalesChannelApiResponse(
                id, DEFAULT_CHANNEL_NAME, DEFAULT_STATUS, DEFAULT_ISO_DATE, DEFAULT_ISO_DATE);
    }

    public static SalesChannelApiResponse apiResponse(Long id, String channelName, String status) {
        return new SalesChannelApiResponse(
                id, channelName, status, DEFAULT_ISO_DATE, DEFAULT_ISO_DATE);
    }

    public static List<SalesChannelApiResponse> apiResponses(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> apiResponse((long) i)).toList();
    }

    // ===== SalesChannelIdApiResponse =====

    public static SalesChannelIdApiResponse idApiResponse(Long salesChannelId) {
        return SalesChannelIdApiResponse.of(salesChannelId);
    }
}
