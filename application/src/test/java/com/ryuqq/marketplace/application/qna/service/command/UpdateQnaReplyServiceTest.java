package com.ryuqq.marketplace.application.qna.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.qna.QnaCommandFixtures;
import com.ryuqq.marketplace.application.qna.dto.command.UpdateQnaReplyCommand;
import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;
import com.ryuqq.marketplace.application.qna.manager.QnaCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaReadManager;
import com.ryuqq.marketplace.domain.qna.QnaFixtures;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
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
@DisplayName("UpdateQnaReplyService 단위 테스트")
class UpdateQnaReplyServiceTest {

    @InjectMocks private UpdateQnaReplyService sut;

    @Mock private QnaReadManager readManager;
    @Mock private QnaCommandManager commandManager;

    @Nested
    @DisplayName("execute() - QnA 답변 수정")
    class ExecuteTest {

        @Test
        @DisplayName("ANSWERED 상태 QnA의 답변을 수정하고 수정된 결과를 반환한다")
        void execute_AnsweredQna_UpdatesReplyAndReturnsResult() {
            // given
            Qna qna = QnaFixtures.answeredQna();
            long replyId = qna.replies().get(0).idValue();
            String updatedContent = "수정된 답변 내용입니다.";
            UpdateQnaReplyCommand command = new UpdateQnaReplyCommand(qna.idValue(), replyId, updatedContent);
            given(readManager.getById(command.qnaId())).willReturn(qna);

            // when
            QnaReplyResult result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.replyId()).isEqualTo(replyId);
            assertThat(result.content()).isEqualTo(updatedContent);
        }

        @Test
        @DisplayName("답변 수정 성공 시 QnA를 persist한다")
        void execute_AnsweredQna_PersistsQna() {
            // given
            Qna qna = QnaFixtures.answeredQna();
            long replyId = qna.replies().get(0).idValue();
            UpdateQnaReplyCommand command = new UpdateQnaReplyCommand(qna.idValue(), replyId, "수정된 내용");
            given(readManager.getById(command.qnaId())).willReturn(qna);

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persist(qna);
        }

        @Test
        @DisplayName("존재하지 않는 replyId로 수정 시도 시 예외가 발생한다")
        void execute_NonExistentReplyId_ThrowsException() {
            // given
            Qna qna = QnaFixtures.answeredQna();
            long nonExistentReplyId = 999L;
            UpdateQnaReplyCommand command = new UpdateQnaReplyCommand(qna.idValue(), nonExistentReplyId, "수정 내용");
            given(readManager.getById(command.qnaId())).willReturn(qna);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("반환된 QnaReplyResult에 authorName이 포함된다")
        void execute_AnsweredQna_ResultContainsAuthorName() {
            // given
            Qna qna = QnaFixtures.answeredQna();
            long replyId = qna.replies().get(0).idValue();
            UpdateQnaReplyCommand command = new UpdateQnaReplyCommand(qna.idValue(), replyId, "수정된 내용");
            given(readManager.getById(command.qnaId())).willReturn(qna);

            // when
            QnaReplyResult result = sut.execute(command);

            // then
            assertThat(result.authorName()).isEqualTo(QnaFixtures.DEFAULT_ANSWER_AUTHOR);
        }
    }
}
