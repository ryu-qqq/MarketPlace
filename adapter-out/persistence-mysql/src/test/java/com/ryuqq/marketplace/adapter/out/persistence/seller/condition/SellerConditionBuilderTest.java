package com.ryuqq.marketplace.adapter.out.persistence.seller.condition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchCriteria;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchField;
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
 * SellerConditionBuilderTest - 셀러 조건 빌더 단위 테스트.
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
@DisplayName("SellerConditionBuilder 단위 테스트")
class SellerConditionBuilderTest {

    private SellerConditionBuilder conditionBuilder;

    @Mock private SellerSearchCriteria criteria;

    @BeforeEach
    void setUp() {
        conditionBuilder = new SellerConditionBuilder();
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
    // 2. idIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("idIn 메서드 테스트")
    class IdInTest {

        @Test
        @DisplayName("유효한 ID 목록 입력 시 BooleanExpression을 반환합니다")
        void idIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<Long> ids = List.of(1L, 2L, 3L);

            // when
            BooleanExpression result = conditionBuilder.idIn(ids);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void idIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
        void idIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idIn(Collections.emptyList());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. idNe 테스트
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
    // 4. sellerNameEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerNameEq 메서드 테스트")
    class SellerNameEqTest {

        @Test
        @DisplayName("유효한 셀러명 입력 시 BooleanExpression을 반환합니다")
        void sellerNameEq_WithValidName_ReturnsBooleanExpression() {
            // given
            String sellerName = "테스트 셀러";

            // when
            BooleanExpression result = conditionBuilder.sellerNameEq(sellerName);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 셀러명 입력 시 null을 반환합니다")
        void sellerNameEq_WithNullName_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerNameEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 5. sellerNameContains 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerNameContains 메서드 테스트")
    class SellerNameContainsTest {

        @Test
        @DisplayName("유효한 셀러명 입력 시 BooleanExpression을 반환합니다")
        void sellerNameContains_WithValidName_ReturnsBooleanExpression() {
            // given
            String sellerName = "테스트";

            // when
            BooleanExpression result = conditionBuilder.sellerNameContains(sellerName);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 셀러명 입력 시 null을 반환합니다")
        void sellerNameContains_WithNullName_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerNameContains(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 셀러명 입력 시 null을 반환합니다")
        void sellerNameContains_WithBlankName_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerNameContains("   ");

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 6. activeEq (Boolean) 테스트
    // ========================================================================

    @Nested
    @DisplayName("activeEq (Boolean) 메서드 테스트")
    class ActiveEqBooleanTest {

        @Test
        @DisplayName("true 입력 시 BooleanExpression을 반환합니다")
        void activeEq_WithTrue_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.activeEq(Boolean.TRUE);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("false 입력 시 BooleanExpression을 반환합니다")
        void activeEq_WithFalse_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.activeEq(Boolean.FALSE);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 입력 시 null을 반환합니다")
        void activeEq_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.activeEq((Boolean) null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 7. searchFieldContains 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchFieldContains 메서드 테스트")
    class SearchFieldContainsTest {

        @Test
        @DisplayName("SELLER_NAME 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchFieldContains_WithSellerNameField_ReturnsBooleanExpression() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(SellerSearchField.SELLER_NAME, "테스트");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 필드로 검색 시 통합 검색 BooleanExpression을 반환합니다")
        void searchFieldContains_WithNullField_ReturnsUnifiedSearchExpression() {
            // when
            BooleanExpression result = conditionBuilder.searchFieldContains(null, "테스트");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 검색어 입력 시 null을 반환합니다")
        void searchFieldContains_WithNullSearchWord_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(SellerSearchField.SELLER_NAME, null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 검색어 입력 시 null을 반환합니다")
        void searchFieldContains_WithBlankSearchWord_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(SellerSearchField.SELLER_NAME, "   ");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("REGISTRATION_NUMBER 필드로 검색 시 null을 반환합니다")
        void searchFieldContains_WithRegistrationNumberField_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(
                            SellerSearchField.REGISTRATION_NUMBER, "123-45-67890");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("COMPANY_NAME 필드로 검색 시 null을 반환합니다")
        void searchFieldContains_WithCompanyNameField_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(SellerSearchField.COMPANY_NAME, "테스트");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("REPRESENTATIVE_NAME 필드로 검색 시 null을 반환합니다")
        void searchFieldContains_WithRepresentativeNameField_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(
                            SellerSearchField.REPRESENTATIVE_NAME, "홍길동");

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 8. searchCondition (Criteria) 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchCondition (Criteria) 메서드 테스트")
    class SearchConditionCriteriaTest {

        @Test
        @DisplayName("검색 조건이 있으면 BooleanExpression을 반환합니다")
        void searchCondition_WithSearchCondition_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.searchField()).willReturn(SellerSearchField.SELLER_NAME);
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
    // 9. activeEq (Criteria) 테스트
    // ========================================================================

    @Nested
    @DisplayName("activeEq (Criteria) 메서드 테스트")
    class ActiveEqCriteriaTest {

        @Test
        @DisplayName("활성화 필터가 있으면 BooleanExpression을 반환합니다")
        void activeEq_WithActiveFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasActiveFilter()).willReturn(true);
            given(criteria.active()).willReturn(true);

            // when
            BooleanExpression result = conditionBuilder.activeEq(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("활성화 필터가 없으면 null을 반환합니다")
        void activeEq_WithoutActiveFilter_ReturnsNull() {
            // given
            given(criteria.hasActiveFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.activeEq(criteria);

            // then
            assertThat(result).isNull();
        }
    }
}
