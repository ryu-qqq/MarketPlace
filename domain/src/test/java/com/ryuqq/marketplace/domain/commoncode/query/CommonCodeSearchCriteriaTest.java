package com.ryuqq.marketplace.domain.commoncode.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeSearchCriteria 테스트")
class CommonCodeSearchCriteriaTest {

    private static final String TYPE_CODE = "PAYMENT_METHOD";

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("of()로 검색 조건을 생성한다")
        void createWithOf() {
            // given
            QueryContext<CommonCodeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when
            CommonCodeSearchCriteria criteria =
                    CommonCodeSearchCriteria.of(TYPE_CODE, true, queryContext);

            // then
            assertThat(criteria.commonCodeTypeCode()).isEqualTo(TYPE_CODE);
            assertThat(criteria.active()).isTrue();
            assertThat(criteria.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("defaultOf()로 기본 검색 조건을 생성한다")
        void createDefaultOf() {
            // when
            CommonCodeSearchCriteria criteria = CommonCodeSearchCriteria.defaultOf(TYPE_CODE);

            // then
            assertThat(criteria.commonCodeTypeCode()).isEqualTo(TYPE_CODE);
            assertThat(criteria.active()).isNull();
            assertThat(criteria.queryContext().sortKey()).isEqualTo(CommonCodeSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("activeOnly()로 활성화된 항목만 조회하는 조건을 생성한다")
        void createActiveOnly() {
            // when
            CommonCodeSearchCriteria criteria = CommonCodeSearchCriteria.activeOnly(TYPE_CODE);

            // then
            assertThat(criteria.active()).isTrue();
        }
    }

    @Nested
    @DisplayName("유효성 검증 테스트")
    class ValidationTest {

        @Test
        @DisplayName("commonCodeTypeCode가 null이면 전체 조회 조건으로 생성된다")
        void nullTypeCodeCreatesWithoutTypeFilter() {
            // when
            CommonCodeSearchCriteria criteria =
                    CommonCodeSearchCriteria.of(
                            null, true, QueryContext.defaultOf(CommonCodeSortKey.CREATED_AT));

            // then
            assertThat(criteria.commonCodeTypeCode()).isNull();
            assertThat(criteria.active()).isTrue();
        }

        @Test
        @DisplayName("null queryContext는 기본값으로 대체된다")
        void nullQueryContextDefaultsToDefault() {
            // when
            CommonCodeSearchCriteria criteria = CommonCodeSearchCriteria.of(TYPE_CODE, null, null);

            // then
            assertThat(criteria.queryContext()).isNotNull();
            assertThat(criteria.queryContext().sortKey()).isEqualTo(CommonCodeSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("commonCodeTypeCode는 대문자로 변환되고 trim된다")
        void codeIsNormalizedToUpperCase() {
            // when
            CommonCodeSearchCriteria criteria =
                    CommonCodeSearchCriteria.of(
                            "  payment_method  ",
                            null,
                            QueryContext.defaultOf(CommonCodeSortKey.CREATED_AT));

            // then
            assertThat(criteria.commonCodeTypeCode()).isEqualTo("PAYMENT_METHOD");
        }

        @Test
        @DisplayName("빈 문자열 commonCodeTypeCode는 null로 변환된다")
        void blankCodeBecomesNull() {
            // when
            CommonCodeSearchCriteria criteria =
                    CommonCodeSearchCriteria.of(
                            "   ", null, QueryContext.defaultOf(CommonCodeSortKey.CREATED_AT));

            // then
            assertThat(criteria.commonCodeTypeCode()).isNull();
        }
    }

    @Nested
    @DisplayName("편의 메서드 테스트")
    class ConvenienceMethodTest {

        @Test
        @DisplayName("hasActiveFilter()는 활성화 필터가 있으면 true를 반환한다")
        void hasActiveFilterReturnsTrueWhenActiveExists() {
            // given
            CommonCodeSearchCriteria criteria = CommonCodeSearchCriteria.activeOnly(TYPE_CODE);

            // then
            assertThat(criteria.hasActiveFilter()).isTrue();
        }

        @Test
        @DisplayName("hasActiveFilter()는 활성화 필터가 없으면 false를 반환한다")
        void hasActiveFilterReturnsFalseWhenNoActive() {
            // given
            CommonCodeSearchCriteria criteria = CommonCodeSearchCriteria.defaultOf(TYPE_CODE);

            // then
            assertThat(criteria.hasActiveFilter()).isFalse();
        }

        @Test
        @DisplayName("size()는 페이지 크기를 반환한다")
        void returnsSize() {
            // given
            CommonCodeSearchCriteria criteria = CommonCodeSearchCriteria.defaultOf(TYPE_CODE);

            // then
            assertThat(criteria.size()).isEqualTo(PageRequest.DEFAULT_SIZE);
        }

        @Test
        @DisplayName("offset()은 오프셋을 반환한다")
        void returnsOffset() {
            // given
            QueryContext<CommonCodeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(2, 20));
            CommonCodeSearchCriteria criteria =
                    CommonCodeSearchCriteria.of(TYPE_CODE, null, queryContext);

            // then
            assertThat(criteria.offset()).isEqualTo(40);
        }

        @Test
        @DisplayName("page()는 현재 페이지를 반환한다")
        void returnsPage() {
            // given
            QueryContext<CommonCodeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(3, 20));
            CommonCodeSearchCriteria criteria =
                    CommonCodeSearchCriteria.of(TYPE_CODE, null, queryContext);

            // then
            assertThat(criteria.page()).isEqualTo(3);
        }
    }
}
