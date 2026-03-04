package com.ryuqq.marketplace.adapter.in.rest.selleradmin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.SellerAdminPublicApiFixtures;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(SellerAdminPublicQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SellerAdminPublicQueryController 단위 테스트")
class SellerAdminPublicQueryControllerTest {

    private static final String BASE_URL = "/api/v1/market/public/seller-admins";
    private static final String VERIFY_URL = BASE_URL + "/verify";

    @Autowired private MockMvc mockMvc;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;
    @MockitoBean private VerifySellerAdminUseCase verifyUseCase;

    @Nested
    @DisplayName("GET /api/v1/market/public/seller-admins/verify - 셀러 관리자 본인 확인")
    class VerifyTest {

        @Test
        @DisplayName("존재하는 셀러 관리자이면 200과 exists=true를 반환한다")
        void verify_ExistingAdmin_Returns200WithExistsTrue() throws Exception {
            // given
            VerifySellerAdminResult result = SellerAdminPublicApiFixtures.foundResult();

            given(verifyUseCase.execute(any())).willReturn(result);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(VERIFY_URL)
                                    .param("name", SellerAdminPublicApiFixtures.DEFAULT_NAME)
                                    .param(
                                            "phoneNumber",
                                            SellerAdminPublicApiFixtures.DEFAULT_PHONE_NUMBER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.exists").value(true))
                    .andExpect(
                            jsonPath("$.data.status")
                                    .value(SellerAdminPublicApiFixtures.DEFAULT_STATUS_ACTIVE));
        }

        @Test
        @DisplayName("존재하지 않는 셀러 관리자이면 200과 exists=false를 반환한다")
        void verify_NotExistingAdmin_Returns200WithExistsFalse() throws Exception {
            // given
            VerifySellerAdminResult result = SellerAdminPublicApiFixtures.notFoundResult();

            given(verifyUseCase.execute(any())).willReturn(result);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(VERIFY_URL)
                                    .param("name", SellerAdminPublicApiFixtures.DEFAULT_NAME)
                                    .param(
                                            "phoneNumber",
                                            SellerAdminPublicApiFixtures.DEFAULT_PHONE_NUMBER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.exists").value(false))
                    .andExpect(jsonPath("$.data.status").doesNotExist());
        }

        @Test
        @DisplayName("PENDING_APPROVAL 상태인 셀러 관리자이면 200과 해당 상태를 반환한다")
        void verify_PendingAdmin_Returns200WithPendingStatus() throws Exception {
            // given
            VerifySellerAdminResult result =
                    SellerAdminPublicApiFixtures.foundResult(
                            SellerAdminPublicApiFixtures.DEFAULT_STATUS_PENDING);

            given(verifyUseCase.execute(any())).willReturn(result);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(VERIFY_URL)
                                    .param("name", SellerAdminPublicApiFixtures.DEFAULT_NAME)
                                    .param(
                                            "phoneNumber",
                                            SellerAdminPublicApiFixtures.DEFAULT_PHONE_NUMBER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.exists").value(true))
                    .andExpect(
                            jsonPath("$.data.status")
                                    .value(SellerAdminPublicApiFixtures.DEFAULT_STATUS_PENDING));
        }

        @Test
        @DisplayName("name이 없으면 400을 반환한다")
        void verify_MissingName_Returns400() throws Exception {
            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(VERIFY_URL)
                                    .param(
                                            "phoneNumber",
                                            SellerAdminPublicApiFixtures.DEFAULT_PHONE_NUMBER))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("phoneNumber가 없으면 400을 반환한다")
        void verify_MissingPhoneNumber_Returns400() throws Exception {
            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(VERIFY_URL)
                                    .param("name", SellerAdminPublicApiFixtures.DEFAULT_NAME))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("name이 빈 문자열이면 400을 반환한다")
        void verify_BlankName_Returns400() throws Exception {
            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(VERIFY_URL)
                                    .param("name", "")
                                    .param(
                                            "phoneNumber",
                                            SellerAdminPublicApiFixtures.DEFAULT_PHONE_NUMBER))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("phoneNumber가 빈 문자열이면 400을 반환한다")
        void verify_BlankPhoneNumber_Returns400() throws Exception {
            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(VERIFY_URL)
                                    .param("name", SellerAdminPublicApiFixtures.DEFAULT_NAME)
                                    .param("phoneNumber", ""))
                    .andExpect(status().isBadRequest());
        }
    }
}
