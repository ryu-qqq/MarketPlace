package com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.LegacyShipmentApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.dto.response.LegacyShipmentCompanyCodeResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.mapper.LegacyShipmentQueryApiMapper;
import com.ryuqq.marketplace.application.legacyshipment.dto.response.LegacyShipmentCompanyCodeResult;
import com.ryuqq.marketplace.application.legacyshipment.port.in.LegacyGetShipmentCompanyCodesUseCase;
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
@WebMvcTest(LegacyShipmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("LegacyShipmentController REST Docs 테스트")
class LegacyShipmentControllerRestDocsTest {

    private static final String BASE_URL = "/api/v1/legacy/shipment/company-codes";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private LegacyGetShipmentCompanyCodesUseCase legacyGetShipmentCompanyCodesUseCase;
    @MockitoBean private LegacyShipmentQueryApiMapper legacyShipmentQueryApiMapper;
    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("레거시 택배사 코드 목록 조회 API")
    class GetCompanyCodesTest {

        @Test
        @DisplayName("택배사 코드 목록 조회 성공")
        void getCompanyCodes_Success() throws Exception {
            // given
            List<LegacyShipmentCompanyCodeResult> results = LegacyShipmentApiFixtures.results();
            List<LegacyShipmentCompanyCodeResponse> responses =
                    LegacyShipmentApiFixtures.responses();

            given(legacyGetShipmentCompanyCodesUseCase.execute()).willReturn(results);
            given(legacyShipmentQueryApiMapper.toCompanyCodeResponses(results))
                    .willReturn(responses);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(
                            jsonPath("$.data[0].shipmentCompanyName")
                                    .value(LegacyShipmentApiFixtures.DEFAULT_COMPANY_NAME_CJ))
                    .andExpect(
                            jsonPath("$.data[0].shipmentCompanyCode")
                                    .value(LegacyShipmentApiFixtures.DEFAULT_COMPANY_CODE_CJ))
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andExpect(jsonPath("$.response.message").value("success"))
                    .andDo(
                            document(
                                    "legacy-shipment/get-company-codes",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("택배사 코드 목록"),
                                            fieldWithPath("data[].shipmentCompanyName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("택배사명"),
                                            fieldWithPath("data[].shipmentCompanyCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("택배사 코드"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }

        @Test
        @DisplayName("단일 택배사 코드도 올바르게 조회된다")
        void getCompanyCodes_SingleResult_Success() throws Exception {
            // given
            List<LegacyShipmentCompanyCodeResult> results =
                    LegacyShipmentApiFixtures.singleResult();
            List<LegacyShipmentCompanyCodeResponse> responses =
                    LegacyShipmentApiFixtures.singleResponse();

            given(legacyGetShipmentCompanyCodesUseCase.execute()).willReturn(results);
            given(legacyShipmentQueryApiMapper.toCompanyCodeResponses(results))
                    .willReturn(responses);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(1));
        }

        @Test
        @DisplayName("빈 목록도 올바르게 응답된다")
        void getCompanyCodes_EmptyResult_Success() throws Exception {
            // given
            given(legacyGetShipmentCompanyCodesUseCase.execute()).willReturn(List.of());
            given(legacyShipmentQueryApiMapper.toCompanyCodeResponses(List.of()))
                    .willReturn(List.of());

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));
        }
    }
}
