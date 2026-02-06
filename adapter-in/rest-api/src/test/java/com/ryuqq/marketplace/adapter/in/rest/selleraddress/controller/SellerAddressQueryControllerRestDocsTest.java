package com.ryuqq.marketplace.adapter.in.rest.selleraddress.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.SellerAddressAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.SellerAddressApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.response.SellerAddressApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.response.SellerAddressMetadataApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.mapper.SellerAddressQueryApiMapper;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressMetadataResult;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressPageResult;
import com.ryuqq.marketplace.application.selleraddress.port.in.query.GetSellerAddressMetadataUseCase;
import com.ryuqq.marketplace.application.selleraddress.port.in.query.SearchSellerAddressUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(SellerAddressQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SellerAddressQueryController REST Docs 테스트")
class SellerAddressQueryControllerRestDocsTest {

    private static final String BASE_URL = SellerAddressAdminEndpoints.SELLER_ADDRESSES_QUERY;
    private static final Long SELLER_ID = 1L;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchSellerAddressUseCase searchUseCase;
    @MockitoBean private GetSellerAddressMetadataUseCase metadataUseCase;
    @MockitoBean private SellerAddressQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("셀러 주소 목록 검색 API")
    class SearchTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void search_ValidRequest_Returns200WithPage() throws Exception {
            // given
            SellerAddressPageResult pagedResult = SellerAddressApiFixtures.pagedResult(3, 0, 20);
            PageApiResponse<SellerAddressApiResponse> pageResponse =
                    PageApiResponse.of(SellerAddressApiFixtures.apiResponses(3), 0, 20, 3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchUseCase.execute(any())).willReturn(pagedResult);
            given(mapper.toPageResponse(ArgumentMatchers.<SellerAddressPageResult>any()))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("sellerIds", "1")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(3))
                    .andDo(
                            document(
                                    "seller-address/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("sellerIds")
                                                    .description("셀러 ID 목록 (1건 이상 필수)"),
                                            parameterWithName("addressTypes")
                                                    .description("주소 유형 필터 (SHIPPING, RETURN)")
                                                    .optional(),
                                            parameterWithName("defaultAddress")
                                                    .description("기본 주소 필터")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (addressName, address 등)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0-based)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]").description("셀러 주소 목록"),
                                            fieldWithPath("data.content[].id").description("주소 ID"),
                                            fieldWithPath("data.content[].sellerId")
                                                    .description("셀러 ID"),
                                            fieldWithPath("data.content[].addressType")
                                                    .description("주소 유형"),
                                            fieldWithPath("data.content[].addressName")
                                                    .description("주소명"),
                                            fieldWithPath("data.content[].address")
                                                    .description("주소 정보"),
                                            fieldWithPath("data.content[].address.zipCode")
                                                    .description("우편번호"),
                                            fieldWithPath("data.content[].address.line1")
                                                    .description("도로명주소"),
                                            fieldWithPath("data.content[].address.line2")
                                                    .description("상세주소"),
                                            fieldWithPath("data.content[].defaultAddress")
                                                    .description("기본 주소 여부"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .description("생성일시"),
                                            fieldWithPath("data.content[].updatedAt")
                                                    .description("수정일시"),
                                            fieldWithPath("data.page").description("현재 페이지 번호"),
                                            fieldWithPath("data.size").description("페이지 크기"),
                                            fieldWithPath("data.totalElements")
                                                    .description("전체 데이터 수"),
                                            fieldWithPath("data.totalPages")
                                                    .description("전체 페이지 수"),
                                            fieldWithPath("data.first").description("첫 페이지 여부"),
                                            fieldWithPath("data.last").description("마지막 페이지 여부"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void search_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            SellerAddressPageResult emptyResult = SellerAddressApiFixtures.emptyPagedResult();
            PageApiResponse<SellerAddressApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(ArgumentMatchers.<SellerAddressPageResult>any()))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("셀러 주소 메타데이터 조회 API")
    class MetadataTest {

        @Test
        @DisplayName("유효한 sellerId로 200과 메타데이터를 반환한다")
        void getMetadata_ValidSellerId_Returns200WithMetadata() throws Exception {
            // given
            SellerAddressMetadataResult metadataResult =
                    new SellerAddressMetadataResult(5, 3, 2, true, false);
            SellerAddressMetadataApiResponse apiResponse =
                    new SellerAddressMetadataApiResponse(5, 3, 2, true, false);

            given(metadataUseCase.execute(eq(SELLER_ID))).willReturn(metadataResult);
            given(mapper.toMetadataResponse(any(SellerAddressMetadataResult.class)))
                    .willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                            BASE_URL + SellerAddressAdminEndpoints.METADATA)
                                    .param("sellerId", String.valueOf(SELLER_ID)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(5))
                    .andExpect(jsonPath("$.data.shippingCount").value(3))
                    .andExpect(jsonPath("$.data.returnCount").value(2))
                    .andExpect(jsonPath("$.data.hasDefaultShipping").value(true))
                    .andExpect(jsonPath("$.data.hasDefaultReturn").value(false))
                    .andDo(
                            document(
                                    "seller-address/metadata",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("sellerId")
                                                    .description("셀러 ID (필수)")),
                                    responseFields(
                                            fieldWithPath("data.totalCount").description("전체 주소 수"),
                                            fieldWithPath("data.shippingCount")
                                                    .description("출고지 수"),
                                            fieldWithPath("data.returnCount").description("반품지 수"),
                                            fieldWithPath("data.hasDefaultShipping")
                                                    .description("기본 출고지 설정 여부"),
                                            fieldWithPath("data.hasDefaultReturn")
                                                    .description("기본 반품지 설정 여부"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }
}
