package com.ryuqq.marketplace.adapter.in.rest.legacy.seller.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.LegacySellerApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.dto.response.LegacySellerResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.mapper.LegacySellerQueryApiMapper;
import com.ryuqq.marketplace.application.legacyseller.dto.response.LegacySellerResult;
import com.ryuqq.marketplace.application.legacyseller.port.in.LegacyGetCurrentSellerUseCase;
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
@WebMvcTest(LegacySellerController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("LegacySellerController REST Docs 테스트")
class LegacySellerControllerRestDocsTest {

    private static final String BASE_URL = "/api/v1/legacy/seller";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private LegacyGetCurrentSellerUseCase legacyGetCurrentSellerUseCase;
    @MockitoBean private LegacySellerQueryApiMapper legacySellerQueryApiMapper;
    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("레거시 현재 셀러 조회 API")
    class GetCurrentSellerTest {

        @Test
        @DisplayName("현재 인증된 셀러 정보 조회 성공")
        void getCurrentSeller_Success() throws Exception {
            // given
            LegacySellerResult result = LegacySellerApiFixtures.sellerResult();
            LegacySellerResponse response = LegacySellerApiFixtures.sellerResponse();

            given(legacyGetCurrentSellerUseCase.execute(any())).willReturn(result);
            given(legacySellerQueryApiMapper.toSellerResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.data.sellerId")
                                    .value(LegacySellerApiFixtures.DEFAULT_SELLER_ID))
                    .andExpect(
                            jsonPath("$.data.sellerName")
                                    .value(LegacySellerApiFixtures.DEFAULT_SELLER_NAME))
                    .andExpect(
                            jsonPath("$.data.bizNo").value(LegacySellerApiFixtures.DEFAULT_BIZ_NO))
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andExpect(jsonPath("$.response.message").value("success"))
                    .andDo(
                            document(
                                    "legacy-seller/get-current",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    responseFields(
                                            fieldWithPath("data.sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("data.sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("data.bizNo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사업자등록번호"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }

        @Test
        @DisplayName("다른 셀러 정보도 올바르게 조회된다")
        void getCurrentSeller_DifferentSeller_Success() throws Exception {
            // given
            LegacySellerResult result =
                    LegacySellerApiFixtures.sellerResult(99L, "다른 셀러", "999-99-99999");
            LegacySellerResponse response =
                    LegacySellerApiFixtures.sellerResponse(99L, "다른 셀러", "999-99-99999");

            given(legacyGetCurrentSellerUseCase.execute(any())).willReturn(result);
            given(legacySellerQueryApiMapper.toSellerResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.sellerId").value(99L))
                    .andExpect(jsonPath("$.data.sellerName").value("다른 셀러"));
        }
    }
}
