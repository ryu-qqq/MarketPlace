package com.ryuqq.marketplace.application.outboundsync.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductCommandManager;
import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.application.outboundsync.dto.command.ExecuteOutboundSyncCommand;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionResult;
import com.ryuqq.marketplace.application.outboundsync.internal.OutboundSyncExecutionStrategy;
import com.ryuqq.marketplace.application.outboundsync.internal.OutboundSyncStrategyRouter;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxReadManager;
import com.ryuqq.marketplace.application.sellersaleschannel.manager.SellerSalesChannelReadManager;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.outboundsync.OutboundSyncOutboxFixtures;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.sellersaleschannel.SellerSalesChannelFixtures;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import com.ryuqq.marketplace.domain.shop.ShopFixtures;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
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
@DisplayName("ExecuteOutboundSyncService 단위 테스트")
class ExecuteOutboundSyncServiceTest {

    @InjectMocks private ExecuteOutboundSyncService sut;

    @Mock private OutboundSyncOutboxReadManager outboxReadManager;
    @Mock private OutboundSyncOutboxCommandManager outboxCommandManager;
    @Mock private OutboundSyncStrategyRouter strategyRouter;
    @Mock private SellerSalesChannelReadManager channelReadManager;
    @Mock private ShopReadManager shopReadManager;
    @Mock private OutboundProductReadManager productReadManager;
    @Mock private OutboundProductCommandManager productCommandManager;
    @Mock private OutboundSyncExecutionStrategy strategy;

    @Nested
    @DisplayName("execute() - ExternalServiceUnavailableException 발생 시 deferRetry 처리")
    class DeferRetryTest {

        @Test
        @DisplayName("ExternalServiceUnavailableException 발생 시 deferRetry를 호출하여 PENDING으로 복귀한다")
        void execute_WhenExternalServiceUnavailable_CallsDeferRetryAndTransitionsToPending() {
            // given
            OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.processingOutbox();
            ExecuteOutboundSyncCommand command =
                    ExecuteOutboundSyncCommand.of(
                            outbox.idValue(),
                            outbox.productGroupIdValue(),
                            outbox.salesChannelIdValue(),
                            SyncType.CREATE);

            SellerSalesChannel channel = SellerSalesChannelFixtures.connectedSellerSalesChannel();
            Shop shop = ShopFixtures.activeShop();

            given(outboxReadManager.getById(command.outboxId())).willReturn(outbox);
            given(channelReadManager.getBySellerIdAndSalesChannelId(any(), any()))
                    .willReturn(channel);
            given(strategyRouter.route(any(), eq(SyncType.CREATE))).willReturn(strategy);
            given(shopReadManager.getById(any())).willReturn(shop);
            given(strategy.execute(any()))
                    .willThrow(
                            new ExternalServiceUnavailableException(
                                    "네이버 커머스 서비스 일시 중단 (Circuit Breaker OPEN)"));

            // deferRetry 호출 시 re-read 패턴으로 fresh outbox를 다시 조회
            OutboundSyncOutbox freshOutbox = OutboundSyncOutboxFixtures.processingOutbox();
            given(outboxReadManager.getById(outbox.idValue()))
                    .willReturn(outbox)
                    .willReturn(freshOutbox);

            // when
            sut.execute(command);

            // then
            assertThat(freshOutbox.status()).isEqualTo(SyncStatus.PENDING);
            assertThat(freshOutbox.errorMessage()).isEqualTo("외부 서비스 일시 장애로 인한 지연 재시도");
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("ExternalServiceUnavailableException 발생 시 retryCount가 증가하지 않는다")
        void execute_WhenExternalServiceUnavailable_DoesNotIncrementRetryCount() {
            // given
            OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.processingOutbox();
            int originalRetryCount = outbox.retryCount();
            ExecuteOutboundSyncCommand command =
                    ExecuteOutboundSyncCommand.of(
                            outbox.idValue(),
                            outbox.productGroupIdValue(),
                            outbox.salesChannelIdValue(),
                            SyncType.CREATE);

            SellerSalesChannel channel = SellerSalesChannelFixtures.connectedSellerSalesChannel();
            Shop shop = ShopFixtures.activeShop();

            given(outboxReadManager.getById(command.outboxId())).willReturn(outbox);
            given(channelReadManager.getBySellerIdAndSalesChannelId(any(), any()))
                    .willReturn(channel);
            given(strategyRouter.route(any(), eq(SyncType.CREATE))).willReturn(strategy);
            given(shopReadManager.getById(any())).willReturn(shop);
            given(strategy.execute(any()))
                    .willThrow(new ExternalServiceUnavailableException("CB OPEN"));

            OutboundSyncOutbox freshOutbox = OutboundSyncOutboxFixtures.processingOutbox();
            given(outboxReadManager.getById(outbox.idValue()))
                    .willReturn(outbox)
                    .willReturn(freshOutbox);

            // when
            sut.execute(command);

            // then
            assertThat(freshOutbox.retryCount()).isEqualTo(originalRetryCount);
        }
    }

    @Nested
    @DisplayName("execute() - 채널 연동 비활성 상태 시 스킵 처리")
    class DisconnectedChannelTest {

