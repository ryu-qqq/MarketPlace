package com.ryuqq.marketplace.domain.commoncode.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeSearchCriteria 테스트")
class CommonCodeSearchCriteriaTest {

    private static final CommonCodeTypeId TYPE_ID = CommonCodeTypeId.of(1L);

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
                    CommonCodeSearchCriteria.of(TYPE_ID, true, "CARD", queryContext);

            // then
            assertThat(criteria.commonCodeTypeId()).isEqualTo(TYPE_ID);
            assertThat(criteria.active()).isTrue();
            assertThat(criteria.code()).isEqualTo("CARD");
            assertThat(criteria.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("defaultOf()로 기본 검색 조건을 생성한다")
        void createDefaultOf() {
            // when
            CommonCodeSearchCriteria criteria = CommonCodeSearchCriteria.defaultOf(TYPE_ID);

            // then
            assertThat(criteria.commonCodeTypeId()).isEqualTo(TYPE_ID);
            assertThat(criteria.active()).isNull();
            assertThat(criteria.code()).isNull();
            assertThat(criteria.queryContext().sortKey()).isEqualTo(CommonCodeSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("activeOnly()로 활성화된 항목만 조회하는 조건을 생성한다")
        void createActiveOnly() {
            // when
            CommonCodeSearchCriteria criteria = CommonCodeSearchCriteria.activeOnly(TYPE_ID);

            // then
            assertThat(criteria.active()).isTrue();
            assertThat(criteria.code()).isNull();
        }
    }

    @Nested
    @DisplayName("유효성 검증 테스트")
    class ValidationTest {

        @Test
        @DisplayName("commonCodeTypeId가 null이면 예외를 발생시킨다")
        void nullTypeIdThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    CommonCodeSearchCriteria.of(
                                            null,
                                            true,
                                            "CODE",
                                            QueryContext.defaultOf(CommonCodeSortKey.CREATED_AT)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("null queryContext는 기본값으로 대체된다")
        void nullQueryContextDefaultsToDefault() {
            // when
            CommonCodeSearchCriteria criteria =
                    CommonCodeSearchCriteria.of(TYPE_ID, null, null, null);

            // then
            assertThat(criteria.queryContext()).isNotNull();
            assertThat(criteria.queryContext().sortKey()).isEqualTo(CommonCodeSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("code는 대문자로 변환되고 trim된다")
        void codeIsNormalizedToUpperCase() {
            // when
            CommonCodeSearchCriteria criteria =
                    CommonCodeSearchCriteria.of(
                            TYPE_ID,
                            null,
                            "  card  ",
                            QueryContext.defaultOf(CommonCodeSortKey.CREATED_AT));

            // then
            assertThat(criteria.code()).isEqualTo("CARD");
        }

        @Test
        @DisplayName("빈 문자열 code는 null로 변환된다")
        void blankCodeBecomesNull() {
            // when
            CommonCodeSearchCriteria criteria =
                    CommonCodeSearchCriteria.of(
                            TYPE_ID,
                            null,
                            "   ",
                            QueryContext.defaultOf(CommonCodeSortKey.CREATED_AT));

            // then
            assertThat(criteria.code()).isNull();
        }
    }

    @Nested
    @DisplayName("편의 메서드 테스트")
    class ConvenienceMethodTest {

        @Test
        @DisplayName("commonCodeTypeIdValue()는 타입 ID 원시값을 반환한다")
        void returnsTypeIdValue() {
            // given
            CommonCodeSearchCriteria criteria = CommonCodeSearchCriteria.defaultOf(TYPE_ID);

            // then
            assertThat(criteria.commonCodeTypeIdValue()).isEqualTo(1L);
        }

        @Test
        @DisplayName("hasCodeFilter()는 코드 필터가 있으면 true를 반환한다")
        void hasCodeFilterReturnsTrueWhenCodeExists() {
            // given
            CommonCodeSearchCriteria criteria =
                    CommonCodeSearchCriteria.of(
                            TYPE_ID,
                            null,
                            "CARD",
                            QueryContext.defaultOf(CommonCodeSortKey.CREATED_AT));

            // then
            assertThat(criteria.hasCodeFilter()).isTrue();
        }

        @Test
        @DisplayName("hasCodeFilter()는 코드가 없으면 false를 반환한다")
        void hasCodeFilterReturnsFalseWhenNoCode() {
            // given
            CommonCodeSearchCriteria criteria = CommonCodeSearchCriteria.defaultOf(TYPE_ID);

            // then
            assertThat(criteria.hasCodeFilter()).isFalse();
        }

        @Test
        @DisplayName("hasActiveFilter()는 활성화 필터가 있으면 true를 반환한다")
        void hasActiveFilterReturnsTrueWhenActiveExists() {
            // given
            CommonCodeSearchCriteria criteria = CommonCodeSearchCriteria.activeOnly(TYPE_ID);

            // then
            assertThat(criteria.hasActiveFilter()).isTrue();
        }

        @Test
        @DisplayName("hasActiveFilter()는 활성화 필터가 없으면 false를 반환한다")
        void hasActiveFilterReturnsFalseWhenNoActive() {
            // given
            CommonCodeSearchCriteria criteria = CommonCodeSearchCriteria.defaultOf(TYPE_ID);

            // then
            assertThat(criteria.hasActiveFilter()).isFalse();
        }

        @Test
        @DisplayName("size()는 페이지 크기를 반환한다")
        void returnsSize() {
            // given
            CommonCodeSearchCriteria criteria = CommonCodeSearchCriteria.defaultOf(TYPE_ID);

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
                    CommonCodeSearchCriteria.of(TYPE_ID, null, null, queryContext);

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
                    CommonCodeSearchCriteria.of(TYPE_ID, null, null, queryContext);

            // then
            assertThat(criteria.page()).isEqualTo(3);
        }
    }
}
