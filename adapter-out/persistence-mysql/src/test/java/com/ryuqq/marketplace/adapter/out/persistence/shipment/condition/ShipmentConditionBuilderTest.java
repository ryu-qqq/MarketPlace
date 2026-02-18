package com.ryuqq.marketplace.adapter.out.persistence.shipment.condition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentDateField;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchCriteria;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchField;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
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
 * ShipmentConditionBuilderTest - 배송 조건 빌더 단위 테스트.
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
@DisplayName("ShipmentConditionBuilder 단위 테스트")
class ShipmentConditionBuilderTest {

    private ShipmentConditionBuilder conditionBuilder;

    @Mock private ShipmentSearchCriteria criteria;

    @BeforeEach
    void setUp() {
        conditionBuilder = new ShipmentConditionBuilder();
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
            String id = "01944b2a-1234-7fff-8888-abcdef012345";

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
    // 2. orderIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("orderIdEq 메서드 테스트")
    class OrderIdEqTest {

        @Test
        @DisplayName("유효한 orderId 입력 시 BooleanExpression을 반환합니다")
        void orderIdEq_WithValidOrderId_ReturnsBooleanExpression() {
            // given
            String orderId = "ORD-20260218-9999";

            // when
            BooleanExpression result = conditionBuilder.orderIdEq(orderId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null orderId 입력 시 null을 반환합니다")
        void orderIdEq_WithNullOrderId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.orderIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. statusIn 테스트
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
                    .willReturn(List.of(ShipmentStatus.READY, ShipmentStatus.SHIPPED));

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
            given(criteria.statuses()).willReturn(List.of(ShipmentStatus.DELIVERED));

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 4. searchCondition 테스트
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
            given(criteria.searchWord()).willReturn("1234567890");

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
        @DisplayName("ORDER_ID 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithOrderIdField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField()).willReturn(ShipmentSearchField.ORDER_ID);
            given(criteria.searchWord()).willReturn("ORD-2026");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("TRACKING_NUMBER 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithTrackingNumberField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField()).willReturn(ShipmentSearchField.TRACKING_NUMBER);
            given(criteria.searchWord()).willReturn("12345");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("CUSTOMER_NAME 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithCustomerNameField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField()).willReturn(ShipmentSearchField.CUSTOMER_NAME);
            given(criteria.searchWord()).willReturn("홍길동");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 5. dateRange 테스트
    // ========================================================================

    @Nested
    @DisplayName("dateRange 메서드 테스트")
    class DateRangeTest {

        @Test
        @DisplayName("dateRange가 null이면 null을 반환합니다")
        void dateRange_WithNullDateRange_ReturnsNull() {
            // given
            given(criteria.dateRange()).willReturn(null);

            // when
            BooleanExpression result = conditionBuilder.dateRange(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("dateRange가 비어있으면 null을 반환합니다")
        void dateRange_WithEmptyDateRange_ReturnsNull() {
            // given
            DateRange emptyRange = DateRange.of(null, null);
            given(criteria.dateRange()).willReturn(emptyRange);

            // when
            BooleanExpression result = conditionBuilder.dateRange(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("SHIPPED 날짜 필드와 시작일이 있으면 BooleanExpression을 반환합니다")
        void dateRange_WithShippedFieldAndStart_ReturnsBooleanExpression() {
            // given
            DateRange dateRange = DateRange.from(LocalDate.now().minusDays(1));
            given(criteria.dateRange()).willReturn(dateRange);
            given(criteria.dateField()).willReturn(ShipmentDateField.SHIPPED);

            // when
            BooleanExpression result = conditionBuilder.dateRange(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("PAYMENT 날짜 필드와 범위가 있으면 BooleanExpression을 반환합니다")
        void dateRange_WithPaymentFieldAndRange_ReturnsBooleanExpression() {
            // given
            DateRange dateRange = DateRange.of(LocalDate.now().minusDays(7), LocalDate.now());
            given(criteria.dateRange()).willReturn(dateRange);
            given(criteria.dateField()).willReturn(ShipmentDateField.PAYMENT);

            // when
            BooleanExpression result = conditionBuilder.dateRange(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("ORDER_CONFIRMED 날짜 필드와 범위가 있으면 BooleanExpression을 반환합니다")
        void dateRange_WithOrderConfirmedFieldAndRange_ReturnsBooleanExpression() {
            // given
            DateRange dateRange = DateRange.of(LocalDate.now().minusDays(7), LocalDate.now());
            given(criteria.dateRange()).willReturn(dateRange);
            given(criteria.dateField()).willReturn(ShipmentDateField.ORDER_CONFIRMED);

            // when
            BooleanExpression result = conditionBuilder.dateRange(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("dateField가 null이면 기본 SHIPPED 필드를 사용합니다")
        void dateRange_WithNullDateField_UsesDefaultShippedField() {
            // given
            DateRange dateRange = DateRange.from(LocalDate.now().minusDays(1));
            given(criteria.dateRange()).willReturn(dateRange);
            given(criteria.dateField()).willReturn(null);

            // when
            BooleanExpression result = conditionBuilder.dateRange(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 6. notDeleted 테스트
    // ========================================================================

    @Nested
    @DisplayName("notDeleted 메서드 테스트")
    class NotDeletedTest {

        @Test
        @DisplayName("notDeleted는 항상 BooleanExpression을 반환합니다")
        void notDeleted_AlwaysReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.notDeleted();

            // then
            assertThat(result).isNotNull();
        }
    }
}
