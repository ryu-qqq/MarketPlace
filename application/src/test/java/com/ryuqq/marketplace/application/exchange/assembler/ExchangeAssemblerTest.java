package com.ryuqq.marketplace.application.exchange.assembler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.claimhistory.assembler.ClaimHistoryAssembler;
import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeDetailResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeListResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangePageResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeSummaryResult;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@Tag("unit")
@DisplayName("ExchangeAssembler 단위 테스트")
class ExchangeAssemblerTest {

    private ClaimHistoryAssembler historyAssembler;
    private ExchangeAssembler sut;

    @BeforeEach
    void setUp() {
        historyAssembler = Mockito.mock(ClaimHistoryAssembler.class);
        sut = new ExchangeAssembler(historyAssembler);
    }

    @Nested
    @DisplayName("toListResult() - ExchangeClaim → ExchangeListResult 변환")
    class ToListResultTest {

        @Test
        @DisplayName("REQUESTED 상태 클레임을 ExchangeListResult로 변환한다")
        void toListResult_RequestedClaim_ReturnsListResult() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();

            // when
            ExchangeListResult result = sut.toListResult(claim);

            // then
            assertThat(result).isNotNull();
            assertThat(result.exchangeClaimId()).isEqualTo(claim.idValue());
            assertThat(result.claimNumber()).isEqualTo(claim.claimNumberValue());
            assertThat(result.orderItemId()).isEqualTo(claim.orderItemIdValue());
            assertThat(result.exchangeQty()).isEqualTo(claim.exchangeQty());
            assertThat(result.exchangeStatus()).isEqualTo("REQUESTED");
            assertThat(result.reasonType()).isEqualTo(claim.reason().reasonType().name());
            assertThat(result.reasonDetail()).isEqualTo(claim.reason().reasonDetail());
            assertThat(result.requestedBy()).isEqualTo(claim.requestedBy());
        }

        @Test
        @DisplayName("교환 옵션이 있는 클레임을 변환하면 targetSkuCode가 포함된다")
        void toListResult_ClaimWithOption_IncludesTargetSkuCode() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();

            // when
            ExchangeListResult result = sut.toListResult(claim);

