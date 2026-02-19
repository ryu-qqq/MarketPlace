package com.ryuqq.marketplace.adapter.in.rest.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
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
import com.ryuqq.authhub.sdk.context.UserContext;
import com.ryuqq.authhub.sdk.context.UserContextHolder;
import com.ryuqq.marketplace.adapter.in.rest.auth.AuthAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.auth.AuthApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.auth.config.AuthCookieConfig;
import com.ryuqq.marketplace.adapter.in.rest.auth.dto.command.LoginApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.auth.dto.response.LoginApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.auth.mapper.AuthCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.port.in.LoginUseCase;
import com.ryuqq.marketplace.application.auth.port.in.LogoutUseCase;
import com.ryuqq.marketplace.application.auth.port.in.RefreshTokenUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(AuthCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@Import(AuthCookieConfig.class)
@TestPropertySource(
        properties = {
            "api.auth.cookie.access-max-age=3600",
            "api.auth.cookie.refresh-max-age=604800",
            "api.auth.cookie.secure=true",
            "api.auth.cookie.same-site=Strict",
            "api.auth.cookie.path=/",
            "api.auth.cookie.http-only=true"
        })
@DisplayName("AuthCommandController REST Docs 테스트")
class AuthCommandControllerRestDocsTest {

    private static final String BASE_URL = AuthAdminEndpoints.BASE;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private LoginUseCase loginUseCase;
    @MockitoBean private LogoutUseCase logoutUseCase;
    @MockitoBean private RefreshTokenUseCase refreshTokenUseCase;
    @MockitoBean private AuthCommandApiMapper commandMapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("로그인 API")
    class LoginTest {

        @Test
        @DisplayName("로그인 성공")
        void login_Success() throws Exception {
            // given
            LoginApiRequest request = AuthApiFixtures.loginRequest();
            LoginResult result = AuthApiFixtures.successLoginResult();
            LoginApiResponse response = AuthApiFixtures.loginApiResponse();

            given(commandMapper.toCommand(any(LoginApiRequest.class))).willReturn(null);
            given(loginUseCase.execute(any())).willReturn(result);
            given(commandMapper.toResponse(any(LoginResult.class))).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            BASE_URL + AuthAdminEndpoints.LOGIN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.data.accessToken")
                                    .value(AuthApiFixtures.DEFAULT_ACCESS_TOKEN))
                    .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                    .andDo(
                            document(
                                    "auth/login",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("identifier")
                                                    .type(JsonFieldType.STRING)
                                                    .description("아이디 또는 이메일"),
                                            fieldWithPath("password")
                                                    .type(JsonFieldType.STRING)
                                                    .description("비밀번호")),
                                    responseFields(
                                            fieldWithPath("data.accessToken")
                                                    .type(JsonFieldType.STRING)
                                                    .description("액세스 토큰"),
                                            fieldWithPath("data.refreshToken")
                                                    .type(JsonFieldType.STRING)
                                                    .description("리프레시 토큰"),
                                            fieldWithPath("data.tokenType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("토큰 타입"),
                                            fieldWithPath("data.expiresIn")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("만료 시간 (초)"),
                                            fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
                                            fieldWithPath("requestId").type(JsonFieldType.STRING).description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("로그아웃 API")
    class LogoutTest {

        @Test
        @DisplayName("로그아웃 성공")
        void logout_Success() throws Exception {
            // given - UserContextHolder에 인증 정보 설정
            UserContext userContext =
                    UserContext.builder().userId(AuthApiFixtures.DEFAULT_USER_ID).build();
            UserContextHolder.setContext(userContext);

            given(commandMapper.toCommand(AuthApiFixtures.DEFAULT_USER_ID)).willReturn(null);
            doNothing().when(logoutUseCase).execute(any());

            try {
                // when & then
                mockMvc.perform(
                                RestDocumentationRequestBuilders.post(
                                        BASE_URL + AuthAdminEndpoints.LOGOUT))
                        .andExpect(status().isOk())
                        .andDo(
                                document(
                                        "auth/logout",
                                        preprocessRequest(prettyPrint()),
                                        preprocessResponse(prettyPrint()),
                                        responseFields(
                                                fieldWithPath("data")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("응답 데이터")
                                                        .optional(),
                                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
                                                fieldWithPath("requestId").type(JsonFieldType.STRING).description("요청 ID"))));
            } finally {
                UserContextHolder.clearContext();
            }
        }
    }
}
