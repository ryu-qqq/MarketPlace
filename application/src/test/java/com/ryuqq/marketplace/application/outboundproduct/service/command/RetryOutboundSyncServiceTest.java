package com.ryuqq.marketplace.application.outboundproduct.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.outboundproduct.OmsProductCommandFixtures;
import com.ryuqq.marketplace.application.outboundproduct.factory.OmsProductCommandFactory;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxReadManager;
import com.ryuqq.marketplace.domain.outboundsync.OutboundSyncOutboxFixtures;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
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
@DisplayName("RetryOutboundSyncService 단위 테스트")
class RetryOutboundSyncServiceTest {

    @InjectMocks private RetryOutboundSyncService sut;

    @Mock private OmsProductCommandFactory commandFactory;
    @Mock private OutboundSyncOutboxReadManager outboxReadManager;
    @Mock private OutboundSyncOutboxCommandManager outboxCommandManager;

    @Nested
    @DisplayName("execute() - FAILED Outbox 재처리 요청")
    class ExecuteTest {

        @Test
        @DisplayName("FAILED 상태 Outbox를 PENDING으로 전환하고 persist를 호출한다")
        void execute_FailedOutbox_TransitionsToPendingAndPersists() {
            // given
            long outboxId = OutboundSyncOutboxFixtures.DEFAULT_ID;
            StatusChangeContext<Long> context = OmsProductCommandFixtures.retryContext(outboxId);
            OutboundSyncOutbox failedOutbox = OutboundSyncOutboxFixtures.failedOutbox();

            given(commandFactory.createRetryContext(outboxId)).willReturn(context);
            given(outboxReadManager.getById(context.id())).willReturn(failedOutbox);

            // when
            sut.execute(outboxId);

            // then
            then(commandFactory).should().createRetryContext(outboxId);
            then(outboxReadManager).should().getById(context.id());
            then(outboxCommandManager).should().persist(failedOutbox);
        }

        @Test
        @DisplayName("retry() 호출 후 outbox 상태가 PENDING으로 변경된다")
        void execute_AfterRetry_OutboxStatusIsPending() {
            // given
            long outboxId = OutboundSyncOutboxFixtures.DEFAULT_ID;
            StatusChangeContext<Long> context = OmsProductCommandFixtures.retryContext(outboxId);
            OutboundSyncOutbox failedOutbox = OutboundSyncOutboxFixtures.failedOutbox();

            given(commandFactory.createRetryContext(outboxId)).willReturn(context);
            given(outboxReadManager.getById(context.id())).willReturn(failedOutbox);

            // when
            sut.execute(outboxId);

            // then
            org.assertj.core.api.Assertions.assertThat(failedOutbox.isPending()).isTrue();
        }

        @Test
        @DisplayName("FAILED가 아닌 PENDING 상태 Outbox에 retry() 호출 시 IllegalStateException이 발생한다")
        void execute_PendingOutbox_ThrowsIllegalStateException() {
            // given
            long outboxId = OutboundSyncOutboxFixtures.DEFAULT_ID;
            StatusChangeContext<Long> context = OmsProductCommandFixtures.retryContext(outboxId);
            OutboundSyncOutbox pendingOutbox = OutboundSyncOutboxFixtures.pendingOutbox();

            given(commandFactory.createRetryContext(outboxId)).willReturn(context);
            given(outboxReadManager.getById(context.id())).willReturn(pendingOutbox);

            // when & then
            assertThatThrownBy(() -> sut.execute(outboxId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("FAILED 상태에서만 재처리할 수 있습니다");

            then(outboxCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("FAILED가 아닌 PROCESSING 상태 Outbox에 retry() 호출 시 IllegalStateException이 발생한다")
        void execute_ProcessingOutbox_ThrowsIllegalStateException() {
            // given
            long outboxId = OutboundSyncOutboxFixtures.DEFAULT_ID;
            StatusChangeContext<Long> context = OmsProductCommandFixtures.retryContext(outboxId);
            OutboundSyncOutbox processingOutbox = OutboundSyncOutboxFixtures.processingOutbox();

            given(commandFactory.createRetryContext(outboxId)).willReturn(context);
            given(outboxReadManager.getById(context.id())).willReturn(processingOutbox);

            // when & then
            assertThatThrownBy(() -> sut.execute(outboxId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("FAILED 상태에서만 재처리할 수 있습니다");

            then(outboxCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("존재하지 않는 outboxId 조회 시 ReadManager에서 던진 예외가 전파된다")
        void execute_NonExistentOutboxId_PropagatesExceptionFromReadManager() {
            // given
            long nonExistentOutboxId = 9999L;
            StatusChangeContext<Long> context =
                    OmsProductCommandFixtures.retryContext(nonExistentOutboxId);

            given(commandFactory.createRetryContext(nonExistentOutboxId)).willReturn(context);
            given(outboxReadManager.getById(context.id()))
                    .willThrow(
                            new RuntimeException("Outbox를 찾을 수 없습니다. id=" + nonExistentOutboxId));

            // when & then
            assertThatThrownBy(() -> sut.execute(nonExistentOutboxId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Outbox를 찾을 수 없습니다");

            then(outboxCommandManager).shouldHaveNoInteractions();
        }
    }
}
