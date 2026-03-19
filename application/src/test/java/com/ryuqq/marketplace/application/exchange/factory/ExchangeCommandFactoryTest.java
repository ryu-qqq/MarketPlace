package com.ryuqq.marketplace.application.exchange.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.exchange.ExchangeCommandFixtures;
import com.ryuqq.marketplace.application.exchange.dto.command.RequestExchangeBatchCommand.ExchangeRequestItem;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory.ExchangeClaimWithHistory;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ExchangeCommandFactory 단위 테스트")
class ExchangeCommandFactoryTest {

    @InjectMocks private ExchangeCommandFactory sut;

    @Mock private TimeProvider timeProvider;
    @Mock private ClaimHistoryFactory historyFactory;

    @Nested
    @DisplayName("createExchangeRequest() - 교환 요청 번들 생성")
    class CreateExchangeRequestTest {

        @Test
        @DisplayName("ExchangeRequestItem으로 ExchangeClaimWithHistory를 생성한다")
        void createExchangeRequest_ValidItem_ReturnsBundle() {
            // given
            ExchangeRequestItem item = ExchangeCommandFixtures.exchangeRequestItem();
            String requestedBy = "buyer@example.com";
            long sellerId = 100L;
            Instant now = CommonVoFixtures.now();
            ClaimHistory history = Mockito.mock(ClaimHistory.class);

            given(timeProvider.now()).willReturn(now);
            given(
                            historyFactory.createStatusChange(
                                    org.mockito.ArgumentMatchers.any(),
                                    org.mockito.ArgumentMatchers.anyString(),
                                    org.mockito.ArgumentMatchers.isNull(),
                                    org.mockito.ArgumentMatchers.eq("REQUESTED"),
                                    org.mockito.ArgumentMatchers.anyString(),
                                    org.mockito.ArgumentMatchers.anyString()))
                    .willReturn(history);

            // when
            ExchangeClaimWithHistory result =
                    sut.createExchangeRequest(item, requestedBy, sellerId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.claim()).isNotNull();
            assertThat(result.claim().orderItemIdValue()).isEqualTo(item.orderItemId());
            assertThat(result.history()).isEqualTo(history);
        }
    }

    @Nested
    @DisplayName("createApproveHistory() - 승인 이력 생성")
    class CreateApproveHistoryTest {

        @Test
        @DisplayName("ExchangeClaim으로 승인 ClaimHistory를 생성한다")
        void createApproveHistory_ValidClaim_ReturnsHistory() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();
            String processedBy = "admin@marketplace.com";
            ClaimHistory expectedHistory = Mockito.mock(ClaimHistory.class);

            given(
                            historyFactory.createStatusChange(
                                    org.mockito.ArgumentMatchers.any(),
                                    org.mockito.ArgumentMatchers.anyString(),
                                    org.mockito.ArgumentMatchers.eq("REQUESTED"),
                                    org.mockito.ArgumentMatchers.eq("COLLECTING"),
                                    org.mockito.ArgumentMatchers.anyString(),
                                    org.mockito.ArgumentMatchers.anyString()))
                    .willReturn(expectedHistory);

            // when
            ClaimHistory result = sut.createApproveHistory(claim, processedBy);

            // then
            assertThat(result).isEqualTo(expectedHistory);
        }
    }

    @Nested
    @DisplayName("createCollectBundle() - 수거 완료 번들 생성")
    class CreateCollectBundleTest {

        @Test
        @DisplayName("수거 완료 시 Outbox와 History를 포함한 번들을 생성한다")
        void createCollectBundle_ValidClaim_ReturnsBundle() {
            // given
            ExchangeClaim claim = ExchangeFixtures.collectingExchangeClaim();
            String processedBy = "admin@marketplace.com";
            Instant now = CommonVoFixtures.now();
            ClaimHistory history = Mockito.mock(ClaimHistory.class);

            given(timeProvider.now()).willReturn(now);
            given(
                            historyFactory.createStatusChange(
                                    org.mockito.ArgumentMatchers.any(),
                                    org.mockito.ArgumentMatchers.anyString(),
                                    org.mockito.ArgumentMatchers.eq("COLLECTING"),
                                    org.mockito.ArgumentMatchers.eq("COLLECTED"),
                                    org.mockito.ArgumentMatchers.anyString(),
                                    org.mockito.ArgumentMatchers.anyString()))
                    .willReturn(history);

            // when
            OutboxWithHistory result = sut.createCollectBundle(claim, processedBy);

            // then
            assertThat(result).isNotNull();
            assertThat(result.outbox()).isNotNull();
            assertThat(result.history()).isEqualTo(history);
        }
    }

    @Nested
    @DisplayName("createRejectBundle() - 거절 번들 생성")
    class CreateRejectBundleTest {

        @Test
        @DisplayName("거절 시 Outbox와 History를 포함한 번들을 생성한다")
        void createRejectBundle_ValidClaim_ReturnsBundle() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();
            String fromStatus = claim.status().name();
            String processedBy = "admin@marketplace.com";
            Instant now = CommonVoFixtures.now();
            ClaimHistory history = Mockito.mock(ClaimHistory.class);

            given(timeProvider.now()).willReturn(now);
            given(
                            historyFactory.createStatusChange(
                                    org.mockito.ArgumentMatchers.any(),
                                    org.mockito.ArgumentMatchers.anyString(),
                                    org.mockito.ArgumentMatchers.eq(fromStatus),
                                    org.mockito.ArgumentMatchers.eq("REJECTED"),
                                    org.mockito.ArgumentMatchers.anyString(),
                                    org.mockito.ArgumentMatchers.anyString()))
                    .willReturn(history);

            // when
            OutboxWithHistory result = sut.createRejectBundle(claim, fromStatus, processedBy);

            // then
            assertThat(result).isNotNull();
            assertThat(result.outbox()).isNotNull();
            assertThat(result.history()).isEqualTo(history);
        }
    }

    @Nested
    @DisplayName("now() - 현재 시간 반환")
    class NowTest {

        @Test
        @DisplayName("TimeProvider를 통해 현재 시간을 반환한다")
        void now_ReturnsCurrentTime() {
            // given
            Instant expected = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(expected);

            // when
            Instant result = sut.now();

            // then
            assertThat(result).isEqualTo(expected);
        }
    }
}
