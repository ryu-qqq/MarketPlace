package com.ryuqq.marketplace.adapter.in.rest.commoncode.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.CommonCodeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.query.SearchCommonCodesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.response.CommonCodeApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.mapper.CommonCodeQueryApiMapper;
import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodePageResult;
import com.ryuqq.marketplace.application.commoncode.port.in.query.SearchCommonCodeUseCase;
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
@WebMvcTest(CommonCodeQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("CommonCodeQueryController REST Docs 테스트")
class CommonCodeQueryControllerRestDocsTest {

    private static final String BASE_URL = "/api/v1/market/common-codes";

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchCommonCodeUseCase searchCommonCodeUseCase;
    @MockitoBean private CommonCodeQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("공통 코드 목록 조회 API")
    class SearchTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void search_ValidRequest_Returns200WithPage() throws Exception {
            // given
            CommonCodePageResult pageResult = CommonCodeApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<CommonCodeApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    CommonCodeApiFixtures.apiResponse(1L),
                                    CommonCodeApiFixtures.apiResponse(2L),
                                    CommonCodeApiFixtures.apiResponse(3L)),
                            0,
                            20,
                            3);

            given(mapper.toSearchParams(any(SearchCommonCodesPageApiRequest.class)))
                    .willReturn(null);
            given(searchCommonCodeUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(CommonCodePageResult.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("commonCodeTypeId", "1")
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
                                    "common-code/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("commonCodeTypeId")
                                                    .description("공통 코드 타입 ID (필수)"),
                                            parameterWithName("active")
                                                    .description("활성화 여부 필터")
                                                    .optional(),
                                            parameterWithName("code")
                                                    .description("코드 검색 (부분 일치)")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 키 (CREATED_AT, DISPLAY_ORDER,"
                                                                    + " CODE)")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC, DESC)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기 (최대 100)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("공통 코드 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("공통 코드 ID"),
                                            fieldWithPath("data.content[].commonCodeTypeId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("공통 코드 타입 ID"),
                                            fieldWithPath("data.content[].code")
                                                    .type(JsonFieldType.STRING)
                                                    .description("코드값"),
                                            fieldWithPath("data.content[].displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시명"),
                                            fieldWithPath("data.content[].displayOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("표시 순서"),
                                            fieldWithPath("data.content[].active")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("활성화 여부"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시 (ISO 8601)"),
                                            fieldWithPath("data.content[].updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시 (ISO 8601)"),
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
    }
}
