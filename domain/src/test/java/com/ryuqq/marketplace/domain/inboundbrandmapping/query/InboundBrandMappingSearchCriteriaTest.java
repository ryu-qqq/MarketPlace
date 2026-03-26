package com.ryuqq.marketplace.domain.inboundbrandmapping.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.inboundbrandmapping.vo.InboundBrandMappingStatus;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundBrandMappingSearchCriteria 단위 테스트")
class InboundBrandMappingSearchCriteriaTest {

    private QueryContext<InboundBrandMappingSortKey> defaultQueryContext() {
        return QueryContext.defaultOf(InboundBrandMappingSortKey.CREATED_AT);
    }

    @Nested
    @DisplayName("of() 생성 테스트")
    class OfTest {

        @Test
        @DisplayName("전체 파라미터로 검색 조건을 생성한다")
        void createWithAllParams() {
            InboundBrandMappingSearchCriteria criteria =
                    InboundBrandMappingSearchCriteria.of(
                            1L,
                            List.of(InboundBrandMappingStatus.ACTIVE),
                            InboundBrandMappingSearchField.EXTERNAL_CODE,
                            "BR001",
                            defaultQueryContext());

            assertThat(criteria.inboundSourceId()).isEqualTo(1L);
            assertThat(criteria.statuses()).containsExactly(InboundBrandMappingStatus.ACTIVE);
            assertThat(criteria.searchField())
                    .isEqualTo(InboundBrandMappingSearchField.EXTERNAL_CODE);
            assertThat(criteria.searchWord()).isEqualTo("BR001");
        }

        @Test
        @DisplayName("null 파라미터로 검색 조건을 생성한다")
        void createWithNullParams() {
            InboundBrandMappingSearchCriteria criteria =
                    InboundBrandMappingSearchCriteria.of(
                            null, null, null, null, defaultQueryContext());

            assertThat(criteria.inboundSourceId()).isNull();
            assertThat(criteria.statuses()).isNull();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
        }
    }

    @Nested
    @DisplayName("hasInboundSourceIdFilter() 테스트")
    class HasInboundSourceIdFilterTest {

        @Test
        @DisplayName("sourceId 필터가 있으면 true를 반환한다")
        void returnsTrueWhenSourceIdExists() {
            InboundBrandMappingSearchCriteria criteria =
                    InboundBrandMappingSearchCriteria.of(
                            1L, null, null, null, defaultQueryContext());

            assertThat(criteria.hasInboundSourceIdFilter()).isTrue();
        }

