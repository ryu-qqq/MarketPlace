package com.ryuqq.marketplace.adapter.in.rest.commoncodetype.controller;

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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.CommonCodeTypeAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.CommonCodeTypeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command.ChangeActiveStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command.RegisterCommonCodeTypeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command.UpdateCommonCodeTypeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.mapper.CommonCodeTypeCommandApiMapper;
import com.ryuqq.marketplace.application.commoncodetype.port.in.command.ChangeCommonCodeTypeStatusUseCase;
import com.ryuqq.marketplace.application.commoncodetype.port.in.command.RegisterCommonCodeTypeUseCase;
import com.ryuqq.marketplace.application.commoncodetype.port.in.command.UpdateCommonCodeTypeUseCase;
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
@WebMvcTest(CommonCodeTypeCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("CommonCodeTypeCommandController REST Docs 테스트")
class CommonCodeTypeCommandControllerRestDocsTest {

    private static final String BASE_URL = CommonCodeTypeAdminEndpoints.COMMON_CODE_TYPES;
    private static final Long CODE_TYPE_ID = 1L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterCommonCodeTypeUseCase registerUseCase;
    @MockitoBean private UpdateCommonCodeTypeUseCase updateUseCase;
    @MockitoBean private ChangeCommonCodeTypeStatusUseCase changeStatusUseCase;
    @MockitoBean private CommonCodeTypeCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("공통 코드 타입 등록 API")
    class RegisterTest {

        @Test
        @DisplayName("등록 성공")
        void register_Success() throws Exception {
            // given
            RegisterCommonCodeTypeApiRequest request = CommonCodeTypeApiFixtures.registerRequest();

            given(mapper.toCommand(any(RegisterCommonCodeTypeApiRequest.class))).willReturn(null);
            given(registerUseCase.execute(any())).willReturn(CODE_TYPE_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data").value(CODE_TYPE_ID))
                    .andDo(
                            document(
                                    "common-code-type/register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("code")
                                                    .type(JsonFieldType.STRING)
                                                    .description("코드 (영문 대문자 + 숫자 + 언더스코어)"),
                                            fieldWithPath("name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이름"),
                                            fieldWithPath("description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명"),
                                            fieldWithPath("displayOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("표시 순서 (0~9999)")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 공통 코드 타입 ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("공통 코드 타입 수정 API")
    class UpdateTest {

        @Test
        @DisplayName("수정 성공")
        void update_Success() throws Exception {
            // given
            UpdateCommonCodeTypeApiRequest request = CommonCodeTypeApiFixtures.updateRequest();

            given(mapper.toCommand(any(Long.class), any(UpdateCommonCodeTypeApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(
                                            BASE_URL + CommonCodeTypeAdminEndpoints.ID,
                                            CODE_TYPE_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "common-code-type/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("commonCodeTypeId")
                                                    .description("공통 코드 타입 ID")),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이름"),
                                            fieldWithPath("description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명"),
                                            fieldWithPath("displayOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("표시 순서")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터")
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("공통 코드 타입 활성화/비활성화 API")
    class ChangeActiveStatusTest {

        @Test
        @DisplayName("상태 변경 성공")
        void changeActiveStatus_Success() throws Exception {
            // given
            ChangeActiveStatusApiRequest request =
                    CommonCodeTypeApiFixtures.changeActiveStatusRequest();

            given(mapper.toCommand(any(ChangeActiveStatusApiRequest.class))).willReturn(null);
            doNothing().when(changeStatusUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL + CommonCodeTypeAdminEndpoints.ACTIVE_STATUS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "common-code-type/change-active-status",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("ids")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("공통 코드 타입 ID 목록"),
                                            fieldWithPath("active")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description(
                                                            "활성화 여부 (true: 활성화, false: 비활성화)")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터")
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }
}
