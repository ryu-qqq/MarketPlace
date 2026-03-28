package com.ryuqq.marketplace.adapter.in.rest.settlement.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.SettlementApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.DailySettlementApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementCompleteBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementEntryListApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementHoldBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementReleaseBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.DailySettlementApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse;
import com.ryuqq.marketplace.application.settlement.SettlementEntryQueryFixtures;
import com.ryuqq.marketplace.application.settlement.dto.query.DailySettlementSearchParams;
import com.ryuqq.marketplace.application.settlement.dto.response.DailySettlementResult;
import com.ryuqq.marketplace.application.settlement.dto.response.SettlementEntryPageResult;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.CompleteSettlementEntryBatchCommand;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.HoldSettlementEntryBatchCommand;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.ReleaseSettlementEntryBatchCommand;
import com.ryuqq.marketplace.application.settlement.entry.dto.query.SettlementEntrySearchParams;
import com.ryuqq.marketplace.application.settlement.entry.dto.response.SettlementEntryListResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementApiMapper 단위 테스트")
class SettlementApiMapperTest {

    private SettlementApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SettlementApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 정산 목록 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("COMPLETED 상태를 CONFIRMED + SETTLED로 확장 변환한다")
        void toSearchParams_CompletedStatus_ExpandsToConfirmedAndSettled() {
            // given
            SettlementEntryListApiRequest request =
                    SettlementApiFixtures.listRequestWithStatus("COMPLETED");

            // when
            SettlementEntrySearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.statuses()).containsExactlyInAnyOrder("CONFIRMED", "SETTLED");
        }

        @Test
        @DisplayName("PENDING 상태는 그대로 변환한다")
        void toSearchParams_PendingStatus_KeepsAsPending() {
            // given
            SettlementEntryListApiRequest request =
                    SettlementApiFixtures.listRequestWithStatus("PENDING");

            // when
            SettlementEntrySearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.statuses()).containsExactly("PENDING");
        }

        @Test
        @DisplayName("page/size가 null이면 기본값(0, 20)으로 변환한다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            SettlementEntryListApiRequest request = SettlementApiFixtures.listRequestNullPageSize();

            // when
            SettlementEntrySearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("page/size가 명시된 경우 해당 값으로 변환한다")
        void toSearchParams_ExplicitPageSize_MapsCorrectly() {
            // given
            SettlementEntryListApiRequest request =
                    SettlementApiFixtures.listRequestWithPageSize(2, 50);

            // when
            SettlementEntrySearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.page()).isEqualTo(2);
            assertThat(result.size()).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 결과 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("SettlementEntryPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            SettlementEntryListResult entry =
                    SettlementEntryQueryFixtures.settlementEntryListResult("entry-001");
            PageMeta pageMeta = new PageMeta(0, 20, 1L, 1);
            SettlementEntryPageResult pageResult =
                    new SettlementEntryPageResult(List.of(entry), pageMeta);

            // when
            PageApiResponse<SettlementListItemApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(1);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("빈 결과이면 빈 페이지 응답을 반환한다")
        void toPageResponse_EmptyResult_ReturnsEmptyPage() {
            // given
            PageMeta pageMeta = new PageMeta(0, 20, 0L, 0);
            SettlementEntryPageResult pageResult =
                    new SettlementEntryPageResult(List.of(), pageMeta);

            // when
            PageApiResponse<SettlementListItemApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("toListItemResponse() - 목록 단건 변환 (상태 매핑 검증)")
    class ToListItemResponseTest {

        @Test
        @DisplayName("내부 상태 CONFIRMED는 API 상태 COMPLETED로 매핑된다")
        void toListItemResponse_ConfirmedStatus_MapsToCompleted() {
            // given
            SettlementEntryListResult entry =
                    SettlementEntryQueryFixtures.confirmedEntryListResult("entry-001");
            PageMeta pageMeta = new PageMeta(0, 20, 1L, 1);
            SettlementEntryPageResult pageResult =
                    new SettlementEntryPageResult(List.of(entry), pageMeta);

            // when
            PageApiResponse<SettlementListItemApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content().get(0).status()).isEqualTo("COMPLETED");
        }

        @Test
        @DisplayName("PENDING 상태이면 holdInfo가 null이다")
        void toListItemResponse_PendingStatus_HoldInfoIsNull() {
            // given
            SettlementEntryListResult entry =
                    SettlementEntryQueryFixtures.settlementEntryListResult("entry-001");
            PageMeta pageMeta = new PageMeta(0, 20, 1L, 1);
            SettlementEntryPageResult pageResult =
                    new SettlementEntryPageResult(List.of(entry), pageMeta);

            // when
            PageApiResponse<SettlementListItemApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content().get(0).holdInfo()).isNull();
        }

        @Test
        @DisplayName("HOLD 상태이면 holdInfo가 포함된다")
        void toListItemResponse_HoldStatus_HoldInfoIncluded() {
            // given
            SettlementEntryListResult holdEntry =
                    new SettlementEntryListResult(
                            "entry-002",
                            "HOLD",
                            100L,
                            "SALES",
                            9001L,
                            50000,
                            1000,
                            5000,
                            45000,
                            null,
                            null,
                            Instant.now().plusSeconds(604800),
                            "보류 사유 테스트",
                            Instant.now(),
                            Instant.now());
            PageMeta pageMeta = new PageMeta(0, 20, 1L, 1);
            SettlementEntryPageResult pageResult =
                    new SettlementEntryPageResult(List.of(holdEntry), pageMeta);

            // when
            PageApiResponse<SettlementListItemApiResponse> response =
                    mapper.toPageResponse(pageResult);
            SettlementListItemApiResponse item = response.content().get(0);

            // then
            assertThat(item.status()).isEqualTo("HOLD");
            assertThat(item.holdInfo()).isNotNull();
            assertThat(item.holdInfo().holdReason()).isEqualTo("보류 사유 테스트");
        }

        @Test
        @DisplayName("orderItemId가 orderId로 매핑된다 (V4 간극 규칙)")
        void toListItemResponse_OrderItemIdMappedAsOrderId() {
            // given
            SettlementEntryListResult entry =
                    SettlementEntryQueryFixtures.settlementEntryListResult("entry-001");
            PageMeta pageMeta = new PageMeta(0, 20, 1L, 1);
            SettlementEntryPageResult pageResult =
                    new SettlementEntryPageResult(List.of(entry), pageMeta);

            // when
            PageApiResponse<SettlementListItemApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content().get(0).orderId()).isEqualTo(String.valueOf(1001L));
        }
    }

    @Nested
    @DisplayName("toDailySearchParams() - 일별 조회 요청 변환")
    class ToDailySearchParamsTest {

        @Test
        @DisplayName("String 날짜를 LocalDate로 변환한다")
        void toDailySearchParams_StringDate_ConvertsToLocalDate() {
            // given
            DailySettlementApiRequest request =
                    new DailySettlementApiRequest("2026-03-01", "2026-03-31", null, 0, 20);

            // when
            DailySettlementSearchParams result = mapper.toDailySearchParams(request);

            // then
            assertThat(result.startDate()).isEqualTo(LocalDate.of(2026, 3, 1));
            assertThat(result.endDate()).isEqualTo(LocalDate.of(2026, 3, 31));
        }

        @Test
        @DisplayName("sellerIds가 null이면 빈 목록으로 변환한다")
        void toDailySearchParams_NullSellerIds_UsesEmptyList() {
            // given
            DailySettlementApiRequest request =
                    new DailySettlementApiRequest("2026-03-01", "2026-03-31", null, 0, 20);

            // when
            DailySettlementSearchParams result = mapper.toDailySearchParams(request);

            // then
            assertThat(result.sellerIds()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toDailyPageResponse() - 일별 집계 결과 변환")
    class ToDailyPageResponseTest {

        @Test
        @DisplayName("DailySettlementResult를 DailySettlementApiResponse로 변환한다")
        void toDailyPageResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            DailySettlementResult result =
                    new DailySettlementResult(
                            LocalDate.of(2026, 3, 19), 10L, 500000, 25000, 475000);
            List<DailySettlementResult> results = List.of(result);

            // when
            PageApiResponse<DailySettlementApiResponse> response =
                    mapper.toDailyPageResponse(results, 0, 20);

            // then
            assertThat(response.content()).hasSize(1);
            DailySettlementApiResponse item = response.content().get(0);
            assertThat(item.settlementDay()).isEqualTo("2026-03-19");
            assertThat(item.orderCount()).isEqualTo(10L);
            assertThat(item.totalSalesAmount()).isEqualTo(500000);
        }

        @Test
        @DisplayName("discount/mileage 필드는 0으로 채워진다")
        void toDailyPageResponse_DiscountMileageAreZero() {
            // given
            DailySettlementResult result =
                    new DailySettlementResult(LocalDate.of(2026, 3, 19), 5L, 100000, 10000, 90000);
            List<DailySettlementResult> results = List.of(result);

            // when
            PageApiResponse<DailySettlementApiResponse> response =
                    mapper.toDailyPageResponse(results, 0, 20);
            DailySettlementApiResponse item = response.content().get(0);

            // then
            assertThat(item.discount().totalAmount()).isZero();
            assertThat(item.discount().sellerAmount()).isZero();
            assertThat(item.discount().platformAmount()).isZero();
            assertThat(item.mileage().totalAmount()).isZero();
            assertThat(item.mileage().sellerAmount()).isZero();
            assertThat(item.mileage().platformAmount()).isZero();
        }
    }

    @Nested
    @DisplayName("Batch 커맨드 변환")
    class BatchCommandTest {

        @Test
        @DisplayName("toCompleteBatchCommand: SettlementCompleteBatchApiRequest를 커맨드로 변환한다")
        void toCompleteBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            SettlementCompleteBatchApiRequest request =
                    SettlementApiFixtures.completeBatchRequest();

            // when
            CompleteSettlementEntryBatchCommand command = mapper.toCompleteBatchCommand(request);

            // then
            assertThat(command.entryIds())
                    .containsExactly(SettlementApiFixtures.DEFAULT_SETTLEMENT_ID);
        }

        @Test
        @DisplayName("toHoldBatchCommand: SettlementHoldBatchApiRequest를 커맨드로 변환한다")
        void toHoldBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            SettlementHoldBatchApiRequest request = SettlementApiFixtures.holdBatchRequest();

            // when
            HoldSettlementEntryBatchCommand command = mapper.toHoldBatchCommand(request);

            // then
            assertThat(command.entryIds())
                    .containsExactly(SettlementApiFixtures.DEFAULT_SETTLEMENT_ID);
            assertThat(command.holdReason()).isEqualTo(SettlementApiFixtures.DEFAULT_HOLD_REASON);
        }

        @Test
        @DisplayName("toReleaseBatchCommand: SettlementReleaseBatchApiRequest를 커맨드로 변환한다")
        void toReleaseBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            SettlementReleaseBatchApiRequest request = SettlementApiFixtures.releaseBatchRequest();

            // when
            ReleaseSettlementEntryBatchCommand command = mapper.toReleaseBatchCommand(request);

            // then
            assertThat(command.entryIds())
                    .containsExactly(SettlementApiFixtures.DEFAULT_SETTLEMENT_ID);
        }
    }
}
