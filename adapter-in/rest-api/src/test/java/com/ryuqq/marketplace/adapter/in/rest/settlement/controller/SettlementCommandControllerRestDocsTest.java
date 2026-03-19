package com.ryuqq.marketplace.adapter.in.rest.settlement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.ryuqq.marketplace.adapter.in.rest.settlement.SettlementAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.settlement.SettlementApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.HoldSettlementApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementCompleteBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementHoldBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementReleaseBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.mapper.SettlementApiMapper;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.CompleteSettlementEntryBatchCommand;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.HoldSettlementEntryBatchCommand;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.ReleaseSettlementEntryBatchCommand;
import com.ryuqq.marketplace.application.settlement.entry.port.in.command.CompleteSettlementEntryBatchUseCase;
import com.ryuqq.marketplace.application.settlement.entry.port.in.command.HoldSettlementEntryBatchUseCase;
import com.ryuqq.marketplace.application.settlement.entry.port.in.command.ReleaseSettlementEntryBatchUseCase;
import com.ryuqq.marketplace.application.settlement.port.in.command.HoldSettlementUseCase;
import com.ryuqq.marketplace.application.settlement.port.in.command.ReleaseSettlementUseCase;
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
@WebMvcTest(SettlementCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SettlementCommandController REST Docs 테스트")
class SettlementCommandControllerRestDocsTest {

    private static final String SETTLEMENTS_URL = SettlementAdminEndpoints.SETTLEMENTS;
    private static final String DEFAULT_SETTLEMENT_ID = SettlementApiFixtures.DEFAULT_SETTLEMENT_ID;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private HoldSettlementUseCase holdSettlementUseCase;
    @MockitoBean private ReleaseSettlementUseCase releaseSettlementUseCase;
    @MockitoBean private CompleteSettlementEntryBatchUseCase completeSettlementEntryBatchUseCase;
    @MockitoBean private HoldSettlementEntryBatchUseCase holdSettlementEntryBatchUseCase;
    @MockitoBean private ReleaseSettlementEntryBatchUseCase releaseSettlementEntryBatchUseCase;
    @MockitoBean private SettlementApiMapper settlementApiMapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("정산 보류 API")
    class HoldSettlementTest {

        @Test
        @DisplayName("정산 보류 성공")
        void hold_Success() throws Exception {
            // given
            HoldSettlementApiRequest request = SettlementApiFixtures.holdRequest();
            willDoNothing().given(holdSettlementUseCase).execute(anyString(), anyString());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            SETTLEMENTS_URL + SettlementAdminEndpoints.HOLD,
                                            DEFAULT_SETTLEMENT_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andDo(
                            document(
                                    "settlement/hold",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("settlementId").description("정산 ID")),
                                    requestFields(
                                            fieldWithPath("reason")
                                                    .type(JsonFieldType.STRING)
                                                    .description("보류 사유")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NULL)
                                                    .description("응답 데이터 (없음)")
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("보류 사유가 없으면 400을 반환한다")
        void hold_BlankReason_Returns400() throws Exception {
            // given
            HoldSettlementApiRequest request = SettlementApiFixtures.holdRequest("");

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            SETTLEMENTS_URL + SettlementAdminEndpoints.HOLD,
                                            DEFAULT_SETTLEMENT_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("정산 보류 해제 API")
    class ReleaseSettlementTest {

        @Test
        @DisplayName("정산 보류 해제 성공")
        void release_Success() throws Exception {
            // given
            willDoNothing().given(releaseSettlementUseCase).execute(anyString());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                    SETTLEMENTS_URL + SettlementAdminEndpoints.RELEASE,
                                    DEFAULT_SETTLEMENT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andDo(
                            document(
                                    "settlement/release",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("settlementId").description("정산 ID")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NULL)
                                                    .description("응답 데이터 (없음)")
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
    @DisplayName("정산 일괄 완료 API")
    class CompleteBatchTest {

        @Test
        @DisplayName("정산 일괄 완료 성공")
        void completeBatch_Success() throws Exception {
            // given
            SettlementCompleteBatchApiRequest request =
                    SettlementApiFixtures.completeBatchRequest();
            willDoNothing()
                    .given(completeSettlementEntryBatchUseCase)
                    .execute(any(CompleteSettlementEntryBatchCommand.class));
            given(settlementApiMapper.toCompleteBatchCommand(any()))
                    .willReturn(new CompleteSettlementEntryBatchCommand(request.settlementIds()));

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            SETTLEMENTS_URL
                                                    + SettlementAdminEndpoints.COMPLETE_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andDo(
                            document(
                                    "settlement/complete-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("settlementIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("완료 처리할 정산 원장 ID 목록")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NULL)
                                                    .description("응답 데이터 (없음)")
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
    @DisplayName("정산 일괄 보류 API")
    class HoldBatchTest {

        @Test
        @DisplayName("정산 일괄 보류 성공")
        void holdBatch_Success() throws Exception {
            // given
            SettlementHoldBatchApiRequest request = SettlementApiFixtures.holdBatchRequest();
            willDoNothing()
                    .given(holdSettlementEntryBatchUseCase)
                    .execute(any(HoldSettlementEntryBatchCommand.class));
            given(settlementApiMapper.toHoldBatchCommand(any()))
                    .willReturn(
                            new HoldSettlementEntryBatchCommand(
                                    request.settlementIds(), request.holdReason()));

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            SETTLEMENTS_URL + SettlementAdminEndpoints.HOLD_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andDo(
                            document(
                                    "settlement/hold-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("settlementIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("보류 처리할 정산 원장 ID 목록"),
                                            fieldWithPath("holdReason")
                                                    .type(JsonFieldType.STRING)
                                                    .description("보류 사유")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NULL)
                                                    .description("응답 데이터 (없음)")
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
    @DisplayName("정산 일괄 보류 해제 API")
    class ReleaseBatchTest {

        @Test
        @DisplayName("정산 일괄 보류 해제 성공")
        void releaseBatch_Success() throws Exception {
            // given
            SettlementReleaseBatchApiRequest request = SettlementApiFixtures.releaseBatchRequest();
            willDoNothing()
                    .given(releaseSettlementEntryBatchUseCase)
                    .execute(any(ReleaseSettlementEntryBatchCommand.class));
            given(settlementApiMapper.toReleaseBatchCommand(any()))
                    .willReturn(new ReleaseSettlementEntryBatchCommand(request.settlementIds()));

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            SETTLEMENTS_URL
                                                    + SettlementAdminEndpoints.RELEASE_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andDo(
                            document(
                                    "settlement/release-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("settlementIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("보류 해제할 정산 원장 ID 목록")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NULL)
                                                    .description("응답 데이터 (없음)")
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
