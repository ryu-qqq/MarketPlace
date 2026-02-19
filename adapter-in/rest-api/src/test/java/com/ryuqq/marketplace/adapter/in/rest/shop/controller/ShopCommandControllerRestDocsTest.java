package com.ryuqq.marketplace.adapter.in.rest.shop.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.shop.ShopAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.shop.ShopApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.command.RegisterShopApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.command.UpdateShopApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shop.mapper.ShopCommandApiMapper;
import com.ryuqq.marketplace.application.shop.port.in.command.RegisterShopUseCase;
import com.ryuqq.marketplace.application.shop.port.in.command.UpdateShopUseCase;
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
@WebMvcTest(ShopCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ShopCommandController REST Docs 테스트")
class ShopCommandControllerRestDocsTest {

    private static final String BASE_URL = ShopAdminEndpoints.SHOPS;
    private static final Long SHOP_ID = 1L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterShopUseCase registerShopUseCase;
    @MockitoBean private UpdateShopUseCase updateShopUseCase;
    @MockitoBean private ShopCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("외부몰 등록 API")
    class RegisterShopTest {

        @Test
        @DisplayName("외부몰 등록 성공")
        void registerShop_Success() throws Exception {
            // given
            RegisterShopApiRequest request = ShopApiFixtures.registerRequest();

            given(mapper.toCommand(any(RegisterShopApiRequest.class))).willReturn(null);
            given(registerShopUseCase.execute(any())).willReturn(SHOP_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.shopId").value(SHOP_ID))
                    .andDo(
                            document(
                                    "shop/register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("salesChannelId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 ID"),
                                            fieldWithPath("shopName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부몰명"),
                                            fieldWithPath("accountId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("계정 ID")),
                                    responseFields(
                                            fieldWithPath("data.shopId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 외부몰 ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("유효성 검증 실패 시 400을 반환한다")
        void registerShop_ValidationFails_Returns400() throws Exception {
            // given
            RegisterShopApiRequest invalidRequest = new RegisterShopApiRequest(null, "", ""); // 빈 값

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("외부몰 수정 API")
    class UpdateShopTest {

        @Test
        @DisplayName("외부몰 수정 성공")
        void updateShop_Success() throws Exception {
            // given
            UpdateShopApiRequest request = ShopApiFixtures.updateRequest();

            given(mapper.toCommand(any(Long.class), any(UpdateShopApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateShopUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(
                                            BASE_URL + ShopAdminEndpoints.SHOP_ID, SHOP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "shop/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("shopId").description("외부몰 ID")),
                                    requestFields(
                                            fieldWithPath("shopName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부몰명"),
                                            fieldWithPath("accountId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("계정 ID"),
                                            fieldWithPath("status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상태 (ACTIVE, INACTIVE)"))));
        }

        @Test
        @DisplayName("유효성 검증 실패 시 400을 반환한다")
        void updateShop_ValidationFails_Returns400() throws Exception {
            // given
            UpdateShopApiRequest invalidRequest = new UpdateShopApiRequest("", "", null); // 빈 값

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(
                                            BASE_URL + ShopAdminEndpoints.SHOP_ID, SHOP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }
}
