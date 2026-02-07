package com.ryuqq.marketplace.adapter.in.rest.selleraddress.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.SellerAddressAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.SellerAddressApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.command.RegisterSellerAddressApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.command.UpdateSellerAddressApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.mapper.SellerAddressCommandApiMapper;
import com.ryuqq.marketplace.application.selleraddress.port.in.command.DeleteSellerAddressUseCase;
import com.ryuqq.marketplace.application.selleraddress.port.in.command.RegisterSellerAddressUseCase;
import com.ryuqq.marketplace.application.selleraddress.port.in.command.UpdateSellerAddressUseCase;
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
@WebMvcTest(SellerAddressCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SellerAddressCommandController REST Docs 테스트")
class SellerAddressCommandControllerRestDocsTest {

    private static final String BASE_URL = SellerAddressAdminEndpoints.SELLER_ADDRESSES;
    private static final Long SELLER_ID = 1L;
    private static final Long ADDRESS_ID = 10L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterSellerAddressUseCase registerUseCase;
    @MockitoBean private UpdateSellerAddressUseCase updateUseCase;
    @MockitoBean private DeleteSellerAddressUseCase deleteUseCase;
    @MockitoBean private SellerAddressCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("셀러 주소 등록 API")
    class RegisterTest {

        @Test
        @DisplayName("등록 성공 시 201과 addressId를 반환한다")
        void register_Success_Returns201WithAddressId() throws Exception {
            // given
            RegisterSellerAddressApiRequest request = SellerAddressApiFixtures.registerRequest();

            given(mapper.toCommand(eq(SELLER_ID), any(RegisterSellerAddressApiRequest.class)))
                    .willReturn(null);
            given(registerUseCase.execute(any())).willReturn(ADDRESS_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL, SELLER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.addressId").value(ADDRESS_ID))
                    .andDo(
                            document(
                                    "seller-address/register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerId").description("셀러 ID")),
                                    requestFields(
                                            fieldWithPath("addressType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주소 유형 (SHIPPING, RETURN)"),
                                            fieldWithPath("addressName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주소명")
                                                    .optional(),
                                            fieldWithPath("address").description("주소 정보"),
                                            fieldWithPath("address.zipCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("우편번호"),
                                            fieldWithPath("address.line1")
                                                    .type(JsonFieldType.STRING)
                                                    .description("도로명주소"),
                                            fieldWithPath("address.line2")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세주소")
                                                    .optional(),
                                            fieldWithPath("defaultAddress")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("기본 주소 여부")),
                                    responseFields(
                                            fieldWithPath("data.addressId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 주소 ID"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("셀러 주소 수정 API")
    class UpdateTest {

        @Test
        @DisplayName("수정 성공 시 204 No Content를 반환한다")
        void update_Success_Returns204() throws Exception {
            // given
            UpdateSellerAddressApiRequest request = SellerAddressApiFixtures.updateRequest();

            given(mapper.toCommand(eq(ADDRESS_ID), any(UpdateSellerAddressApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(
                                            BASE_URL + SellerAddressAdminEndpoints.ID,
                                            SELLER_ID,
                                            ADDRESS_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "seller-address/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerId").description("셀러 ID"),
                                            parameterWithName("addressId").description("주소 ID")),
                                    requestFields(
                                            fieldWithPath("addressName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주소명")
                                                    .optional(),
                                            fieldWithPath("address").description("주소 정보"),
                                            fieldWithPath("address.zipCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("우편번호"),
                                            fieldWithPath("address.line1")
                                                    .type(JsonFieldType.STRING)
                                                    .description("도로명주소"),
                                            fieldWithPath("address.line2")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세주소")
                                                    .optional(),
                                            fieldWithPath("defaultAddress")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("기본 주소로 설정 여부")
                                                    .optional())));
        }
    }

    @Nested
    @DisplayName("셀러 주소 삭제(소프트) API")
    class DeleteTest {

        @Test
        @DisplayName("삭제 성공 시 204 No Content를 반환한다")
        void delete_Success_Returns204() throws Exception {
            // given
            given(mapper.toDeleteCommand(ADDRESS_ID)).willReturn(null);
            doNothing().when(deleteUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                    BASE_URL
                                            + SellerAddressAdminEndpoints.ID
                                            + SellerAddressAdminEndpoints.STATUS,
                                    SELLER_ID,
                                    ADDRESS_ID))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "seller-address/delete",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerId").description("셀러 ID"),
                                            parameterWithName("addressId").description("주소 ID"))));
        }
    }
}
