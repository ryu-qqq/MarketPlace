package com.ryuqq.marketplace.adapter.in.rest.adminmenu.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.marketplace.adapter.in.rest.adminmenu.AdminMenuAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.adminmenu.AdminMenuApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.adminmenu.dto.response.AdminMenuApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.adminmenu.mapper.AdminMenuQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.application.adminmenu.port.in.query.GetAccessibleMenusUseCase;
import com.ryuqq.marketplace.domain.adminmenu.AdminMenuFixtures;
import com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu;
import com.ryuqq.marketplace.domain.adminmenu.vo.AdminRole;
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
@WebMvcTest(AdminMenuQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("AdminMenuQueryController REST Docs 테스트")
class AdminMenuQueryControllerRestDocsTest {

    private static final String BASE_URL = AdminMenuAdminEndpoints.ADMIN_MENUS;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetAccessibleMenusUseCase getAccessibleMenusUseCase;
    @MockitoBean private AdminMenuQueryApiMapper mapper;
    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("역할별 메뉴 조회 API")
    class GetAccessibleMenusTest {

        @Test
        @DisplayName("인증된 사용자가 요청하면 200과 메뉴 트리를 반환한다")
        void getAccessibleMenus_Authenticated_Returns200WithTree() throws Exception {
            // given
            AdminRole role = AdminRole.ADMIN;
            AdminMenu group = AdminMenuFixtures.activeGroupMenu();
            AdminMenu item = AdminMenuFixtures.activeItemMenu(2L, group.idValue());
            List<AdminMenu> menus = List.of(group, item);
            AdminMenuApiResponse treeResponse = AdminMenuApiFixtures.treeResponse();

            given(accessChecker.resolveHighestRole()).willReturn(role);
            given(getAccessibleMenusUseCase.execute(role)).willReturn(menus);
            given(mapper.toTreeResponse(menus)).willReturn(treeResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.menus").isArray())
                    .andExpect(jsonPath("$.data.menus.length()").value(1))
                    .andExpect(jsonPath("$.data.menus[0].title").value("판매자 관리"))
                    .andExpect(jsonPath("$.data.menus[0].iconName").value("Users"))
                    .andExpect(jsonPath("$.data.menus[0].items").isArray())
                    .andExpect(jsonPath("$.data.menus[0].items.length()").value(2))
                    .andExpect(jsonPath("$.data.menus[0].items[0].title").value("판매자 입점 관리"))
                    .andExpect(
                            jsonPath("$.data.menus[0].items[0].url").value("/seller/application"))
                    .andExpect(jsonPath("$.data.menus[0].items[0].iconName").value("UserCheck"))
                    .andDo(
                            document(
                                    "admin-menu/get-accessible-menus",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    responseFields(
                                            fieldWithPath("data.menus[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("메뉴 그룹 목록"),
                                            fieldWithPath("data.menus[].title")
                                                    .type(JsonFieldType.STRING)
                                                    .description("그룹 제목"),
                                            fieldWithPath("data.menus[].iconName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("그룹 아이콘명"),
                                            fieldWithPath("data.menus[].items[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("하위 메뉴 항목 목록"),
                                            fieldWithPath("data.menus[].items[].title")
                                                    .type(JsonFieldType.STRING)
                                                    .description("메뉴 항목 제목"),
                                            fieldWithPath("data.menus[].items[].url")
                                                    .type(JsonFieldType.STRING)
                                                    .description("메뉴 항목 URL 경로"),
                                            fieldWithPath("data.menus[].items[].iconName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("메뉴 항목 아이콘명"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("접근 가능한 메뉴가 없으면 200과 빈 트리를 반환한다")
        void getAccessibleMenus_NoMenus_Returns200WithEmptyTree() throws Exception {
            // given
            AdminRole role = AdminRole.VIEWER;
            AdminMenuApiResponse emptyResponse = AdminMenuApiFixtures.emptyTreeResponse();

            given(accessChecker.resolveHighestRole()).willReturn(role);
            given(getAccessibleMenusUseCase.execute(role)).willReturn(List.of());
            given(mapper.toTreeResponse(List.of())).willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.menus").isEmpty());
        }
    }
}
