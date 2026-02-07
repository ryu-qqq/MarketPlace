package com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.condition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSearchCriteria;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSearchField;
import com.ryuqq.marketplace.domain.sellerapplication.vo.ApplicationStatus;
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
 * SellerApplicationConditionBuilderTest - 입점 신청 조건 빌더 단위 테스트.
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
@DisplayName("SellerApplicationConditionBuilder 단위 테스트")
class SellerApplicationConditionBuilderTest {

    private SellerApplicationConditionBuilder sut;

    @Mock private SellerApplicationSearchCriteria criteria;

    @BeforeEach
    void setUp() {
        sut = new SellerApplicationConditionBuilder();
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
            BooleanExpression result = sut.idEq(id);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null ID 입력 시 null을 반환합니다")
        void idEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = sut.idEq(null);

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
            BooleanExpression result = sut.idIn(ids);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void idIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = sut.idIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
        void idIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = sut.idIn(Collections.emptyList());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. statusEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusEq 메서드 테스트")
    class StatusEqTest {

        @Test
        @DisplayName("유효한 상태 입력 시 BooleanExpression을 반환합니다")
        void statusEq_WithValidStatus_ReturnsBooleanExpression() {
            // given
            ApplicationStatus status = ApplicationStatus.PENDING;

            // when
            BooleanExpression result = sut.statusEq(status);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 상태 입력 시 null을 반환합니다")
        void statusEq_WithNullStatus_ReturnsNull() {
            // when
            BooleanExpression result = sut.statusEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 4. registrationNumberEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("registrationNumberEq 메서드 테스트")
    class RegistrationNumberEqTest {

        @Test
        @DisplayName("유효한 사업자등록번호 입력 시 BooleanExpression을 반환합니다")
        void registrationNumberEq_WithValidNumber_ReturnsBooleanExpression() {
            // given
            String registrationNumber = "123-45-67890";

            // when
            BooleanExpression result = sut.registrationNumberEq(registrationNumber);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 사업자등록번호 입력 시 null을 반환합니다")
        void registrationNumberEq_WithNullNumber_ReturnsNull() {
            // when
            BooleanExpression result = sut.registrationNumberEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 5. companyNameContains 테스트
    // ========================================================================

    @Nested
    @DisplayName("companyNameContains 메서드 테스트")
    class CompanyNameContainsTest {

        @Test
        @DisplayName("유효한 회사명 입력 시 BooleanExpression을 반환합니다")
        void companyNameContains_WithValidName_ReturnsBooleanExpression() {
            // given
            String companyName = "테스트";

            // when
            BooleanExpression result = sut.companyNameContains(companyName);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 회사명 입력 시 null을 반환합니다")
        void companyNameContains_WithNullName_ReturnsNull() {
            // when
            BooleanExpression result = sut.companyNameContains(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 회사명 입력 시 null을 반환합니다")
        void companyNameContains_WithBlankName_ReturnsNull() {
            // when
            BooleanExpression result = sut.companyNameContains("   ");

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 6. representativeContains 테스트
    // ========================================================================

    @Nested
    @DisplayName("representativeContains 메서드 테스트")
    class RepresentativeContainsTest {

        @Test
        @DisplayName("유효한 대표자명 입력 시 BooleanExpression을 반환합니다")
        void representativeContains_WithValidName_ReturnsBooleanExpression() {
            // given
            String representative = "홍길동";

            // when
            BooleanExpression result = sut.representativeContains(representative);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 대표자명 입력 시 null을 반환합니다")
        void representativeContains_WithNullName_ReturnsNull() {
            // when
            BooleanExpression result = sut.representativeContains(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 대표자명 입력 시 null을 반환합니다")
        void representativeContains_WithBlankName_ReturnsNull() {
            // when
            BooleanExpression result = sut.representativeContains("   ");

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
        @DisplayName("COMPANY_NAME 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchFieldContains_WithCompanyNameField_ReturnsBooleanExpression() {
            // when
            BooleanExpression result =
                    sut.searchFieldContains(SellerApplicationSearchField.COMPANY_NAME, "테스트");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("REPRESENTATIVE_NAME 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchFieldContains_WithRepresentativeNameField_ReturnsBooleanExpression() {
            // when
            BooleanExpression result =
                    sut.searchFieldContains(
                            SellerApplicationSearchField.REPRESENTATIVE_NAME, "홍길동");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 필드로 검색 시 통합 검색 BooleanExpression을 반환합니다")
        void searchFieldContains_WithNullField_ReturnsUnifiedSearchExpression() {
            // when
            BooleanExpression result = sut.searchFieldContains(null, "테스트");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 검색어 입력 시 null을 반환합니다")
        void searchFieldContains_WithNullSearchWord_ReturnsNull() {
            // when
            BooleanExpression result =
                    sut.searchFieldContains(SellerApplicationSearchField.COMPANY_NAME, null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 검색어 입력 시 null을 반환합니다")
        void searchFieldContains_WithBlankSearchWord_ReturnsNull() {
            // when
            BooleanExpression result =
                    sut.searchFieldContains(SellerApplicationSearchField.COMPANY_NAME, "   ");

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
            given(criteria.searchField()).willReturn(SellerApplicationSearchField.COMPANY_NAME);
            given(criteria.searchWord()).willReturn("테스트");

            // when
            BooleanExpression result = sut.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색 조건이 없으면 null을 반환합니다")
        void searchCondition_WithoutSearchCondition_ReturnsNull() {
            // given
            given(criteria.hasSearchCondition()).willReturn(false);

            // when
            BooleanExpression result = sut.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 9. statusCondition 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusCondition 메서드 테스트")
    class StatusConditionTest {

        @Test
        @DisplayName("상태 필터가 있으면 BooleanExpression을 반환합니다")
        void statusCondition_WithStatusFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasStatusFilter()).willReturn(true);
            given(criteria.status()).willReturn(List.of(ApplicationStatus.PENDING));

            // when
            BooleanExpression result = sut.statusCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("상태 필터가 없으면 null을 반환합니다")
        void statusCondition_WithoutStatusFilter_ReturnsNull() {
            // given
            given(criteria.hasStatusFilter()).willReturn(false);

            // when
            BooleanExpression result = sut.statusCondition(criteria);

            // then
            assertThat(result).isNull();
        }
    }
}
