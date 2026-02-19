package com.ryuqq.marketplace.adapter.in.rest.saleschannel.controller;

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

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.SalesChannelAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.response.SalesChannelApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.mapper.SalesChannelQueryApiMapper;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelPageResult;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelResult;
import com.ryuqq.marketplace.application.saleschannel.port.in.query.SearchSalesChannelByOffsetUseCase;
import java.time.Instant;
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
@WebMvcTest(SalesChannelQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SalesChannelQueryController REST Docs 테스트")
class SalesChannelQueryControllerRestDocsTest {

    private static final String BASE_URL = SalesChannelAdminEndpoints.SALES_CHANNELS;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchSalesChannelByOffsetUseCase searchSalesChannelByOffsetUseCase;
    @MockitoBean private SalesChannelQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("판매채널 목록 검색 API")
    class SearchSalesChannelsTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchSalesChannels_ValidRequest_Returns200WithPage() throws Exception {
            // given
            Instant now = Instant.parse("2025-01-23T01:30:00Z");
            SalesChannelPageResult pageResult =
                    SalesChannelPageResult.of(
                            List.of(
                                    new SalesChannelResult(1L, "네이버", "ACTIVE", now, now),
                                    new SalesChannelResult(2L, "쿠팡", "ACTIVE", now, now),
                                    new SalesChannelResult(3L, "지마켓", "INACTIVE", now, now)),
                            0,
                            20,
                            3);
            PageApiResponse<SalesChannelApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    salesChannelApiResponse(1L, "네이버", "ACTIVE"),
                                    salesChannelApiResponse(2L, "쿠팡", "ACTIVE"),
                                    salesChannelApiResponse(3L, "지마켓", "INACTIVE")),
                            0,
                            20,
                            3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchSalesChannelByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(SalesChannelPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(3))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(20))
                    .andExpect(jsonPath("$.data.totalElements").value(3))
                    .andDo(
                            document(
                                    "sales-channel/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("statuses")
                                                    .description("상태 필터 (ACTIVE, INACTIVE)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (CHANNEL_NAME)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 키 (CREATED_AT, CHANNEL_NAME, 기본값:"
                                                                    + " CREATED_AT)")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC, DESC, 기본값: DESC)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터, 기본값: 0)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기 (기본값: 20)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("판매채널 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 ID"),
                                            fieldWithPath("data.content[].channelName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("판매채널명"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상태 (ACTIVE, INACTIVE)"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시"),
                                            fieldWithPath("data.content[].updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
                                            fieldWithPath("data.page")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재 페이지 번호"),
                                            fieldWithPath("data.size")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("페이지 크기"),
                                            fieldWithPath("data.totalElements")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 데이터 수"),
                                            fieldWithPath("data.totalPages")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 페이지 수"),
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

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchSalesChannels_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            SalesChannelPageResult emptyResult = SalesChannelPageResult.empty(20);
            PageApiResponse<SalesChannelApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchSalesChannelByOffsetUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(SalesChannelPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    private SalesChannelApiResponse salesChannelApiResponse(
            Long id, String channelName, String status) {
        return new SalesChannelApiResponse(
                id, channelName, status, "2025-01-23T10:30:00+09:00", "2025-01-23T10:30:00+09:00");
    }
}
