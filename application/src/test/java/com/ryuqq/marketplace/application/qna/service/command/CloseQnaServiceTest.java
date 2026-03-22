package com.ryuqq.marketplace.application.qna.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.qna.QnaCommandFixtures;
import com.ryuqq.marketplace.application.qna.dto.command.CloseQnaCommand;
import com.ryuqq.marketplace.application.qna.manager.QnaCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaReadManager;
import com.ryuqq.marketplace.domain.qna.QnaFixtures;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
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
@DisplayName("CloseQnaService 단위 테스트")
class CloseQnaServiceTest {

    @InjectMocks private CloseQnaService sut;

    @Mock private QnaReadManager readManager;
    @Mock private QnaCommandManager commandManager;

    @Nested
    @DisplayName("execute() - QnA 종결")
    class ExecuteTest {

        @Test
        @DisplayName("ANSWERED 상태 QnA 종결 시 상태가 CLOSED로 변경된다")
        void execute_AnsweredQna_ChangesStatusToClosed() {
            // given
            Qna qna = QnaFixtures.answeredQna();
            CloseQnaCommand command = QnaCommandFixtures.closeCommand(qna.idValue());
            given(readManager.getById(command.qnaId())).willReturn(qna);

            // when
            sut.execute(command);

            // then
            assertThat(qna.status()).isEqualTo(QnaStatus.CLOSED);
        }

        @Test
        @DisplayName("ANSWERED 상태 QnA 종결 시 QnA를 persist한다")
        void execute_AnsweredQna_PersistsQna() {
            // given
            Qna qna = QnaFixtures.answeredQna();
            CloseQnaCommand command = QnaCommandFixtures.closeCommand(qna.idValue());
            given(readManager.getById(command.qnaId())).willReturn(qna);

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persist(qna);
        }

        @Test
        @DisplayName("PENDING 상태 QnA 종결 시도 시 IllegalStateException이 발생한다")
        void execute_PendingQna_ThrowsIllegalStateException() {
            // given
            Qna qna = QnaFixtures.pendingQna();
            CloseQnaCommand command = QnaCommandFixtures.closeCommand(qna.idValue());
            given(readManager.getById(command.qnaId())).willReturn(qna);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("PENDING 상태 QnA 종결 실패 시 persist를 호출하지 않는다")
        void execute_PendingQna_DoesNotPersist() {
            // given
            Qna qna = QnaFixtures.pendingQna();
            CloseQnaCommand command = QnaCommandFixtures.closeCommand(qna.idValue());
            given(readManager.getById(command.qnaId())).willReturn(qna);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(IllegalStateException.class);

            then(commandManager).shouldHaveNoInteractions();
        }
    }
}
