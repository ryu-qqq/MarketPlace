package com.ryuqq.marketplace.application.inboundorder.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.claimsync.ClaimSyncFixtures;
import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.claimsync.dto.result.ClaimSyncResult;
import com.ryuqq.marketplace.application.claimsync.internal.ClaimSyncCoordinator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ReceiveClaimWebhookService 단위 테스트")
class ReceiveClaimWebhookServiceTest {

    @InjectMocks private ReceiveClaimWebhookService sut;

    @Mock private ClaimSyncCoordinator coordinator;

    @Nested
    @DisplayName("execute() - 클레임 웹훅 수신 처리")
    class ExecuteTest {

        @Test
        @DisplayName("취소 클레임 페이로드를 수신하면 코디네이터에 위임하고 결과를 반환한다")
        void execute_CancelClaimPayloads_DelegatesToCoordinatorAndReturnsResult() {
            // given
            long salesChannelId = 1L;
            List<ExternalClaimPayload> payloads =
                    List.of(ClaimSyncFixtures.cancelRequestPayload());
            ClaimSyncResult expected = ClaimSyncFixtures.resultWithCancel(1, 1);

            given(coordinator.syncAll(payloads, salesChannelId)).willReturn(expected);

            // when
            ClaimSyncResult result = sut.execute(payloads, salesChannelId);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.totalProcessed()).isEqualTo(1);
            assertThat(result.cancelSynced()).isEqualTo(1);
            then(coordinator).should().syncAll(payloads, salesChannelId);
        }

        @Test
        @DisplayName("반품 클레임 페이로드를 수신하면 코디네이터에 위임하고 결과를 반환한다")
        void execute_ReturnClaimPayloads_DelegatesToCoordinatorAndReturnsResult() {
            // given
            long salesChannelId = 1L;
            List<ExternalClaimPayload> payloads =
                    List.of(ClaimSyncFixtures.returnRequestPayload());
            ClaimSyncResult expected = ClaimSyncFixtures.resultWithRefund(1, 1);

            given(coordinator.syncAll(payloads, salesChannelId)).willReturn(expected);

            // when
            ClaimSyncResult result = sut.execute(payloads, salesChannelId);

            // then
            assertThat(result.totalProcessed()).isEqualTo(1);
            assertThat(result.refundSynced()).isEqualTo(1);
            then(coordinator).should().syncAll(payloads, salesChannelId);
        }

        @Test
        @DisplayName("교환 클레임 페이로드를 수신하면 코디네이터에 위임하고 결과를 반환한다")
        void execute_ExchangeClaimPayloads_DelegatesToCoordinatorAndReturnsResult() {
            // given
            long salesChannelId = 1L;
            List<ExternalClaimPayload> payloads =
                    List.of(ClaimSyncFixtures.exchangeRequestPayload());
            ClaimSyncResult expected = ClaimSyncFixtures.resultWithExchange(1, 1);

            given(coordinator.syncAll(payloads, salesChannelId)).willReturn(expected);

            // when
            ClaimSyncResult result = sut.execute(payloads, salesChannelId);

            // then
            assertThat(result.totalProcessed()).isEqualTo(1);
            assertThat(result.exchangeSynced()).isEqualTo(1);
            then(coordinator).should().syncAll(payloads, salesChannelId);
        }

        @Test
        @DisplayName("빈 페이로드 목록을 수신하면 코디네이터에 위임하고 empty 결과를 반환한다")
        void execute_EmptyPayloads_DelegatesToCoordinatorAndReturnsEmptyResult() {
            // given
            long salesChannelId = 1L;
            List<ExternalClaimPayload> emptyPayloads = List.of();
            ClaimSyncResult expected = ClaimSyncFixtures.emptyResult();

            given(coordinator.syncAll(emptyPayloads, salesChannelId)).willReturn(expected);

            // when
            ClaimSyncResult result = sut.execute(emptyPayloads, salesChannelId);

            // then
            assertThat(result.totalProcessed()).isEqualTo(0);
            then(coordinator).should().syncAll(emptyPayloads, salesChannelId);
        }

        @Test
        @DisplayName("복합 클레임 페이로드를 수신하면 집계된 결과를 반환한다")
        void execute_MixedClaimPayloads_ReturnsAggregatedResult() {
            // given
            long salesChannelId = 1L;
            List<ExternalClaimPayload> payloads = List.of(
                    ClaimSyncFixtures.cancelRequestPayload(),
                    ClaimSyncFixtures.returnRequestPayload(),
                    ClaimSyncFixtures.exchangeRequestPayload());
            ClaimSyncResult expected = ClaimSyncFixtures.fullResult(3, 1, 1, 1, 0, 0);

            given(coordinator.syncAll(payloads, salesChannelId)).willReturn(expected);

            // when
            ClaimSyncResult result = sut.execute(payloads, salesChannelId);

            // then
            assertThat(result.totalProcessed()).isEqualTo(3);
            assertThat(result.cancelSynced()).isEqualTo(1);
            assertThat(result.refundSynced()).isEqualTo(1);
            assertThat(result.exchangeSynced()).isEqualTo(1);
            assertThat(result.failed()).isEqualTo(0);
        }
    }
}
