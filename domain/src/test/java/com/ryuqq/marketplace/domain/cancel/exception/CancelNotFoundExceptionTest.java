package com.ryuqq.marketplace.domain.cancel.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelNotFoundException 단위 테스트")
class CancelNotFoundExceptionTest {

    @Nested
    @DisplayName("기본 생성자 테스트")
    class DefaultConstructorTest {

        @Test
        @DisplayName("기본 생성자로 생성하면 CAN-001 에러코드를 가진다")
        void createWithDefaultConstructor() {
            CancelNotFoundException exception = new CancelNotFoundException();

            assertThat(exception.code()).isEqualTo("CAN-001");
        }

        @Test
        @DisplayName("기본 생성자로 생성하면 HTTP 404 상태코드를 가진다")
        void defaultConstructorHas404Status() {
            CancelNotFoundException exception = new CancelNotFoundException();

            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("기본 생성자로 생성하면 기본 메시지를 가진다")
        void defaultConstructorHasDefaultMessage() {
            CancelNotFoundException exception = new CancelNotFoundException();

            assertThat(exception.getMessage()).contains("찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("cancelId를 포함한 생성자 테스트")
    class CancelIdConstructorTest {

        @Test
        @DisplayName("cancelId로 생성하면 메시지에 ID가 포함된다")
        void createWithCancelId() {
            String cancelId = "cancel-001";
            CancelNotFoundException exception = new CancelNotFoundException(cancelId);

            assertThat(exception.getMessage()).contains(cancelId);
        }

        @Test
        @DisplayName("cancelId로 생성해도 CAN-001 에러코드를 가진다")
        void createWithCancelIdHasCorrectErrorCode() {
            CancelNotFoundException exception = new CancelNotFoundException("cancel-001");

            assertThat(exception.code()).isEqualTo("CAN-001");
        }

        @Test
        @DisplayName("cancelId로 생성해도 HTTP 404 상태코드를 가진다")
        void createWithCancelIdHas404Status() {
            CancelNotFoundException exception = new CancelNotFoundException("cancel-001");

            assertThat(exception.httpStatus()).isEqualTo(404);
        }
    }

    @Nested
    @DisplayName("상속 구조 검증")
    class InheritanceTest {

        @Test
        @DisplayName("CancelNotFoundException은 CancelException을 상속한다")
        void extendsFromCancelException() {
            CancelNotFoundException exception = new CancelNotFoundException();

            assertThat(exception).isInstanceOf(CancelException.class);
        }

        @Test
        @DisplayName("CancelNotFoundException은 DomainException을 상속한다")
        void extendsFromDomainException() {
            CancelNotFoundException exception = new CancelNotFoundException();

            assertThat(exception)
                    .isInstanceOf(
                            com.ryuqq.marketplace.domain.common.exception.DomainException.class);
        }
    }
}
