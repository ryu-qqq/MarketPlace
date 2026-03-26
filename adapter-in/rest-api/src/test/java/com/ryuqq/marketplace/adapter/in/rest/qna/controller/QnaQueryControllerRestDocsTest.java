package com.ryuqq.marketplace.adapter.in.rest.qna.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.qna.QnaApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.qna.QnaEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.qna.dto.response.QnaApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.qna.mapper.QnaQueryApiMapper;
import com.ryuqq.marketplace.application.qna.dto.result.QnaListResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.application.qna.port.in.query.GetQnaDetailUseCase;
import com.ryuqq.marketplace.application.qna.port.in.query.GetQnaListUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(QnaQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("QnaQueryController REST Docs 테스트")
class QnaQueryControllerRestDocsTest {

    private static final String BASE_URL = QnaEndpoints.QNAS;
    private static final long QNA_ID = QnaApiFixtures.DEFAULT_QNA_ID;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetQnaListUseCase getQnaListUseCase;
    @MockitoBean private GetQnaDetailUseCase getQnaDetailUseCase;
    @MockitoBean private QnaQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("QnA 목록 조회 API")
    class SearchQnasTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchQnasByOffset_ValidRequest_Returns200WithPage() throws Exception {
            // given
            QnaListResult listResult = QnaApiFixtures.listResult(3, 0, 20);
            PageApiResponse<QnaApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    QnaApiFixtures.qnaApiResponse(1L),
                                    QnaApiFixtures.qnaApiResponse(2L),
                                    QnaApiFixtures.qnaApiResponse(3L)),
                            0,
                            20,
                            3);

            given(getQnaListUseCase.execute(anyLong(), any(), anyInt(), anyInt()))
                    .willReturn(listResult);
            given(mapper.toPageResponse(any(QnaListResult.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("sellerId", "10")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(3))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(20))
                    .andExpect(jsonPath("$.data.totalElements").value(3))
                    .andDo(
                            document(
                                    "qna/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("sellerId")
                                                    .description("셀러 ID"),
                                            parameterWithName("status")
                                                    .description(
                                                            "QnA 상태 필터 (PENDING, ANSWERED, CLOSED)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0-based). 기본값: 0")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기. 기본값: 20")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("QnA 목록"),
                                            fieldWithPath("data.content[].qnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA ID"),
                                            fieldWithPath("data.content[].sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("data.content[].productGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품그룹 ID"),
                                            fieldWithPath("data.content[].orderId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("주문 ID (주문 문의일 때, nullable)")
                                                    .optional(),
                                            fieldWithPath("data.content[].qnaType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "문의 유형 (PRODUCT, SHIPPING, ORDER, EXCHANGE, REFUND, RESTOCK, PRICE, ETC)"),
                                            fieldWithPath("data.content[].salesChannelId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 ID"),
                                            fieldWithPath("data.content[].externalQnaId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 QnA ID"),
                                            fieldWithPath("data.content[].questionTitle")
                                                    .type(JsonFieldType.STRING)
                                                    .description("질문 제목"),
                                            fieldWithPath("data.content[].questionContent")
                                                    .type(JsonFieldType.STRING)
                                                    .description("질문 내용"),
                                            fieldWithPath("data.content[].questionAuthor")
                                                    .type(JsonFieldType.STRING)
                                                    .description("질문자"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "QnA 상태 (PENDING, ANSWERED, CLOSED)"),
                                            fieldWithPath("data.content[].replies")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("답변 목록"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시"),
                                            fieldWithPath("data.content[].updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
                                            fieldWithPath("data.page")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재 페이지 번호"),
                                            fieldWithPath("data.size")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("페이지 크기"),
                                            fieldWithPath("data.totalElements")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 데이터 수"),
                                            fieldWithPath("data.totalPages")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 페이지 수"),
                                            fieldWithPath("data.first")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("첫 페이지 여부"),
                                            fieldWithPath("data.last")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("마지막 페이지 여부"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("상태 필터를 포함한 요청이 정상 처리된다")
        void searchQnasByOffset_WithStatusFilter_Returns200() throws Exception {
            // given
            QnaListResult listResult = QnaApiFixtures.listResult(1, 0, 20);
            PageApiResponse<QnaApiResponse> pageResponse =
                    PageApiResponse.of(List.of(QnaApiFixtures.qnaApiResponse(1L)), 0, 20, 1);

            given(getQnaListUseCase.execute(anyLong(), any(), anyInt(), anyInt()))
                    .willReturn(listResult);
            given(mapper.toPageResponse(any(QnaListResult.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("sellerId", "10")
                                    .param("status", "PENDING")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchQnasByOffset_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            QnaListResult listResult = QnaApiFixtures.emptyListResult();
            PageApiResponse<QnaApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(getQnaListUseCase.execute(anyLong(), any(), anyInt(), anyInt()))
                    .willReturn(listResult);
            given(mapper.toPageResponse(any(QnaListResult.class))).willReturn(emptyResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("sellerId", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("QnA 상세 조회 API")
    class GetQnaTest {

        @Test
        @DisplayName("qnaId로 QnA 상세 조회 성공")
        void getQna_ValidQnaId_Returns200WithDetail() throws Exception {
            // given
            QnaResult result = QnaApiFixtures.qnaResultWithReplies(QNA_ID);
            QnaApiResponse response = QnaApiFixtures.qnaApiResponseWithReplies(QNA_ID);

            given(getQnaDetailUseCase.execute(QNA_ID)).willReturn(result);
            given(mapper.toResponse(any(QnaResult.class))).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + QnaEndpoints.QNA_ID, QNA_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.qnaId").value(QNA_ID))
                    .andExpect(jsonPath("$.data.sellerId").value(QnaApiFixtures.DEFAULT_SELLER_ID))
                    .andExpect(jsonPath("$.data.status").value("ANSWERED"))
                    .andExpect(jsonPath("$.data.replies").isArray())
                    .andExpect(jsonPath("$.data.replies.length()").value(2))
                    .andDo(
                            document(
                                    "qna/get-detail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("qnaId").description("QnA ID")),
                                    responseFields(
                                            fieldWithPath("data.qnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA ID"),
                                            fieldWithPath("data.sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("data.productGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품그룹 ID"),
                                            fieldWithPath("data.orderId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("주문 ID (주문 문의일 때, nullable)")
                                                    .optional(),
                                            fieldWithPath("data.qnaType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "문의 유형 (PRODUCT, SHIPPING, ORDER, EXCHANGE, REFUND, RESTOCK, PRICE, ETC)"),
                                            fieldWithPath("data.salesChannelId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 ID"),
                                            fieldWithPath("data.externalQnaId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 QnA ID"),
                                            fieldWithPath("data.questionTitle")
                                                    .type(JsonFieldType.STRING)
                                                    .description("질문 제목"),
                                            fieldWithPath("data.questionContent")
                                                    .type(JsonFieldType.STRING)
                                                    .description("질문 내용"),
                                            fieldWithPath("data.questionAuthor")
                                                    .type(JsonFieldType.STRING)
                                                    .description("질문자"),
                                            fieldWithPath("data.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "QnA 상태 (PENDING, ANSWERED, CLOSED)"),
                                            fieldWithPath("data.replies[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("답변 목록"),
                                            fieldWithPath("data.replies[].replyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("답변 ID"),
                                            fieldWithPath("data.replies[].parentReplyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("부모 답변 ID (대댓글)")
                                                    .optional(),
                                            fieldWithPath("data.replies[].content")
                                                    .type(JsonFieldType.STRING)
                                                    .description("답변 내용"),
                                            fieldWithPath("data.replies[].authorName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("답변자"),
                                            fieldWithPath("data.replies[].replyType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "답변 유형 (SELLER_ANSWER, BUYER_FOLLOW_UP)"),
                                            fieldWithPath("data.replies[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("답변 생성일시"),
                                            fieldWithPath("data.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시"),
                                            fieldWithPath("data.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("답변이 없는 QnA 상세 조회 성공")
        void getQna_WithNoReplies_Returns200WithEmptyReplies() throws Exception {
            // given
            QnaResult result = QnaApiFixtures.qnaResult(QNA_ID);
            QnaApiResponse response = QnaApiFixtures.qnaApiResponse(QNA_ID);

            given(getQnaDetailUseCase.execute(QNA_ID)).willReturn(result);
            given(mapper.toResponse(any(QnaResult.class))).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + QnaEndpoints.QNA_ID, QNA_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.qnaId").value(QNA_ID))
                    .andExpect(jsonPath("$.data.status").value("PENDING"))
                    .andExpect(jsonPath("$.data.replies").isArray())
                    .andExpect(jsonPath("$.data.replies.length()").value(0));
        }
    }
}
