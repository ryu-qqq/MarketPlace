package com.ryuqq.marketplace.domain.adminmenu.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("AdminMenuException 단위 테스트")
class AdminMenuExceptionTest {

    @Nested
    @DisplayName("AdminMenuErrorCode - 에러 코드 검증")
    class ErrorCodeTest {

        @Test
        @DisplayName("ADMIN_MENU_NOT_FOUND 에러 코드가 올바르다")
        void adminMenuNotFound_HasCorrectValues() {
            AdminMenuErrorCode errorCode = AdminMenuErrorCode.ADMIN_MENU_NOT_FOUND;

            assertThat(errorCode.getCode()).isEqualTo("ADMIN_MENU-001");
            assertThat(errorCode.getHttpStatus()).isEqualTo(404);
            assertThat(errorCode.getMessage()).contains("관리자 메뉴");
        }
    }

    @Nested
    @DisplayName("AdminMenuException - 예외 생성")
    class ExceptionCreationTest {

        @Test
        @DisplayName("ErrorCode로 예외를 생성한다")
        void create_WithErrorCode_SetsMessage() {
            AdminMenuException ex = new AdminMenuException(AdminMenuErrorCode.ADMIN_MENU_NOT_FOUND);

            assertThat(ex.getMessage())
                    .isEqualTo(AdminMenuErrorCode.ADMIN_MENU_NOT_FOUND.getMessage());
            assertThat(ex.code()).isEqualTo("ADMIN_MENU-001");
            assertThat(ex.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("커스텀 메시지로 예외를 생성한다")
        void create_WithCustomMessage_OverridesMessage() {
            String customMessage = "ID: 999 메뉴를 찾을 수 없습니다";
            AdminMenuException ex =
                    new AdminMenuException(AdminMenuErrorCode.ADMIN_MENU_NOT_FOUND, customMessage);

            assertThat(ex.getMessage()).isEqualTo(customMessage);
            assertThat(ex.code()).isEqualTo("ADMIN_MENU-001");
        }

        @Test
        @DisplayName("원인 예외를 포함하여 생성한다")
        void create_WithCause_SetsCause() {
            RuntimeException cause = new RuntimeException("원인 예외");
            AdminMenuException ex =
                    new AdminMenuException(AdminMenuErrorCode.ADMIN_MENU_NOT_FOUND, cause);

            assertThat(ex.getCause()).isEqualTo(cause);
        }
    }
}
