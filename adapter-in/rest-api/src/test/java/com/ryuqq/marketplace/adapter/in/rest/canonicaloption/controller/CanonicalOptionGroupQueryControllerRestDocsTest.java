package com.ryuqq.marketplace.adapter.in.rest.canonicaloption.controller;

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

import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.CanonicalOptionAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.CanonicalOptionApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.response.CanonicalOptionGroupApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.mapper.CanonicalOptionGroupQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupPageResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import com.ryuqq.marketplace.application.canonicaloption.port.in.query.GetCanonicalOptionGroupUseCase;
import com.ryuqq.marketplace.application.canonicaloption.port.in.query.SearchCanonicalOptionGroupByOffsetUseCase;
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
@WebMvcTest(CanonicalOptionGroupQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("CanonicalOptionGroupQueryController REST Docs 테스트")
class CanonicalOptionGroupQueryControllerRestDocsTest {

    private static final String BASE_URL = CanonicalOptionAdminEndpoints.CANONICAL_OPTION_GROUPS;

    @Autowired private MockMvc mockMvc;

    @MockitoBean
    private SearchCanonicalOptionGroupByOffsetUseCase searchCanonicalOptionGroupByOffsetUseCase;

    @MockitoBean private GetCanonicalOptionGroupUseCase getCanonicalOptionGroupUseCase;
    @MockitoBean private CanonicalOptionGroupQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("정규 옵션그룹 목록 검색 API")
    class SearchCanonicalOptionGroupsTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchCanonicalOptionGroups_ValidRequest_Returns200WithPage() throws Exception {
            // given
            CanonicalOptionGroupPageResult pageResult =
                    CanonicalOptionApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<CanonicalOptionGroupApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    CanonicalOptionApiFixtures.groupApiResponse(1L),
                                    CanonicalOptionApiFixtures.groupApiResponse(2L),
                                    CanonicalOptionApiFixtures.groupApiResponse(3L)),
                            0,
                            20,
                            3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchCanonicalOptionGroupByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(CanonicalOptionGroupPageResult.class)))
                    .willReturn(pageResponse);

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
                                    "canonical-option-group/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("active")
                                                    .description(
                                                            "활성 상태 필터 (true/false, 미지정시" + " 전체)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (CODE, NAME_KO, NAME_EN)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 키 (CREATED_AT, CODE). 기본값:"
                                                                    + " CREATED_AT")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC, DESC)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션그룹 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("그룹 ID"),
                                            fieldWithPath("data.content[].code")
                                                    .type(JsonFieldType.STRING)
                                                    .description("그룹 코드"),
                                            fieldWithPath("data.content[].nameKo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("한글명"),
                                            fieldWithPath("data.content[].nameEn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("영문명"),
                                            fieldWithPath("data.content[].active")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("활성 상태"),
                                            fieldWithPath("data.content[].values[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 값 목록"),
                                            fieldWithPath("data.content[].values[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("옵션 값 ID"),
                                            fieldWithPath("data.content[].values[].code")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 값 코드"),
                                            fieldWithPath("data.content[].values[].nameKo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("한글명"),
                                            fieldWithPath("data.content[].values[].nameEn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("영문명"),
                                            fieldWithPath("data.content[].values[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시"),
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
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchCanonicalOptionGroups_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            CanonicalOptionGroupPageResult emptyResult =
                    CanonicalOptionApiFixtures.emptyPageResult();
            PageApiResponse<CanonicalOptionGroupApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchCanonicalOptionGroupByOffsetUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(CanonicalOptionGroupPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("정규 옵션그룹 상세 조회 API")
    class GetCanonicalOptionGroupTest {

        @Test
        @DisplayName("유효한 ID로 조회하면 200과 상세 응답을 반환한다")
        void getCanonicalOptionGroup_ValidId_Returns200() throws Exception {
            // given
            Long canonicalOptionGroupId = 1L;
            CanonicalOptionGroupResult groupResult =
                    CanonicalOptionApiFixtures.groupResult(canonicalOptionGroupId);
            CanonicalOptionGroupApiResponse groupResponse =
                    CanonicalOptionApiFixtures.groupApiResponse(canonicalOptionGroupId);

            given(getCanonicalOptionGroupUseCase.execute(canonicalOptionGroupId))
                    .willReturn(groupResult);
            given(mapper.toResponse(any(CanonicalOptionGroupResult.class)))
                    .willReturn(groupResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + "/{canonicalOptionGroupId}", canonicalOptionGroupId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(canonicalOptionGroupId))
                    .andExpect(jsonPath("$.data.values").isArray())
                    .andDo(
                            document(
                                    "canonical-option-group/get-detail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("canonicalOptionGroupId")
                                                    .description("정규 옵션그룹 ID")),
                                    responseFields(
                                            fieldWithPath("data.id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("그룹 ID"),
                                            fieldWithPath("data.code")
                                                    .type(JsonFieldType.STRING)
                                                    .description("그룹 코드"),
                                            fieldWithPath("data.nameKo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("한글명"),
                                            fieldWithPath("data.nameEn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("영문명"),
                                            fieldWithPath("data.active")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("활성 상태"),
                                            fieldWithPath("data.values[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 값 목록"),
                                            fieldWithPath("data.values[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("옵션 값 ID"),
                                            fieldWithPath("data.values[].code")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 값 코드"),
                                            fieldWithPath("data.values[].nameKo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("한글명"),
                                            fieldWithPath("data.values[].nameEn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("영문명"),
                                            fieldWithPath("data.values[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("data.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }
}
