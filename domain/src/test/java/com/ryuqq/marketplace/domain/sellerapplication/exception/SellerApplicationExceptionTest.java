package com.ryuqq.marketplace.domain.sellerapplication.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerApplication 예외 테스트")
class SellerApplicationExceptionTest {

    @Nested
    @DisplayName("SellerApplicationErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("SELLER_APPLICATION_NOT_FOUND 에러 코드가 올바르다")
        void sellerApplicationNotFound() {
            SellerApplicationErrorCode code =
                    SellerApplicationErrorCode.SELLER_APPLICATION_NOT_FOUND;
            assertThat(code.getCode()).isEqualTo("SELAPP-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("SELLER_APPLICATION_ALREADY_PROCESSED 에러 코드가 올바르다")
        void sellerApplicationAlreadyProcessed() {
            SellerApplicationErrorCode code =
                    SellerApplicationErrorCode.SELLER_APPLICATION_ALREADY_PROCESSED;
            assertThat(code.getCode()).isEqualTo("SELAPP-002");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("이미 처리된");
        }

        @Test
        @DisplayName("SELLER_APPLICATION_PENDING_EXISTS 에러 코드가 올바르다")
        void sellerApplicationPendingExists() {
            SellerApplicationErrorCode code =
                    SellerApplicationErrorCode.SELLER_APPLICATION_PENDING_EXISTS;
            assertThat(code.getCode()).isEqualTo("SELAPP-003");
            assertThat(code.getHttpStatus()).isEqualTo(409);
            assertThat(code.getMessage()).contains("대기 중인 신청");
        }

        @Test
        @DisplayName("REJECTION_REASON_REQUIRED 에러 코드가 올바르다")
        void rejectionReasonRequired() {
            SellerApplicationErrorCode code = SellerApplicationErrorCode.REJECTION_REASON_REQUIRED;
            assertThat(code.getCode()).isEqualTo("SELAPP-004");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("거절 사유");
        }
    }

    @Nested
    @DisplayName("예외 클래스 테스트")
    class ExceptionClassTest {

        @Test
        @DisplayName("SellerApplicationNotFoundException 생성 시 올바른 에러 코드를 가진다")
        void sellerApplicationNotFoundException() {
            SellerApplicationNotFoundException exception = new SellerApplicationNotFoundException();
            assertThat(exception.code()).isEqualTo("SELAPP-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("SellerApplicationNotFoundException에 ID를 포함한 메시지를 생성한다")
        void sellerApplicationNotFoundExceptionWithId() {
            SellerApplicationNotFoundException exception =
                    new SellerApplicationNotFoundException(123L);
            assertThat(exception.getMessage()).contains("123");
            assertThat(exception.code()).isEqualTo("SELAPP-001");
        }

        @Test
        @DisplayName("SellerApplicationException은 커스텀 메시지를 지원한다")
        void sellerApplicationExceptionWithCustomMessage() {
            SellerApplicationException exception =
                    new SellerApplicationException(
                            SellerApplicationErrorCode.SELLER_APPLICATION_NOT_FOUND, "커스텀 메시지");
            assertThat(exception.getMessage()).isEqualTo("커스텀 메시지");
            assertThat(exception.code()).isEqualTo("SELAPP-001");
        }

        @Test
        @DisplayName("SellerApplicationException은 원인 예외를 지원한다")
        void sellerApplicationExceptionWithCause() {
            RuntimeException cause = new RuntimeException("원인");
            SellerApplicationException exception =
                    new SellerApplicationException(
                            SellerApplicationErrorCode.SELLER_APPLICATION_NOT_FOUND, cause);
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("SELAPP-001");
        }
    }
}
