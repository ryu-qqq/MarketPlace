package com.ryuqq.marketplace.domain.brand.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Brand Exception 단위 테스트
 *
 * <p><strong>테스트 대상 (7개)</strong>:</p>
 * <ul>
 *   <li>BrandErrorCode (enum)</li>
 *   <li>BrandNotFoundException</li>
 *   <li>BrandBlockedException</li>
 *   <li>BrandCodeDuplicateException</li>
 *   <li>CanonicalNameDuplicateException</li>
 *   <li>BrandAliasNotFoundException</li>
 *   <li>BrandAliasDuplicateException</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("Brand Exception 단위 테스트")
@Tag("unit")
@Tag("domain")
@Tag("brand")
@Tag("exception")
class BrandExceptionTest {

    // ==================== BrandErrorCode 테스트 ====================

    @Nested
    @DisplayName("BrandErrorCode 테스트")
    class BrandErrorCodeTest {

        @Test
        @DisplayName("[성공] BRAND_NOT_FOUND 에러 코드 확인")
        void brandNotFound_ShouldHaveCorrectValues() {
            BrandErrorCode errorCode = BrandErrorCode.BRAND_NOT_FOUND;

            assertEquals("BRAND-001", errorCode.getCode());
            assertEquals(404, errorCode.getHttpStatus());
            assertEquals("브랜드를 찾을 수 없습니다", errorCode.getMessage());
        }

        @Test
        @DisplayName("[성공] BRAND_CODE_DUPLICATE 에러 코드 확인")
        void brandCodeDuplicate_ShouldHaveCorrectValues() {
            BrandErrorCode errorCode = BrandErrorCode.BRAND_CODE_DUPLICATE;

            assertEquals("BRAND-002", errorCode.getCode());
            assertEquals(409, errorCode.getHttpStatus());
            assertEquals("브랜드 코드가 중복됩니다", errorCode.getMessage());
        }

        @Test
        @DisplayName("[성공] CANONICAL_NAME_DUPLICATE 에러 코드 확인")
        void canonicalNameDuplicate_ShouldHaveCorrectValues() {
            BrandErrorCode errorCode = BrandErrorCode.CANONICAL_NAME_DUPLICATE;

            assertEquals("BRAND-003", errorCode.getCode());
            assertEquals(409, errorCode.getHttpStatus());
            assertEquals("표준 브랜드명이 중복됩니다", errorCode.getMessage());
        }

        @Test
        @DisplayName("[성공] BRAND_BLOCKED 에러 코드 확인")
        void brandBlocked_ShouldHaveCorrectValues() {
            BrandErrorCode errorCode = BrandErrorCode.BRAND_BLOCKED;

            assertEquals("BRAND-004", errorCode.getCode());
            assertEquals(403, errorCode.getHttpStatus());
            assertEquals("차단된 브랜드입니다", errorCode.getMessage());
        }

        @Test
        @DisplayName("[성공] BRAND_ALIAS_NOT_FOUND 에러 코드 확인")
        void brandAliasNotFound_ShouldHaveCorrectValues() {
            BrandErrorCode errorCode = BrandErrorCode.BRAND_ALIAS_NOT_FOUND;

            assertEquals("BRAND-005", errorCode.getCode());
            assertEquals(404, errorCode.getHttpStatus());
            assertEquals("브랜드 별칭을 찾을 수 없습니다", errorCode.getMessage());
        }

        @Test
        @DisplayName("[성공] BRAND_ALIAS_DUPLICATE 에러 코드 확인")
        void brandAliasDuplicate_ShouldHaveCorrectValues() {
            BrandErrorCode errorCode = BrandErrorCode.BRAND_ALIAS_DUPLICATE;

            assertEquals("BRAND-006", errorCode.getCode());
            assertEquals(409, errorCode.getHttpStatus());
            assertEquals("브랜드 별칭이 중복됩니다", errorCode.getMessage());
        }

        @Test
        @DisplayName("[성공] 모든 에러 코드 값 확인")
        void allErrorCodes_ShouldExist() {
            assertEquals(6, BrandErrorCode.values().length);
        }
    }

    // ==================== BrandNotFoundException 테스트 ====================

    @Nested
    @DisplayName("BrandNotFoundException 테스트")
    class BrandNotFoundExceptionTest {

        @Test
        @DisplayName("[성공] brandId로 예외 생성")
        void constructor_WithBrandId_ShouldCreate() {
            // Given
            Long brandId = 123L;

            // When
            BrandNotFoundException exception = new BrandNotFoundException(brandId);

            // Then
            assertNotNull(exception);
            assertEquals("BRAND-001", exception.code());
            assertTrue(exception.getMessage().contains("123"));
            assertEquals(brandId, exception.args().get("brandId"));
        }

        @Test
        @DisplayName("[성공] code로 예외 생성")
        void constructor_WithCode_ShouldCreate() {
            // Given
            String code = "NIKE";

            // When
            BrandNotFoundException exception = new BrandNotFoundException(code);

            // Then
            assertNotNull(exception);
            assertEquals("BRAND-001", exception.code());
            assertTrue(exception.getMessage().contains("NIKE"));
            assertEquals(code, exception.args().get("code"));
        }
    }

    // ==================== BrandBlockedException 테스트 ====================

    @Nested
    @DisplayName("BrandBlockedException 테스트")
    class BrandBlockedExceptionTest {

        @Test
        @DisplayName("[성공] brandId만으로 예외 생성")
        void constructor_WithBrandIdOnly_ShouldCreate() {
            // Given
            Long brandId = 123L;

            // When
            BrandBlockedException exception = new BrandBlockedException(brandId);

            // Then
            assertNotNull(exception);
            assertEquals("BRAND-004", exception.code());
            assertTrue(exception.getMessage().contains("123"));
            assertEquals(brandId, exception.args().get("brandId"));
        }

