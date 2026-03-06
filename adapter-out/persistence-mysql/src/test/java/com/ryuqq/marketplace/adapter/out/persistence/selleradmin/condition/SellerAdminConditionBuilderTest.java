package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.condition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSearchCriteria;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSearchField;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
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
 * SellerAdminConditionBuilderTest - 셀러 관리자 조건 빌더 단위 테스트.
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
@DisplayName("SellerAdminConditionBuilder 단위 테스트")
class SellerAdminConditionBuilderTest {

    private SellerAdminConditionBuilder conditionBuilder;

    @Mock private SellerAdminSearchCriteria criteria;

    @BeforeEach
    void setUp() {
        conditionBuilder = new SellerAdminConditionBuilder();
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
            String id = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";

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
    // 2. sellerIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerIdEq 메서드 테스트")
    class SellerIdEqTest {

        @Test
        @DisplayName("유효한 셀러 ID 입력 시 BooleanExpression을 반환합니다")
        void sellerIdEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long sellerId = 1L;

            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(sellerId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 셀러 ID 입력 시 null을 반환합니다")
        void sellerIdEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. sellerIdsIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerIdsIn 메서드 테스트")
    class SellerIdsInTest {

        @Test
        @DisplayName("유효한 셀러 ID 목록 입력 시 BooleanExpression을 반환합니다")
        void sellerIdsIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<Long> sellerIds = List.of(1L, 2L, 3L);

            // when
            BooleanExpression result = conditionBuilder.sellerIdsIn(sellerIds);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void sellerIdsIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdsIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
        void sellerIdsIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdsIn(Collections.emptyList());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 4. loginIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("loginIdEq 메서드 테스트")
    class LoginIdEqTest {

        @Test
        @DisplayName("유효한 로그인 ID 입력 시 BooleanExpression을 반환합니다")
        void loginIdEq_WithValidLoginId_ReturnsBooleanExpression() {
            // given
            String loginId = "admin@test.com";

            // when
            BooleanExpression result = conditionBuilder.loginIdEq(loginId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 로그인 ID 입력 시 null을 반환합니다")
        void loginIdEq_WithNullLoginId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.loginIdEq(null);

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
        @DisplayName("유효한 상태 목록 입력 시 BooleanExpression을 반환합니다")
        void statusIn_WithValidStatuses_ReturnsBooleanExpression() {
            // given
            List<SellerAdminStatus> statuses =
                    List.of(SellerAdminStatus.ACTIVE, SellerAdminStatus.PENDING_APPROVAL);

            // when
            BooleanExpression result = conditionBuilder.statusIn(statuses);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void statusIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.statusIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
        void statusIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.statusIn(Collections.emptyList());

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
        @DisplayName("LOGIN_ID 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithLoginIdField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.searchField()).willReturn(SellerAdminSearchField.LOGIN_ID);
            given(criteria.searchWord()).willReturn("admin");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("NAME 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithNameField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.searchField()).willReturn(SellerAdminSearchField.NAME);
            given(criteria.searchWord()).willReturn("홍길동");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 필드로 검색 시 통합 검색 BooleanExpression을 반환합니다")
        void searchCondition_WithNullField_ReturnsUnifiedSearchExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.searchField()).willReturn(null);
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
    // 7. dateRangeCondition 테스트
    // ========================================================================

    @Nested
    @DisplayName("dateRangeCondition 메서드 테스트")
    class DateRangeConditionTest {

        @Test
        @DisplayName("시작일과 종료일이 있으면 BooleanExpression을 반환합니다")
        void dateRangeCondition_WithStartAndEndDate_ReturnsBooleanExpression() {
            // given
            DateRange dateRange = DateRange.of(LocalDate.now().minusDays(7), LocalDate.now());
            given(criteria.hasDateRange()).willReturn(true);
            given(criteria.dateRange()).willReturn(dateRange);

            // when
            BooleanExpression result = conditionBuilder.dateRangeCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("시작일만 있으면 BooleanExpression을 반환합니다")
        void dateRangeCondition_WithStartDateOnly_ReturnsBooleanExpression() {
            // given
            DateRange dateRange = DateRange.of(LocalDate.now().minusDays(7), null);
            given(criteria.hasDateRange()).willReturn(true);
            given(criteria.dateRange()).willReturn(dateRange);

            // when
            BooleanExpression result = conditionBuilder.dateRangeCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("종료일만 있으면 BooleanExpression을 반환합니다")
        void dateRangeCondition_WithEndDateOnly_ReturnsBooleanExpression() {
            // given
            DateRange dateRange = DateRange.of(null, LocalDate.now());
            given(criteria.hasDateRange()).willReturn(true);
            given(criteria.dateRange()).willReturn(dateRange);

            // when
            BooleanExpression result = conditionBuilder.dateRangeCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("날짜 범위가 없으면 null을 반환합니다")
        void dateRangeCondition_WithoutDateRange_ReturnsNull() {
            // given
            given(criteria.hasDateRange()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.dateRangeCondition(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 8. nameEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("nameEq 메서드 테스트")
    class NameEqTest {

        @Test
        @DisplayName("유효한 이름 입력 시 BooleanExpression을 반환합니다")
        void nameEq_WithValidName_ReturnsBooleanExpression() {
            // given
            String name = "홍길동";

            // when
            BooleanExpression result = conditionBuilder.nameEq(name);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 이름 입력 시 null을 반환합니다")
        void nameEq_WithNullName_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.nameEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 9. phoneNumberEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("phoneNumberEq 메서드 테스트")
    class PhoneNumberEqTest {

        @Test
        @DisplayName("유효한 핸드폰 번호 입력 시 BooleanExpression을 반환합니다")
        void phoneNumberEq_WithValidPhoneNumber_ReturnsBooleanExpression() {
            // given
            String phoneNumber = "010-1234-5678";

            // when
            BooleanExpression result = conditionBuilder.phoneNumberEq(phoneNumber);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 핸드폰 번호 입력 시 null을 반환합니다")
        void phoneNumberEq_WithNullPhoneNumber_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.phoneNumberEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 11. notDeleted 테스트
    // ========================================================================

    @Nested
    @DisplayName("notDeleted 메서드 테스트")
    class NotDeletedTest {

        @Test
        @DisplayName("notDeleted 조건은 항상 BooleanExpression을 반환합니다")
        void notDeleted_ReturnsNotNullExpression() {
            // when
            BooleanExpression result = conditionBuilder.notDeleted();

            // then
            assertThat(result).isNotNull();
        }
    }
}
