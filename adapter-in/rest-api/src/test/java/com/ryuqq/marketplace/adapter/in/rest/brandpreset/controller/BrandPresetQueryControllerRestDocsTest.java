package com.ryuqq.marketplace.adapter.in.rest.brandpreset.controller;

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

import com.ryuqq.marketplace.adapter.in.rest.brandpreset.BrandPresetAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.BrandPresetApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.response.BrandPresetApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.response.BrandPresetDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.mapper.BrandPresetQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetDetailResult;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetPageResult;
import com.ryuqq.marketplace.application.brandpreset.port.in.query.GetBrandPresetDetailUseCase;
import com.ryuqq.marketplace.application.brandpreset.port.in.query.SearchBrandPresetByOffsetUseCase;
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
@WebMvcTest(BrandPresetQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("BrandPresetQueryController REST Docs 테스트")
class BrandPresetQueryControllerRestDocsTest {

    private static final String BASE_URL = BrandPresetAdminEndpoints.BRAND_PRESETS;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchBrandPresetByOffsetUseCase searchBrandPresetByOffsetUseCase;
    @MockitoBean private GetBrandPresetDetailUseCase getBrandPresetDetailUseCase;
    @MockitoBean private BrandPresetQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("브랜드 프리셋 목록 검색 API")
    class SearchBrandPresetsTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchBrandPresets_ValidRequest_Returns200WithPage() throws Exception {
            // given
            BrandPresetPageResult pageResult = BrandPresetApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<BrandPresetApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    BrandPresetApiFixtures.apiResponse(1L),
                                    BrandPresetApiFixtures.apiResponse(2L),
                                    BrandPresetApiFixtures.apiResponse(3L)),
                            0,
                            20,
                            3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchBrandPresetByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(BrandPresetPageResult.class))).willReturn(pageResponse);

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
                                    "brand-preset/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("salesChannelIds")
                                                    .description("판매채널 ID 목록")
                                                    .optional(),
                                            parameterWithName("statuses")
                                                    .description("상태 필터 (ACTIVE, INACTIVE)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description(
                                                            "검색 필드 (PRESET_NAME, SHOP_NAME,"
                                                                    + " ACCOUNT_ID, BRAND_NAME,"
                                                                    + " BRAND_CODE)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("startDate")
                                                    .description("등록일 시작 (YYYY-MM-DD)")
                                                    .optional(),
                                            parameterWithName("endDate")
                                                    .description("등록일 종료 (YYYY-MM-DD)")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 키 (CREATED_AT). 기본값: CREATED_AT")
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
                                                    .description("브랜드 프리셋 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("프리셋 ID"),
                                            fieldWithPath("data.content[].shopId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("Shop ID"),
                                            fieldWithPath("data.content[].shopName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("쇼핑몰명"),
                                            fieldWithPath("data.content[].salesChannelId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 ID"),
                                            fieldWithPath("data.content[].salesChannelName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("판매채널명"),
                                            fieldWithPath("data.content[].accountId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("계정 ID"),
                                            fieldWithPath("data.content[].presetName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("프리셋 이름"),
                                            fieldWithPath("data.content[].brandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("브랜드명"),
                                            fieldWithPath("data.content[].brandCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("브랜드 코드"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일"),
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
        void searchBrandPresets_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            BrandPresetPageResult emptyResult = BrandPresetApiFixtures.emptyPageResult();
            PageApiResponse<BrandPresetApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchBrandPresetByOffsetUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(BrandPresetPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("브랜드 프리셋 상세 조회 API")
    class GetBrandPresetDetailTest {

        @Test
        @DisplayName("유효한 ID로 조회하면 200과 상세 응답을 반환한다")
        void getBrandPreset_ValidId_Returns200() throws Exception {
            // given
            Long brandPresetId = 1L;
            BrandPresetDetailResult detailResult =
                    BrandPresetApiFixtures.brandPresetDetailResult(brandPresetId);
            BrandPresetDetailApiResponse detailResponse = createDetailApiResponse(brandPresetId);

            given(getBrandPresetDetailUseCase.execute(brandPresetId)).willReturn(detailResult);
            given(mapper.toDetailResponse(any(BrandPresetDetailResult.class)))
                    .willReturn(detailResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + "/{brandPresetId}", brandPresetId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(brandPresetId))
                    .andExpect(jsonPath("$.data.mappingBrand.brandCode").exists())
                    .andExpect(jsonPath("$.data.internalBrands").isArray())
                    .andDo(
                            document(
                                    "brand-preset/get-detail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("brandPresetId")
                                                    .description("브랜드 프리셋 ID")),
                                    responseFields(
                                            fieldWithPath("data.id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("프리셋 ID"),
                                            fieldWithPath("data.shopId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("Shop ID"),
                                            fieldWithPath("data.shopName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("쇼핑몰명"),
                                            fieldWithPath("data.salesChannelId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 ID"),
                                            fieldWithPath("data.salesChannelName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("판매채널명"),
                                            fieldWithPath("data.accountId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("계정 ID"),
                                            fieldWithPath("data.presetName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("프리셋 이름"),
                                            fieldWithPath("data.mappingBrand")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("매핑된 판매채널 브랜드"),
                                            fieldWithPath("data.mappingBrand.brandCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 브랜드 코드"),
                                            fieldWithPath("data.mappingBrand.brandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 브랜드명"),
                                            fieldWithPath("data.internalBrands[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("매핑된 내부 브랜드 목록"),
                                            fieldWithPath("data.internalBrands[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("내부 브랜드 ID"),
                                            fieldWithPath("data.internalBrands[].brandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("브랜드명"),
                                            fieldWithPath("data.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일"),
                                            fieldWithPath("data.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    private BrandPresetDetailApiResponse createDetailApiResponse(Long id) {
        BrandPresetDetailApiResponse.MappingBrandResponse mappingBrand =
                new BrandPresetDetailApiResponse.MappingBrandResponse("TEST_BRAND_CODE", "테스트 브랜드");
        List<BrandPresetDetailApiResponse.InternalBrandResponse> internalBrands =
                List.of(
                        new BrandPresetDetailApiResponse.InternalBrandResponse(100L, "내부 브랜드 A"),
                        new BrandPresetDetailApiResponse.InternalBrandResponse(200L, "내부 브랜드 B"));
        return new BrandPresetDetailApiResponse(
                id,
                1L,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                "테스트 브랜드 프리셋",
                mappingBrand,
                internalBrands,
                "2025-02-10 10:30:00",
                "2025-02-10 10:30:00");
    }
}
