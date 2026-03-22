package com.ryuqq.marketplace.adapter.in.rest.legacy.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.LegacyAuthApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.request.LegacyCreateAuthTokenRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.response.LegacyAuthTokenResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.mapper.LegacyAuthCommandApiMapper;
import com.ryuqq.marketplace.application.legacy.auth.dto.command.LegacyLoginCommand;
import com.ryuqq.marketplace.application.legacy.auth.port.in.LegacyLoginUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(LegacyAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("LegacyAuthController REST Docs 테스트")
class LegacyAuthControllerRestDocsTest {

    private static final String BASE_URL = "/api/v1/legacy/auth/authentication";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private LegacyLoginUseCase legacyLoginUseCase;
    @MockitoBean private LegacyAuthCommandApiMapper legacyAuthCommandApiMapper;
    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("레거시 인증 토큰 생성 API")
    class GetAccessTokenTest {

        @Test
        @DisplayName("인증 토큰 생성 성공")
        void getAccessToken_Success() throws Exception {
            // given
            LegacyCreateAuthTokenRequest request = LegacyAuthApiFixtures.request();
            LegacyLoginCommand command = LegacyAuthApiFixtures.command();
            LegacyAuthTokenResponse tokenResponse = LegacyAuthApiFixtures.authTokenResponse();

            given(legacyAuthCommandApiMapper.toLoginCommand(any())).willReturn(command);
            given(legacyLoginUseCase.execute(any()))
                    .willReturn(LegacyAuthApiFixtures.DEFAULT_TOKEN);
            given(legacyAuthCommandApiMapper.toAuthTokenResponse(any())).willReturn(tokenResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.token").value(LegacyAuthApiFixtures.DEFAULT_TOKEN))
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andExpect(jsonPath("$.response.message").value("success"))
                    .andDo(
                            document(
                                    "legacy-auth/authentication",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("userId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사용자 ID (필수)"),
                                            fieldWithPath("password")
                                                    .type(JsonFieldType.STRING)
                                                    .description("비밀번호 (필수)"),
                                            fieldWithPath("roleType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("역할 타입 (필수, 예: SELLER)")),
                                    responseFields(
                                            fieldWithPath("data.token")
                                                    .type(JsonFieldType.STRING)
                                                    .description("발급된 JWT 액세스 토큰"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }

        @Test
        @DisplayName("다른 roleType으로도 인증 토큰 생성 성공")
        void getAccessToken_WithAdminRole_Success() throws Exception {
            // given
            LegacyCreateAuthTokenRequest request =
                    LegacyAuthApiFixtures.requestWith("adminUser", "adminPass", "ADMIN");
            LegacyLoginCommand command = new LegacyLoginCommand("adminUser", "adminPass");
            LegacyAuthTokenResponse tokenResponse =
                    LegacyAuthApiFixtures.authTokenResponse("admin.jwt.token");

            given(legacyAuthCommandApiMapper.toLoginCommand(any())).willReturn(command);
            given(legacyLoginUseCase.execute(any())).willReturn("admin.jwt.token");
            given(legacyAuthCommandApiMapper.toAuthTokenResponse(any())).willReturn(tokenResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.token").value("admin.jwt.token"));
        }
    }
}
