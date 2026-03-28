package com.ryuqq.marketplace.adapter.out.persistence.cancel.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.cancel.query.CancelDateField;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchField;
import com.ryuqq.marketplace.domain.cancel.query.CancelSortKey;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CancelConditionBuilder 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("CancelConditionBuilder 단위 테스트")
class CancelConditionBuilderTest {

    private CancelConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new CancelConditionBuilder();
    }

    // ========================================================================
    // 1. idEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("idEq 메서드 테스트")
    class IdEqTest {

        @Test
        @DisplayName("유효한 id 입력 시 BooleanExpression을 반환합니다")
        void idEq_WithValidId_ReturnsBooleanExpression() {
            // given
            String id = "01900000-0000-7000-0000-000000000001";

            // when
            BooleanExpression result = conditionBuilder.idEq(id);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null id 입력 시 null을 반환합니다")
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
        @DisplayName("비어 있지 않은 id 목록 입력 시 BooleanExpression을 반환합니다")
        void idIn_WithNonEmptyIds_ReturnsBooleanExpression() {
            // given
            List<String> ids = List.of("id-001", "id-002", "id-003");

            // when
            BooleanExpression result = conditionBuilder.idIn(ids);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void idIn_WithNullIds_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
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
            Long orderItemId = 1001L;

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
        @DisplayName("비어 있지 않은 orderItemId 목록 입력 시 BooleanExpression을 반환합니다")
        void orderItemIdIn_WithNonEmptyIds_ReturnsBooleanExpression() {
            // given
            List<Long> ids = List.of(1001L, 2001L);

            // when
            BooleanExpression result = conditionBuilder.orderItemIdIn(ids);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void orderItemIdIn_WithNullIds_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.orderItemIdIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
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
        @DisplayName("상태 필터가 있는 criteria 입력 시 BooleanExpression을 반환합니다")
        void statusIn_WithStatusFilter_ReturnsBooleanExpression() {
            // given
            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            List.of(CancelStatus.REQUESTED, CancelStatus.APPROVED),
                            List.of(),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("상태 필터가 없는 criteria 입력 시 null을 반환합니다")
        void statusIn_WithNoStatusFilter_ReturnsNull() {
            // given
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 7. typeIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("typeIn 메서드 테스트")
    class TypeInTest {

        @Test
        @DisplayName("타입 필터가 있는 criteria 입력 시 BooleanExpression을 반환합니다")
        void typeIn_WithTypeFilter_ReturnsBooleanExpression() {
            // given
            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            List.of(),
                            List.of(CancelType.BUYER_CANCEL),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            // when
            BooleanExpression result = conditionBuilder.typeIn(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("타입 필터가 없는 criteria 입력 시 null을 반환합니다")
        void typeIn_WithNoTypeFilter_ReturnsNull() {
            // given
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            // when
            BooleanExpression result = conditionBuilder.typeIn(criteria);

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
        @DisplayName("검색어가 있는 criteria 입력 시 BooleanExpression을 반환합니다")
        void searchCondition_WithSearchWord_ReturnsBooleanExpression() {
            // given
            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            List.of(),
                            List.of(),
                            null,
                            "CAN-20260319",
                            null,
                            null,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색어가 없는 criteria 입력 시 null을 반환합니다")
        void searchCondition_WithNoSearchWord_ReturnsNull() {
            // given
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("CANCEL_NUMBER 검색 필드로 조회 시 BooleanExpression을 반환합니다")
        void searchCondition_WithCancelNumberField_ReturnsBooleanExpression() {
            // given
            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            List.of(),
                            List.of(),
                            CancelSearchField.CANCEL_NUMBER,
                            "CAN-2026",
                            null,
                            null,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("빈 검색어는 조건이 없는 것으로 처리하여 null을 반환합니다")
        void searchCondition_WithBlankSearchWord_ReturnsNull() {
            // given
            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            List.of(),
                            List.of(),
                            null,
                            "   ",
                            null,
                            null,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 9. dateRange 테스트
    // ========================================================================

    @Nested
    @DisplayName("dateRange 메서드 테스트")
    class DateRangeTest {

        @Test
        @DisplayName("dateRange가 없는 criteria 입력 시 null을 반환합니다")
        void dateRange_WithNullDateRange_ReturnsNull() {
            // given
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            // when
            BooleanExpression result = conditionBuilder.dateRange(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("REQUESTED dateField로 dateRange 조건을 생성합니다")
        void dateRange_WithRequestedDateField_ReturnsBooleanExpression() {
            // given
            DateRange dateRange = DateRange.of(LocalDate.now().minusDays(7), LocalDate.now());
            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            List.of(),
                            List.of(),
                            null,
                            null,
                            dateRange,
                            CancelDateField.REQUESTED,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            // when
            BooleanExpression result = conditionBuilder.dateRange(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("COMPLETED dateField로 dateRange 조건을 생성합니다")
        void dateRange_WithCompletedDateField_ReturnsBooleanExpression() {
            // given
            DateRange dateRange = DateRange.of(LocalDate.now().minusDays(30), LocalDate.now());
            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            List.of(),
                            List.of(),
                            null,
                            null,
                            dateRange,
                            CancelDateField.COMPLETED,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            // when
            BooleanExpression result = conditionBuilder.dateRange(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("dateField가 null이면 기본값 REQUESTED로 처리하여 BooleanExpression을 반환합니다")
        void dateRange_WithNullDateField_UsesDefaultRequestedField() {
            // given
            DateRange dateRange = DateRange.of(LocalDate.now().minusDays(7), LocalDate.now());
            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            List.of(),
                            List.of(),
                            null,
                            null,
                            dateRange,
                            null,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            // when
            BooleanExpression result = conditionBuilder.dateRange(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }
}
