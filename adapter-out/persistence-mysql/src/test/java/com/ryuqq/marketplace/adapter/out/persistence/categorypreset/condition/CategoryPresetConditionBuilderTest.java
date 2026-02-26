package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.condition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSearchCriteria;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CategoryPresetConditionBuilderTest - CategoryPreset 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryPresetConditionBuilder 단위 테스트")
class CategoryPresetConditionBuilderTest {

    private CategoryPresetConditionBuilder conditionBuilder;

    @Mock private CategoryPresetSearchCriteria criteria;

    @BeforeEach
    void setUp() {
        conditionBuilder = new CategoryPresetConditionBuilder();
    }

    // ========================================================================
    // 1. idEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("idEq 메서드 테스트")
    class IdEqTest {

        @Test
        @DisplayName("유효한 ID 입력 시 BooleanExpression을 반환합니다")
        void idEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long id = 1L;

            // when
            BooleanExpression result = conditionBuilder.idEq(id);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null ID 입력 시 null을 반환합니다")
        void idEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 2. idsIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("idsIn 메서드 테스트")
    class IdsInTest {

        @Test
        @DisplayName("유효한 ID 목록 입력 시 BooleanExpression을 반환합니다")
        void idsIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<Long> ids = List.of(1L, 2L, 3L);

            // when
            BooleanExpression result = conditionBuilder.idsIn(ids);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void idsIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idsIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
        void idsIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idsIn(Collections.emptyList());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. salesChannelIdsIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("salesChannelIdsIn 메서드 테스트")
    class SalesChannelIdsInTest {

        @Test
        @DisplayName("SalesChannel 필터가 있으면 BooleanExpression을 반환합니다")
        void salesChannelIdsIn_WithSalesChannelFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSalesChannelFilter()).willReturn(true);
            given(criteria.salesChannelIds()).willReturn(List.of(1L, 2L));

            // when
            BooleanExpression result = conditionBuilder.salesChannelIdsIn(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("SalesChannel 필터가 없으면 null을 반환합니다")
        void salesChannelIdsIn_WithoutSalesChannelFilter_ReturnsNull() {
            // given
            given(criteria.hasSalesChannelFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.salesChannelIdsIn(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 4. statusesIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusesIn 메서드 테스트")
    class StatusesInTest {

        @Test
        @DisplayName("Status 필터가 있으면 BooleanExpression을 반환합니다")
        void statusesIn_WithStatusFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasStatusFilter()).willReturn(true);
            given(criteria.statuses()).willReturn(List.of("ACTIVE", "INACTIVE"));

            // when
            BooleanExpression result = conditionBuilder.statusesIn(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Status 필터가 없으면 null을 반환합니다")
        void statusesIn_WithoutStatusFilter_ReturnsNull() {
            // given
            given(criteria.hasStatusFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.statusesIn(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 5. searchCondition 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchCondition 메서드 테스트")
    class SearchConditionTest {

        @Test
        @DisplayName("PRESET_NAME 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithPresetNameField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchFilter()).willReturn(true);
            given(criteria.searchField()).willReturn("PRESET_NAME");
            given(criteria.searchWord()).willReturn("테스트");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("SHOP_NAME 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithShopNameField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchFilter()).willReturn(true);
            given(criteria.searchField()).willReturn("SHOP_NAME");
            given(criteria.searchWord()).willReturn("테스트샵");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("ACCOUNT_ID 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithAccountIdField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchFilter()).willReturn(true);
            given(criteria.searchField()).willReturn("ACCOUNT_ID");
            given(criteria.searchWord()).willReturn("account123");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("CATEGORY_CODE 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithCategoryCodeField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchFilter()).willReturn(true);
            given(criteria.searchField()).willReturn("CATEGORY_CODE");
            given(criteria.searchWord()).willReturn("C123");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("CATEGORY_PATH 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithCategoryPathField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchFilter()).willReturn(true);
            given(criteria.searchField()).willReturn("CATEGORY_PATH");
            given(criteria.searchWord()).willReturn("전자제품");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색 필터가 없으면 null을 반환합니다")
        void searchCondition_WithoutSearchFilter_ReturnsNull() {
            // given
            given(criteria.hasSearchFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("알 수 없는 필드로 검색 시 null을 반환합니다")
        void searchCondition_WithUnknownField_ReturnsNull() {
            // given
            given(criteria.hasSearchFilter()).willReturn(true);
            given(criteria.searchField()).willReturn("UNKNOWN_FIELD");
            given(criteria.searchWord()).willReturn("테스트");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 6. createdAtGoe 테스트
    // ========================================================================

    @Nested
    @DisplayName("createdAtGoe 메서드 테스트")
    class CreatedAtGoeTest {

        @Test
        @DisplayName("시작일 필터가 있으면 BooleanExpression을 반환합니다")
        void createdAtGoe_WithStartDateFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasStartDateFilter()).willReturn(true);
            given(criteria.startDate()).willReturn(LocalDate.of(2024, 1, 1));

            // when
            BooleanExpression result = conditionBuilder.createdAtGoe(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("시작일 필터가 없으면 null을 반환합니다")
        void createdAtGoe_WithoutStartDateFilter_ReturnsNull() {
            // given
            given(criteria.hasStartDateFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.createdAtGoe(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 7. createdAtLoe 테스트
    // ========================================================================

    @Nested
    @DisplayName("createdAtLoe 메서드 테스트")
    class CreatedAtLoeTest {

        @Test
        @DisplayName("종료일 필터가 있으면 BooleanExpression을 반환합니다")
        void createdAtLoe_WithEndDateFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasEndDateFilter()).willReturn(true);
            given(criteria.endDate()).willReturn(LocalDate.of(2024, 12, 31));

            // when
            BooleanExpression result = conditionBuilder.createdAtLoe(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("종료일 필터가 없으면 null을 반환합니다")
        void createdAtLoe_WithoutEndDateFilter_ReturnsNull() {
            // given
            given(criteria.hasEndDateFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.createdAtLoe(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 8. statusActive 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusActive 메서드 테스트")
    class StatusActiveTest {

        @Test
        @DisplayName("활성 상태 조건 BooleanExpression을 반환합니다")
        void statusActive_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.statusActive();

            // then
            assertThat(result).isNotNull();
        }
    }
}
