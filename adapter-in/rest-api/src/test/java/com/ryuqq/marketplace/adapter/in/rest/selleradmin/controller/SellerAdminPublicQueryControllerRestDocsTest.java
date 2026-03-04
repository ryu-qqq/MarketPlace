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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.SellerAdminPublicApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.SellerAdminPublicEndpoints;
import com.ryuqq.marketplace.application.selleradmin.dto.response.VerifySellerAdminResult;
import com.ryuqq.marketplace.application.selleradmin.port.in.query.VerifySellerAdminUseCase;
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
@WebMvcTest(SellerAdminPublicQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SellerAdminPublicQueryController REST Docs 테스트")
class SellerAdminPublicQueryControllerRestDocsTest {

    private static final String BASE_URL = SellerAdminPublicEndpoints.BASE;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private VerifySellerAdminUseCase verifyUseCase;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("셀러 관리자 본인 확인 API")
    class VerifySellerAdminTest {

        @Test
        @DisplayName("셀러 관리자 본인 확인 성공")
        void verify_ExistingAdmin_Success() throws Exception {
            // given
            VerifySellerAdminResult result = SellerAdminPublicApiFixtures.foundResult();
            given(verifyUseCase.execute(any())).willReturn(result);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                            BASE_URL + SellerAdminPublicEndpoints.VERIFY)
                                    .param("name", SellerAdminPublicApiFixtures.DEFAULT_NAME)
                                    .param("loginId", SellerAdminPublicApiFixtures.DEFAULT_LOGIN_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.exists").value(true))
                    .andExpect(jsonPath("$.data.status").value(SellerAdminPublicApiFixtures.DEFAULT_STATUS))
                    .andDo(
                            document(
                                    "seller-admin-public/verify",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("name").description("관리자 이름"),
                                            parameterWithName("loginId").description("로그인 ID")),
                                    responseFields(
                                            fieldWithPath("data.exists")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("존재 여부"),
                                            fieldWithPath("data.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러 관리자 상태"),
                                            fieldWithPath("data.sellerAdminId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러 관리자 ID"),
                                            fieldWithPath("data.phoneNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("핸드폰 번호"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시각"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }
}
