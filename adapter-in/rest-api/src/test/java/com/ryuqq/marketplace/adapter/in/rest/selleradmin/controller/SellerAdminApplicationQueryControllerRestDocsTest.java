package com.ryuqq.marketplace.adapter.in.rest.selleradmin.controller;

import static org.mockito.ArgumentMatchers.any;
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

import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.SellerAdminApplicationApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.SellerAdminApplicationEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.mapper.SellerAdminApplicationQueryApiMapper;
import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminApplicationPageResult;
import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminApplicationResult;
import com.ryuqq.marketplace.application.selleradmin.port.in.query.GetSellerAdminApplicationUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.query.SearchSellerAdminApplicationsUseCase;
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
@WebMvcTest(SellerAdminApplicationQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SellerAdminApplicationQueryController REST Docs 테스트")
class SellerAdminApplicationQueryControllerRestDocsTest {

    private static final String BASE_URL = SellerAdminApplicationEndpoints.BASE;
    private static final String SELLER_ADMIN_ID =
            SellerAdminApplicationApiFixtures.DEFAULT_SELLER_ADMIN_ID;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetSellerAdminApplicationUseCase getUseCase;
    @MockitoBean private SearchSellerAdminApplicationsUseCase searchUseCase;
    @MockitoBean private SellerAdminApplicationQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("셀러 관리자 가입 신청 상세 조회 API")
    class GetSellerAdminApplicationTest {

        @Test
        @DisplayName("셀러 관리자 가입 신청 상세 조회 성공")
        void get_Success() throws Exception {
            // given
            SellerAdminApplicationResult result =
                    SellerAdminApplicationApiFixtures.applicationResult(SELLER_ADMIN_ID);

            given(mapper.toGetQuery(SELLER_ADMIN_ID))
                    .willReturn(
                            com.ryuqq.marketplace.application.selleradmin.dto.query
                                    .GetSellerAdminApplicationQuery.of(SELLER_ADMIN_ID));
            given(getUseCase.execute(any())).willReturn(result);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + SellerAdminApplicationEndpoints.DETAIL,
                                    SELLER_ADMIN_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.sellerAdminId").value(SELLER_ADMIN_ID))
                    .andExpect(jsonPath("$.data.sellerId").value(1))
                    .andExpect(jsonPath("$.data.loginId").value("admin@example.com"))
                    .andExpect(jsonPath("$.data.name").value("홍길동"))
                    .andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"))
                    .andExpect(jsonPath("$.data.status").value("PENDING_APPROVAL"))
                    .andDo(
                            document(
                                    "seller-admin-application/get",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerAdminId")
                                                    .description("셀러 관리자 ID (UUIDv7)")),
                                    responseFields(
                                            fieldWithPath("data.sellerAdminId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러 관리자 ID"),
                                            fieldWithPath("data.sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("data.loginId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("로그인 ID"),
                                            fieldWithPath("data.name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("관리자 이름"),
                                            fieldWithPath("data.phoneNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("휴대폰 번호"),
                                            fieldWithPath("data.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("신청 상태"),
                                            fieldWithPath("data.authUserId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("인증 서버 사용자 ID (승인 후)")
                                                    .optional(),
                                            fieldWithPath("data.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시"),
                                            fieldWithPath("data.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시각"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("셀러 관리자 가입 신청 목록 검색 API")
    class SearchSellerAdminApplicationsTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void search_ValidRequest_Returns200WithPage() throws Exception {
            // given
            SellerAdminApplicationPageResult pageResult =
                    SellerAdminApplicationApiFixtures.pageResult(3, 0, 20);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchUseCase.execute(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
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
                                    "seller-admin-application/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("sellerIds")
                                                    .description("셀러 ID 목록 (슈퍼관리자용, 생략 시 전체)")
                                                    .optional(),
                                            parameterWithName("status")
                                                    .description(
                                                            "상태 필터 목록 (PENDING_APPROVAL, ACTIVE,"
                                                                + " INACTIVE, SUSPENDED, REJECTED)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (LOGIN_ID, NAME)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("startDate")
                                                    .description("신청일 시작 (yyyy-MM-dd)")
                                                    .optional(),
                                            parameterWithName("endDate")
                                                    .description("신청일 종료 (yyyy-MM-dd)")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description("정렬 기준 (createdAt)")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC, DESC)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0-based)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("신청 목록"),
                                            fieldWithPath("data.content[].sellerAdminId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러 관리자 ID"),
                                            fieldWithPath("data.content[].sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("data.content[].loginId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("로그인 ID"),
                                            fieldWithPath("data.content[].name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("관리자 이름"),
                                            fieldWithPath("data.content[].phoneNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("휴대폰 번호"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("신청 상태"),
                                            fieldWithPath("data.content[].authUserId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("인증 서버 사용자 ID (승인 후)")
                                                    .optional(),
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
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시각"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("셀러 ID 필터를 사용할 수 있다")
        void search_WithSellerIdFilter_Returns200() throws Exception {
            // given
            SellerAdminApplicationPageResult pageResult =
                    SellerAdminApplicationApiFixtures.pageResult(1, 0, 20);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchUseCase.execute(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("sellerIds", "1")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("상태 필터와 검색어를 함께 사용할 수 있다")
        void search_WithStatusAndSearchWord_Returns200() throws Exception {
            // given
            SellerAdminApplicationPageResult pageResult =
                    SellerAdminApplicationApiFixtures.pageResult(1, 0, 20);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchUseCase.execute(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("status", "PENDING_APPROVAL")
                                    .param("searchField", "loginId")
                                    .param("searchWord", "admin@example.com")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void search_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            SellerAdminApplicationPageResult emptyResult =
                    SellerAdminApplicationApiFixtures.emptyPageResult();

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchUseCase.execute(any())).willReturn(emptyResult);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }
}
