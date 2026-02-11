package com.ryuqq.marketplace.domain.brand.exception;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Brand Exception 단위 테스트")
class BrandExceptionTest {

    @Nested
    @DisplayName("BrandException 테스트")
    class BrandExceptionBaseTest {
        @Test
        @DisplayName("에러 코드로 예외를 생성한다")
        void createWithErrorCode() {
            // given
            BrandErrorCode errorCode = BrandErrorCode.BRAND_NOT_FOUND;

            // when
            BrandException exception = new BrandException(errorCode);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(errorCode);
            assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
        }

        @Test
        @DisplayName("에러 코드와 커스텀 메시지로 예외를 생성한다")
        void createWithErrorCodeAndCustomMessage() {
            // given
            BrandErrorCode errorCode = BrandErrorCode.BRAND_NOT_FOUND;
            String customMessage = "ID가 100인 브랜드를 찾을 수 없습니다";

            // when
            BrandException exception = new BrandException(errorCode, customMessage);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(errorCode);
            assertThat(exception.getMessage()).isEqualTo(customMessage);
        }

        @Test
        @DisplayName("에러 코드와 원인으로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            // given
            BrandErrorCode errorCode = BrandErrorCode.BRAND_NOT_FOUND;
            Exception cause = new RuntimeException("Database connection failed");

            // when
            BrandException exception = new BrandException(errorCode, cause);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(errorCode);
            assertThat(exception.getCause()).isEqualTo(cause);
        }
    }

    @Nested
    @DisplayName("BrandNotFoundException 테스트")
    class BrandNotFoundExceptionTest {
        @Test
        @DisplayName("기본 생성자로 예외를 생성한다")
        void createWithDefaultConstructor() {
            // when
            BrandNotFoundException exception = new BrandNotFoundException();

            // then
            assertThat(exception).isInstanceOf(BrandException.class);
            assertThat(exception.getErrorCode()).isEqualTo(BrandErrorCode.BRAND_NOT_FOUND);
            assertThat(exception.getMessage()).isEqualTo("브랜드를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("브랜드 ID로 예외를 생성한다")
        void createWithBrandId() {
            // given
            Long brandId = 100L;

            // when
            BrandNotFoundException exception = new BrandNotFoundException(brandId);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(BrandErrorCode.BRAND_NOT_FOUND);
            assertThat(exception.getMessage()).isEqualTo("ID가 100인 브랜드를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("브랜드 코드로 예외를 생성한다")
        void createWithBrandCode() {
            // given
            String brandCode = "NIKE";

            // when
            BrandNotFoundException exception = new BrandNotFoundException(brandCode);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(BrandErrorCode.BRAND_NOT_FOUND);
            assertThat(exception.getMessage()).isEqualTo("코드가 NIKE인 브랜드를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("BrandCodeDuplicateException 테스트")
    class BrandCodeDuplicateExceptionTest {
        @Test
        @DisplayName("기본 생성자로 예외를 생성한다")
        void createWithDefaultConstructor() {
            // when
            BrandCodeDuplicateException exception = new BrandCodeDuplicateException();

            // then
            assertThat(exception).isInstanceOf(BrandException.class);
            assertThat(exception.getErrorCode()).isEqualTo(BrandErrorCode.BRAND_CODE_DUPLICATE);
            assertThat(exception.getMessage()).isEqualTo("이미 존재하는 브랜드 코드입니다");
        }

        @Test
        @DisplayName("브랜드 코드로 예외를 생성한다")
        void createWithBrandCode() {
            // given
            String brandCode = "NIKE";

            // when
            BrandCodeDuplicateException exception = new BrandCodeDuplicateException(brandCode);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(BrandErrorCode.BRAND_CODE_DUPLICATE);
            assertThat(exception.getMessage()).isEqualTo("브랜드 코드 'NIKE'가 이미 존재합니다");
        }
    }

    @Nested
    @DisplayName("예외 상속 구조 테스트")
    class ExceptionHierarchyTest {
        @Test
        @DisplayName("BrandException은 DomainException을 상속한다")
        void brandExceptionExtendsDomainException() {
            // given
            BrandException exception = new BrandException(BrandErrorCode.BRAND_NOT_FOUND);

            // when & then
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("BrandNotFoundException은 BrandException을 상속한다")
        void brandNotFoundExceptionExtendsBrandException() {
            // given
            BrandNotFoundException exception = new BrandNotFoundException();

            // when & then
            assertThat(exception).isInstanceOf(BrandException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("BrandCodeDuplicateException은 BrandException을 상속한다")
        void brandCodeDuplicateExceptionExtendsBrandException() {
            // given
            BrandCodeDuplicateException exception = new BrandCodeDuplicateException();

            // when & then
            assertThat(exception).isInstanceOf(BrandException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("예외 throw 테스트")
    class ThrowExceptionTest {
        @Test
        @DisplayName("BrandNotFoundException을 throw할 수 있다")
        void canThrowBrandNotFoundException() {
            // when & then
            assertThatThrownBy(
                            () -> {
                                throw new BrandNotFoundException(100L);
                            })
                    .isInstanceOf(BrandNotFoundException.class)
                    .hasMessageContaining("ID가 100인 브랜드를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("BrandCodeDuplicateException을 throw할 수 있다")
        void canThrowBrandCodeDuplicateException() {
            // when & then
            assertThatThrownBy(
                            () -> {
                                throw new BrandCodeDuplicateException("NIKE");
                            })
                    .isInstanceOf(BrandCodeDuplicateException.class)
                    .hasMessageContaining("브랜드 코드 'NIKE'가 이미 존재합니다");
        }
    }
}
