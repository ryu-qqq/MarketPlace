package com.ryuqq.marketplace.adapter.in.rest.seller.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.seller.SellerApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.seller.SellerPublicEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.response.SellerPublicProfileApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.seller.mapper.SellerQueryApiMapper;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPublicProfileResult;
import com.ryuqq.marketplace.application.seller.port.in.query.GetSellerPublicProfileUseCase;
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
@WebMvcTest(SellerPublicQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SellerPublicQueryController REST Docs 테스트")
class SellerPublicQueryControllerRestDocsTest {

    private static final Long SELLER_ID = 1L;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetSellerPublicProfileUseCase getSellerPublicProfileUseCase;
    @MockitoBean private SellerQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("셀러 공개 프로필 조회 API")
    class GetSellerPublicProfileTest {

        @Test
        @DisplayName("셀러 공개 프로필 조회 성공")
        void getSellerPublicProfile_Success() throws Exception {
            // given
            SellerPublicProfileResult result = SellerApiFixtures.publicProfileResult();
            SellerPublicProfileApiResponse response = SellerApiFixtures.publicProfileApiResponse();

            given(getSellerPublicProfileUseCase.execute(SELLER_ID)).willReturn(result);
            given(mapper.toPublicProfileResponse(result)).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    SellerPublicEndpoints.SELLER_PROFILE, SELLER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.sellerName").value("테스트셀러"))
                    .andExpect(jsonPath("$.data.displayName").value("테스트 브랜드"))
                    .andExpect(jsonPath("$.data.companyName").value("테스트컴퍼니"))
                    .andExpect(jsonPath("$.data.representative").value("홍길동"))
                    .andDo(
                            document(
                                    "seller/public-profile",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerId").description("셀러 ID")),
                                    responseFields(
                                            fieldWithPath("data.sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("data.displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시명"),
                                            fieldWithPath("data.companyName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("회사명"),
                                            fieldWithPath("data.representative")
                                                    .type(JsonFieldType.STRING)
                                                    .description("대표자명"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }
}
