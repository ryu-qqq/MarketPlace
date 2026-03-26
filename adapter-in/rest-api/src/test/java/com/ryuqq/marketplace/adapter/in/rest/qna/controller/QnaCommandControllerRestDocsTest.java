package com.ryuqq.marketplace.adapter.in.rest.qna.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.qna.QnaApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.qna.QnaEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.qna.dto.request.AnswerQnaApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.qna.mapper.QnaCommandApiMapper;
import com.ryuqq.marketplace.application.qna.dto.command.AnswerQnaCommand;
import com.ryuqq.marketplace.application.qna.dto.command.CloseQnaCommand;
import com.ryuqq.marketplace.application.qna.port.in.command.AnswerQnaUseCase;
import com.ryuqq.marketplace.application.qna.port.in.command.CloseQnaUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(QnaCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("QnaCommandController REST Docs 테스트")
class QnaCommandControllerRestDocsTest {

    private static final String BASE_URL = QnaEndpoints.QNAS;
    private static final long QNA_ID = QnaApiFixtures.DEFAULT_QNA_ID;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private AnswerQnaUseCase answerQnaUseCase;
    @MockitoBean private CloseQnaUseCase closeQnaUseCase;
    @MockitoBean private QnaCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("QnA 답변 등록 API")
    class AnswerQnaTest {

        @Test
        @DisplayName("유효한 답변 요청이면 201을 반환한다")
        void answerQna_ValidRequest_Returns201() throws Exception {
            // given
            AnswerQnaApiRequest request = QnaApiFixtures.answerRequest();
            AnswerQnaCommand command = QnaApiFixtures.answerCommand(QNA_ID);

            given(mapper.toCommand(anyLong(), any(AnswerQnaApiRequest.class))).willReturn(command);
            given(answerQnaUseCase.execute(any(AnswerQnaCommand.class))).willReturn(null);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                    BASE_URL + QnaEndpoints.ANSWER, QNA_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andDo(
                            document(
                                    "qna/answer",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("qnaId").description("QnA ID")),
                                    requestFields(
                                            fieldWithPath("content")
                                                    .type(JsonFieldType.STRING)
                                                    .description("답변 내용"),
                                            fieldWithPath("authorName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("답변자 이름"),
                                            fieldWithPath("parentReplyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("부모 답변 ID (대댓글 시 입력, 없으면 null)")
                                                    .optional())));
        }

        @Test
        @DisplayName("대댓글 답변 요청이면 201을 반환한다")
        void answerQna_WithParentReplyId_Returns201() throws Exception {
            // given
            AnswerQnaApiRequest request = QnaApiFixtures.answerRequestWithParent(1L);
            AnswerQnaCommand command = new AnswerQnaCommand(
                    QNA_ID,
                    "",
                    QnaApiFixtures.DEFAULT_ANSWER_CONTENT,
                    QnaApiFixtures.DEFAULT_AUTHOR_NAME,
                    1L);

            given(mapper.toCommand(anyLong(), any(AnswerQnaApiRequest.class))).willReturn(command);
            given(answerQnaUseCase.execute(any(AnswerQnaCommand.class))).willReturn(null);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                    BASE_URL + QnaEndpoints.ANSWER, QNA_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("content가 blank이면 400을 반환한다")
        void answerQna_BlankContent_Returns400() throws Exception {
            // given
            AnswerQnaApiRequest request = new AnswerQnaApiRequest("", QnaApiFixtures.DEFAULT_AUTHOR_NAME, null);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                    BASE_URL + QnaEndpoints.ANSWER, QNA_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("authorName이 blank이면 400을 반환한다")
        void answerQna_BlankAuthorName_Returns400() throws Exception {
            // given
            AnswerQnaApiRequest request = new AnswerQnaApiRequest(QnaApiFixtures.DEFAULT_ANSWER_CONTENT, "", null);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                    BASE_URL + QnaEndpoints.ANSWER, QNA_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("QnA 종결 API")
    class CloseQnaTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void closeQna_ValidRequest_Returns204() throws Exception {
            // given
            CloseQnaCommand command = QnaApiFixtures.closeCommand(QNA_ID);

            given(mapper.toCloseCommand(anyLong())).willReturn(command);
            doNothing().when(closeQnaUseCase).execute(any(CloseQnaCommand.class));

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                    BASE_URL + QnaEndpoints.CLOSE, QNA_ID))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "qna/close",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("qnaId")
                                                    .description("종결할 QnA ID"))));
        }

        @Test
        @DisplayName("다른 qnaId로도 종결 요청이 정상 처리된다")
        void closeQna_DifferentQnaId_Returns204() throws Exception {
            // given
            long anotherQnaId = 99L;
            CloseQnaCommand command = QnaApiFixtures.closeCommand(anotherQnaId);

            given(mapper.toCloseCommand(anyLong())).willReturn(command);
            doNothing().when(closeQnaUseCase).execute(any(CloseQnaCommand.class));

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                    BASE_URL + QnaEndpoints.CLOSE, anotherQnaId))
                    .andExpect(status().isNoContent());
        }
    }
}
