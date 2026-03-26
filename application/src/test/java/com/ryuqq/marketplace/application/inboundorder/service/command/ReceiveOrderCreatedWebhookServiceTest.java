package com.ryuqq.marketplace.application.inboundorder.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundorder.ExternalOrderPayloadFixtures;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.result.InboundOrderPollingResult;
import com.ryuqq.marketplace.application.inboundorder.internal.InboundOrderReceiveCoordinator;
import java.time.Instant;
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
@DisplayName("ReceiveOrderCreatedWebhookService 단위 테스트")
class ReceiveOrderCreatedWebhookServiceTest {

    @InjectMocks private ReceiveOrderCreatedWebhookService sut;

    @Mock private InboundOrderReceiveCoordinator coordinator;
    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("execute() - 주문 생성 웹훅 수신 처리")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 페이로드 목록을 수신하면 코디네이터에 위임하고 결과를 반환한다")
        void execute_ValidPayloads_DelegatesToCoordinatorAndReturnsResult() {
            // given
            long salesChannelId = 1L;
            long shopId = 10L;
            Instant now = Instant.now();
            List<ExternalOrderPayload> payloads =
                    List.of(ExternalOrderPayloadFixtures.defaultPayload());
            InboundOrderPollingResult expected = InboundOrderPollingResult.of(1, 1, 0, 0, 0);

            given(timeProvider.now()).willReturn(now);
            given(coordinator.receiveAll(payloads, salesChannelId, shopId, now))
                    .willReturn(expected);

            // when
            InboundOrderPollingResult result = sut.execute(payloads, salesChannelId, shopId);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.created()).isEqualTo(1);
            assertThat(result.failed()).isEqualTo(0);
            then(coordinator).should().receiveAll(payloads, salesChannelId, shopId, now);
        }

        @Test
        @DisplayName("빈 페이로드 목록을 수신하면 코디네이터에 위임하고 empty 결과를 반환한다")
        void execute_EmptyPayloads_DelegatesToCoordinatorAndReturnsEmptyResult() {
            // given
            long salesChannelId = 1L;
            long shopId = 10L;
            Instant now = Instant.now();
            List<ExternalOrderPayload> emptyPayloads = List.of();
            InboundOrderPollingResult expected = InboundOrderPollingResult.empty();

            given(timeProvider.now()).willReturn(now);
            given(coordinator.receiveAll(emptyPayloads, salesChannelId, shopId, now))
                    .willReturn(expected);

            // when
            InboundOrderPollingResult result = sut.execute(emptyPayloads, salesChannelId, shopId);

            // then
            assertThat(result.total()).isEqualTo(0);
            assertThat(result.created()).isEqualTo(0);
            then(coordinator)
                    .should()
                    .receiveAll(
                            eq(emptyPayloads), eq(salesChannelId), eq(shopId), any(Instant.class));
        }

        @Test
        @DisplayName("복수의 페이로드를 수신하면 코디네이터가 전체 건수를 처리한다")
        void execute_MultiplePayloads_ReturnsAggregatedResult() {
            // given
            long salesChannelId = 2L;
            long shopId = 20L;
            Instant now = Instant.now();
            List<ExternalOrderPayload> payloads =
                    List.of(
                            ExternalOrderPayloadFixtures.payload("NAVER-ORD-001"),
                            ExternalOrderPayloadFixtures.payload("NAVER-ORD-002"),
                            ExternalOrderPayloadFixtures.payload("NAVER-ORD-003"));
            InboundOrderPollingResult expected = InboundOrderPollingResult.of(3, 2, 1, 0, 0);

            given(timeProvider.now()).willReturn(now);
            given(coordinator.receiveAll(payloads, salesChannelId, shopId, now))
                    .willReturn(expected);

            // when
            InboundOrderPollingResult result = sut.execute(payloads, salesChannelId, shopId);

            // then
            assertThat(result.total()).isEqualTo(3);
            assertThat(result.created()).isEqualTo(2);
            assertThat(result.pending()).isEqualTo(1);
        }

        @Test
        @DisplayName("중복 주문이 있는 경우 코디네이터가 중복 건수를 포함한 결과를 반환한다")
        void execute_WithDuplicatedOrders_ReturnsDuplicatedCount() {
            // given
            long salesChannelId = 1L;
            long shopId = 10L;
            Instant now = Instant.now();
            List<ExternalOrderPayload> payloads =
                    List.of(
                            ExternalOrderPayloadFixtures.defaultPayload(),
                            ExternalOrderPayloadFixtures.defaultPayload());
            InboundOrderPollingResult expected = InboundOrderPollingResult.of(2, 1, 0, 1, 0);

            given(timeProvider.now()).willReturn(now);
            given(coordinator.receiveAll(payloads, salesChannelId, shopId, now))
                    .willReturn(expected);

            // when
            InboundOrderPollingResult result = sut.execute(payloads, salesChannelId, shopId);

            // then
            assertThat(result.total()).isEqualTo(2);
            assertThat(result.created()).isEqualTo(1);
            assertThat(result.duplicated()).isEqualTo(1);
        }
    }
}
