package com.ryuqq.marketplace.domain.inboundcategorymapping.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingStatus;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundCategoryMappingSearchCriteria 단위 테스트")
class InboundCategoryMappingSearchCriteriaTest {

    private QueryContext<InboundCategoryMappingSortKey> defaultQueryContext() {
        return QueryContext.defaultOf(InboundCategoryMappingSortKey.CREATED_AT);
    }

    @Nested
    @DisplayName("of() 생성 테스트")
    class OfTest {

        @Test
        @DisplayName("전체 파라미터로 검색 조건을 생성한다")
        void createWithAllParams() {
            InboundCategoryMappingSearchCriteria criteria =
                    InboundCategoryMappingSearchCriteria.of(
                            1L,
                            List.of(InboundCategoryMappingStatus.ACTIVE),
                            InboundCategoryMappingSearchField.EXTERNAL_CODE,
                            "CAT001",
                            defaultQueryContext());

            assertThat(criteria.inboundSourceId()).isEqualTo(1L);
            assertThat(criteria.statuses()).containsExactly(InboundCategoryMappingStatus.ACTIVE);
            assertThat(criteria.searchField())
                    .isEqualTo(InboundCategoryMappingSearchField.EXTERNAL_CODE);
            assertThat(criteria.searchWord()).isEqualTo("CAT001");
        }
    }

    @Nested
    @DisplayName("hasInboundSourceIdFilter() 테스트")
    class HasInboundSourceIdFilterTest {

        @Test
        @DisplayName("sourceId 필터가 있으면 true를 반환한다")
        void returnsTrueWhenSourceIdExists() {
            InboundCategoryMappingSearchCriteria criteria =
                    InboundCategoryMappingSearchCriteria.of(
                            1L, null, null, null, defaultQueryContext());

            assertThat(criteria.hasInboundSourceIdFilter()).isTrue();
        }

        @Test
        @DisplayName("sourceId 필터가 없으면 false를 반환한다")
        void returnsFalseWhenSourceIdIsNull() {
            InboundCategoryMappingSearchCriteria criteria =
                    InboundCategoryMappingSearchCriteria.of(
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
            InboundCategoryMappingSearchCriteria criteria =
                    InboundCategoryMappingSearchCriteria.of(
                            null,
                            List.of(InboundCategoryMappingStatus.ACTIVE),
                            null,
                            null,
                            defaultQueryContext());

            assertThat(criteria.hasStatusesFilter()).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 null이면 false를 반환한다")
        void returnsFalseWhenStatusesIsNull() {
            InboundCategoryMappingSearchCriteria criteria =
                    InboundCategoryMappingSearchCriteria.of(
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
            InboundCategoryMappingSearchCriteria criteria =
                    InboundCategoryMappingSearchCriteria.of(
                            null, null, null, "CAT001", defaultQueryContext());

            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("검색어가 공백이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsBlank() {
            InboundCategoryMappingSearchCriteria criteria =
                    InboundCategoryMappingSearchCriteria.of(
                            null, null, null, "  ", defaultQueryContext());

            assertThat(criteria.hasSearchCondition()).isFalse();
        }
    }

    @Nested
    @DisplayName("statusNames() 테스트")
    class StatusNamesTest {

        @Test
        @DisplayName("상태 목록의 이름 문자열 리스트를 반환한다")
        void returnsStatusNameStrings() {
            InboundCategoryMappingSearchCriteria criteria =
                    InboundCategoryMappingSearchCriteria.of(
                            null,
                            List.of(
                                    InboundCategoryMappingStatus.ACTIVE,
                                    InboundCategoryMappingStatus.INACTIVE),
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
        @DisplayName("statuses 리스트는 외부 변경에 영향을 받지 않는다")
        void statusListIsImmutable() {
            List<InboundCategoryMappingStatus> mutableStatuses =
                    new ArrayList<>(List.of(InboundCategoryMappingStatus.ACTIVE));

            InboundCategoryMappingSearchCriteria criteria =
                    InboundCategoryMappingSearchCriteria.of(
                            null, mutableStatuses, null, null, defaultQueryContext());

            mutableStatuses.add(InboundCategoryMappingStatus.INACTIVE);

            assertThat(criteria.statuses()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("InboundCategoryMappingSearchField 테스트")
    class SearchFieldTest {

        @Test
        @DisplayName("EXTERNAL_CODE 필드명이 올바르다")
        void externalCodeFieldName() {
            assertThat(InboundCategoryMappingSearchField.EXTERNAL_CODE.fieldName())
                    .isEqualTo("externalCode");
        }

        @Test
        @DisplayName("fromString으로 필드를 변환한다")
        void parseFromString() {
            assertThat(InboundCategoryMappingSearchField.fromString("externalName"))
                    .isEqualTo(InboundCategoryMappingSearchField.EXTERNAL_NAME);
        }

        @Test
        @DisplayName("null이면 null을 반환한다")
        void parseNullReturnsNull() {
            assertThat(InboundCategoryMappingSearchField.fromString(null)).isNull();
        }
    }

    @Nested
    @DisplayName("InboundCategoryMappingSortKey 테스트")
    class SortKeyTest {

        @Test
        @DisplayName("CREATED_AT 필드명이 올바르다")
        void createdAtFieldName() {
            assertThat(InboundCategoryMappingSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("defaultKey()는 CREATED_AT을 반환한다")
        void defaultKeyIsCreatedAt() {
            assertThat(InboundCategoryMappingSortKey.defaultKey())
                    .isEqualTo(InboundCategoryMappingSortKey.CREATED_AT);
        }
    }
}
