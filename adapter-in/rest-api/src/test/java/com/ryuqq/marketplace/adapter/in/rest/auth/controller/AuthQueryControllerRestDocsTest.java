package com.ryuqq.marketplace.adapter.in.rest.auth.controller;

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

import com.ryuqq.authhub.sdk.context.UserContext;
import com.ryuqq.authhub.sdk.context.UserContextHolder;
import com.ryuqq.marketplace.adapter.in.rest.auth.AuthAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.auth.AuthApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.auth.dto.response.MyInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.auth.mapper.AuthQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import com.ryuqq.marketplace.application.auth.port.in.GetMyInfoUseCase;
import org.junit.jupiter.api.AfterEach;
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
@WebMvcTest(AuthQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("AuthQueryController REST Docs 테스트")
class AuthQueryControllerRestDocsTest {

    private static final String BASE_URL = AuthAdminEndpoints.BASE;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetMyInfoUseCase getMyInfoUseCase;
    @MockitoBean private AuthQueryApiMapper queryMapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @AfterEach
    void tearDown() {
        UserContextHolder.clearContext();
    }

    @Nested
    @DisplayName("내 정보 조회 API")
    class MeTest {

        @Test
        @DisplayName("인증된 사용자면 200과 내 정보를 반환한다")
        void me_AuthenticatedUser_Returns200WithMyInfo() throws Exception {
            // given
            UserContext userContext =
                    UserContext.builder()
                            .userId(AuthApiFixtures.DEFAULT_USER_ID)
                            .email(AuthApiFixtures.DEFAULT_EMAIL)
                            .roles(java.util.Set.of("ADMIN"))
                            .permissions(java.util.Set.of("auth:read"))
                            .build();
            UserContextHolder.setContext(userContext);

            MyInfoResult result = AuthApiFixtures.myInfoResult();
            MyInfoApiResponse response = AuthApiFixtures.myInfoApiResponse();

            given(getMyInfoUseCase.execute(AuthApiFixtures.DEFAULT_USER_ID)).willReturn(result);
            given(queryMapper.toResponse(any(MyInfoResult.class))).willReturn(response);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + AuthAdminEndpoints.ME))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.userId").value(AuthApiFixtures.DEFAULT_USER_ID))
                    .andExpect(jsonPath("$.data.email").value(AuthApiFixtures.DEFAULT_EMAIL))
                    .andExpect(jsonPath("$.data.name").value(AuthApiFixtures.DEFAULT_NAME))
                    .andExpect(jsonPath("$.data.roles").isArray())
                    .andExpect(jsonPath("$.data.permissions").isArray())
                    .andDo(
                            document(
                                    "auth/me",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    responseFields(
                                            fieldWithPath("data.userId").description("사용자 ID"),
                                            fieldWithPath("data.email").description("이메일"),
                                            fieldWithPath("data.name").description("사용자 이름"),
                                            fieldWithPath("data.tenantId").description("테넌트 ID"),
                                            fieldWithPath("data.tenantName").description("테넌트 이름"),
                                            fieldWithPath("data.organizationId")
                                                    .description("조직 ID"),
                                            fieldWithPath("data.organizationName")
                                                    .description("조직 이름"),
                                            fieldWithPath("data.roles[]").description("역할 목록"),
                                            fieldWithPath("data.roles[].id").description("역할 ID"),
                                            fieldWithPath("data.roles[].name").description("역할 이름"),
                                            fieldWithPath("data.permissions[]")
                                                    .description("권한 목록"),
                                            fieldWithPath("data.sellerAdminId")
                                                    .description("셀러 관리자 ID")
                                                    .optional(),
                                            fieldWithPath("data.sellerId")
                                                    .description("셀러 ID")
                                                    .optional(),
                                            fieldWithPath("data.phoneNumber")
                                                    .description("핸드폰 번호")
                                                    .optional(),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }
}
