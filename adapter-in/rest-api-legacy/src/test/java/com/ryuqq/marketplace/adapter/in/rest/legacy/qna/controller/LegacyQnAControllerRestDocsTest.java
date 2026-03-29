package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.LegacyQnAApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.LegacyQnAEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyCreateQnaAnswerRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyUpdateQnaAnswerRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyCreateQnaAnswerResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyDetailQnaResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyFetchQnaResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.mapper.LegacyQnaCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.mapper.LegacyQnaQueryApiMapper;
import com.ryuqq.marketplace.application.legacy.qna.dto.query.LegacyQnaSearchParams;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaPageResult;
import com.ryuqq.marketplace.application.legacy.qna.port.in.LegacyQnaDetailQueryUseCase;
import com.ryuqq.marketplace.application.legacy.qna.port.in.LegacyQnaListQueryUseCase;
import com.ryuqq.marketplace.application.qna.dto.command.AnswerQnaCommand;
import com.ryuqq.marketplace.application.qna.dto.command.UpdateQnaReplyCommand;
import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;
import com.ryuqq.marketplace.application.qna.port.in.command.AnswerQnaUseCase;
import com.ryuqq.marketplace.application.qna.port.in.command.UpdateQnaReplyUseCase;
import com.ryuqq.marketplace.domain.qna.vo.QnaReplyType;
import java.time.Instant;
import java.util.List;
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
@WebMvcTest(LegacyQnAController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("LegacyQnAController REST Docs 테스트")
class LegacyQnAControllerRestDocsTest {

    private static final String QNA_ID_URL = LegacyQnAEndpoints.QNA_ID;
    private static final String QNAS_URL = LegacyQnAEndpoints.QNAS;
    private static final String QNA_REPLY_URL = LegacyQnAEndpoints.QNA_REPLY;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private LegacyQnaDetailQueryUseCase legacyQnaDetailUseCase;
    @MockitoBean private LegacyQnaListQueryUseCase legacyQnaListUseCase;
    @MockitoBean private AnswerQnaUseCase answerQnaUseCase;
    @MockitoBean private UpdateQnaReplyUseCase updateQnaReplyUseCase;
    @MockitoBean private LegacyQnaQueryApiMapper queryApiMapper;
    @MockitoBean private LegacyQnaCommandApiMapper commandApiMapper;
    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @MockitoBean
    private com.ryuqq.marketplace.adapter.in.rest.legacy.common.security.LegacyAccessChecker
            legacyAccessChecker;

    @Nested
    @DisplayName("QnA 단건 상세 조회 API")
    class FetchQnaTest {

        @Test
        @DisplayName("QnA ID로 상세 조회 성공")
        void fetchQna_Success() throws Exception {
            // given
            LegacyQnaDetailResult result = LegacyQnAApiFixtures.legacyQnaDetailResult();
            LegacyDetailQnaResponse response = LegacyQnAApiFixtures.detailQnaResponse();

            given(legacyQnaDetailUseCase.execute(anyLong())).willReturn(result);
            given(queryApiMapper.toDetailResponse(any(LegacyQnaDetailResult.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    QNA_ID_URL, LegacyQnAApiFixtures.DEFAULT_QNA_ID))
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.data.qna.qnaId").value(LegacyQnAApiFixtures.DEFAULT_QNA_ID))
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andExpect(jsonPath("$.response.message").value("success"))
                    .andDo(
                            document(
                                    "legacy-qna/fetch-qna",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("qnaId").description("QnA ID")),
                                    responseFields(
                                            fieldWithPath("data.qna.qnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA ID"),
                                            fieldWithPath("data.qna.qnaContents.title")
                                                    .type(JsonFieldType.STRING)
                                                    .description("질문 제목"),
                                            fieldWithPath("data.qna.qnaContents.content")
                                                    .type(JsonFieldType.STRING)
                                                    .description("질문 내용"),
                                            fieldWithPath("data.qna.privateYn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("비공개 여부 (Y/N)"),
                                            fieldWithPath("data.qna.qnaStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description("QnA 상태 (예: PENDING, ANSWERED)"),
                                            fieldWithPath("data.qna.qnaType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("QnA 유형 (예: PRODUCT, ORDER)"),
                                            fieldWithPath("data.qna.qnaDetailType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("QnA 세부 유형"),
                                            fieldWithPath("data.qna.sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("data.qna.userInfo.userType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사용자 유형")
                                                    .optional(),
                                            fieldWithPath("data.qna.userInfo.userId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("사용자 ID")
                                                    .optional(),
                                            fieldWithPath("data.qna.userInfo.userName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사용자명"),
                                            fieldWithPath("data.qna.userInfo.phoneNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("전화번호")
                                                    .optional(),
                                            fieldWithPath("data.qna.userInfo.email")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이메일")
                                                    .optional(),
                                            fieldWithPath("data.qna.userInfo.gender")
                                                    .type(JsonFieldType.STRING)
                                                    .description("성별")
                                                    .optional(),
                                            fieldWithPath("data.qna.qnaTarget.productGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 그룹 ID"),
                                            fieldWithPath("data.qna.qnaTarget.productGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 그룹명"),
                                            fieldWithPath(
                                                            "data.qna.qnaTarget.productGroupMainImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 대표 이미지 URL"),
                                            fieldWithPath("data.qna.qnaTarget.brand.brandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("브랜드 ID"),
                                            fieldWithPath("data.qna.qnaTarget.brand.brandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("브랜드명"),
                                            fieldWithPath("data.qna.qnaTarget.paymentId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("결제 ID (주문 QnA인 경우)")
                                                    .optional(),
                                            fieldWithPath("data.qna.qnaTarget.orderId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("주문 ID (주문 QnA인 경우)")
                                                    .optional(),
                                            fieldWithPath("data.qna.qnaTarget.option")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 (주문 QnA인 경우)")
                                                    .optional(),
                                            fieldWithPath("data.qna.qnaImages")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("QnA 이미지 목록"),
                                            fieldWithPath("data.qna.qnaImages[].qnaIssueType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 이슈 유형")
                                                    .optional(),
                                            fieldWithPath("data.qna.qnaImages[].qnaImageId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA 이미지 ID")
                                                    .optional(),
                                            fieldWithPath("data.qna.qnaImages[].qnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA ID")
                                                    .optional(),
                                            fieldWithPath("data.qna.qnaImages[].qnaAnswerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA 답변 ID")
                                                    .optional(),
                                            fieldWithPath("data.qna.qnaImages[].imageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 URL"),
                                            fieldWithPath("data.qna.qnaImages[].displayOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("노출 순서"),
                                            fieldWithPath("data.qna.insertDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일시"),
                                            fieldWithPath("data.qna.updateDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
                                            fieldWithPath("data.answerQnas")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("답변 목록"),
                                            fieldWithPath("data.answerQnas[].qnaAnswerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("답변 ID"),
                                            fieldWithPath("data.answerQnas[].qnaAnswerParentId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("부모 답변 ID")
                                                    .optional(),
                                            fieldWithPath("data.answerQnas[].qnaWriterType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("작성자 유형 (예: SELLER, ADMIN)"),
                                            fieldWithPath("data.answerQnas[].qnaContents.title")
                                                    .type(JsonFieldType.STRING)
                                                    .description("답변 제목"),
                                            fieldWithPath("data.answerQnas[].qnaContents.content")
                                                    .type(JsonFieldType.STRING)
                                                    .description("답변 내용"),
                                            fieldWithPath("data.answerQnas[].qnaImages")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("답변 이미지 목록"),
                                            fieldWithPath(
                                                            "data.answerQnas[].qnaImages[].qnaIssueType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 이슈 유형")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.answerQnas[].qnaImages[].qnaImageId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA 이미지 ID")
                                                    .optional(),
                                            fieldWithPath("data.answerQnas[].qnaImages[].qnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA ID")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.answerQnas[].qnaImages[].qnaAnswerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA 답변 ID")
                                                    .optional(),
                                            fieldWithPath("data.answerQnas[].qnaImages[].imageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 URL"),
                                            fieldWithPath(
                                                            "data.answerQnas[].qnaImages[].displayOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("노출 순서"),
                                            fieldWithPath("data.answerQnas[].insertOperator")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록자"),
                                            fieldWithPath("data.answerQnas[].updateOperator")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정자"),
                                            fieldWithPath("data.answerQnas[].insertDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("답변 등록일시"),
                                            fieldWithPath("data.answerQnas[].updateDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("답변 수정일시"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }
    }

    @Nested
    @DisplayName("QnA 목록 조회 API")
    class GetQnasTest {

        @Test
        @DisplayName("QnA 목록 페이징 조회 성공")
        void getQnas_Success() throws Exception {
            // given
            LegacyQnaPageResult pageResult =
                    new LegacyQnaPageResult(
                            List.of(LegacyQnAApiFixtures.legacyQnaDetailResult()),
                            1L,
                            LegacyQnAApiFixtures.DEFAULT_QNA_ID);
            List<LegacyFetchQnaResponse> responses =
                    List.of(LegacyQnAApiFixtures.fetchQnaResponse());

            given(queryApiMapper.toSearchParams(any(), anyInt(), any()))
                    .willReturn(
                            new LegacyQnaSearchParams(
                                    null, "PRODUCT", null, null, null, 1L, null, null, null, 20));
            given(legacyQnaListUseCase.execute(any(LegacyQnaSearchParams.class)))
                    .willReturn(pageResult);
            given(queryApiMapper.toFetchResponses(any())).willReturn(responses);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(QNAS_URL)
                                    .param("qnaType", LegacyQnAApiFixtures.DEFAULT_QNA_TYPE)
                                    .param("size", "20")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andDo(
                            document(
                                    "legacy-qna/get-qnas",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("qnaType")
                                                    .description("QnA 유형 (필수, 예: PRODUCT, ORDER)"),
                                            parameterWithName("size")
                                                    .description("페이지 크기")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("QnA 목록"),
                                            fieldWithPath("data.content[].qnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA ID"),
                                            fieldWithPath("data.content[].qnaContents.title")
                                                    .type(JsonFieldType.STRING)
                                                    .description("질문 제목"),
                                            fieldWithPath("data.content[].qnaContents.content")
                                                    .type(JsonFieldType.STRING)
                                                    .description("질문 내용"),
                                            fieldWithPath("data.content[].privateYn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("비공개 여부"),
                                            fieldWithPath("data.content[].qnaStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description("QnA 상태"),
                                            fieldWithPath("data.content[].qnaType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("QnA 유형"),
                                            fieldWithPath("data.content[].qnaDetailType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("QnA 세부 유형"),
                                            fieldWithPath("data.content[].sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("data.content[].userInfo.userType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사용자 유형")
                                                    .optional(),
                                            fieldWithPath("data.content[].userInfo.userId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("사용자 ID")
                                                    .optional(),
                                            fieldWithPath("data.content[].userInfo.userName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사용자명"),
                                            fieldWithPath("data.content[].userInfo.phoneNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("전화번호")
                                                    .optional(),
                                            fieldWithPath("data.content[].userInfo.email")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이메일")
                                                    .optional(),
                                            fieldWithPath("data.content[].userInfo.gender")
                                                    .type(JsonFieldType.STRING)
                                                    .description("성별")
                                                    .optional(),
                                            fieldWithPath("data.content[].qnaTarget.productGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 그룹 ID"),
                                            fieldWithPath(
                                                            "data.content[].qnaTarget.productGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 그룹명"),
                                            fieldWithPath(
                                                            "data.content[].qnaTarget.productGroupMainImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 대표 이미지 URL"),
                                            fieldWithPath("data.content[].qnaTarget.brand.brandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("브랜드 ID"),
                                            fieldWithPath("data.content[].qnaTarget.brand.brandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("브랜드명"),
                                            fieldWithPath("data.content[].qnaTarget.paymentId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("결제 ID (주문 QnA인 경우)")
                                                    .optional(),
                                            fieldWithPath("data.content[].qnaTarget.orderId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("주문 ID (주문 QnA인 경우)")
                                                    .optional(),
                                            fieldWithPath("data.content[].qnaTarget.option")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 (주문 QnA인 경우)")
                                                    .optional(),
                                            fieldWithPath("data.content[].qnaImages")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("QnA 이미지 목록"),
                                            fieldWithPath("data.content[].qnaImages[].qnaIssueType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 이슈 유형")
                                                    .optional(),
                                            fieldWithPath("data.content[].qnaImages[].qnaImageId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA 이미지 ID")
                                                    .optional(),
                                            fieldWithPath("data.content[].qnaImages[].qnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA ID")
                                                    .optional(),
                                            fieldWithPath("data.content[].qnaImages[].qnaAnswerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA 답변 ID")
                                                    .optional(),
                                            fieldWithPath("data.content[].qnaImages[].imageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 URL"),
                                            fieldWithPath("data.content[].qnaImages[].displayOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("노출 순서"),
                                            fieldWithPath("data.content[].insertDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일시"),
                                            fieldWithPath("data.content[].updateDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
                                            fieldWithPath("data.pageable")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("페이지 정보"),
                                            fieldWithPath("data.pageable.pageNumber")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("페이지 번호"),
                                            fieldWithPath("data.pageable.pageSize")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("페이지 크기"),
                                            fieldWithPath("data.pageable.sort")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("정렬 정보"),
                                            fieldWithPath("data.pageable.sort.unsorted")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("정렬 미적용 여부"),
                                            fieldWithPath("data.pageable.sort.sorted")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("정렬 적용 여부"),
                                            fieldWithPath("data.pageable.sort.empty")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("정렬 정보 비어있음 여부"),
                                            fieldWithPath("data.pageable.offset")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("오프셋"),
                                            fieldWithPath("data.pageable.paged")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("페이징 적용 여부"),
                                            fieldWithPath("data.pageable.unpaged")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("페이징 미적용 여부"),
                                            fieldWithPath("data.sort")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("전체 정렬 정보"),
                                            fieldWithPath("data.sort.unsorted")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("정렬 미적용 여부"),
                                            fieldWithPath("data.sort.sorted")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("정렬 적용 여부"),
                                            fieldWithPath("data.sort.empty")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("정렬 정보 비어있음 여부"),
                                            fieldWithPath("data.totalElements")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 QnA 수"),
                                            fieldWithPath("data.totalPages")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 페이지 수"),
                                            fieldWithPath("data.number")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재 페이지 번호"),
                                            fieldWithPath("data.size")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("페이지 크기"),
                                            fieldWithPath("data.numberOfElements")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재 페이지 요소 수"),
                                            fieldWithPath("data.first")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("첫 번째 페이지 여부"),
                                            fieldWithPath("data.last")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("마지막 페이지 여부"),
                                            fieldWithPath("data.empty")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("목록 비어있음 여부"),
                                            fieldWithPath("data.lastDomainId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("마지막 도메인 ID (커서 기반 페이징용)")
                                                    .optional(),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }

        @Test
        @DisplayName("빈 QnA 목록 조회 성공")
        void getQnas_EmptyResult_Success() throws Exception {
            // given
            LegacyQnaPageResult emptyResult = new LegacyQnaPageResult(List.of(), 0L, null);

            given(queryApiMapper.toSearchParams(any(), anyInt(), any()))
                    .willReturn(
                            new LegacyQnaSearchParams(
                                    null, "PRODUCT", null, null, null, 1L, null, null, null, 20));
            given(legacyQnaListUseCase.execute(any(LegacyQnaSearchParams.class)))
                    .willReturn(emptyResult);
            given(queryApiMapper.toFetchResponses(any())).willReturn(List.of());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(QNAS_URL)
                                    .param("qnaType", LegacyQnAApiFixtures.DEFAULT_QNA_TYPE)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("QnA 답변 등록 API")
    class ReplyQnaTest {

        @Test
        @DisplayName("QnA 답변 등록 성공")
        void replyQna_Success() throws Exception {
            // given
            LegacyCreateQnaAnswerRequest request = LegacyQnAApiFixtures.createAnswerRequest();
            QnaReplyResult replyResult =
                    new QnaReplyResult(
                            LegacyQnAApiFixtures.DEFAULT_QNA_ANSWER_ID,
                            null,
                            "답변 내용입니다.",
                            "SELLER",
                            QnaReplyType.SELLER_ANSWER,
                            Instant.now());
            LegacyCreateQnaAnswerResponse response = LegacyQnAApiFixtures.createQnaAnswerResponse();

            given(commandApiMapper.toAnswerCommand(any()))
                    .willReturn(
                            new AnswerQnaCommand(
                                    LegacyQnAApiFixtures.DEFAULT_QNA_ID,
                                    "답변 제목",
                                    "답변 내용입니다.",
                                    "SELLER",
                                    null));
            given(answerQnaUseCase.execute(any())).willReturn(replyResult);
            given(commandApiMapper.toCreateAnswerResponse(anyLong(), any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(QNA_REPLY_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.qnaId").value(LegacyQnAApiFixtures.DEFAULT_QNA_ID))
                    .andExpect(
                            jsonPath("$.data.qnaAnswerId")
                                    .value(LegacyQnAApiFixtures.DEFAULT_QNA_ANSWER_ID))
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andDo(
                            document(
                                    "legacy-qna/reply-qna",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("qnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA ID (필수)"),
                                            fieldWithPath("qnaContents.title")
                                                    .type(JsonFieldType.STRING)
                                                    .description("답변 제목 (1~100자)"),
                                            fieldWithPath("qnaContents.content")
                                                    .type(JsonFieldType.STRING)
                                                    .description("답변 내용 (1~500자)"),
                                            fieldWithPath("qnaImages")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("답변 이미지 목록 (최대 3장)")
                                                    .optional(),
                                            fieldWithPath("qnaImages[].qnaImageId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA 이미지 ID")
                                                    .optional(),
                                            fieldWithPath("qnaImages[].qnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA ID")
                                                    .optional(),
                                            fieldWithPath("qnaImages[].qnaAnswerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA 답변 ID")
                                                    .optional(),
                                            fieldWithPath("qnaImages[].imageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 URL"),
                                            fieldWithPath("qnaImages[].displayOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("노출 순서 (필수)")),
                                    responseFields(
                                            fieldWithPath("data.qnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA ID"),
                                            fieldWithPath("data.qnaAnswerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 답변 ID"),
                                            fieldWithPath("data.qnaType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("QnA 유형"),
                                            fieldWithPath("data.qnaStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description("QnA 상태"),
                                            fieldWithPath("data.qnaImages")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("답변 이미지 목록"),
                                            fieldWithPath("data.qnaImages[].qnaIssueType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 이슈 유형")
                                                    .optional(),
                                            fieldWithPath("data.qnaImages[].qnaImageId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA 이미지 ID")
                                                    .optional(),
                                            fieldWithPath("data.qnaImages[].qnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA ID")
                                                    .optional(),
                                            fieldWithPath("data.qnaImages[].qnaAnswerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA 답변 ID")
                                                    .optional(),
                                            fieldWithPath("data.qnaImages[].imageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 URL"),
                                            fieldWithPath("data.qnaImages[].displayOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("노출 순서"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }
    }

    @Nested
    @DisplayName("QnA 답변 수정 API")
    class UpdateReplyQnaTest {

        @Test
        @DisplayName("QnA 답변 수정 성공")
        void updateReplyQna_Success() throws Exception {
            // given
            LegacyUpdateQnaAnswerRequest request = LegacyQnAApiFixtures.updateAnswerRequest();
            QnaReplyResult replyResult =
                    new QnaReplyResult(
                            LegacyQnAApiFixtures.DEFAULT_QNA_ANSWER_ID,
                            null,
                            "수정된 답변 내용입니다.",
                            "SELLER",
                            QnaReplyType.SELLER_ANSWER,
                            Instant.now());
            LegacyCreateQnaAnswerResponse response = LegacyQnAApiFixtures.createQnaAnswerResponse();

            given(commandApiMapper.toUpdateCommand(any()))
                    .willReturn(
                            new UpdateQnaReplyCommand(
                                    LegacyQnAApiFixtures.DEFAULT_QNA_ID,
                                    LegacyQnAApiFixtures.DEFAULT_QNA_ANSWER_ID,
                                    "수정된 답변 내용입니다."));
            given(updateQnaReplyUseCase.execute(any())).willReturn(replyResult);
            given(commandApiMapper.toCreateAnswerResponse(anyLong(), any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(QNA_REPLY_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.qnaId").value(LegacyQnAApiFixtures.DEFAULT_QNA_ID))
                    .andExpect(
                            jsonPath("$.data.qnaAnswerId")
                                    .value(LegacyQnAApiFixtures.DEFAULT_QNA_ANSWER_ID))
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andDo(
                            document(
                                    "legacy-qna/update-reply-qna",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("qnaAnswerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수정할 답변 ID (필수)"),
                                            fieldWithPath("qnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA ID (필수)"),
                                            fieldWithPath("qnaContents.title")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정할 답변 제목 (1~100자)"),
                                            fieldWithPath("qnaContents.content")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정할 답변 내용 (1~500자)"),
                                            fieldWithPath("qnaImages")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("수정할 이미지 목록 (최대 3장)")
                                                    .optional(),
                                            fieldWithPath("qnaImages[].qnaImageId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA 이미지 ID")
                                                    .optional(),
                                            fieldWithPath("qnaImages[].qnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA ID")
                                                    .optional(),
                                            fieldWithPath("qnaImages[].qnaAnswerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA 답변 ID")
                                                    .optional(),
                                            fieldWithPath("qnaImages[].imageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 URL"),
                                            fieldWithPath("qnaImages[].displayOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("노출 순서 (필수)")),
                                    responseFields(
                                            fieldWithPath("data.qnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA ID"),
                                            fieldWithPath("data.qnaAnswerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수정된 답변 ID"),
                                            fieldWithPath("data.qnaType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("QnA 유형"),
                                            fieldWithPath("data.qnaStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description("QnA 상태"),
                                            fieldWithPath("data.qnaImages")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("답변 이미지 목록"),
                                            fieldWithPath("data.qnaImages[].qnaIssueType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 이슈 유형")
                                                    .optional(),
                                            fieldWithPath("data.qnaImages[].qnaImageId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA 이미지 ID")
                                                    .optional(),
                                            fieldWithPath("data.qnaImages[].qnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA ID")
                                                    .optional(),
                                            fieldWithPath("data.qnaImages[].qnaAnswerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("QnA 답변 ID")
                                                    .optional(),
                                            fieldWithPath("data.qnaImages[].imageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 URL"),
                                            fieldWithPath("data.qnaImages[].displayOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("노출 순서"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }
    }
}
