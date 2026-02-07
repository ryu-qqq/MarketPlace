package com.ryuqq.marketplace.domain.commoncode.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeException 테스트")
class CommonCodeExceptionTest {

    @Nested
    @DisplayName("기본 생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("ErrorCode로 예외를 생성한다")
        void createWithErrorCode() {
            // when
            CommonCodeException exception =
                    new CommonCodeException(CommonCodeErrorCode.COMMON_CODE_NOT_FOUND);

            // then
            assertThat(exception.getMessage()).isEqualTo("공통 코드를 찾을 수 없습니다");
            assertThat(exception.code()).isEqualTo("CMC-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("ErrorCode와 커스텀 메시지로 예외를 생성한다")
        void createWithErrorCodeAndMessage() {
            // when
            CommonCodeException exception =
                    new CommonCodeException(
                            CommonCodeErrorCode.COMMON_CODE_NOT_FOUND, "ID 789 공통 코드 없음");

            // then
            assertThat(exception.getMessage()).isEqualTo("ID 789 공통 코드 없음");
            assertThat(exception.code()).isEqualTo("CMC-001");
        }

        @Test
        @DisplayName("ErrorCode와 원인 예외로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            // given
            RuntimeException cause = new RuntimeException("원인 예외");

            // when
            CommonCodeException exception =
                    new CommonCodeException(CommonCodeErrorCode.COMMON_CODE_NOT_FOUND, cause);

            // then
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("CMC-001");
        }
    }

    @Nested
    @DisplayName("구체적 예외 클래스 테스트")
    class ConcreteExceptionTest {

        @Test
        @DisplayName("CommonCodeNotFoundException 기본 생성")
        void createCommonCodeNotFoundException() {
            // when
            CommonCodeNotFoundException exception = new CommonCodeNotFoundException();

            // then
            assertThat(exception.code()).isEqualTo("CMC-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("공통 코드를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("CommonCodeNotFoundException ID 포함 생성")
        void createCommonCodeNotFoundExceptionWithId() {
            // when
            CommonCodeNotFoundException exception = new CommonCodeNotFoundException(123L);

            // then
            assertThat(exception.code()).isEqualTo("CMC-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("ID가 123인 공통 코드를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("CommonCodeDuplicateException 기본 생성")
        void createCommonCodeDuplicateException() {
            // when
            CommonCodeDuplicateException exception = new CommonCodeDuplicateException();

            // then
            assertThat(exception.code()).isEqualTo("CMC-002");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.getMessage()).isEqualTo("동일한 타입과 코드가 이미 존재합니다");
        }

        @Test
        @DisplayName("CommonCodeDuplicateException 타입과 코드 포함 생성")
        void createCommonCodeDuplicateExceptionWithTypeAndCode() {
            // when
            CommonCodeDuplicateException exception =
                    new CommonCodeDuplicateException("PAYMENT_TYPE", "CARD");

            // then
            assertThat(exception.code()).isEqualTo("CMC-002");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.getMessage()).isEqualTo("타입 'PAYMENT_TYPE', 코드 'CARD'는 이미 존재합니다");
        }

        @Test
        @DisplayName("CommonCodeInactiveException 생성")
        void createCommonCodeInactiveException() {
            // when
            CommonCodeInactiveException exception = new CommonCodeInactiveException();

            // then
            assertThat(exception.code()).isEqualTo("CMC-003");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).isEqualTo("비활성화된 공통 코드입니다");
        }
    }

    @Nested
    @DisplayName("상속 관계 테스트")
    class InheritanceTest {

        @Test
        @DisplayName("CommonCodeException은 DomainException을 상속한다")
        void commonCodeExceptionExtendsDomainException() {
            // given
            CommonCodeException exception =
                    new CommonCodeException(CommonCodeErrorCode.COMMON_CODE_NOT_FOUND);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("CommonCodeNotFoundException은 CommonCodeException을 상속한다")
        void commonCodeNotFoundExceptionExtendsCommonCodeException() {
            // given
            CommonCodeNotFoundException exception = new CommonCodeNotFoundException();

            // then
            assertThat(exception).isInstanceOf(CommonCodeException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("CommonCodeDuplicateException은 CommonCodeException을 상속한다")
        void commonCodeDuplicateExceptionExtendsCommonCodeException() {
            // given
            CommonCodeDuplicateException exception = new CommonCodeDuplicateException();

            // then
            assertThat(exception).isInstanceOf(CommonCodeException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("CommonCodeInactiveException은 CommonCodeException을 상속한다")
        void commonCodeInactiveExceptionExtendsCommonCodeException() {
            // given
            CommonCodeInactiveException exception = new CommonCodeInactiveException();

            // then
            assertThat(exception).isInstanceOf(CommonCodeException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }
    }
}
