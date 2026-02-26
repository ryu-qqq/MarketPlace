package com.ryuqq.marketplace.domain.commoncodetype.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeTypeException 테스트")
class CommonCodeTypeExceptionTest {

    @Nested
    @DisplayName("기본 생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("ErrorCode로 예외를 생성한다")
        void createWithErrorCode() {
            // when
            CommonCodeTypeException exception =
                    new CommonCodeTypeException(CommonCodeTypeErrorCode.NOT_FOUND);

            // then
            assertThat(exception.getMessage()).isEqualTo("공통 코드 타입을 찾을 수 없습니다");
            assertThat(exception.code()).isEqualTo("CCT-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("ErrorCode와 커스텀 메시지로 예외를 생성한다")
        void createWithErrorCodeAndMessage() {
            // when
            CommonCodeTypeException exception =
                    new CommonCodeTypeException(
                            CommonCodeTypeErrorCode.NOT_FOUND, "ID 999 공통 코드 타입 없음");

            // then
            assertThat(exception.getMessage()).isEqualTo("ID 999 공통 코드 타입 없음");
            assertThat(exception.code()).isEqualTo("CCT-001");
        }

        @Test
        @DisplayName("ErrorCode와 원인 예외로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            // given
            RuntimeException cause = new RuntimeException("원인 예외");

            // when
            CommonCodeTypeException exception =
                    new CommonCodeTypeException(CommonCodeTypeErrorCode.NOT_FOUND, cause);

            // then
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("CCT-001");
        }
    }

    @Nested
    @DisplayName("구체적 예외 클래스 테스트")
    class ConcreteExceptionTest {

        @Test
        @DisplayName("CommonCodeTypeNotFoundException 기본 생성")
        void createCommonCodeTypeNotFoundException() {
            // when
            CommonCodeTypeNotFoundException exception = new CommonCodeTypeNotFoundException();

            // then
            assertThat(exception.code()).isEqualTo("CCT-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("공통 코드 타입을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("CommonCodeTypeNotFoundException ID 포함 생성")
        void createCommonCodeTypeNotFoundExceptionWithId() {
            // when
            CommonCodeTypeNotFoundException exception = new CommonCodeTypeNotFoundException(123L);

            // then
            assertThat(exception.code()).isEqualTo("CCT-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("ID가 123인 공통 코드 타입을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("DuplicateCommonCodeTypeCodeException 기본 생성")
        void createDuplicateCommonCodeTypeCodeException() {
            // when
            DuplicateCommonCodeTypeCodeException exception =
                    new DuplicateCommonCodeTypeCodeException();

            // then
            assertThat(exception.code()).isEqualTo("CCT-002");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.getMessage()).isEqualTo("동일한 코드가 이미 존재합니다");
        }

        @Test
        @DisplayName("DuplicateCommonCodeTypeCodeException 코드 포함 생성")
        void createDuplicateCommonCodeTypeCodeExceptionWithCode() {
            // when
            DuplicateCommonCodeTypeCodeException exception =
                    new DuplicateCommonCodeTypeCodeException("PAYMENT_TYPE");

            // then
            assertThat(exception.code()).isEqualTo("CCT-002");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.getMessage()).isEqualTo("코드 'PAYMENT_TYPE'는 이미 존재합니다");
        }

        @Test
        @DisplayName("DuplicateCommonCodeTypeDisplayOrderException 기본 생성")
        void createDuplicateCommonCodeTypeDisplayOrderException() {
            // when
            DuplicateCommonCodeTypeDisplayOrderException exception =
                    new DuplicateCommonCodeTypeDisplayOrderException();

            // then
            assertThat(exception.code()).isEqualTo("CCT-003");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).isEqualTo("동일한 표시 순서가 이미 존재합니다");
        }

        @Test
        @DisplayName("DuplicateCommonCodeTypeDisplayOrderException 순서 포함 생성")
        void createDuplicateCommonCodeTypeDisplayOrderExceptionWithOrder() {
            // when
            DuplicateCommonCodeTypeDisplayOrderException exception =
                    new DuplicateCommonCodeTypeDisplayOrderException(5);

            // then
            assertThat(exception.code()).isEqualTo("CCT-003");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).isEqualTo("표시 순서 '5'는 이미 존재합니다");
        }

        @Test
        @DisplayName("ActiveCommonCodesExistException 기본 생성")
        void createActiveCommonCodesExistException() {
            // when
            ActiveCommonCodesExistException exception = new ActiveCommonCodesExistException();

            // then
            assertThat(exception.code()).isEqualTo("CCT-004");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).isEqualTo("활성화된 공통 코드가 존재하여 비활성화할 수 없습니다");
        }

        @Test
        @DisplayName("ActiveCommonCodesExistException 타입코드 포함 생성")
        void createActiveCommonCodesExistExceptionWithTypeCode() {
            // when
            ActiveCommonCodesExistException exception =
                    new ActiveCommonCodesExistException("PAYMENT_TYPE");

            // then
            assertThat(exception.code()).isEqualTo("CCT-004");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage())
                    .isEqualTo("공통 코드 타입 'PAYMENT_TYPE'에 활성화된 공통 코드가 존재합니다. 먼저 해당 공통 코드를 비활성화하세요");
        }
    }

    @Nested
    @DisplayName("상속 관계 테스트")
    class InheritanceTest {

        @Test
        @DisplayName("CommonCodeTypeException은 DomainException을 상속한다")
        void commonCodeTypeExceptionExtendsDomainException() {
            // given
            CommonCodeTypeException exception =
                    new CommonCodeTypeException(CommonCodeTypeErrorCode.NOT_FOUND);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("CommonCodeTypeNotFoundException은 CommonCodeTypeException을 상속한다")
        void commonCodeTypeNotFoundExceptionExtendsCommonCodeTypeException() {
            // given
            CommonCodeTypeNotFoundException exception = new CommonCodeTypeNotFoundException();

            // then
            assertThat(exception).isInstanceOf(CommonCodeTypeException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("DuplicateCommonCodeTypeCodeException은 CommonCodeTypeException을 상속한다")
        void duplicateCommonCodeTypeCodeExceptionExtendsCommonCodeTypeException() {
            // given
            DuplicateCommonCodeTypeCodeException exception =
                    new DuplicateCommonCodeTypeCodeException();

            // then
            assertThat(exception).isInstanceOf(CommonCodeTypeException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("DuplicateCommonCodeTypeDisplayOrderException은 CommonCodeTypeException을 상속한다")
        void duplicateCommonCodeTypeDisplayOrderExceptionExtendsCommonCodeTypeException() {
            // given
            DuplicateCommonCodeTypeDisplayOrderException exception =
                    new DuplicateCommonCodeTypeDisplayOrderException();

            // then
            assertThat(exception).isInstanceOf(CommonCodeTypeException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("ActiveCommonCodesExistException은 CommonCodeTypeException을 상속한다")
        void activeCommonCodesExistExceptionExtendsCommonCodeTypeException() {
            // given
            ActiveCommonCodesExistException exception = new ActiveCommonCodesExistException();

            // then
            assertThat(exception).isInstanceOf(CommonCodeTypeException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }
    }
}
