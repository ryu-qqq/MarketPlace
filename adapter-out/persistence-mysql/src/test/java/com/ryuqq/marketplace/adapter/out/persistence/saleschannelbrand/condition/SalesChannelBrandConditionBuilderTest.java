package com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.condition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSearchCriteria;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSearchField;
import com.ryuqq.marketplace.domain.saleschannelbrand.vo.SalesChannelBrandStatus;
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
 * SalesChannelBrandConditionBuilderTest - SalesChannelBrand 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SalesChannelBrandConditionBuilder 단위 테스트")
class SalesChannelBrandConditionBuilderTest {

    private SalesChannelBrandConditionBuilder conditionBuilder;

    @Mock private SalesChannelBrandSearchCriteria criteria;

    @BeforeEach
    void setUp() {
        conditionBuilder = new SalesChannelBrandConditionBuilder();
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
    // 2. salesChannelIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("salesChannelIdEq 메서드 테스트")
    class SalesChannelIdEqTest {

        @Test
        @DisplayName("유효한 salesChannelId 입력 시 BooleanExpression을 반환합니다")
        void salesChannelIdEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long salesChannelId = 1L;

            // when
            BooleanExpression result = conditionBuilder.salesChannelIdEq(salesChannelId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null salesChannelId 입력 시 null을 반환합니다")
        void salesChannelIdEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.salesChannelIdEq(null);

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
        @DisplayName("유효한 salesChannelId 목록 입력 시 BooleanExpression을 반환합니다")
        void salesChannelIdsIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<Long> salesChannelIds = List.of(1L, 2L, 3L);

            // when
            BooleanExpression result = conditionBuilder.salesChannelIdsIn(salesChannelIds);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void salesChannelIdsIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.salesChannelIdsIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
        void salesChannelIdsIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.salesChannelIdsIn(Collections.emptyList());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 4. externalBrandCodeEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("externalBrandCodeEq 메서드 테스트")
    class ExternalBrandCodeEqTest {

        @Test
        @DisplayName("유효한 externalBrandCode 입력 시 BooleanExpression을 반환합니다")
        void externalBrandCodeEq_WithValidCode_ReturnsBooleanExpression() {
            // given
            String externalBrandCode = "BRAND-001";

            // when
            BooleanExpression result = conditionBuilder.externalBrandCodeEq(externalBrandCode);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null externalBrandCode 입력 시 null을 반환합니다")
        void externalBrandCodeEq_WithNullCode_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.externalBrandCodeEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 5. statusIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusIn 메서드 테스트")
    class StatusInTest {

        @Test
        @DisplayName("상태 필터가 있으면 BooleanExpression을 반환합니다")
        void statusIn_WithStatusFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasStatusFilter()).willReturn(true);
            given(criteria.statuses())
                    .willReturn(List.of(SalesChannelBrandStatus.ACTIVE, SalesChannelBrandStatus.INACTIVE));

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("상태 필터가 없으면 null을 반환합니다")
        void statusIn_WithoutStatusFilter_ReturnsNull() {
            // given
            given(criteria.hasStatusFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("ACTIVE 상태만 필터링할 때 BooleanExpression을 반환합니다")
        void statusIn_WithActiveStatusOnly_ReturnsBooleanExpression() {
            // given
            given(criteria.hasStatusFilter()).willReturn(true);
            given(criteria.statuses()).willReturn(List.of(SalesChannelBrandStatus.ACTIVE));

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("INACTIVE 상태만 필터링할 때 BooleanExpression을 반환합니다")
        void statusIn_WithInactiveStatusOnly_ReturnsBooleanExpression() {
            // given
            given(criteria.hasStatusFilter()).willReturn(true);
            given(criteria.statuses()).willReturn(List.of(SalesChannelBrandStatus.INACTIVE));

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 6. searchCondition 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchCondition 메서드 테스트")
    class SearchConditionTest {

        @Test
        @DisplayName("검색 조건이 없으면 null을 반환합니다")
        void searchCondition_WithoutSearchCondition_ReturnsNull() {
            // given
            given(criteria.hasSearchCondition()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("검색 필드 없이 검색어만 있으면 통합 검색 BooleanExpression을 반환합니다")
        void searchCondition_WithSearchWordOnly_ReturnsUnifiedSearchExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(false);
            given(criteria.searchWord()).willReturn("테스트");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("EXTERNAL_CODE 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithExternalCodeField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField()).willReturn(SalesChannelBrandSearchField.EXTERNAL_CODE);
            given(criteria.searchWord()).willReturn("BRAND");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("EXTERNAL_NAME 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithExternalNameField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField()).willReturn(SalesChannelBrandSearchField.EXTERNAL_NAME);
            given(criteria.searchWord()).willReturn("테스트");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색 필드가 없으면 코드와 이름 모두 검색합니다")
        void searchCondition_WithoutSearchField_SearchesBothCodeAndName() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(false);
            given(criteria.searchWord()).willReturn("BRAND");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }
}
