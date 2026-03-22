package com.ryuqq.marketplace.application.qna.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.qna.QnaCommandFixtures;
import com.ryuqq.marketplace.application.qna.dto.command.RecoverTimeoutQnaOutboxCommand;
import com.ryuqq.marketplace.application.qna.manager.QnaOutboxCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaOutboxReadManager;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import java.time.Instant;
import java.util.List;
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
@DisplayName("RecoverTimeoutQnaOutboxService 단위 테스트")
class RecoverTimeoutQnaOutboxServiceTest {

    @InjectMocks private RecoverTimeoutQnaOutboxService sut;

    @Mock private QnaOutboxReadManager readManager;
    @Mock private QnaOutboxCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 타임아웃 QnA 아웃박스 복구")
    class ExecuteTest {

        @Test
        @DisplayName("타임아웃된 아웃박스를 PENDING으로 복구하고 저장한다")
        void execute_TimeoutOutboxes_RecoversToPendingAndPersists() {
            // given
            RecoverTimeoutQnaOutboxCommand command =
                    QnaCommandFixtures.recoverTimeoutOutboxCommand();
            QnaOutbox outbox = Mockito.mock(QnaOutbox.class);

            given(
                            readManager.findProcessingTimeoutOutboxes(
                                    Mockito.any(Instant.class), Mockito.eq(command.batchSize())))
                    .willReturn(List.of(outbox));

            // when
            sut.execute(command);

            // then
            then(outbox).should().recoverFromTimeout(Mockito.any(Instant.class));
            then(commandManager).should().persist(outbox);
        }

        @Test
        @DisplayName("타임아웃 아웃박스가 없으면 복구 처리를 수행하지 않는다")
        void execute_NoTimeoutOutboxes_DoesNotPersist() {
            // given
            RecoverTimeoutQnaOutboxCommand command =
                    QnaCommandFixtures.recoverTimeoutOutboxCommand();

            given(
                            readManager.findProcessingTimeoutOutboxes(
                                    Mockito.any(Instant.class), Mockito.eq(command.batchSize())))
                    .willReturn(List.of());

            // when
            sut.execute(command);

            // then
            then(commandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("여러 타임아웃 아웃박스를 모두 복구 처리한다")
        void execute_MultipleTimeoutOutboxes_RecovershAll() {
            // given
            RecoverTimeoutQnaOutboxCommand command =
                    QnaCommandFixtures.recoverTimeoutOutboxCommand();
            QnaOutbox outbox1 = Mockito.mock(QnaOutbox.class);
            QnaOutbox outbox2 = Mockito.mock(QnaOutbox.class);

            given(
                            readManager.findProcessingTimeoutOutboxes(
                                    Mockito.any(Instant.class), Mockito.eq(command.batchSize())))
                    .willReturn(List.of(outbox1, outbox2));

            // when
            sut.execute(command);

            // then
            then(outbox1).should().recoverFromTimeout(Mockito.any(Instant.class));
            then(commandManager).should().persist(outbox1);
            then(outbox2).should().recoverFromTimeout(Mockito.any(Instant.class));
            then(commandManager).should().persist(outbox2);
        }

        @Test
        @DisplayName("timeoutSeconds에 따라 올바른 기준 시간으로 타임아웃 아웃박스를 조회한다")
        void execute_WithCustomTimeoutSeconds_QueriesWithCorrectThreshold() {
            // given
            RecoverTimeoutQnaOutboxCommand command =
                    QnaCommandFixtures.recoverTimeoutOutboxCommand(30, 600);

            given(
                            readManager.findProcessingTimeoutOutboxes(
                                    Mockito.any(Instant.class), Mockito.eq(30)))
                    .willReturn(List.of());

            // when
            sut.execute(command);

            // then
            then(readManager)
                    .should()
                    .findProcessingTimeoutOutboxes(Mockito.any(Instant.class), Mockito.eq(30));
        }
    }
}
