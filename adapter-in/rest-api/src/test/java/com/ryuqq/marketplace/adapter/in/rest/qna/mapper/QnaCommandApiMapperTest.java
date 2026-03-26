package com.ryuqq.marketplace.adapter.in.rest.qna.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.qna.QnaApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.qna.dto.request.AnswerQnaApiRequest;
import com.ryuqq.marketplace.application.qna.dto.command.AnswerQnaCommand;
import com.ryuqq.marketplace.application.qna.dto.command.CloseQnaCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QnaCommandApiMapper 단위 테스트")
class QnaCommandApiMapperTest {

    private QnaCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new QnaCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(long, AnswerQnaApiRequest) - 답변 등록 Command 변환")
    class ToCommandTest {

        @Test
        @DisplayName("qnaId와 content가 정확히 Command로 변환된다")
        void toCommand_ValidRequest_ReturnsCommand() {
            // given
            long qnaId = QnaApiFixtures.DEFAULT_QNA_ID;
            AnswerQnaApiRequest request = QnaApiFixtures.answerRequest();

            // when
            AnswerQnaCommand command = mapper.toCommand(qnaId, request);

            // then
            assertThat(command.qnaId()).isEqualTo(qnaId);
            assertThat(command.content()).isEqualTo(QnaApiFixtures.DEFAULT_ANSWER_CONTENT);
            assertThat(command.authorName()).isEqualTo(QnaApiFixtures.DEFAULT_AUTHOR_NAME);
            assertThat(command.parentReplyId()).isNull();
        }

        @Test
        @DisplayName("parentReplyId가 있으면 Command에 그대로 전달된다")
        void toCommand_WithParentReplyId_IsIncludedInCommand() {
            // given
            long qnaId = QnaApiFixtures.DEFAULT_QNA_ID;
            Long parentReplyId = 5L;
            AnswerQnaApiRequest request = QnaApiFixtures.answerRequestWithParent(parentReplyId);

            // when
            AnswerQnaCommand command = mapper.toCommand(qnaId, request);

            // then
            assertThat(command.parentReplyId()).isEqualTo(parentReplyId);
        }

        @Test
        @DisplayName("parentReplyId가 null이면 Command에도 null로 전달된다")
        void toCommand_NullParentReplyId_IsNullInCommand() {
            // given
            long qnaId = QnaApiFixtures.DEFAULT_QNA_ID;
            AnswerQnaApiRequest request = new AnswerQnaApiRequest(
                    QnaApiFixtures.DEFAULT_ANSWER_CONTENT,
                    QnaApiFixtures.DEFAULT_AUTHOR_NAME,
                    null);

            // when
            AnswerQnaCommand command = mapper.toCommand(qnaId, request);

            // then
            assertThat(command.parentReplyId()).isNull();
        }

        @Test
        @DisplayName("다른 qnaId도 정확히 Command에 전달된다")
        void toCommand_DifferentQnaId_IsCorrectlyMapped() {
            // given
            long qnaId = 999L;
            AnswerQnaApiRequest request = QnaApiFixtures.answerRequest();

            // when
            AnswerQnaCommand command = mapper.toCommand(qnaId, request);

            // then
            assertThat(command.qnaId()).isEqualTo(999L);
        }

        @Test
        @DisplayName("다른 content와 authorName도 정확히 Command에 전달된다")
        void toCommand_DifferentContentAndAuthor_AreCorrectlyMapped() {
            // given
            long qnaId = QnaApiFixtures.DEFAULT_QNA_ID;
            AnswerQnaApiRequest request = QnaApiFixtures.answerRequest("직접 입력한 답변입니다.", "판매자B");

            // when
            AnswerQnaCommand command = mapper.toCommand(qnaId, request);

            // then
            assertThat(command.content()).isEqualTo("직접 입력한 답변입니다.");
            assertThat(command.authorName()).isEqualTo("판매자B");
        }
    }

    @Nested
    @DisplayName("toCloseCommand(long) - QnA 종결 Command 변환")
    class ToCloseCommandTest {

        @Test
        @DisplayName("qnaId가 정확히 CloseQnaCommand로 변환된다")
        void toCloseCommand_ValidQnaId_ReturnsCloseCommand() {
            // given
            long qnaId = QnaApiFixtures.DEFAULT_QNA_ID;

            // when
            CloseQnaCommand command = mapper.toCloseCommand(qnaId);

            // then
            assertThat(command.qnaId()).isEqualTo(qnaId);
        }

        @Test
        @DisplayName("다른 qnaId도 정확히 CloseQnaCommand에 전달된다")
        void toCloseCommand_DifferentQnaId_IsCorrectlyMapped() {
            // given
            long qnaId = 42L;

            // when
            CloseQnaCommand command = mapper.toCloseCommand(qnaId);

            // then
            assertThat(command.qnaId()).isEqualTo(42L);
        }
    }
}
