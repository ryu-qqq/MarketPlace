package com.ryuqq.marketplace.domain.selleradmin.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAdmin 예외 테스트")
class SellerAdminErrorCodeTest {

    @Nested
    @DisplayName("SellerAdminErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("SELLER_ADMIN_NOT_FOUND 에러 코드가 올바르다")
        void sellerAdminNotFound() {
            SellerAdminErrorCode code = SellerAdminErrorCode.SELLER_ADMIN_NOT_FOUND;

            assertThat(code.getCode()).isEqualTo("SELADM-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("SELLER_ADMIN_APPLICATION_NOT_FOUND 에러 코드가 올바르다")
        void sellerAdminApplicationNotFound() {
            SellerAdminErrorCode code = SellerAdminErrorCode.SELLER_ADMIN_APPLICATION_NOT_FOUND;

            assertThat(code.getCode()).isEqualTo("SELADM-002");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("SELLER_ADMIN_ALREADY_PROCESSED 에러 코드가 올바르다")
        void sellerAdminAlreadyProcessed() {
            SellerAdminErrorCode code = SellerAdminErrorCode.SELLER_ADMIN_ALREADY_PROCESSED;

            assertThat(code.getCode()).isEqualTo("SELADM-003");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("처리");
        }

        @Test
        @DisplayName("SELLER_ADMIN_PENDING_EXISTS 에러 코드가 올바르다")
        void sellerAdminPendingExists() {
            SellerAdminErrorCode code = SellerAdminErrorCode.SELLER_ADMIN_PENDING_EXISTS;

            assertThat(code.getCode()).isEqualTo("SELADM-004");
            assertThat(code.getHttpStatus()).isEqualTo(409);
            assertThat(code.getMessage()).contains("대기");
        }

        @Test
        @DisplayName("REJECTION_REASON_REQUIRED 에러 코드가 올바르다")
        void rejectionReasonRequired() {
            SellerAdminErrorCode code = SellerAdminErrorCode.REJECTION_REASON_REQUIRED;

            assertThat(code.getCode()).isEqualTo("SELADM-005");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("거절 사유");
        }

        @Test
        @DisplayName("PASSWORD_RESET_NOT_ALLOWED 에러 코드가 올바르다")
        void passwordResetNotAllowed() {
            SellerAdminErrorCode code = SellerAdminErrorCode.PASSWORD_RESET_NOT_ALLOWED;

            assertThat(code.getCode()).isEqualTo("SELADM-006");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("비밀번호");
        }

        @Test
        @DisplayName("SELLER_ADMIN_NOT_APPROVED 에러 코드가 올바르다")
        void sellerAdminNotApproved() {
            SellerAdminErrorCode code = SellerAdminErrorCode.SELLER_ADMIN_NOT_APPROVED;

            assertThat(code.getCode()).isEqualTo("SELADM-007");
            assertThat(code.getHttpStatus()).isEqualTo(403);
            assertThat(code.getMessage()).contains("승인");
        }

        @Test
        @DisplayName("SELLER_ADMIN_INVALID_PASSWORD 에러 코드가 올바르다")
        void sellerAdminInvalidPassword() {
            SellerAdminErrorCode code = SellerAdminErrorCode.SELLER_ADMIN_INVALID_PASSWORD;

            assertThat(code.getCode()).isEqualTo("SELADM-008");
            assertThat(code.getHttpStatus()).isEqualTo(401);
            assertThat(code.getMessage()).contains("비밀번호");
        }
    }

    @Nested
    @DisplayName("SellerAdminException 예외 클래스 테스트")
    class ExceptionClassTest {

        @Test
        @DisplayName("SellerAdminException은 ErrorCode만으로 생성된다")
        void sellerAdminExceptionWithErrorCode() {
            SellerAdminException exception =
                    new SellerAdminException(SellerAdminErrorCode.SELLER_ADMIN_NOT_FOUND);

            assertThat(exception.code()).isEqualTo("SELADM-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("SellerAdminException은 커스텀 메시지를 지원한다")
        void sellerAdminExceptionWithCustomMessage() {
            SellerAdminException exception =
                    new SellerAdminException(
                            SellerAdminErrorCode.SELLER_ADMIN_NOT_FOUND,
                            "ID가 abc인 셀러 관리자를 찾을 수 없습니다");

            assertThat(exception.code()).isEqualTo("SELADM-001");
            assertThat(exception.getMessage()).isEqualTo("ID가 abc인 셀러 관리자를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("SellerAdminException은 원인 예외를 지원한다")
        void sellerAdminExceptionWithCause() {
            RuntimeException cause = new RuntimeException("원인 예외");

            SellerAdminException exception =
                    new SellerAdminException(SellerAdminErrorCode.SELLER_ADMIN_NOT_FOUND, cause);

            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("SELADM-001");
        }
    }

    @Nested
    @DisplayName("SellerAdminNotFoundException 테스트")
    class NotFoundExceptionTest {

        @Test
        @DisplayName("기본 생성자로 생성하면 SELLER_ADMIN_APPLICATION_NOT_FOUND 에러 코드를 사용한다")
        void defaultConstructorUsesApplicationNotFoundCode() {
            SellerAdminNotFoundException exception = new SellerAdminNotFoundException();

            assertThat(exception.code()).isEqualTo("SELADM-002");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("String ID로 생성하면 메시지에 ID가 포함된다")
        void stringIdConstructorIncludesIdInMessage() {
            SellerAdminNotFoundException exception =
                    new SellerAdminNotFoundException("test-admin-id");

            assertThat(exception.getMessage()).contains("test-admin-id");
        }

        @Test
        @DisplayName("withMessage 팩토리 메서드로 커스텀 메시지를 설정한다")
        void withMessageFactoryMethod() {
            SellerAdminNotFoundException exception =
                    SellerAdminNotFoundException.withMessage("커스텀 메시지");

            assertThat(exception.getMessage()).isEqualTo("커스텀 메시지");
            assertThat(exception.code()).isEqualTo("SELADM-002");
        }
    }

    @Test
    @DisplayName("SellerAdminErrorCode 값이 8개 존재한다")
    void hasExpectedValues() {
        assertThat(SellerAdminErrorCode.values()).hasSize(8);
    }
}
