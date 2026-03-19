package com.ryuqq.marketplace.application.cancel.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.cancel.CancelCommandFixtures;
import com.ryuqq.marketplace.application.cancel.dto.command.ExecuteCancelOutboxCommand;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxCommandManager;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxReadManager;
import com.ryuqq.marketplace.application.cancel.port.out.client.CancelClaimSyncStrategy;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import java.time.Instant;
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
@DisplayName("ExecuteCancelOutboxService 단위 테스트")
class ExecuteCancelOutboxServiceTest {

    @InjectMocks private ExecuteCancelOutboxService sut;

    @Mock private CancelOutboxReadManager outboxReadManager;
    @Mock private CancelOutboxCommandManager outboxCommandManager;
    @Mock private CancelClaimSyncStrategy claimSyncStrategy;
    @Mock private CancelCommandFactory commandFactory;

    @Nested
    @DisplayName("execute() - 취소 Outbox 실행")
    class ExecuteTest {

        @Test
        @DisplayName("성공적으로 외부 API 호출 시 Outbox를 COMPLETED로 업데이트한다")
        void execute_SuccessSync_CompletesOutbox() {
            // given
            ExecuteCancelOutboxCommand command = CancelCommandFixtures.executeCancelOutboxCommand();
            CancelOutbox outbox = org.mockito.Mockito.mock(CancelOutbox.class);
            CancelOutbox freshOutbox = org.mockito.Mockito.mock(CancelOutbox.class);
            OutboxSyncResult successResult = OutboxSyncResult.success();
            Instant now = Instant.now();

            given(outboxReadManager.getById(command.outboxId())).willReturn(outbox);
            given(claimSyncStrategy.execute(outbox)).willReturn(successResult);
            given(outboxReadManager.getById(outbox.idValue())).willReturn(freshOutbox);
            given(commandFactory.now()).willReturn(now);

            // when
            sut.execute(command);

            // then
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("외부 API 호출 실패 시 Outbox를 FAILED로 업데이트한다")
        void execute_FailedSync_RecordsFailure() {
            // given
            ExecuteCancelOutboxCommand command = CancelCommandFixtures.executeCancelOutboxCommand();
            CancelOutbox outbox = org.mockito.Mockito.mock(CancelOutbox.class);
            CancelOutbox freshOutbox = org.mockito.Mockito.mock(CancelOutbox.class);
            OutboxSyncResult failResult = OutboxSyncResult.failure(false, "외부 API 오류");
            Instant now = Instant.now();

            given(outboxReadManager.getById(command.outboxId())).willReturn(outbox);
            given(claimSyncStrategy.execute(outbox)).willReturn(failResult);
            given(outboxReadManager.getById(outbox.idValue())).willReturn(freshOutbox);
            given(commandFactory.now()).willReturn(now);

            // when
            sut.execute(command);

            // then
            then(freshOutbox)
                    .should()
                    .recordFailure(
                            org.mockito.ArgumentMatchers.eq(false),
                            org.mockito.ArgumentMatchers.eq("외부 API 오류"),
                            org.mockito.ArgumentMatchers.eq(now));
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("외부 서비스 일시 장애 시 deferRetry 처리한다")
        void execute_ExternalServiceUnavailable_DefersRetry() {
            // given
            ExecuteCancelOutboxCommand command = CancelCommandFixtures.executeCancelOutboxCommand();
            CancelOutbox outbox = org.mockito.Mockito.mock(CancelOutbox.class);
            CancelOutbox freshOutbox = org.mockito.Mockito.mock(CancelOutbox.class);
            Instant now = Instant.now();

            given(outboxReadManager.getById(command.outboxId())).willReturn(outbox);
            given(claimSyncStrategy.execute(outbox))
                    .willThrow(new ExternalServiceUnavailableException("외부 서비스 장애"));
            given(outboxReadManager.getById(outbox.idValue())).willReturn(freshOutbox);
            given(commandFactory.now()).willReturn(now);

            // when
            sut.execute(command);

            // then
            then(freshOutbox).should().recoverFromTimeout(now);
            then(outboxCommandManager).should().persist(freshOutbox);
        }
    }
}
