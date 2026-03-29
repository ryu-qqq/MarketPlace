package com.ryuqq.marketplace.application.qna.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.when;

import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.qna.QnaCommandFixtures;
import com.ryuqq.marketplace.application.qna.dto.command.ExecuteQnaOutboxCommand;
import com.ryuqq.marketplace.application.qna.manager.QnaOutboxCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaOutboxReadManager;
import com.ryuqq.marketplace.application.qna.port.out.client.QnaAnswerSyncStrategy;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
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
@DisplayName("ExecuteQnaOutboxService 단위 테스트")
class ExecuteQnaOutboxServiceTest {

    private ExecuteQnaOutboxService sut;

    @Mock private QnaOutboxReadManager readManager;
    @Mock private QnaOutboxCommandManager commandManager;
    @Mock private QnaAnswerSyncStrategy syncStrategy;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        sut = new ExecuteQnaOutboxService(readManager, commandManager, java.util.Optional.of(syncStrategy));
    }

    @Nested
    @DisplayName("execute() - QnA 아웃박스 실행")
    class ExecuteTest {

        @Test
        @DisplayName("외부 API 호출 성공 시 freshOutbox를 COMPLETED로 전환하고 저장한다")
        void execute_SyncSuccess_CompletesOutbox() {
            // given
            // 서비스는 command.outboxId()를 키로 readManager.getById()를 2회 호출한다
            // 1회차: 외부 API 호출용, 2회차: 상태 업데이트용 freshOutbox
            ExecuteQnaOutboxCommand command = QnaCommandFixtures.executeOutboxCommand();
            QnaOutbox outbox = Mockito.mock(QnaOutbox.class);
            QnaOutbox freshOutbox = Mockito.mock(QnaOutbox.class);

            when(readManager.getById(command.outboxId()))
                    .thenReturn(outbox)
                    .thenReturn(freshOutbox);
            given(syncStrategy.execute(outbox)).willReturn(OutboxSyncResult.success());

            // when
            sut.execute(command);

            // then
            then(freshOutbox).should().complete(Mockito.any(Instant.class));
            then(commandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("외부 API 호출 실패(재시도 가능) 시 freshOutbox에 실패를 기록하고 저장한다")
        void execute_SyncRetryableFailure_RecordsFailureAndPersists() {
            // given
            ExecuteQnaOutboxCommand command = QnaCommandFixtures.executeOutboxCommand();
            QnaOutbox outbox = Mockito.mock(QnaOutbox.class);
            QnaOutbox freshOutbox = Mockito.mock(QnaOutbox.class);
            OutboxSyncResult failureResult = OutboxSyncResult.failure(true, "외부 API 오류");

            when(readManager.getById(command.outboxId()))
                    .thenReturn(outbox)
                    .thenReturn(freshOutbox);
            given(syncStrategy.execute(outbox)).willReturn(failureResult);

            // when
            sut.execute(command);

            // then
            then(freshOutbox)
                    .should()
                    .recordFailure(
                            Mockito.eq(true), Mockito.eq("외부 API 오류"), Mockito.any(Instant.class));
            then(commandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("외부 API 호출 실패(재시도 불가) 시 freshOutbox에 즉시 실패를 기록한다")
        void execute_SyncNonRetryableFailure_RecordsImmediateFailure() {
            // given
            ExecuteQnaOutboxCommand command = QnaCommandFixtures.executeOutboxCommand();
            QnaOutbox outbox = Mockito.mock(QnaOutbox.class);
            QnaOutbox freshOutbox = Mockito.mock(QnaOutbox.class);
            OutboxSyncResult nonRetryableFailure = OutboxSyncResult.failure(false, "영구 오류");

            when(readManager.getById(command.outboxId()))
                    .thenReturn(outbox)
                    .thenReturn(freshOutbox);
            given(syncStrategy.execute(outbox)).willReturn(nonRetryableFailure);

            // when
            sut.execute(command);

            // then
            then(freshOutbox)
                    .should()
                    .recordFailure(
                            Mockito.eq(false), Mockito.eq("영구 오류"), Mockito.any(Instant.class));
            then(commandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("외부 서비스 일시 장애 시 freshOutbox를 PENDING으로 복구한다")
        void execute_ExternalServiceUnavailable_RecoversToPending() {
            // given
            // ExternalServiceUnavailableException 발생 시 catch 블록에서 별도 getById 호출
            ExecuteQnaOutboxCommand command = QnaCommandFixtures.executeOutboxCommand();
            QnaOutbox outbox = Mockito.mock(QnaOutbox.class);
            QnaOutbox freshOutbox = Mockito.mock(QnaOutbox.class);

            when(readManager.getById(command.outboxId()))
                    .thenReturn(outbox)
                    .thenReturn(freshOutbox);
            given(syncStrategy.execute(outbox))
                    .willThrow(new ExternalServiceUnavailableException("서비스 일시 장애"));

            // when
            sut.execute(command);

            // then
            then(freshOutbox).should().recoverFromTimeout(Mockito.any(Instant.class));
            then(commandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("예기치 않은 예외 발생 시 freshOutbox에 재시도 가능한 실패를 기록한다")
        void execute_UnexpectedException_RecordsRetryableFailure() {
            // given
            // Exception 발생 시 catch 블록에서 별도 getById 호출 후 recordFailure
            ExecuteQnaOutboxCommand command = QnaCommandFixtures.executeOutboxCommand();
            QnaOutbox outbox = Mockito.mock(QnaOutbox.class);
            QnaOutbox freshOutbox = Mockito.mock(QnaOutbox.class);

            when(readManager.getById(command.outboxId()))
                    .thenReturn(outbox)
                    .thenReturn(freshOutbox);
            willThrow(new RuntimeException("예기치 않은 오류")).given(syncStrategy).execute(outbox);

            // when
            sut.execute(command);

            // then
            then(freshOutbox)
                    .should()
                    .recordFailure(
                            Mockito.eq(true), Mockito.eq("예기치 않은 오류"), Mockito.any(Instant.class));
            then(commandManager).should().persist(freshOutbox);
        }
    }
}
