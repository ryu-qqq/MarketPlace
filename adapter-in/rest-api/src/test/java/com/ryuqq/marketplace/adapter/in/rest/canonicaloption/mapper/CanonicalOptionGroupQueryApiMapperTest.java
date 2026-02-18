package com.ryuqq.marketplace.adapter.in.rest.canonicaloption.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.CanonicalOptionApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.query.SearchCanonicalOptionGroupsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.response.CanonicalOptionGroupApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.application.canonicaloption.dto.query.CanonicalOptionGroupSearchParams;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupPageResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CanonicalOptionGroupQueryApiMapper 단위 테스트")
class CanonicalOptionGroupQueryApiMapperTest {

    private CanonicalOptionGroupQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CanonicalOptionGroupQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName(
                "SearchCanonicalOptionGroupsApiRequest를 CanonicalOptionGroupSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            SearchCanonicalOptionGroupsApiRequest request =
                    CanonicalOptionApiFixtures.searchRequest(true, "CODE", "SIZE", 0, 20);

            // when
            CanonicalOptionGroupSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.active()).isTrue();
            assertThat(result.searchField()).isEqualTo("CODE");
            assertThat(result.searchWord()).isEqualTo("SIZE");
            assertThat(result.commonSearchParams().page()).isZero();
            assertThat(result.commonSearchParams().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("page/size가 null이면 기본값으로 변환한다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            SearchCanonicalOptionGroupsApiRequest request =
                    new SearchCanonicalOptionGroupsApiRequest(
                            null, null, null, null, null, null, null);

            // when
            CanonicalOptionGroupSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.commonSearchParams().page()).isZero();
            assertThat(result.commonSearchParams().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("모든 필터가 null이면 전체 조회 파라미터로 변환한다")
        void toSearchParams_AllNulls_ReturnsAllSearchParams() {
            // given
            SearchCanonicalOptionGroupsApiRequest request =
                    CanonicalOptionApiFixtures.searchRequest();

            // when
            CanonicalOptionGroupSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.active()).isNull();
            assertThat(result.searchField()).isNull();
            assertThat(result.searchWord()).isNull();
        }
    }

    @Nested
    @DisplayName("toResponse() - 단일 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("CanonicalOptionGroupResult를 CanonicalOptionGroupApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            CanonicalOptionGroupResult result = CanonicalOptionApiFixtures.groupResult(1L);

            // when
            CanonicalOptionGroupApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.code()).isEqualTo("SIZE_1");
            assertThat(response.nameKo()).isEqualTo("사이즈1");
            assertThat(response.nameEn()).isEqualTo("Size1");
            assertThat(response.active()).isTrue();
            assertThat(response.values()).hasSize(2);
            assertThat(response.createdAt()).isNotNull();
        }

        @Test
        @DisplayName("옵션 값 목록이 올바르게 변환된다")
        void toResponse_ConvertsValues_Correctly() {
            // given
            CanonicalOptionGroupResult result = CanonicalOptionApiFixtures.groupResult(1L);

            // when
            CanonicalOptionGroupApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.values()).hasSize(2);
            assertThat(response.values().get(0).id()).isEqualTo(1L);
            assertThat(response.values().get(1).id()).isEqualTo(2L);
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            CanonicalOptionGroupResult result = CanonicalOptionApiFixtures.groupResult(1L);

            // when
            CanonicalOptionGroupApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.createdAt()).contains("T");
            assertThat(response.createdAt()).contains("+09:00");
        }
    }

    @Nested
    @DisplayName("toResponses() - 목록 변환")
    class ToResponsesTest {

        @Test
        @DisplayName("CanonicalOptionGroupResult 목록을 CanonicalOptionGroupApiResponse 목록으로 변환한다")
        void toResponses_ConvertsList_ReturnsResponseList() {
            // given
            List<CanonicalOptionGroupResult> results = CanonicalOptionApiFixtures.groupResults(3);

            // when
            List<CanonicalOptionGroupApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).code()).isEqualTo("SIZE_1");
            assertThat(responses.get(1).code()).isEqualTo("SIZE_2");
            assertThat(responses.get(2).code()).isEqualTo("SIZE_3");
        }

        @Test
        @DisplayName("빈 목록이면 빈 응답 목록을 반환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // given
            List<CanonicalOptionGroupResult> results = List.of();

            // when
            List<CanonicalOptionGroupApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 결과 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("CanonicalOptionGroupPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            CanonicalOptionGroupPageResult pageResult =
                    CanonicalOptionApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<CanonicalOptionGroupApiResponse> response =
                    mapper.toPageResponse(pageResult);

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
            CanonicalOptionGroupPageResult pageResult =
                    CanonicalOptionApiFixtures.emptyPageResult();

            // when
            PageApiResponse<CanonicalOptionGroupApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }

        @Test
        @DisplayName("페이지 메타 정보가 정확히 변환된다")
        void toPageResponse_ConvertsPageMeta_Correctly() {
            // given
            CanonicalOptionGroupPageResult pageResult =
                    CanonicalOptionApiFixtures.pageResult(5, 1, 10);

            // when
            PageApiResponse<CanonicalOptionGroupApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            assertThat(response.page()).isEqualTo(1);
            assertThat(response.size()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(5);
        }
    }
}
