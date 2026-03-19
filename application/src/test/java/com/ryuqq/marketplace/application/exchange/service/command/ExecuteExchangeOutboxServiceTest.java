package com.ryuqq.marketplace.application.exchange.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.when;

import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.exchange.ExchangeCommandFixtures;
import com.ryuqq.marketplace.application.exchange.dto.command.ExecuteExchangeOutboxCommand;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxCommandManager;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxReadManager;
import com.ryuqq.marketplace.application.exchange.port.out.client.ExchangeClaimSyncStrategy;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
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
@DisplayName("ExecuteExchangeOutboxService 단위 테스트")
class ExecuteExchangeOutboxServiceTest {

    @InjectMocks private ExecuteExchangeOutboxService sut;

    @Mock private ExchangeOutboxReadManager outboxReadManager;
    @Mock private ExchangeOutboxCommandManager outboxCommandManager;
    @Mock private ExchangeClaimSyncStrategy claimSyncStrategy;

    @Nested
    @DisplayName("execute() - 교환 Outbox 실행")
    class ExecuteTest {

        @Test
        @DisplayName("외부 API 호출 성공 시 Outbox를 COMPLETED로 전환한다")
        void execute_SyncSuccess_CompletesOutbox() {
            // given
            Long outboxId = 1L;
            ExecuteExchangeOutboxCommand command =
                    ExchangeCommandFixtures.executeExchangeOutboxCommand(outboxId, "COLLECT");
            ExchangeOutbox outbox = Mockito.mock(ExchangeOutbox.class);
            ExchangeOutbox freshOutbox = Mockito.mock(ExchangeOutbox.class);

            given(outbox.idValue()).willReturn(outboxId);
            // thenReturn 체이닝: 첫 번째 호출에 outbox, 이후 호출에 freshOutbox
            when(outboxReadManager.getById(outboxId)).thenReturn(outbox).thenReturn(freshOutbox);
            given(claimSyncStrategy.execute(outbox)).willReturn(OutboxSyncResult.success());

            // when
            sut.execute(command);

            // then
            then(freshOutbox).should().complete(Mockito.any());
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("외부 API 호출 실패 시 Outbox에 실패를 기록한다")
        void execute_SyncFailure_RecordsFailure() {
            // given
            Long outboxId = 2L;
            ExecuteExchangeOutboxCommand command =
                    ExchangeCommandFixtures.executeExchangeOutboxCommand(outboxId, "COLLECT");
            ExchangeOutbox outbox = Mockito.mock(ExchangeOutbox.class);
            ExchangeOutbox freshOutbox = Mockito.mock(ExchangeOutbox.class);
            OutboxSyncResult failureResult = OutboxSyncResult.failure(true, "외부 API 오류");

            given(outbox.idValue()).willReturn(outboxId);
            when(outboxReadManager.getById(outboxId)).thenReturn(outbox).thenReturn(freshOutbox);
            given(claimSyncStrategy.execute(outbox)).willReturn(failureResult);

            // when
            sut.execute(command);

            // then
            then(freshOutbox)
                    .should()
                    .recordFailure(Mockito.eq(true), Mockito.eq("외부 API 오류"), Mockito.any());
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("외부 서비스 일시 장애 시 Outbox를 PENDING으로 복구한다")
        void execute_ExternalServiceUnavailable_DefersRetry() {
            // given
            Long outboxId = 3L;
            ExecuteExchangeOutboxCommand command =
                    ExchangeCommandFixtures.executeExchangeOutboxCommand(outboxId, "COLLECT");
            ExchangeOutbox outbox = Mockito.mock(ExchangeOutbox.class);
            ExchangeOutbox freshOutbox = Mockito.mock(ExchangeOutbox.class);

            given(outbox.idValue()).willReturn(outboxId);
            when(outboxReadManager.getById(outboxId)).thenReturn(outbox).thenReturn(freshOutbox);
            given(claimSyncStrategy.execute(outbox))
                    .willThrow(new ExternalServiceUnavailableException("서비스 일시 장애"));

            // when
            sut.execute(command);

            // then
            then(freshOutbox).should().recoverFromTimeout(Mockito.any());
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("예기치 않은 예외 발생 시 재시도 가능한 실패로 기록한다")
        void execute_UnexpectedException_RecordsRetryableFailure() {
            // given
            Long outboxId = 4L;
            ExecuteExchangeOutboxCommand command =
                    ExchangeCommandFixtures.executeExchangeOutboxCommand(outboxId, "COLLECT");
            ExchangeOutbox outbox = Mockito.mock(ExchangeOutbox.class);
            ExchangeOutbox freshOutbox = Mockito.mock(ExchangeOutbox.class);

            given(outbox.idValue()).willReturn(outboxId);
            when(outboxReadManager.getById(outboxId)).thenReturn(outbox).thenReturn(freshOutbox);
            willThrow(new RuntimeException("예기치 않은 오류")).given(claimSyncStrategy).execute(outbox);

            // when
            sut.execute(command);

            // then
            then(freshOutbox)
                    .should()
                    .recordFailure(Mockito.eq(true), Mockito.contains("실행 중 예외"), Mockito.any());
            then(outboxCommandManager).should().persist(freshOutbox);
        }
    }
}
