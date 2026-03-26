package com.ryuqq.marketplace.adapter.out.persistence.refund.condition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.refund.query.RefundDateField;
import com.ryuqq.marketplace.domain.refund.query.RefundSearchCriteria;
import com.ryuqq.marketplace.domain.refund.query.RefundSearchField;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.time.LocalDate;
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
 * RefundConditionBuilderTest - 환불 클레임 조건 빌더 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RefundConditionBuilder 단위 테스트")
class RefundConditionBuilderTest {

    private RefundConditionBuilder conditionBuilder;

    @Mock private RefundSearchCriteria criteria;

    @BeforeEach
    void setUp() {
        conditionBuilder = new RefundConditionBuilder();
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
            String id = "01900000-0000-7000-0000-000000000101";

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
        @DisplayName("ID 목록이 있으면 BooleanExpression을 반환합니다")
        void idIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<String> ids =
                    List.of(
                            "01900000-0000-7000-0000-000000000101",
                            "01900000-0000-7000-0000-000000000102");

            // when
            BooleanExpression result = conditionBuilder.idIn(ids);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null ID 목록 입력 시 null을 반환합니다")
        void idIn_WithNullIds_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 ID 목록 입력 시 null을 반환합니다")
        void idIn_WithEmptyIds_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idIn(List.of());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. sellerIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerIdEq 메서드 테스트")
    class SellerIdEqTest {

        @Test
        @DisplayName("유효한 sellerId 입력 시 BooleanExpression을 반환합니다")
        void sellerIdEq_WithValidSellerId_ReturnsBooleanExpression() {
            // given
            Long sellerId = 10L;

            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(sellerId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null sellerId 입력 시 null을 반환합니다")
        void sellerIdEq_WithNullSellerId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 4. orderItemIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("orderItemIdEq 메서드 테스트")
    class OrderItemIdEqTest {

        @Test
        @DisplayName("유효한 orderItemId 입력 시 BooleanExpression을 반환합니다")
        void orderItemIdEq_WithValidOrderItemId_ReturnsBooleanExpression() {
            // given
            String orderItemId = "01900000-0000-7000-0000-000000000010";

            // when
            BooleanExpression result = conditionBuilder.orderItemIdEq(orderItemId);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 5. orderItemIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("orderItemIdIn 메서드 테스트")
    class OrderItemIdInTest {

        @Test
        @DisplayName("orderItemId 목록이 있으면 BooleanExpression을 반환합니다")
        void orderItemIdIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<String> ids =
                    List.of(
                            "01900000-0000-7000-0000-000000000010",
                            "01900000-0000-7000-0000-000000000011");

            // when
            BooleanExpression result = conditionBuilder.orderItemIdIn(ids);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null orderItemId 목록 입력 시 null을 반환합니다")
        void orderItemIdIn_WithNullIds_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.orderItemIdIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 orderItemId 목록 입력 시 null을 반환합니다")
        void orderItemIdIn_WithEmptyIds_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.orderItemIdIn(List.of());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 6. statusIn 테스트
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
                    .willReturn(List.of(RefundStatus.REQUESTED, RefundStatus.COLLECTING));

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
        @DisplayName("단일 상태 필터 시 BooleanExpression을 반환합니다")
        void statusIn_WithSingleStatus_ReturnsBooleanExpression() {
            // given
            given(criteria.hasStatusFilter()).willReturn(true);
            given(criteria.statuses()).willReturn(List.of(RefundStatus.COMPLETED));

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 7. holdFilter 테스트
    // ========================================================================

    @Nested
    @DisplayName("holdFilter 메서드 테스트")
    class HoldFilterTest {

        @Test
        @DisplayName("보류 상태 필터(isHold=true)가 있으면 BooleanExpression을 반환합니다")
        void holdFilter_WithHoldTrue_ReturnsBooleanExpression() {
            // given
            given(criteria.hasHoldFilter()).willReturn(true);
            given(criteria.isHold()).willReturn(true);

            // when
            BooleanExpression result = conditionBuilder.holdFilter(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("비보류 상태 필터(isHold=false)가 있으면 BooleanExpression을 반환합니다")
        void holdFilter_WithHoldFalse_ReturnsBooleanExpression() {
            // given
            given(criteria.hasHoldFilter()).willReturn(true);
            given(criteria.isHold()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.holdFilter(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("보류 필터가 없으면 null을 반환합니다")
        void holdFilter_WithoutHoldFilter_ReturnsNull() {
            // given
            given(criteria.hasHoldFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.holdFilter(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 8. searchCondition 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchCondition 메서드 테스트")
    class SearchConditionTest {

        @Test
        @DisplayName("검색 조건이 있으면 BooleanExpression을 반환합니다")
        void searchCondition_WithSearchCondition_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(false);
            given(criteria.searchWord()).willReturn("REF-20260319");

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

        @Test
        @DisplayName("CLAIM_NUMBER 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithClaimNumberField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField()).willReturn(RefundSearchField.CLAIM_NUMBER);
            given(criteria.searchWord()).willReturn("REF-001");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 9. dateRange 테스트
    // ========================================================================

    @Nested
    @DisplayName("dateRange 메서드 테스트")
    class DateRangeTest {

        @Test
        @DisplayName("dateRange가 없으면 null을 반환합니다")
        void dateRange_WithNoDateRange_ReturnsNull() {
            // given
            given(criteria.hasDateRange()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.dateRange(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("REQUESTED 날짜 필드와 시작일이 있으면 BooleanExpression을 반환합니다")
        void dateRange_WithRequestedFieldAndStart_ReturnsBooleanExpression() {
            // given
            DateRange dateRange = DateRange.from(LocalDate.now().minusDays(7));
            given(criteria.hasDateRange()).willReturn(true);
            given(criteria.dateRange()).willReturn(dateRange);
            given(criteria.dateField()).willReturn(RefundDateField.REQUESTED);

            // when
            BooleanExpression result = conditionBuilder.dateRange(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("COMPLETED 날짜 필드와 범위가 있으면 BooleanExpression을 반환합니다")
        void dateRange_WithCompletedFieldAndRange_ReturnsBooleanExpression() {
            // given
            DateRange dateRange = DateRange.of(LocalDate.now().minusDays(7), LocalDate.now());
            given(criteria.hasDateRange()).willReturn(true);
            given(criteria.dateRange()).willReturn(dateRange);
            given(criteria.dateField()).willReturn(RefundDateField.COMPLETED);

            // when
            BooleanExpression result = conditionBuilder.dateRange(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("dateField가 null이면 기본 REQUESTED 필드를 사용합니다")
        void dateRange_WithNullDateField_UsesDefaultRequestedField() {
            // given
            DateRange dateRange = DateRange.from(LocalDate.now().minusDays(1));
            given(criteria.hasDateRange()).willReturn(true);
            given(criteria.dateRange()).willReturn(dateRange);
            given(criteria.dateField()).willReturn(null);

            // when
            BooleanExpression result = conditionBuilder.dateRange(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }
}
