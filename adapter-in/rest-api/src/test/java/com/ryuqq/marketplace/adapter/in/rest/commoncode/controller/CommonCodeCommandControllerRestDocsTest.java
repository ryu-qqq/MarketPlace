package com.ryuqq.marketplace.adapter.in.rest.commoncode.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import com.ryuqq.marketplace.application.commoncode.port.in.command.ChangeCommonCodeStatusUseCase;
import com.ryuqq.marketplace.application.commoncode.port.in.command.RegisterCommonCodeUseCase;
import com.ryuqq.marketplace.application.commoncode.port.in.command.UpdateCommonCodeUseCase;
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
@WebMvcTest(CommonCodeCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("CommonCodeCommandController REST Docs 테스트")
class CommonCodeCommandControllerRestDocsTest {

    private static final String BASE_URL = "/api/v1/market/common-codes";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterCommonCodeUseCase registerUseCase;
    @MockitoBean private UpdateCommonCodeUseCase updateUseCase;
    @MockitoBean private ChangeCommonCodeStatusUseCase changeStatusUseCase;
    @MockitoBean private CommonCodeCommandApiMapper commandMapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("공통 코드 등록 API")
    class RegisterTest {

        @Test
        @DisplayName("유효한 요청이면 201과 생성된 ID를 반환한다")
        void register_ValidRequest_Returns201WithId() throws Exception {
            // given
            RegisterCommonCodeApiRequest request = CommonCodeApiFixtures.registerRequest();
            Long createdId = 100L;

            given(commandMapper.toCommand(any(RegisterCommonCodeApiRequest.class)))
                    .willReturn(null);
            given(registerUseCase.execute(any())).willReturn(createdId);

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
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("공통 코드 타입 ID"),
                                            fieldWithPath("code")
                                                    .type(JsonFieldType.STRING)
                                                    .description("코드값"),
                                            fieldWithPath("displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시명"),
                                            fieldWithPath("displayOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("표시 순서")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 공통 코드 ID"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("공통 코드 수정 API")
    class UpdateTest {

        @Test
        @DisplayName("유효한 요청이면 200을 반환한다")
        void update_ValidRequest_Returns200() throws Exception {
            // given
            Long id = 1L;
            UpdateCommonCodeApiRequest request = CommonCodeApiFixtures.updateRequest();

            given(commandMapper.toCommand(any(Long.class), any(UpdateCommonCodeApiRequest.class)))
                    .willReturn(null);
            willDoNothing().given(updateUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(BASE_URL + "/{id}", id)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "common-code/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(parameterWithName("id").description("공통 코드 ID")),
                                    requestFields(
                                            fieldWithPath("displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시명"),
                                            fieldWithPath("displayOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("표시 순서")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NULL)
                                                    .description("응답 데이터 (null)")
                                                    .optional(),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("공통 코드 활성화 상태 변경 API")
    class ChangeActiveStatusTest {

        @Test
        @DisplayName("유효한 요청이면 200을 반환한다")
        void changeActiveStatus_ValidRequest_Returns200() throws Exception {
            // given
            ChangeActiveStatusApiRequest request = CommonCodeApiFixtures.activateRequest(1L, 2L);

            given(commandMapper.toCommand(any(ChangeActiveStatusApiRequest.class)))
                    .willReturn(null);
            willDoNothing().given(changeStatusUseCase).execute(any());

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
                                            fieldWithPath("ids")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("공통 코드 ID 목록"),
                                            fieldWithPath("active")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description(
                                                            "활성화 여부 (true: 활성화, false: 비활성화)")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NULL)
                                                    .description("응답 데이터 (null)")
                                                    .optional(),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }
}
