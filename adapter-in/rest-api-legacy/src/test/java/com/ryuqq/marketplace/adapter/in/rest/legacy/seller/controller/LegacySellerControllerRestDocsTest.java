package com.ryuqq.marketplace.adapter.in.rest.legacy.seller.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.security.LegacyAuthContext;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.security.LegacyAuthContextHolder;
import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.LegacySellerApiFixtures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    private static final String BASE_URL = "/api/v1/seller";

    @Autowired private MockMvc mockMvc;

    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @BeforeEach
    void setUp() {
        LegacyAuthContextHolder.setContext(
                new LegacyAuthContext(
                        LegacySellerApiFixtures.DEFAULT_SELLER_ID,
                        LegacySellerApiFixtures.DEFAULT_EMAIL,
                        "SELLER"));
    }

    @AfterEach
    void tearDown() {
        LegacyAuthContextHolder.clear();
    }

    @Nested
    @DisplayName("레거시 현재 셀러 조회 API")
    class GetCurrentSellerTest {

        @Test
        @DisplayName("현재 인증된 셀러 정보 조회 성공")
        void getCurrentSeller_Success() throws Exception {
            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.data.sellerId")
                                    .value(LegacySellerApiFixtures.DEFAULT_SELLER_ID))
                    .andExpect(
                            jsonPath("$.data.email").value(LegacySellerApiFixtures.DEFAULT_EMAIL))
                    .andExpect(
                            jsonPath("$.data.passwordHash").value(""))
                    .andExpect(
                            jsonPath("$.data.roleType").value("SELLER"))
                    .andExpect(
                            jsonPath("$.data.approvalStatus").value("APPROVED"))
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
                                            fieldWithPath("data.email")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이메일"),
                                            fieldWithPath("data.passwordHash")
                                                    .type(JsonFieldType.STRING)
                                                    .description("비밀번호 해시"),
                                            fieldWithPath("data.roleType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("역할 유형"),
                                            fieldWithPath("data.approvalStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description("승인 상태"),
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
            LegacyAuthContextHolder.clear();
            LegacyAuthContextHolder.setContext(
                    new LegacyAuthContext(99L, "other@test.com", "ADMIN"));

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.sellerId").value(99L))
                    .andExpect(jsonPath("$.data.email").value("other@test.com"))
                    .andExpect(jsonPath("$.data.passwordHash").value(""))
                    .andExpect(jsonPath("$.data.roleType").value("ADMIN"))
                    .andExpect(jsonPath("$.data.approvalStatus").value("APPROVED"));
        }
    }
}
