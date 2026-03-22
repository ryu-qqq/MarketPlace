package com.ryuqq.marketplace.application.qna.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.qna.QnaCommandFixtures;
import com.ryuqq.marketplace.application.qna.dto.command.ProcessPendingQnaOutboxCommand;
import com.ryuqq.marketplace.application.qna.internal.QnaOutboxRelayProcessor;
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
@DisplayName("ProcessPendingQnaOutboxService 단위 테스트")
class ProcessPendingQnaOutboxServiceTest {

    @InjectMocks private ProcessPendingQnaOutboxService sut;

    @Mock private QnaOutboxReadManager readManager;
    @Mock private QnaOutboxRelayProcessor relayProcessor;

    @Nested
    @DisplayName("execute() - PENDING QnA 아웃박스 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("PENDING 아웃박스가 있으면 각 아웃박스에 relay를 호출한다")
        void execute_PendingOutboxes_RelaysEachOutbox() {
            // given
            ProcessPendingQnaOutboxCommand command =
                    QnaCommandFixtures.processPendingOutboxCommand();
            QnaOutbox outbox1 = Mockito.mock(QnaOutbox.class);
            QnaOutbox outbox2 = Mockito.mock(QnaOutbox.class);
            Instant beforeTime = Instant.now().minusSeconds(command.delaySeconds());

            given(
                            readManager.findPendingOutboxes(
                                    Mockito.argThat(t -> t.isBefore(Instant.now())),
                                    Mockito.eq(command.batchSize())))
                    .willReturn(List.of(outbox1, outbox2));

            // when
            sut.execute(command);

            // then
            then(relayProcessor).should().relay(outbox1);
            then(relayProcessor).should().relay(outbox2);
        }

        @Test
        @DisplayName("PENDING 아웃박스가 없으면 relay를 호출하지 않는다")
        void execute_NoPendingOutboxes_DoesNotCallRelay() {
            // given
            ProcessPendingQnaOutboxCommand command =
                    QnaCommandFixtures.processPendingOutboxCommand();

            given(
                            readManager.findPendingOutboxes(
                                    Mockito.any(Instant.class), Mockito.eq(command.batchSize())))
                    .willReturn(List.of());

            // when
            sut.execute(command);

            // then
            then(relayProcessor).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("batchSize에 따라 올바른 크기로 아웃박스를 조회한다")
        void execute_WithCustomBatchSize_QueriesWithCorrectBatchSize() {
            // given
            ProcessPendingQnaOutboxCommand command =
                    QnaCommandFixtures.processPendingOutboxCommand(50, 10);

            given(readManager.findPendingOutboxes(Mockito.any(Instant.class), Mockito.eq(50)))
                    .willReturn(List.of());

            // when
            sut.execute(command);

            // then
            then(readManager)
                    .should()
                    .findPendingOutboxes(Mockito.any(Instant.class), Mockito.eq(50));
        }
    }
}
