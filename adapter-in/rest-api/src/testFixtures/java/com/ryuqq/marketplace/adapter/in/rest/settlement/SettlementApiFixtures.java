package com.ryuqq.marketplace.adapter.in.rest.settlement;

import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.HoldSettlementApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementCompleteBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementEntryListApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementHoldBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementReleaseBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.DailySettlementApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.DailySettlementApiResponse.DiscountApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.DailySettlementApiResponse.FeeApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.DailySettlementApiResponse.MileageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse.HoldInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse.SettlementAmountsApiResponse;
import java.util.List;

/**
 * Settlement API 테스트 Fixtures.
 *
 * <p>Settlement REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SettlementApiFixtures {

    private SettlementApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_SETTLEMENT_ID = "01940001-0000-7000-8000-000000000001";
    public static final String DEFAULT_HOLD_REASON = "추가 확인이 필요합니다";
    public static final long DEFAULT_SELLER_ID = 100L;
    public static final String DEFAULT_ORDER_ITEM_ID = "oi-test-001";
    public static final int DEFAULT_SALES_AMOUNT = 50000;
    public static final int DEFAULT_COMMISSION_AMOUNT = 5000;
    public static final int DEFAULT_COMMISSION_RATE = 1000;
    public static final int DEFAULT_SETTLEMENT_AMOUNT = 45000;

    // ===== HoldSettlementApiRequest =====

    public static HoldSettlementApiRequest holdRequest() {
        return new HoldSettlementApiRequest(DEFAULT_HOLD_REASON);
    }

    public static HoldSettlementApiRequest holdRequest(String reason) {
        return new HoldSettlementApiRequest(reason);
    }

    // ===== SettlementEntryListApiRequest =====

    public static SettlementEntryListApiRequest listRequest() {
        return new SettlementEntryListApiRequest(
                List.of("PENDING"),
                List.of(DEFAULT_SELLER_ID),
                null,
                null,
                "2026-03-01",
                "2026-03-31",
                0,
                20);
    }

    public static SettlementEntryListApiRequest listRequestWithStatus(String status) {
        return new SettlementEntryListApiRequest(
                List.of(status), null, null, null, null, null, 0, 20);
    }

    public static SettlementEntryListApiRequest listRequestNullPageSize() {
        return new SettlementEntryListApiRequest(null, null, null, null, null, null, null, null);
    }

    public static SettlementEntryListApiRequest listRequestWithPageSize(int page, int size) {
        return new SettlementEntryListApiRequest(null, null, null, null, null, null, page, size);
    }

    // ===== SettlementListItemApiResponse =====

    public static SettlementListItemApiResponse pendingListItemResponse() {
        SettlementAmountsApiResponse amounts =
                new SettlementAmountsApiResponse(
                        DEFAULT_SALES_AMOUNT,
                        DEFAULT_COMMISSION_AMOUNT,
                        DEFAULT_COMMISSION_RATE,
                        DEFAULT_SETTLEMENT_AMOUNT,
                        0);
        return new SettlementListItemApiResponse(
                DEFAULT_SETTLEMENT_ID,
                "PENDING",
                DEFAULT_ORDER_ITEM_ID,
                "",
                DEFAULT_SELLER_ID,
                amounts,
                "",
                null,
                "2026-03-26",
                null,
                null);
    }

    public static SettlementListItemApiResponse holdListItemResponse() {
        SettlementAmountsApiResponse amounts =
                new SettlementAmountsApiResponse(
                        DEFAULT_SALES_AMOUNT,
                        DEFAULT_COMMISSION_AMOUNT,
                        DEFAULT_COMMISSION_RATE,
                        DEFAULT_SETTLEMENT_AMOUNT,
                        0);
        HoldInfoApiResponse holdInfo =
                new HoldInfoApiResponse(DEFAULT_HOLD_REASON, "2026-03-19 10:00:00");
        return new SettlementListItemApiResponse(
                DEFAULT_SETTLEMENT_ID,
                "HOLD",
                DEFAULT_ORDER_ITEM_ID,
                "",
                DEFAULT_SELLER_ID,
                amounts,
                "",
                null,
                "2026-03-26",
                null,
                holdInfo);
    }

    public static SettlementListItemApiResponse completedListItemResponse() {
        SettlementAmountsApiResponse amounts =
                new SettlementAmountsApiResponse(
                        DEFAULT_SALES_AMOUNT,
                        DEFAULT_COMMISSION_AMOUNT,
                        DEFAULT_COMMISSION_RATE,
                        DEFAULT_SETTLEMENT_AMOUNT,
                        DEFAULT_SETTLEMENT_AMOUNT);
        return new SettlementListItemApiResponse(
                DEFAULT_SETTLEMENT_ID,
                "COMPLETED",
                DEFAULT_ORDER_ITEM_ID,
                "",
                DEFAULT_SELLER_ID,
                amounts,
                "",
                null,
                "2026-03-26",
                "2026-03-26",
                null);
    }

    // ===== Batch Request Fixtures =====

    public static SettlementCompleteBatchApiRequest completeBatchRequest() {
        return new SettlementCompleteBatchApiRequest(List.of(DEFAULT_SETTLEMENT_ID));
    }

    public static SettlementHoldBatchApiRequest holdBatchRequest() {
        return new SettlementHoldBatchApiRequest(
                List.of(DEFAULT_SETTLEMENT_ID), DEFAULT_HOLD_REASON);
    }

    public static SettlementReleaseBatchApiRequest releaseBatchRequest() {
        return new SettlementReleaseBatchApiRequest(List.of(DEFAULT_SETTLEMENT_ID));
    }

    // ===== DailySettlementApiResponse =====

    public static DailySettlementApiResponse emptyDailyResponse() {
        return new DailySettlementApiResponse(
                null,
                null,
                0L,
                0L,
                0L,
                0,
                DiscountApiResponse.zero(),
                MileageApiResponse.zero(),
                new FeeApiResponse(0),
                0,
                0);
    }

    public static DailySettlementApiResponse dailyResponse() {
        return new DailySettlementApiResponse(
                "2026-03-19",
                "2026-03-26",
                10L,
                8L,
                2L,
                500000,
                DiscountApiResponse.zero(),
                MileageApiResponse.zero(),
                new FeeApiResponse(25000),
                475000,
                475000);
    }
}
