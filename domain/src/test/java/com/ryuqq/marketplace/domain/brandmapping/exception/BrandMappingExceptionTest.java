package com.ryuqq.marketplace.domain.brandmapping.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandMappingException 테스트")
class BrandMappingExceptionTest {

    @Nested
    @DisplayName("기본 생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("ErrorCode로 예외를 생성한다")
        void createWithErrorCode() {
            BrandMappingException exception =
                    new BrandMappingException(BrandMappingErrorCode.BRAND_MAPPING_NOT_FOUND);

            assertThat(exception.getMessage()).isEqualTo("브랜드 매핑을 찾을 수 없습니다");
            assertThat(exception.code()).isEqualTo("BRDMAP-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("ErrorCode와 커스텀 메시지로 예외를 생성한다")
        void createWithErrorCodeAndMessage() {
            BrandMappingException exception =
                    new BrandMappingException(
                            BrandMappingErrorCode.BRAND_MAPPING_NOT_FOUND, "ID 100 매핑 없음");

            assertThat(exception.getMessage()).isEqualTo("ID 100 매핑 없음");
            assertThat(exception.code()).isEqualTo("BRDMAP-001");
        }

        @Test
        @DisplayName("ErrorCode와 원인 예외로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            RuntimeException cause = new RuntimeException("원인 예외");
            BrandMappingException exception =
                    new BrandMappingException(BrandMappingErrorCode.BRAND_MAPPING_NOT_FOUND, cause);

            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("BRDMAP-001");
        }
    }

    @Nested
    @DisplayName("구체적 예외 클래스 테스트")
    class ConcreteExceptionTest {

        @Test
        @DisplayName("BrandMappingNotFoundException 기본 생성")
        void createBrandMappingNotFoundException() {
            BrandMappingNotFoundException exception = new BrandMappingNotFoundException();

            assertThat(exception.code()).isEqualTo("BRDMAP-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("브랜드 매핑을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("BrandMappingNotFoundException ID 포함 생성")
        void createBrandMappingNotFoundExceptionWithId() {
            BrandMappingNotFoundException exception = new BrandMappingNotFoundException(456L);

            assertThat(exception.code()).isEqualTo("BRDMAP-001");
            assertThat(exception.getMessage()).contains("456");
        }

        @Test
        @DisplayName("BrandMappingDuplicateException 기본 생성")
        void createBrandMappingDuplicateException() {
            BrandMappingDuplicateException exception = new BrandMappingDuplicateException();

            assertThat(exception.code()).isEqualTo("BRDMAP-002");
            assertThat(exception.httpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("BrandMappingDuplicateException ID 포함 생성")
        void createBrandMappingDuplicateExceptionWithId() {
            BrandMappingDuplicateException exception = new BrandMappingDuplicateException(999L);

            assertThat(exception.code()).isEqualTo("BRDMAP-002");
            assertThat(exception.getMessage()).contains("999");
        }
    }

    @Nested
    @DisplayName("상속 관계 테스트")
    class InheritanceTest {

        @Test
        @DisplayName("BrandMappingException은 DomainException을 상속한다")
        void brandMappingExceptionExtendsDomainException() {
            BrandMappingException exception =
                    new BrandMappingException(BrandMappingErrorCode.BRAND_MAPPING_NOT_FOUND);
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("BrandMappingNotFoundException은 BrandMappingException을 상속한다")
        void notFoundExceptionExtendsBrandMappingException() {
            BrandMappingNotFoundException exception = new BrandMappingNotFoundException();
            assertThat(exception).isInstanceOf(BrandMappingException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("BrandMappingDuplicateException은 BrandMappingException을 상속한다")
        void duplicateExceptionExtendsBrandMappingException() {
            BrandMappingDuplicateException exception = new BrandMappingDuplicateException();
            assertThat(exception).isInstanceOf(BrandMappingException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }
    }
}
