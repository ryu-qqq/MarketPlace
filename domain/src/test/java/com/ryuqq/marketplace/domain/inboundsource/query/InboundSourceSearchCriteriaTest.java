package com.ryuqq.marketplace.domain.inboundsource.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceStatus;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundSourceSearchCriteria 단위 테스트")
class InboundSourceSearchCriteriaTest {

    private QueryContext<InboundSourceSortKey> defaultQueryContext() {
        return QueryContext.defaultOf(InboundSourceSortKey.CREATED_AT);
    }

    @Nested
    @DisplayName("of() 생성 테스트")
    class OfTest {

        @Test
        @DisplayName("전체 파라미터로 검색 조건을 생성한다")
        void createWithAllParams() {
            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(
                            List.of(InboundSourceType.CRAWLING, InboundSourceType.LEGACY),
                            List.of(InboundSourceStatus.ACTIVE),
                            InboundSourceSearchField.CODE,
                            "SETOF",
                            defaultQueryContext());

            assertThat(criteria.types())
                    .containsExactlyInAnyOrder(
                            InboundSourceType.CRAWLING, InboundSourceType.LEGACY);
            assertThat(criteria.statuses()).containsExactly(InboundSourceStatus.ACTIVE);
            assertThat(criteria.searchField()).isEqualTo(InboundSourceSearchField.CODE);
            assertThat(criteria.searchWord()).isEqualTo("SETOF");
        }

        @Test
        @DisplayName("null 파라미터로 검색 조건을 생성한다")
        void createWithNullParams() {
            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(null, null, null, null, defaultQueryContext());

            assertThat(criteria.types()).isNull();
            assertThat(criteria.statuses()).isNull();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
        }
    }

    @Nested
    @DisplayName("hasTypesFilter() 테스트")
    class HasTypesFilterTest {

        @Test
        @DisplayName("타입 필터가 있으면 true를 반환한다")
        void returnsTrueWhenTypesExist() {
            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(
                            List.of(InboundSourceType.CRAWLING),
                            null,
                            null,
                            null,
                            defaultQueryContext());

            assertThat(criteria.hasTypesFilter()).isTrue();
        }

        @Test
        @DisplayName("타입 필터가 null이면 false를 반환한다")
        void returnsFalseWhenTypesIsNull() {
            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(null, null, null, null, defaultQueryContext());

            assertThat(criteria.hasTypesFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasStatusesFilter() 테스트")
    class HasStatusesFilterTest {

        @Test
        @DisplayName("상태 필터가 있으면 true를 반환한다")
        void returnsTrueWhenStatusesExist() {
            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(
                            null,
                            List.of(InboundSourceStatus.ACTIVE),
                            null,
                            null,
                            defaultQueryContext());

            assertThat(criteria.hasStatusesFilter()).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 null이면 false를 반환한다")
        void returnsFalseWhenStatusesIsNull() {
            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(null, null, null, null, defaultQueryContext());

            assertThat(criteria.hasStatusesFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchCondition() 테스트")
    class HasSearchConditionTest {

        @Test
        @DisplayName("검색어가 있으면 true를 반환한다")
        void returnsTrueWhenSearchWordExists() {
            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(
                            null, null, null, "SETOF", defaultQueryContext());

            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("검색어가 공백이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsBlank() {
            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(null, null, null, "  ", defaultQueryContext());

            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("검색어가 null이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsNull() {
            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(null, null, null, null, defaultQueryContext());

            assertThat(criteria.hasSearchCondition()).isFalse();
        }
    }

    @Nested
    @DisplayName("typeNames() / statusNames() 테스트")
    class NamesTest {

        @Test
        @DisplayName("타입 목록의 이름 문자열 리스트를 반환한다")
        void returnsTypeNameStrings() {
            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(
                            List.of(InboundSourceType.CRAWLING, InboundSourceType.LEGACY),
                            null,
                            null,
                            null,
                            defaultQueryContext());

            assertThat(criteria.typeNames()).containsExactlyInAnyOrder("CRAWLING", "LEGACY");
        }

        @Test
        @DisplayName("타입 목록이 null이면 빈 리스트를 반환한다")
        void returnsEmptyTypeNamesWhenNull() {
            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(null, null, null, null, defaultQueryContext());

            assertThat(criteria.typeNames()).isEmpty();
        }

        @Test
        @DisplayName("상태 목록의 이름 문자열 리스트를 반환한다")
        void returnsStatusNameStrings() {
            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(
                            null,
                            List.of(InboundSourceStatus.ACTIVE, InboundSourceStatus.INACTIVE),
                            null,
                            null,
                            defaultQueryContext());

            assertThat(criteria.statusNames()).containsExactlyInAnyOrder("ACTIVE", "INACTIVE");
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("types 리스트는 외부 변경에 영향을 받지 않는다")
        void typeListIsImmutable() {
            List<InboundSourceType> mutableTypes =
                    new ArrayList<>(List.of(InboundSourceType.CRAWLING));

            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(
                            mutableTypes, null, null, null, defaultQueryContext());

            mutableTypes.add(InboundSourceType.LEGACY);

            assertThat(criteria.types()).hasSize(1);
            assertThat(criteria.types()).containsOnly(InboundSourceType.CRAWLING);
        }

        @Test
        @DisplayName("statuses 리스트는 외부 변경에 영향을 받지 않는다")
        void statusListIsImmutable() {
            List<InboundSourceStatus> mutableStatuses =
                    new ArrayList<>(List.of(InboundSourceStatus.ACTIVE));

            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(
                            null, mutableStatuses, null, null, defaultQueryContext());

            mutableStatuses.add(InboundSourceStatus.INACTIVE);

            assertThat(criteria.statuses()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("InboundSourceSearchField 테스트")
    class SearchFieldTest {

        @Test
        @DisplayName("CODE 필드명이 올바르다")
        void codeFieldName() {
            assertThat(InboundSourceSearchField.CODE.fieldName()).isEqualTo("code");
        }

        @Test
        @DisplayName("NAME 필드명이 올바르다")
        void nameFieldName() {
            assertThat(InboundSourceSearchField.NAME.fieldName()).isEqualTo("name");
        }

        @Test
        @DisplayName("fromString으로 필드를 변환한다")
        void parseFromString() {
            assertThat(InboundSourceSearchField.fromString("code"))
                    .isEqualTo(InboundSourceSearchField.CODE);
            assertThat(InboundSourceSearchField.fromString("NAME"))
                    .isEqualTo(InboundSourceSearchField.NAME);
        }

        @Test
        @DisplayName("null이면 null을 반환한다")
        void parseNullReturnsNull() {
            assertThat(InboundSourceSearchField.fromString(null)).isNull();
        }
    }

    @Nested
    @DisplayName("InboundSourceSortKey 테스트")
    class SortKeyTest {

        @Test
        @DisplayName("CREATED_AT 필드명이 올바르다")
        void createdAtFieldName() {
            assertThat(InboundSourceSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("defaultKey()는 CREATED_AT을 반환한다")
        void defaultKeyIsCreatedAt() {
            assertThat(InboundSourceSortKey.defaultKey())
                    .isEqualTo(InboundSourceSortKey.CREATED_AT);
        }
    }
}