        @Test
        @DisplayName("sourceId 필터가 없으면 false를 반환한다")
        void returnsFalseWhenSourceIdIsNull() {
            InboundBrandMappingSearchCriteria criteria =
                    InboundBrandMappingSearchCriteria.of(
                            null, null, null, null, defaultQueryContext());

            assertThat(criteria.hasInboundSourceIdFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasStatusesFilter() 테스트")
    class HasStatusesFilterTest {

        @Test
        @DisplayName("상태 필터가 있으면 true를 반환한다")
        void returnsTrueWhenStatusesExist() {
            InboundBrandMappingSearchCriteria criteria =
                    InboundBrandMappingSearchCriteria.of(
                            null,
                            List.of(InboundBrandMappingStatus.ACTIVE),
                            null,
                            null,
                            defaultQueryContext());

            assertThat(criteria.hasStatusesFilter()).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 null이면 false를 반환한다")
        void returnsFalseWhenStatusesIsNull() {
            InboundBrandMappingSearchCriteria criteria =
                    InboundBrandMappingSearchCriteria.of(
                            null, null, null, null, defaultQueryContext());

            assertThat(criteria.hasStatusesFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchCondition() 테스트")
    class HasSearchConditionTest {

        @Test
        @DisplayName("검색어가 있으면 true를 반환한다")
        void returnsTrueWhenSearchWordExists() {
            InboundBrandMappingSearchCriteria criteria =
                    InboundBrandMappingSearchCriteria.of(
                            null, null, null, "BR001", defaultQueryContext());

            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("검색어가 null이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsNull() {
            InboundBrandMappingSearchCriteria criteria =
                    InboundBrandMappingSearchCriteria.of(
                            null, null, null, null, defaultQueryContext());

            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("검색어가 공백이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsBlank() {
            InboundBrandMappingSearchCriteria criteria =
                    InboundBrandMappingSearchCriteria.of(
                            null, null, null, "   ", defaultQueryContext());

            assertThat(criteria.hasSearchCondition()).isFalse();
        }
    }

    @Nested
    @DisplayName("statusNames() 테스트")
    class StatusNamesTest {

        @Test
        @DisplayName("상태 목록의 이름 문자열 리스트를 반환한다")
        void returnsStatusNameStrings() {
            InboundBrandMappingSearchCriteria criteria =
                    InboundBrandMappingSearchCriteria.of(
                            null,
                            List.of(
                                    InboundBrandMappingStatus.ACTIVE,
                                    InboundBrandMappingStatus.INACTIVE),
                            null,
                            null,
                            defaultQueryContext());

            assertThat(criteria.statusNames()).containsExactlyInAnyOrder("ACTIVE", "INACTIVE");
        }

        @Test
        @DisplayName("상태 목록이 null이면 빈 리스트를 반환한다")
        void returnsEmptyListWhenStatusesIsNull() {
            InboundBrandMappingSearchCriteria criteria =
                    InboundBrandMappingSearchCriteria.of(
                            null, null, null, null, defaultQueryContext());

            assertThat(criteria.statusNames()).isEmpty();
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("statuses 리스트는 외부 변경에 영향을 받지 않는다")
        void statusListIsImmutable() {
            List<InboundBrandMappingStatus> mutableStatuses =
                    new ArrayList<>(List.of(InboundBrandMappingStatus.ACTIVE));

            InboundBrandMappingSearchCriteria criteria =
                    InboundBrandMappingSearchCriteria.of(
                            null, mutableStatuses, null, null, defaultQueryContext());

            mutableStatuses.add(InboundBrandMappingStatus.INACTIVE);

            assertThat(criteria.statuses()).hasSize(1);
            assertThat(criteria.statuses()).containsOnly(InboundBrandMappingStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("InboundBrandMappingSearchField 테스트")
    class SearchFieldTest {

        @Test
        @DisplayName("EXTERNAL_CODE 필드명이 올바르다")
        void externalCodeFieldName() {
            assertThat(InboundBrandMappingSearchField.EXTERNAL_CODE.fieldName())
                    .isEqualTo("externalCode");
        }

        @Test
        @DisplayName("EXTERNAL_NAME 필드명이 올바르다")
        void externalNameFieldName() {
            assertThat(InboundBrandMappingSearchField.EXTERNAL_NAME.fieldName())
                    .isEqualTo("externalName");
        }

        @Test
        @DisplayName("fromString으로 필드를 변환한다")
        void parseFromString() {
            assertThat(InboundBrandMappingSearchField.fromString("externalCode"))
                    .isEqualTo(InboundBrandMappingSearchField.EXTERNAL_CODE);
        }

        @Test
        @DisplayName("null이면 null을 반환한다")
        void parseNullReturnsNull() {
            assertThat(InboundBrandMappingSearchField.fromString(null)).isNull();
        }
    }

    @Nested
    @DisplayName("InboundBrandMappingSortKey 테스트")
    class SortKeyTest {

        @Test
        @DisplayName("CREATED_AT 필드명이 올바르다")
        void createdAtFieldName() {
            assertThat(InboundBrandMappingSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("defaultKey()는 CREATED_AT을 반환한다")
        void defaultKeyIsCreatedAt() {
            assertThat(InboundBrandMappingSortKey.defaultKey())
                    .isEqualTo(InboundBrandMappingSortKey.CREATED_AT);
        }
    }
}
