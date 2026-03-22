package com.ryuqq.marketplace.application.qna.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.qna.QnaCommandFixtures;
import com.ryuqq.marketplace.application.qna.dto.command.AnswerQnaCommand;
import com.ryuqq.marketplace.application.qna.manager.QnaCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaOutboxCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaReadManager;
import com.ryuqq.marketplace.domain.qna.QnaFixtures;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("AnswerQnaService 단위 테스트")
class AnswerQnaServiceTest {

    @InjectMocks private AnswerQnaService sut;

    @Mock private QnaReadManager readManager;
    @Mock private QnaCommandManager commandManager;
    @Mock private QnaOutboxCommandManager outboxCommandManager;
    @Mock private ApplicationEventPublisher eventPublisher;

    @Nested
    @DisplayName("execute() - QnA 답변 등록")
    class ExecuteTest {

        @Test
        @DisplayName("PENDING 상태 QnA에 답변 등록 시 상태가 ANSWERED로 변경된다")
        void execute_PendingQna_ChangesStatusToAnswered() {
            // given
            Qna qna = QnaFixtures.pendingQna();
            AnswerQnaCommand command = QnaCommandFixtures.answerCommand(qna.idValue());
            given(readManager.getById(command.qnaId())).willReturn(qna);

            // when
            sut.execute(command);

            // then
            assertThat(qna.status()).isEqualTo(QnaStatus.ANSWERED);
        }

        @Test
        @DisplayName("답변 등록 성공 시 QnA와 아웃박스를 각각 persist한다")
        void execute_PendingQna_PersistsBothQnaAndOutbox() {
            // given
            Qna qna = QnaFixtures.pendingQna();
            AnswerQnaCommand command = QnaCommandFixtures.answerCommand(qna.idValue());
            given(readManager.getById(command.qnaId())).willReturn(qna);

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persist(qna);
            then(outboxCommandManager).should().persist(any(QnaOutbox.class));
        }

        @Test
        @DisplayName("답변 등록 성공 시 QnaAnsweredEvent를 발행한다")
        void execute_PendingQna_PublishesQnaAnsweredEvent() {
            // given
            Qna qna = QnaFixtures.pendingQna();
            AnswerQnaCommand command = QnaCommandFixtures.answerCommand(qna.idValue());
            given(readManager.getById(command.qnaId())).willReturn(qna);

            // when
            sut.execute(command);

            // then
            then(eventPublisher)
                    .should()
                    .publishEvent(
                            isA(com.ryuqq.marketplace.domain.qna.event.QnaAnsweredEvent.class));
        }

        @Test
        @DisplayName("ANSWERED 상태 QnA에 답변 시도 시 IllegalStateException이 발생한다")
        void execute_AnsweredQna_ThrowsIllegalStateException() {
            // given
            Qna qna = QnaFixtures.answeredQna();
            AnswerQnaCommand command = QnaCommandFixtures.answerCommand(qna.idValue());
            given(readManager.getById(command.qnaId())).willReturn(qna);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("ANSWERED 상태 QnA에 답변 시도 시 아웃박스에 persist를 호출하지 않는다")
        void execute_AnsweredQna_DoesNotPersistOutbox() {
            // given
            Qna qna = QnaFixtures.answeredQna();
            AnswerQnaCommand command = QnaCommandFixtures.answerCommand(qna.idValue());
            given(readManager.getById(command.qnaId())).willReturn(qna);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(IllegalStateException.class);

            then(outboxCommandManager).shouldHaveNoInteractions();
        }
    }
}
