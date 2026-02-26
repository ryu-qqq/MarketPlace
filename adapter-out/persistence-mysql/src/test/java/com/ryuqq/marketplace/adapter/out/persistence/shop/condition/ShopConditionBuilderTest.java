package com.ryuqq.marketplace.adapter.out.persistence.shop.condition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchField;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
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
 * ShopConditionBuilderTest - Shop 조건 빌더 단위 테스트.
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
@DisplayName("ShopConditionBuilder 단위 테스트")
class ShopConditionBuilderTest {

    private ShopConditionBuilder conditionBuilder;

    @Mock private ShopSearchCriteria criteria;

    @BeforeEach
    void setUp() {
        conditionBuilder = new ShopConditionBuilder();
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
    // 2. idNe 테스트
    // ========================================================================

    @Nested
    @DisplayName("idNe 메서드 테스트")
    class IdNeTest {

        @Test
        @DisplayName("유효한 ID 입력 시 BooleanExpression을 반환합니다")
        void idNe_WithValidId_ReturnsBooleanExpression() {
            // given
            Long excludeId = 1L;

            // when
            BooleanExpression result = conditionBuilder.idNe(excludeId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null ID 입력 시 null을 반환합니다")
        void idNe_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idNe(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. shopNameEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("shopNameEq 메서드 테스트")
    class ShopNameEqTest {

        @Test
        @DisplayName("유효한 Shop명 입력 시 BooleanExpression을 반환합니다")
        void shopNameEq_WithValidName_ReturnsBooleanExpression() {
            // given
            String shopName = "테스트 외부몰";

            // when
            BooleanExpression result = conditionBuilder.shopNameEq(shopName);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null Shop명 입력 시 null을 반환합니다")
        void shopNameEq_WithNullName_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.shopNameEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 4. accountIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("accountIdEq 메서드 테스트")
    class AccountIdEqTest {

        @Test
        @DisplayName("유효한 AccountId 입력 시 BooleanExpression을 반환합니다")
        void accountIdEq_WithValidAccountId_ReturnsBooleanExpression() {
            // given
            String accountId = "test-account-123";

            // when
            BooleanExpression result = conditionBuilder.accountIdEq(accountId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null AccountId 입력 시 null을 반환합니다")
        void accountIdEq_WithNullAccountId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.accountIdEq(null);

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
            given(criteria.statuses()).willReturn(List.of(ShopStatus.ACTIVE));

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
    }

    // ========================================================================
    // 6. searchCondition 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchCondition 메서드 테스트")
    class SearchConditionTest {

        @Test
        @DisplayName("SHOP_NAME 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithShopNameField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField()).willReturn(ShopSearchField.SHOP_NAME);
            given(criteria.searchWord()).willReturn("테스트");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("ACCOUNT_ID 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithAccountIdField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField()).willReturn(ShopSearchField.ACCOUNT_ID);
            given(criteria.searchWord()).willReturn("test");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("필드 지정 없이 검색 시 통합 검색 BooleanExpression을 반환합니다")
        void searchCondition_WithoutSearchField_ReturnsUnifiedSearchExpression() {
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
        @DisplayName("검색 조건이 없으면 null을 반환합니다")
        void searchCondition_WithoutSearchCondition_ReturnsNull() {
            // given
            given(criteria.hasSearchCondition()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 7. notDeleted 테스트
    // ========================================================================

    @Nested
    @DisplayName("notDeleted 메서드 테스트")
    class NotDeletedTest {

        @Test
        @DisplayName("삭제되지 않은 조건 BooleanExpression을 반환합니다")
        void notDeleted_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.notDeleted();

            // then
            assertThat(result).isNotNull();
        }
    }
}
