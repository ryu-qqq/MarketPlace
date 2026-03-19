package com.ryuqq.marketplace.adapter.in.rest.settlement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.settlement.SettlementAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.mapper.SettlementApiMapper;
import com.ryuqq.marketplace.application.settlement.dto.response.SettlementEntryPageResult;
import com.ryuqq.marketplace.application.settlement.entry.port.in.query.GetSettlementEntryListUseCase;
import com.ryuqq.marketplace.application.settlement.port.in.query.GetDailySettlementUseCase;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;
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
@WebMvcTest(SettlementQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SettlementQueryController REST Docs 테스트")
class SettlementQueryControllerRestDocsTest {

    private static final String SETTLEMENTS_URL = SettlementAdminEndpoints.SETTLEMENTS;
    private static final String DAILY_URL =
            SettlementAdminEndpoints.SETTLEMENTS + SettlementAdminEndpoints.DAILY;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetSettlementEntryListUseCase getSettlementEntryListUseCase;
    @MockitoBean private GetDailySettlementUseCase getDailySettlementUseCase;
    @MockitoBean private SettlementApiMapper settlementApiMapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("정산 대상 목록 조회 API")
    class GetSettlementsTest {

        @Test
        @DisplayName("정산 대상 목록 조회 성공")
        void getSettlements_Success() throws Exception {
            // given
            SettlementEntryPageResult pageResult =
                    new SettlementEntryPageResult(List.of(), new PageMeta(0, 20, 0L, 0));
            PageApiResponse<SettlementListItemApiResponse> pageResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(getSettlementEntryListUseCase.execute(any())).willReturn(pageResult);
            given(settlementApiMapper.toSearchParams(any())).willReturn(null);
            given(settlementApiMapper.toPageResponse(any())).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(SETTLEMENTS_URL)
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andDo(
                            document(
                                    "settlement/list",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("정산 원장 목록"),
                                            fieldWithPath("data.totalElements")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 데이터 수"),
                                            fieldWithPath("data.totalPages")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 페이지 수"),
                                            fieldWithPath("data.page")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재 페이지"),
                                            fieldWithPath("data.size")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("페이지 크기"),
                                            fieldWithPath("data.first")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("첫 페이지 여부"),
                                            fieldWithPath("data.last")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("마지막 페이지 여부"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("일별 정산 내역 조회 API")
    class GetDailySettlementTest {

        @Test
        @DisplayName("일별 정산 조회 성공 - 빈 목록을 반환한다")
        void getDaily_ReturnsEmptyList() throws Exception {
            // given
            given(getDailySettlementUseCase.execute(any())).willReturn(List.of());
            given(settlementApiMapper.toDailySearchParams(any())).willReturn(null);
            given(settlementApiMapper.toDailyPageResponse(any(), anyInt(), anyInt()))
                    .willReturn(PageApiResponse.of(List.of(), 0, 20, 0));

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(DAILY_URL)
                                    .param("startDate", "2026-03-01")
                                    .param("endDate", "2026-03-19")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andDo(
                            document(
                                    "settlement/daily",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    responseFields(
                                            fieldWithPath("data.content")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("일별 정산 목록"),
                                            fieldWithPath("data.totalElements")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 데이터 수"),
                                            fieldWithPath("data.totalPages")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 페이지 수"),
                                            fieldWithPath("data.page")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재 페이지"),
                                            fieldWithPath("data.size")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("페이지 크기"),
                                            fieldWithPath("data.first")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("첫 페이지 여부"),
                                            fieldWithPath("data.last")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("마지막 페이지 여부"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }
}