        @Test
        @DisplayName("[성공] brandId와 reason으로 예외 생성")
        void constructor_WithBrandIdAndReason_ShouldCreate() {
            // Given
            Long brandId = 123L;
            String reason = "위조품 판매";

            // When
            BrandBlockedException exception = new BrandBlockedException(brandId, reason);

            // Then
            assertNotNull(exception);
            assertEquals("BRAND-004", exception.code());
            assertTrue(exception.getMessage().contains("123"));
            assertTrue(exception.getMessage().contains("위조품 판매"));
            assertEquals(brandId, exception.args().get("brandId"));
            assertEquals(reason, exception.args().get("reason"));
        }
    }

    // ==================== BrandCodeDuplicateException 테스트 ====================

    @Nested
    @DisplayName("BrandCodeDuplicateException 테스트")
    class BrandCodeDuplicateExceptionTest {

        @Test
        @DisplayName("[성공] code로 예외 생성")
        void constructor_WithCode_ShouldCreate() {
            // Given
            String code = "NIKE";

            // When
            BrandCodeDuplicateException exception = new BrandCodeDuplicateException(code);

            // Then
            assertNotNull(exception);
            assertEquals("BRAND-002", exception.code());
            assertTrue(exception.getMessage().contains("NIKE"));
            assertEquals(code, exception.args().get("code"));
        }
    }

    // ==================== CanonicalNameDuplicateException 테스트 ====================

    @Nested
    @DisplayName("CanonicalNameDuplicateException 테스트")
    class CanonicalNameDuplicateExceptionTest {

        @Test
        @DisplayName("[성공] canonicalName으로 예외 생성")
        void constructor_WithCanonicalName_ShouldCreate() {
            // Given
            String canonicalName = "Nike";

            // When
            CanonicalNameDuplicateException exception = new CanonicalNameDuplicateException(canonicalName);

            // Then
            assertNotNull(exception);
            assertEquals("BRAND-003", exception.code());
            assertTrue(exception.getMessage().contains("Nike"));
            assertEquals(canonicalName, exception.args().get("canonicalName"));
        }
    }

    // ==================== BrandAliasNotFoundException 테스트 ====================

    @Nested
    @DisplayName("BrandAliasNotFoundException 테스트")
    class BrandAliasNotFoundExceptionTest {

        @Test
        @DisplayName("[성공] brandId와 aliasId로 예외 생성")
        void constructor_WithBrandIdAndAliasId_ShouldCreate() {
            // Given
            Long brandId = 1L;
            Long aliasId = 100L;

            // When
            BrandAliasNotFoundException exception = new BrandAliasNotFoundException(brandId, aliasId);

            // Then
            assertNotNull(exception);
            assertEquals("BRAND-005", exception.code());
            assertTrue(exception.getMessage().contains("brandId=1"));
            assertTrue(exception.getMessage().contains("aliasId=100"));
            assertEquals(brandId, exception.args().get("brandId"));
            assertEquals(aliasId, exception.args().get("aliasId"));
        }

        @Test
        @DisplayName("[성공] aliasId만으로 예외 생성")
        void constructor_WithAliasIdOnly_ShouldCreate() {
            // Given
            Long aliasId = 100L;

            // When
            BrandAliasNotFoundException exception = new BrandAliasNotFoundException(aliasId);

            // Then
            assertNotNull(exception);
            assertEquals("BRAND-005", exception.code());
            assertTrue(exception.getMessage().contains("100"));
            assertEquals(aliasId, exception.args().get("aliasId"));
        }
    }

    // ==================== BrandAliasDuplicateException 테스트 ====================

    @Nested
    @DisplayName("BrandAliasDuplicateException 테스트")
    class BrandAliasDuplicateExceptionTest {

        @Test
        @DisplayName("[성공] brandId, normalizedAlias, scope로 예외 생성")
        void constructor_WithScope_ShouldCreate() {
            // Given
            Long brandId = 1L;
            String normalizedAlias = "nikekorea";
            String scope = "COUPANG:123";

            // When
            BrandAliasDuplicateException exception = new BrandAliasDuplicateException(brandId, normalizedAlias, scope);

            // Then
            assertNotNull(exception);
            assertEquals("BRAND-006", exception.code());
            assertTrue(exception.getMessage().contains("brandId=1"));
            assertTrue(exception.getMessage().contains("normalizedAlias=nikekorea"));
            assertTrue(exception.getMessage().contains("scope=COUPANG:123"));
            assertEquals(brandId, exception.args().get("brandId"));
            assertEquals(normalizedAlias, exception.args().get("normalizedAlias"));
            assertEquals(scope, exception.args().get("scope"));
        }

        @Test
        @DisplayName("[성공] brandId와 normalizedAlias만으로 예외 생성")
        void constructor_WithoutScope_ShouldCreate() {
            // Given
            Long brandId = 1L;
            String normalizedAlias = "nikekorea";

            // When
            BrandAliasDuplicateException exception = new BrandAliasDuplicateException(brandId, normalizedAlias);

            // Then
            assertNotNull(exception);
            assertEquals("BRAND-006", exception.code());
            assertTrue(exception.getMessage().contains("brandId=1"));
            assertTrue(exception.getMessage().contains("normalizedAlias=nikekorea"));
            assertEquals(brandId, exception.args().get("brandId"));
            assertEquals(normalizedAlias, exception.args().get("normalizedAlias"));
        }
    }
}
