package com.ryuqq.marketplace.adapter.in.rest.commoncode.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
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
import com.ryuqq.marketplace.adapter.in.rest.commoncode.CommonCodeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.command.ChangeActiveStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.command.RegisterCommonCodeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.command.UpdateCommonCodeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.mapper.CommonCodeCommandApiMapper;
import com.ryuqq.marketplace.application.commoncode.dto.command.ChangeCommonCodeStatusCommand;
import com.ryuqq.marketplace.application.commoncode.dto.command.RegisterCommonCodeCommand;
import com.ryuqq.marketplace.application.commoncode.dto.command.UpdateCommonCodeCommand;
import com.ryuqq.marketplace.application.commoncode.port.in.command.ChangeCommonCodeStatusUseCase;
import com.ryuqq.marketplace.application.commoncode.port.in.command.RegisterCommonCodeUseCase;
import com.ryuqq.marketplace.application.commoncode.port.in.command.UpdateCommonCodeUseCase;
import java.util.List;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(CommonCodeCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("CommonCodeCommandController 단위 테스트")
class CommonCodeCommandControllerTest {

    private static final String BASE_URL = "/api/v1/market/common-codes";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterCommonCodeUseCase registerUseCase;
    @MockitoBean private UpdateCommonCodeUseCase updateUseCase;
    @MockitoBean private ChangeCommonCodeStatusUseCase changeStatusUseCase;
    @MockitoBean private CommonCodeCommandApiMapper commandMapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v2/admin/common-codes - 공통 코드 등록")
    class RegisterTest {

        @Test
        @DisplayName("유효한 요청이면 201과 생성된 ID를 반환한다")
        void register_ValidRequest_Returns201WithId() throws Exception {
            // given
            RegisterCommonCodeApiRequest request = CommonCodeApiFixtures.registerRequest();
            RegisterCommonCodeCommand command =
                    new RegisterCommonCodeCommand(1L, "CREDIT_CARD", "신용카드", 1);
            Long createdId = 100L;

            given(commandMapper.toCommand(any(RegisterCommonCodeApiRequest.class)))
                    .willReturn(command);
            given(registerUseCase.execute(command)).willReturn(createdId);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data").value(100))
                    .andDo(
                            document(
                                    "common-code/register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("commonCodeTypeId")
                                                    .description("공통 코드 타입 ID"),
                                            fieldWithPath("code").description("코드값"),
                                            fieldWithPath("displayName").description("표시명"),
                                            fieldWithPath("displayOrder").description("표시 순서")),
                                    responseFields(
                                            fieldWithPath("data").description("생성된 공통 코드 ID"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));

            then(commandMapper).should().toCommand(any(RegisterCommonCodeApiRequest.class));
            then(registerUseCase).should().execute(command);
        }

        @Test
        @DisplayName("commonCodeTypeId가 null이면 400을 반환한다")
        void register_NullTypeId_Returns400() throws Exception {
            // given
            RegisterCommonCodeApiRequest request =
                    new RegisterCommonCodeApiRequest(null, "CODE", "표시명", 1);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("code가 빈 문자열이면 400을 반환한다")
        void register_BlankCode_Returns400() throws Exception {
            // given
            RegisterCommonCodeApiRequest request =
                    new RegisterCommonCodeApiRequest(1L, "", "표시명", 1);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("displayName이 빈 문자열이면 400을 반환한다")
        void register_BlankDisplayName_Returns400() throws Exception {
            // given
            RegisterCommonCodeApiRequest request =
                    new RegisterCommonCodeApiRequest(1L, "CODE", "", 1);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v2/admin/common-codes/{id} - 공통 코드 수정")
    class UpdateTest {

        @Test
        @DisplayName("유효한 요청이면 200을 반환한다")
        void update_ValidRequest_Returns200() throws Exception {
            // given
            Long id = 1L;
            UpdateCommonCodeApiRequest request = CommonCodeApiFixtures.updateRequest();
            UpdateCommonCodeCommand command = new UpdateCommonCodeCommand(id, "수정된 표시명", 2);

            given(commandMapper.toCommand(any(Long.class), any(UpdateCommonCodeApiRequest.class)))
                    .willReturn(command);
            willDoNothing().given(updateUseCase).execute(command);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(BASE_URL + "/{id}", id)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andDo(
                            document(
                                    "common-code/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(parameterWithName("id").description("공통 코드 ID")),
                                    requestFields(
                                            fieldWithPath("displayName").description("표시명"),
                                            fieldWithPath("displayOrder").description("표시 순서")),
                                    responseFields(
                                            fieldWithPath("data").description("응답 데이터 (null)"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));

            then(commandMapper)
                    .should()
                    .toCommand(any(Long.class), any(UpdateCommonCodeApiRequest.class));
            then(updateUseCase).should().execute(command);
        }

        @Test
        @DisplayName("displayName이 빈 문자열이면 400을 반환한다")
        void update_BlankDisplayName_Returns400() throws Exception {
            // given
            UpdateCommonCodeApiRequest request = new UpdateCommonCodeApiRequest("", 1);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(BASE_URL + "/{id}", 1L)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v2/admin/common-codes/active-status - 활성화 상태 변경")
    class ChangeActiveStatusTest {

        @Test
        @DisplayName("유효한 요청이면 200을 반환한다")
        void changeActiveStatus_ValidRequest_Returns200() throws Exception {
            // given
            ChangeActiveStatusApiRequest request = CommonCodeApiFixtures.activateRequest(1L, 2L);
            ChangeCommonCodeStatusCommand command =
                    new ChangeCommonCodeStatusCommand(List.of(1L, 2L), true);

            given(commandMapper.toCommand(any(ChangeActiveStatusApiRequest.class)))
                    .willReturn(command);
            willDoNothing().given(changeStatusUseCase).execute(command);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(BASE_URL + "/active-status")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "common-code/change-active-status",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("ids").description("공통 코드 ID 목록"),
                                            fieldWithPath("active").description("활성화 여부")),
                                    responseFields(
                                            fieldWithPath("data").description("응답 데이터 (null)"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));

            then(commandMapper).should().toCommand(any(ChangeActiveStatusApiRequest.class));
            then(changeStatusUseCase).should().execute(command);
        }

        @Test
        @DisplayName("ids가 비어있으면 400을 반환한다")
        void changeActiveStatus_EmptyIds_Returns400() throws Exception {
            // given
            ChangeActiveStatusApiRequest request =
                    new ChangeActiveStatusApiRequest(List.of(), true);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(BASE_URL + "/active-status")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("active가 null이면 400을 반환한다")
        void changeActiveStatus_NullActive_Returns400() throws Exception {
            // given
            String requestJson =
                    """
                    {"ids": [1, 2], "active": null}
                    """;

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(BASE_URL + "/active-status")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestJson))
                    .andExpect(status().isBadRequest());
        }
    }
}