            // then
            assertThat(result.targetSkuCode()).isEqualTo(claim.exchangeOption().targetSkuCode());
            assertThat(result.targetQuantity()).isEqualTo(claim.exchangeOption().quantity());
        }
    }

    @Nested
    @DisplayName("toDetailResult() - ExchangeClaim + Histories → ExchangeDetailResult 변환")
    class ToDetailResultTest {

        @Test
        @DisplayName("클레임과 이력 목록으로 ExchangeDetailResult를 생성한다")
        void toDetailResult_ClaimWithHistories_ReturnsDetailResult() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();
            List<ClaimHistory> histories = List.of();
            List<ClaimHistoryResult> historyResults = List.of();

            given(historyAssembler.toResults(histories)).willReturn(historyResults);

            // when
            ExchangeDetailResult result = sut.toDetailResult(claim, histories);

            // then
            assertThat(result).isNotNull();
            assertThat(result.exchangeClaimId()).isEqualTo(claim.idValue());
            assertThat(result.claimNumber()).isEqualTo(claim.claimNumberValue());
            assertThat(result.orderItemId()).isEqualTo(claim.orderItemIdValue());
            assertThat(result.sellerId()).isEqualTo(claim.sellerId());
            assertThat(result.exchangeQty()).isEqualTo(claim.exchangeQty());
            assertThat(result.exchangeStatus()).isEqualTo("REQUESTED");
            assertThat(result.histories()).isEqualTo(historyResults);
        }

        @Test
        @DisplayName("교환 옵션이 있는 경우 ExchangeOptionResult가 포함된다")
        void toDetailResult_ClaimWithOption_IncludesExchangeOptionResult() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();
            List<ClaimHistory> histories = List.of();

            given(historyAssembler.toResults(histories)).willReturn(List.of());

            // when
            ExchangeDetailResult result = sut.toDetailResult(claim, histories);

            // then
            assertThat(result.exchangeOption()).isNotNull();
            assertThat(result.exchangeOption().targetSkuCode())
                    .isEqualTo(claim.exchangeOption().targetSkuCode());
        }

        @Test
        @DisplayName("수거 배송 정보(collectShipment)가 있으면 CollectShipmentResult가 포함된다")
        void toDetailResult_ClaimWithCollectShipment_IncludesCollectShipmentResult() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();
            List<ClaimHistory> histories = List.of();

            given(historyAssembler.toResults(histories)).willReturn(List.of());

            // when
            ExchangeDetailResult result = sut.toDetailResult(claim, histories);

            // then
            assertThat(result.collectShipment()).isNotNull();
            assertThat(result.collectShipment().collectStatus()).isEqualTo("PENDING");
            assertThat(result.collectShipment().collectDeliveryCompany()).isEqualTo("CJ대한통운");
        }
    }

    @Nested
    @DisplayName("toPageResult() - ExchangeListResult 목록 → ExchangePageResult 변환")
    class ToPageResultTest {

        @Test
        @DisplayName("결과 목록으로 ExchangePageResult를 생성한다")
        void toPageResult_ReturnsPageResult() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();
            ExchangeListResult listResult = sut.toListResult(claim);
            int page = 0;
            int size = 20;
            long totalCount = 1L;

            // when
            ExchangePageResult result =
                    sut.toPageResult(List.of(listResult), page, size, totalCount);

            // then
            assertThat(result).isNotNull();
            assertThat(result.exchanges()).hasSize(1);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalCount);
        }

        @Test
        @DisplayName("빈 목록으로 빈 ExchangePageResult를 생성한다")
        void toPageResult_EmptyList_ReturnsEmptyPageResult() {
            // given
            int page = 0;
            int size = 20;
            long totalCount = 0L;

            // when
            ExchangePageResult result = sut.toPageResult(List.of(), page, size, totalCount);

            // then
            assertThat(result.exchanges()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("toSummaryResult() - 상태별 카운트 → ExchangeSummaryResult 변환")
    class ToSummaryResultTest {

        @Test
        @DisplayName("상태별 카운트 맵으로 ExchangeSummaryResult를 생성한다")
        void toSummaryResult_StatusCountMap_ReturnsSummaryResult() {
            // given
            Map<ExchangeStatus, Long> statusCounts =
                    Map.of(
                            ExchangeStatus.REQUESTED, 5L,
                            ExchangeStatus.COLLECTING, 3L,
                            ExchangeStatus.COLLECTED, 2L,
                            ExchangeStatus.COMPLETED, 10L);

            // when
            ExchangeSummaryResult result = sut.toSummaryResult(statusCounts);

            // then
            assertThat(result).isNotNull();
            assertThat(result.requested()).isEqualTo(5L);
            assertThat(result.collecting()).isEqualTo(3L);
            assertThat(result.collected()).isEqualTo(2L);
            assertThat(result.completed()).isEqualTo(10L);
            assertThat(result.preparing()).isEqualTo(0L);
            assertThat(result.rejected()).isEqualTo(0L);
        }

        @Test
        @DisplayName("빈 맵이면 모든 카운트가 0인 SummaryResult를 반환한다")
        void toSummaryResult_EmptyMap_ReturnsZeroSummary() {
            // given
            Map<ExchangeStatus, Long> emptyMap = Map.of();

            // when
            ExchangeSummaryResult result = sut.toSummaryResult(emptyMap);

            // then
            assertThat(result.requested()).isEqualTo(0L);
            assertThat(result.collecting()).isEqualTo(0L);
            assertThat(result.collected()).isEqualTo(0L);
            assertThat(result.preparing()).isEqualTo(0L);
            assertThat(result.shipping()).isEqualTo(0L);
            assertThat(result.completed()).isEqualTo(0L);
            assertThat(result.rejected()).isEqualTo(0L);
            assertThat(result.cancelled()).isEqualTo(0L);
        }
    }
}