        @Test
        @DisplayName("채널이 DISCONNECTED이면 동기화를 스킵하고 재시도 불필요로 실패 처리한다")
        void execute_WhenChannelDisconnected_SkipsAndRecordsFailure() {
            // given
            OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.processingOutbox();
            ExecuteOutboundSyncCommand command =
                    ExecuteOutboundSyncCommand.of(
                            outbox.idValue(),
                            outbox.productGroupIdValue(),
                            outbox.salesChannelIdValue(),
                            SyncType.CREATE);

            SellerSalesChannel disconnectedChannel =
                    SellerSalesChannelFixtures.disconnectedSellerSalesChannel();

            given(outboxReadManager.getById(command.outboxId())).willReturn(outbox);
            given(channelReadManager.getBySellerIdAndSalesChannelId(any(), any()))
                    .willReturn(disconnectedChannel);

            OutboundSyncOutbox freshOutbox = OutboundSyncOutboxFixtures.processingOutbox();
            given(outboxReadManager.getById(outbox.idValue()))
                    .willReturn(outbox)
                    .willReturn(freshOutbox);

            // when
            sut.execute(command);

            // then
            assertThat(freshOutbox.status()).isEqualTo(SyncStatus.FAILED);
            then(strategyRouter).shouldHaveNoInteractions();
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("채널이 DISCONNECTED이면 Strategy를 호출하지 않는다")
        void execute_WhenChannelDisconnected_DoesNotCallStrategy() {
            // given
            OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.processingOutbox();
            ExecuteOutboundSyncCommand command =
                    ExecuteOutboundSyncCommand.of(
                            outbox.idValue(),
                            outbox.productGroupIdValue(),
                            outbox.salesChannelIdValue(),
                            SyncType.UPDATE);

            SellerSalesChannel disconnectedChannel =
                    SellerSalesChannelFixtures.disconnectedSellerSalesChannel();

            given(outboxReadManager.getById(command.outboxId())).willReturn(outbox);
            given(channelReadManager.getBySellerIdAndSalesChannelId(any(), any()))
                    .willReturn(disconnectedChannel);

            OutboundSyncOutbox freshOutbox = OutboundSyncOutboxFixtures.processingOutbox();
            given(outboxReadManager.getById(outbox.idValue()))
                    .willReturn(outbox)
                    .willReturn(freshOutbox);

            // when
            sut.execute(command);

            // then
            then(strategy).shouldHaveNoInteractions();
            then(shopReadManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("execute() - 일반 예외 발생 시 failAndRetry 처리")
    class FailAndRetryTest {

        @Test
        @DisplayName("일반 Exception 발생 시 failAndRetry를 호출하여 retryCount가 증가한다")
        void execute_WhenGeneralException_CallsFailAndRetryAndIncrementsRetryCount() {
            // given
            OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.processingOutbox();
            int originalRetryCount = outbox.retryCount();
            ExecuteOutboundSyncCommand command =
                    ExecuteOutboundSyncCommand.of(
                            outbox.idValue(),
                            outbox.productGroupIdValue(),
                            outbox.salesChannelIdValue(),
                            SyncType.CREATE);

            SellerSalesChannel channel = SellerSalesChannelFixtures.connectedSellerSalesChannel();
            Shop shop = ShopFixtures.activeShop();

            given(outboxReadManager.getById(command.outboxId())).willReturn(outbox);
            given(channelReadManager.getBySellerIdAndSalesChannelId(any(), any()))
                    .willReturn(channel);
            given(strategyRouter.route(any(), eq(SyncType.CREATE))).willReturn(strategy);
            given(shopReadManager.getById(any())).willReturn(shop);
            given(strategy.execute(any())).willThrow(new RuntimeException("네트워크 에러"));

            OutboundSyncOutbox freshOutbox = OutboundSyncOutboxFixtures.processingOutbox();
            given(outboxReadManager.getById(outbox.idValue()))
                    .willReturn(outbox)
                    .willReturn(freshOutbox);

            // when
            sut.execute(command);

            // then
            assertThat(freshOutbox.retryCount()).isEqualTo(originalRetryCount + 1);
            then(outboxCommandManager).should().persist(freshOutbox);
        }
    }

    @Nested
    @DisplayName("execute() - 전략 실행 성공 시 완료 처리")
    class SuccessTest {

        @Test
        @DisplayName("전략 실행 성공 시 outbox를 COMPLETED 상태로 전환한다")
        void execute_WhenStrategySucceeds_TransitionsToCompleted() {
            // given
            OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.processingOutbox();
            ExecuteOutboundSyncCommand command =
                    ExecuteOutboundSyncCommand.of(
                            outbox.idValue(),
                            outbox.productGroupIdValue(),
                            outbox.salesChannelIdValue(),
                            SyncType.UPDATE);

            SellerSalesChannel channel = SellerSalesChannelFixtures.connectedSellerSalesChannel();
            Shop shop = ShopFixtures.activeShop();

            given(outboxReadManager.getById(command.outboxId())).willReturn(outbox);
            given(channelReadManager.getBySellerIdAndSalesChannelId(any(), any()))
                    .willReturn(channel);
            given(strategyRouter.route(any(), eq(SyncType.UPDATE))).willReturn(strategy);
            given(shopReadManager.getById(any())).willReturn(shop);
            given(strategy.execute(any()))
                    .willReturn(OutboundSyncExecutionResult.success("ext-123"));

            OutboundSyncOutbox freshOutbox = OutboundSyncOutboxFixtures.processingOutbox();
            given(outboxReadManager.getById(outbox.idValue()))
                    .willReturn(outbox)
                    .willReturn(freshOutbox);

            // when
            sut.execute(command);

            // then
            assertThat(freshOutbox.status()).isEqualTo(SyncStatus.COMPLETED);
            then(outboxCommandManager).should().persist(freshOutbox);
        }
    }
}
