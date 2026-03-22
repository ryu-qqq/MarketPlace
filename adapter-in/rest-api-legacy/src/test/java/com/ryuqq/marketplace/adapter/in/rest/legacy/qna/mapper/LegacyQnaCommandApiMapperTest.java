package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.LegacyQnAApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyCreateQnaAnswerRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyQnaContentsRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyUpdateQnaAnswerRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyCreateQnaAnswerResponse;
import com.ryuqq.marketplace.application.qna.dto.command.AnswerQnaCommand;
import com.ryuqq.marketplace.application.qna.dto.command.UpdateQnaReplyCommand;
import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;
import com.ryuqq.marketplace.domain.qna.vo.QnaReplyType;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyQnaCommandApiMapper 단위 테스트")
class LegacyQnaCommandApiMapperTest {

    private LegacyQnaCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyQnaCommandApiMapper();
    }

    @Nested
    @DisplayName("toAnswerCommand - 답변 등록 요청을 AnswerQnaCommand로 변환")
    class ToAnswerCommandTest {

        @Test
        @DisplayName("답변 등록 요청의 모든 필드가 Command에 올바르게 매핑된다")
        void toAnswerCommand_AllFields_MappedCorrectly() {
            // given
            LegacyCreateQnaAnswerRequest request = LegacyQnAApiFixtures.createAnswerRequest();

            // when
            AnswerQnaCommand command = mapper.toAnswerCommand(request);

            // then
            assertThat(command.qnaId()).isEqualTo(LegacyQnAApiFixtures.DEFAULT_QNA_ID);
            assertThat(command.title()).isEqualTo("답변 제목");
            assertThat(command.content()).isEqualTo("답변 내용입니다.");
            assertThat(command.authorName()).isEqualTo("SELLER");
        }

        @Test
        @DisplayName("parentReplyId는 null로 매핑된다")
        void toAnswerCommand_ParentReplyId_IsNull() {
            // given
            LegacyCreateQnaAnswerRequest request = LegacyQnAApiFixtures.createAnswerRequest();

            // when
            AnswerQnaCommand command = mapper.toAnswerCommand(request);

            // then
            assertThat(command.parentReplyId()).isNull();
        }

        @Test
        @DisplayName("qnaContents가 null이면 title과 content는 빈 문자열로 변환된다")
        void toAnswerCommand_NullContents_ConvertedToEmpty() {
            // given
            LegacyCreateQnaAnswerRequest request =
                    new LegacyCreateQnaAnswerRequest(
                            LegacyQnAApiFixtures.DEFAULT_QNA_ID, null, List.of());

            // when
            AnswerQnaCommand command = mapper.toAnswerCommand(request);

            // then
            assertThat(command.title()).isEqualTo("");
            assertThat(command.content()).isEqualTo("");
        }
    }

    @Nested
    @DisplayName("toUpdateCommand - 답변 수정 요청을 UpdateQnaReplyCommand로 변환")
    class ToUpdateCommandTest {

        @Test
        @DisplayName("답변 수정 요청의 모든 필드가 Command에 올바르게 매핑된다")
        void toUpdateCommand_AllFields_MappedCorrectly() {
            // given
            LegacyUpdateQnaAnswerRequest request = LegacyQnAApiFixtures.updateAnswerRequest();

            // when
            UpdateQnaReplyCommand command = mapper.toUpdateCommand(request);

            // then
            assertThat(command.qnaId()).isEqualTo(LegacyQnAApiFixtures.DEFAULT_QNA_ID);
            assertThat(command.replyId()).isEqualTo(LegacyQnAApiFixtures.DEFAULT_QNA_ANSWER_ID);
            assertThat(command.content()).isEqualTo("수정된 답변 내용입니다.");
        }

        @Test
        @DisplayName("qnaContents가 null이면 content는 빈 문자열로 변환된다")
        void toUpdateCommand_NullContents_ConvertedToEmpty() {
            // given
            LegacyUpdateQnaAnswerRequest request =
                    new LegacyUpdateQnaAnswerRequest(
                            LegacyQnAApiFixtures.DEFAULT_QNA_ANSWER_ID,
                            LegacyQnAApiFixtures.DEFAULT_QNA_ID,
                            null,
                            List.of());

            // when
            UpdateQnaReplyCommand command = mapper.toUpdateCommand(request);

            // then
            assertThat(command.content()).isEqualTo("");
        }
    }

    @Nested
    @DisplayName("toCreateAnswerResponse - QnaReplyResult를 LegacyCreateQnaAnswerResponse로 변환")
    class ToCreateAnswerResponseTest {

        @Test
        @DisplayName("qnaId와 QnaReplyResult를 받아 응답 DTO로 변환한다")
        void toCreateAnswerResponse_ConvertsResult_ReturnsResponse() {
            // given
            QnaReplyResult replyResult = new QnaReplyResult(
                    LegacyQnAApiFixtures.DEFAULT_QNA_ANSWER_ID,
                    null,
                    "답변 내용입니다.",
                    "SELLER",
                    QnaReplyType.SELLER_ANSWER,
                    Instant.now());

            // when
            LegacyCreateQnaAnswerResponse response =
                    mapper.toCreateAnswerResponse(LegacyQnAApiFixtures.DEFAULT_QNA_ID, replyResult);

            // then
            assertThat(response.qnaId()).isEqualTo(LegacyQnAApiFixtures.DEFAULT_QNA_ID);
            assertThat(response.qnaAnswerId()).isEqualTo(LegacyQnAApiFixtures.DEFAULT_QNA_ANSWER_ID);
        }

        @Test
        @DisplayName("replyType이 null이면 qnaType은 빈 문자열로 변환된다")
        void toCreateAnswerResponse_NullReplyType_ReturnsEmptyQnaType() {
            // given
            QnaReplyResult replyResult = new QnaReplyResult(
                    LegacyQnAApiFixtures.DEFAULT_QNA_ANSWER_ID,
                    null,
                    "답변 내용입니다.",
                    "SELLER",
                    null,
                    Instant.now());

            // when
            LegacyCreateQnaAnswerResponse response =
                    mapper.toCreateAnswerResponse(LegacyQnAApiFixtures.DEFAULT_QNA_ID, replyResult);

            // then
            assertThat(response.qnaType()).isEqualTo("");
        }

        @Test
        @DisplayName("qnaImages는 항상 빈 리스트로 반환된다")
        void toCreateAnswerResponse_QnaImages_IsEmpty() {
            // given
            QnaReplyResult replyResult = new QnaReplyResult(
                    LegacyQnAApiFixtures.DEFAULT_QNA_ANSWER_ID,
                    null,
                    "답변 내용입니다.",
                    "SELLER",
                    QnaReplyType.SELLER_ANSWER,
                    Instant.now());

            // when
            LegacyCreateQnaAnswerResponse response =
                    mapper.toCreateAnswerResponse(LegacyQnAApiFixtures.DEFAULT_QNA_ID, replyResult);

            // then
            assertThat(response.qnaImages()).isEmpty();
        }
    }
}
