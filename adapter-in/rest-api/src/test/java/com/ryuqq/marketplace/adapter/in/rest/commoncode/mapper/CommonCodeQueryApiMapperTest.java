package com.ryuqq.marketplace.adapter.in.rest.commoncode.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.CommonCodeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.query.SearchCommonCodesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.response.CommonCodeApiResponse;
import com.ryuqq.marketplace.application.commoncode.dto.query.CommonCodeSearchParams;
import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodePageResult;
import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodeResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeQueryApiMapper 단위 테스트")
class CommonCodeQueryApiMapperTest {

    private final CommonCodeQueryApiMapper sut = new CommonCodeQueryApiMapper();

    @Nested
    @DisplayName("toSearchParams() - 조회 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchCommonCodesPageApiRequest를 CommonCodeSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            SearchCommonCodesPageApiRequest request =
                    CommonCodeApiFixtures.searchRequest("PAYMENT_METHOD", true, 0, 20);

            // when
            CommonCodeSearchParams result = sut.toSearchParams(request);

            // then
            assertThat(result.commonCodeTypeCode()).isEqualTo("PAYMENT_METHOD");
            assertThat(result.active()).isTrue();
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("page/size가 null이면 기본값으로 변환한다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            SearchCommonCodesPageApiRequest request =
                    new SearchCommonCodesPageApiRequest(
                            "PAYMENT_METHOD", null, null, null, null, null);

            // when
            CommonCodeSearchParams result = sut.toSearchParams(request);

            // then
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("toResponse() - 단일 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("CommonCodeResult를 CommonCodeApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            CommonCodeResult result = CommonCodeApiFixtures.commonCodeResult(1L);

            // when
            CommonCodeApiResponse response = sut.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.commonCodeTypeId()).isEqualTo(1L);
            assertThat(response.code()).isEqualTo("CREDIT_CARD");
            assertThat(response.displayName()).isEqualTo("신용카드");
            assertThat(response.displayOrder()).isEqualTo(1);
            assertThat(response.active()).isTrue();
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            CommonCodeResult result = CommonCodeApiFixtures.commonCodeResult(1L);

            // when
            CommonCodeApiResponse response = sut.toResponse(result);

            // then
            assertThat(response.createdAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        }
    }

    @Nested
    @DisplayName("toResponses() - 목록 변환")
    class ToResponsesTest {

        @Test
        @DisplayName("CommonCodeResult 목록을 CommonCodeApiResponse 목록으로 변환한다")
        void toResponses_ConvertsList_ReturnsResponseList() {
            // given
            List<CommonCodeResult> results = CommonCodeApiFixtures.commonCodeResults(3);

            // when
            List<CommonCodeApiResponse> responses = sut.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).code()).isEqualTo("CODE_1");
            assertThat(responses.get(1).code()).isEqualTo("CODE_2");
            assertThat(responses.get(2).code()).isEqualTo("CODE_3");
        }

        @Test
        @DisplayName("빈 목록이면 빈 응답 목록을 반환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // given
            List<CommonCodeResult> results = List.of();

            // when
            List<CommonCodeApiResponse> responses = sut.toResponses(results);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 결과 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("CommonCodePageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            CommonCodePageResult pageResult = CommonCodeApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<CommonCodeApiResponse> response = sut.toPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(3);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("빈 결과이면 빈 페이지 응답을 반환한다")
        void toPageResponse_EmptyResult_ReturnsEmptyPage() {
            // given
            CommonCodePageResult pageResult = CommonCodeApiFixtures.emptyPageResult();

            // when
            PageApiResponse<CommonCodeApiResponse> response = sut.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }
}
