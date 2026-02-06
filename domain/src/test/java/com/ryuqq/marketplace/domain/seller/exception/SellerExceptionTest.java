package com.ryuqq.marketplace.domain.seller.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerException 테스트")
class SellerExceptionTest {

    @Nested
    @DisplayName("기본 생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("ErrorCode로 예외를 생성한다")
        void createWithErrorCode() {
            // when
            SellerException exception = new SellerException(SellerErrorCode.SELLER_NOT_FOUND);

            // then
            assertThat(exception.getMessage()).isEqualTo("셀러를 찾을 수 없습니다");
            assertThat(exception.code()).isEqualTo("SEL-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("ErrorCode와 커스텀 메시지로 예외를 생성한다")
        void createWithErrorCodeAndMessage() {
            // when
            SellerException exception =
                    new SellerException(SellerErrorCode.SELLER_NOT_FOUND, "ID 100 셀러 없음");

            // then
            assertThat(exception.getMessage()).isEqualTo("ID 100 셀러 없음");
            assertThat(exception.code()).isEqualTo("SEL-001");
        }

        @Test
        @DisplayName("ErrorCode와 원인 예외로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            // given
            RuntimeException cause = new RuntimeException("원인 예외");

            // when
            SellerException exception =
                    new SellerException(SellerErrorCode.SELLER_NOT_FOUND, cause);

            // then
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("SEL-001");
        }
    }

    @Nested
    @DisplayName("구체적 예외 클래스 테스트")
    class ConcreteExceptionTest {

        @Test
        @DisplayName("SellerNotFoundException 기본 생성")
        void createSellerNotFoundException() {
            // when
            SellerNotFoundException exception = new SellerNotFoundException();

            // then
            assertThat(exception.code()).isEqualTo("SEL-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("셀러를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("SellerNotFoundException ID 포함 생성")
        void createSellerNotFoundExceptionWithId() {
            // when
            SellerNotFoundException exception = new SellerNotFoundException(456L);

            // then
            assertThat(exception.code()).isEqualTo("SEL-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("ID가 456인 셀러를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("SellerInactiveException 생성")
        void createSellerInactiveException() {
            // when
            SellerInactiveException exception = new SellerInactiveException();

            // then
            assertThat(exception.code()).isEqualTo("SEL-003");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).isEqualTo("비활성화된 셀러입니다");
        }

        @Test
        @DisplayName("BusinessInfoNotFoundException 생성")
        void createBusinessInfoNotFoundException() {
            // when
            BusinessInfoNotFoundException exception = new BusinessInfoNotFoundException();

            // then
            assertThat(exception.code()).isEqualTo("SEL-100");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("사업자 정보를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("AddressNotFoundException 생성")
        void createAddressNotFoundException() {
            // when
            AddressNotFoundException exception = new AddressNotFoundException();

            // then
            assertThat(exception.code()).isEqualTo("SEL-200");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("주소를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("상속 관계 테스트")
    class InheritanceTest {

        @Test
        @DisplayName("SellerException은 DomainException을 상속한다")
        void sellerExceptionExtendsDomainException() {
            // given
            SellerException exception = new SellerException(SellerErrorCode.SELLER_NOT_FOUND);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("SellerNotFoundException은 SellerException을 상속한다")
        void sellerNotFoundExceptionExtendsSellerException() {
            // given
            SellerNotFoundException exception = new SellerNotFoundException();

            // then
            assertThat(exception).isInstanceOf(SellerException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("SellerInactiveException은 SellerException을 상속한다")
        void sellerInactiveExceptionExtendsSellerException() {
            // given
            SellerInactiveException exception = new SellerInactiveException();

            // then
            assertThat(exception).isInstanceOf(SellerException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("BusinessInfoNotFoundException은 SellerException을 상속한다")
        void businessInfoNotFoundExceptionExtendsSellerException() {
            // given
            BusinessInfoNotFoundException exception = new BusinessInfoNotFoundException();

            // then
            assertThat(exception).isInstanceOf(SellerException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("AddressNotFoundException은 SellerException을 상속한다")
        void addressNotFoundExceptionExtendsSellerException() {
            // given
            AddressNotFoundException exception = new AddressNotFoundException();

            // then
            assertThat(exception).isInstanceOf(SellerException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }
    }
}
