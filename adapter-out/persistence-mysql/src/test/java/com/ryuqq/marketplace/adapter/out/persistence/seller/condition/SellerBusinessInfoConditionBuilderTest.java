package com.ryuqq.marketplace.adapter.out.persistence.seller.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerBusinessInfoConditionBuilderTest - 셀러 사업자 정보 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerBusinessInfoConditionBuilder 단위 테스트")
class SellerBusinessInfoConditionBuilderTest {

    private SellerBusinessInfoConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new SellerBusinessInfoConditionBuilder();
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
    // 2. sellerIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerIdEq 메서드 테스트")
    class SellerIdEqTest {

        @Test
        @DisplayName("유효한 셀러 ID 입력 시 BooleanExpression을 반환합니다")
        void sellerIdEq_WithValidSellerId_ReturnsBooleanExpression() {
            // given
            Long sellerId = 1L;

            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(sellerId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 셀러 ID 입력 시 null을 반환합니다")
        void sellerIdEq_WithNullSellerId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. sellerIdNe 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerIdNe 메서드 테스트")
    class SellerIdNeTest {

        @Test
        @DisplayName("유효한 셀러 ID 입력 시 BooleanExpression을 반환합니다")
        void sellerIdNe_WithValidSellerId_ReturnsBooleanExpression() {
            // given
            Long excludeSellerId = 1L;

            // when
            BooleanExpression result = conditionBuilder.sellerIdNe(excludeSellerId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 셀러 ID 입력 시 null을 반환합니다")
        void sellerIdNe_WithNullSellerId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdNe(null);

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
            BooleanExpression result = conditionBuilder.registrationNumberEq(registrationNumber);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 사업자등록번호 입력 시 null을 반환합니다")
        void registrationNumberEq_WithNullNumber_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.registrationNumberEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 5. registrationNumberContains 테스트
    // ========================================================================

    @Nested
    @DisplayName("registrationNumberContains 메서드 테스트")
    class RegistrationNumberContainsTest {

        @Test
        @DisplayName("유효한 사업자등록번호 입력 시 BooleanExpression을 반환합니다")
        void registrationNumberContains_WithValidNumber_ReturnsBooleanExpression() {
            // given
            String registrationNumber = "123";

            // when
            BooleanExpression result =
                    conditionBuilder.registrationNumberContains(registrationNumber);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 사업자등록번호 입력 시 null을 반환합니다")
        void registrationNumberContains_WithNullNumber_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.registrationNumberContains(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 사업자등록번호 입력 시 null을 반환합니다")
        void registrationNumberContains_WithBlankNumber_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.registrationNumberContains("   ");

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 6. companyNameContains 테스트
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
            BooleanExpression result = conditionBuilder.companyNameContains(companyName);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 회사명 입력 시 null을 반환합니다")
        void companyNameContains_WithNullName_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.companyNameContains(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 회사명 입력 시 null을 반환합니다")
        void companyNameContains_WithBlankName_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.companyNameContains("   ");

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 7. representativeContains 테스트
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
            BooleanExpression result = conditionBuilder.representativeContains(representative);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 대표자명 입력 시 null을 반환합니다")
        void representativeContains_WithNullName_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.representativeContains(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 대표자명 입력 시 null을 반환합니다")
        void representativeContains_WithBlankName_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.representativeContains("   ");

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 8. searchFieldContains 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchFieldContains 메서드 테스트")
    class SearchFieldContainsTest {

        @Test
        @DisplayName("REGISTRATION_NUMBER 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchFieldContains_WithRegistrationNumber_ReturnsBooleanExpression() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(
                            SellerSearchField.REGISTRATION_NUMBER, "123");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("COMPANY_NAME 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchFieldContains_WithCompanyName_ReturnsBooleanExpression() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(SellerSearchField.COMPANY_NAME, "테스트");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("REPRESENTATIVE_NAME 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchFieldContains_WithRepresentativeName_ReturnsBooleanExpression() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(
                            SellerSearchField.REPRESENTATIVE_NAME, "홍길동");

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
                    conditionBuilder.searchFieldContains(SellerSearchField.COMPANY_NAME, null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 검색어 입력 시 null을 반환합니다")
        void searchFieldContains_WithBlankSearchWord_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(SellerSearchField.COMPANY_NAME, "   ");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("SELLER_NAME 필드로 검색 시 null을 반환합니다")
        void searchFieldContains_WithSellerNameField_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(SellerSearchField.SELLER_NAME, "테스트");

            // then
            assertThat(result).isNull();
        }
    }
}
