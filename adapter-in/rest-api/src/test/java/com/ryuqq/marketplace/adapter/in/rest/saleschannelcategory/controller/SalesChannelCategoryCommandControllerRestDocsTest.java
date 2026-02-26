package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.SalesChannelCategoryAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.SalesChannelCategoryApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.command.RegisterSalesChannelCategoryApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.mapper.SalesChannelCategoryCommandApiMapper;
import com.ryuqq.marketplace.application.saleschannelcategory.port.in.command.RegisterSalesChannelCategoryUseCase;
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
@WebMvcTest(SalesChannelCategoryCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SalesChannelCategoryCommandController REST Docs 테스트")
class SalesChannelCategoryCommandControllerRestDocsTest {

    private static final Long SALES_CHANNEL_ID = 1L;
    private static final Long CATEGORY_ID = 100L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterSalesChannelCategoryUseCase registerUseCase;
    @MockitoBean private SalesChannelCategoryCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("외부채널 카테고리 등록 API")
    class RegisterCategoryTest {

        @Test
        @DisplayName("외부채널 카테고리 등록 성공")
        void registerCategory_Success() throws Exception {
            // given
            RegisterSalesChannelCategoryApiRequest request =
                    SalesChannelCategoryApiFixtures.registerRequest();

            given(
                            mapper.toCommand(
                                    any(Long.class),
                                    any(RegisterSalesChannelCategoryApiRequest.class)))
                    .willReturn(null);
            given(registerUseCase.execute(any())).willReturn(CATEGORY_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            SalesChannelCategoryAdminEndpoints.CATEGORIES,
                                            SALES_CHANNEL_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.categoryIds[0]").value(CATEGORY_ID))
                    .andDo(
                            document(
                                    "sales-channel-category/register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("salesChannelId")
                                                    .description("판매채널 ID")),
                                    requestFields(
                                            fieldWithPath("externalCategoryCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 카테고리 코드"),
                                            fieldWithPath("externalCategoryName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 카테고리명"),
                                            fieldWithPath("parentId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("부모 카테고리 ID (최상위는 0)"),
                                            fieldWithPath("depth")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 깊이 (0부터 시작)"),
                                            fieldWithPath("path")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리 경로"),
                                            fieldWithPath("sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("leaf")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("리프 노드 여부"),
                                            fieldWithPath("displayPath")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시용 이름 경로")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.categoryIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("생성된 카테고리 ID 목록"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }
}
