package com.ryuqq.marketplace.adapter.in.rest.commoncodetype.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.CommonCodeTypeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.query.SearchCommonCodeTypesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.response.CommonCodeTypeApiResponse;
import com.ryuqq.marketplace.application.commoncodetype.dto.query.CommonCodeTypeSearchParams;
import com.ryuqq.marketplace.application.commoncodetype.dto.response.CommonCodeTypePageResult;
import com.ryuqq.marketplace.application.commoncodetype.dto.response.CommonCodeTypeResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeTypeQueryApiMapper 단위 테스트")
class CommonCodeTypeQueryApiMapperTest {

    private CommonCodeTypeQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CommonCodeTypeQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("모든 필드를 포함한 검색 요청을 변환한다")
        void toSearchParams_AllFields_ReturnsSearchParams() {
            // given
            SearchCommonCodeTypesPageApiRequest request =
                    CommonCodeTypeApiFixtures.searchRequest(
                            true, "code", "PAYMENT", "CARD", "CREATED_AT", "DESC", 0, 20);

            // when
            CommonCodeTypeSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.active()).isTrue();
            assertThat(result.searchField()).isEqualTo("code");
            assertThat(result.searchWord()).isEqualTo("PAYMENT");
            assertThat(result.type()).isEqualTo("CARD");
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
            assertThat(result.sortKey()).isEqualTo("CREATED_AT");
            assertThat(result.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("null 필드에 기본값을 적용한다")
        void toSearchParams_NullFields_AppliesDefaults() {
            // given
            SearchCommonCodeTypesPageApiRequest request = CommonCodeTypeApiFixtures.searchRequest();

            // when
            CommonCodeTypeSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.active()).isNull();
            assertThat(result.searchField()).isNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.type()).isNull();
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("커스텀 페이지 파라미터를 적용한다")
        void toSearchParams_CustomPageParams_AppliesCorrectly() {
            // given
            SearchCommonCodeTypesPageApiRequest request =
                    CommonCodeTypeApiFixtures.searchRequest(
                            false, "name", "결제", null, "DISPLAY_ORDER", "ASC", 2, 50);

            // when
            CommonCodeTypeSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.active()).isFalse();
            assertThat(result.searchField()).isEqualTo("name");
            assertThat(result.searchWord()).isEqualTo("결제");
            assertThat(result.type()).isNull();
            assertThat(result.page()).isEqualTo(2);
            assertThat(result.size()).isEqualTo(50);
            assertThat(result.sortKey()).isEqualTo("DISPLAY_ORDER");
            assertThat(result.sortDirection()).isEqualTo("ASC");
        }
    }

    @Nested
    @DisplayName("toResponse() - 단일 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("CommonCodeTypeResult를 CommonCodeTypeApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            CommonCodeTypeResult result = CommonCodeTypeApiFixtures.codeTypeResult(1L);

            // when
            CommonCodeTypeApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.code()).isEqualTo(CommonCodeTypeApiFixtures.DEFAULT_CODE);
            assertThat(response.name()).isEqualTo(CommonCodeTypeApiFixtures.DEFAULT_NAME);
            assertThat(response.description())
                    .isEqualTo(CommonCodeTypeApiFixtures.DEFAULT_DESCRIPTION);
            assertThat(response.displayOrder())
                    .isEqualTo(CommonCodeTypeApiFixtures.DEFAULT_DISPLAY_ORDER);
            assertThat(response.active()).isTrue();
        }

        @Test
        @DisplayName("날짜를 ISO 8601 형식으로 변환한다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            CommonCodeTypeResult result = CommonCodeTypeApiFixtures.codeTypeResult(1L);

            // when
            CommonCodeTypeApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.createdAt()).contains("2025-01-23");
            assertThat(response.createdAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
            assertThat(response.updatedAt()).isNotNull();
            assertThat(response.updatedAt()).contains("2025-01-23");
            assertThat(response.updatedAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        }

        @Test
        @DisplayName("비활성 상태인 결과를 변환한다")
        void toResponse_InactiveResult_ReturnsInactiveResponse() {
            // given
            CommonCodeTypeResult result =
                    CommonCodeTypeApiFixtures.codeTypeResult(5L, "INACTIVE_CODE", "비활성 타입", false);

            // when
            CommonCodeTypeApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(5L);
            assertThat(response.code()).isEqualTo("INACTIVE_CODE");
            assertThat(response.name()).isEqualTo("비활성 타입");
            assertThat(response.active()).isFalse();
        }
    }

    @Nested
    @DisplayName("toResponses() - 목록 변환")
    class ToResponsesTest {

        @Test
        @DisplayName("결과 목록을 응답 목록으로 변환한다")
        void toResponses_ConvertsResults_ReturnsApiResponses() {
            // given
            List<CommonCodeTypeResult> results = CommonCodeTypeApiFixtures.codeTypeResults(3);

            // when
            List<CommonCodeTypeApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).id()).isEqualTo(1L);
            assertThat(responses.get(1).id()).isEqualTo(2L);
            assertThat(responses.get(2).id()).isEqualTo(3L);
        }

        @Test
        @DisplayName("빈 목록은 빈 응답 목록을 반환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // when
            List<CommonCodeTypeApiResponse> responses = mapper.toResponses(List.of());

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("CommonCodeTypePageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageApiResponse() {
            // given
            CommonCodeTypePageResult pageResult = CommonCodeTypeApiFixtures.pageResult(5, 0, 20);

            // when
            PageApiResponse<CommonCodeTypeApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(5);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(5);
        }

        @Test
        @DisplayName("빈 페이지 결과를 변환한다")
        void toPageResponse_EmptyPageResult_ReturnsEmptyPage() {
            // given
            CommonCodeTypePageResult pageResult = CommonCodeTypeApiFixtures.emptyPageResult();

            // when
            PageApiResponse<CommonCodeTypeApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }
}
