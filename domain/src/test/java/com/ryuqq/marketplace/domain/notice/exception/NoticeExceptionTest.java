package com.ryuqq.marketplace.domain.notice.exception;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NoticeException 테스트")
class NoticeExceptionTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("ErrorCode로 예외를 생성한다")
        void createWithErrorCode() {
            // when
            NoticeException exception =
                    new NoticeException(NoticeErrorCode.NOTICE_CATEGORY_NOT_FOUND);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(NoticeErrorCode.NOTICE_CATEGORY_NOT_FOUND);
            assertThat(exception.getMessage()).isEqualTo("고시정보 카테고리를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("ErrorCode와 커스텀 메시지로 예외를 생성한다")
        void createWithErrorCodeAndCustomMessage() {
            // given
            String customMessage = "ID 999의 고시정보 카테고리를 찾을 수 없습니다";

            // when
            NoticeException exception =
                    new NoticeException(
                            NoticeErrorCode.NOTICE_CATEGORY_NOT_FOUND, customMessage);

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(NoticeErrorCode.NOTICE_CATEGORY_NOT_FOUND);
            assertThat(exception.getMessage()).isEqualTo(customMessage);
        }

        @Test
        @DisplayName("ErrorCode와 원인 예외로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            // given
            RuntimeException cause = new RuntimeException("DB 연결 실패");

            // when
            NoticeException exception =
                    new NoticeException(NoticeErrorCode.NOTICE_FIELD_NOT_FOUND, cause);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(NoticeErrorCode.NOTICE_FIELD_NOT_FOUND);
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.getMessage()).isEqualTo("고시정보 필드를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("예외 정보 조회 테스트")
    class ExceptionInfoTest {

        @Test
        @DisplayName("예외에서 에러 코드를 조회한다")
        void getErrorCode() {
            // given
            NoticeException exception =
                    new NoticeException(NoticeErrorCode.NOTICE_CATEGORY_NOT_FOUND);

            // when
            NoticeErrorCode errorCode = (NoticeErrorCode) exception.getErrorCode();

            // then
            assertThat(errorCode).isEqualTo(NoticeErrorCode.NOTICE_CATEGORY_NOT_FOUND);
            assertThat(errorCode.getCode()).isEqualTo("NOTICE-001");
            assertThat(errorCode.getHttpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("예외에서 HTTP 상태 코드를 조회한다")
        void getHttpStatusFromException() {
            // given
            NoticeException exception =
                    new NoticeException(NoticeErrorCode.NOTICE_FIELD_NOT_FOUND);

            // when
            int httpStatus = exception.getErrorCode().getHttpStatus();

            // then
            assertThat(httpStatus).isEqualTo(404);
        }
    }

    @Nested
    @DisplayName("DomainException 상속 테스트")
    class InheritanceTest {

        @Test
        @DisplayName("NoticeException은 DomainException을 상속한다")
        void extendsDomainException() {
            // given
            NoticeException exception =
                    new NoticeException(NoticeErrorCode.NOTICE_CATEGORY_NOT_FOUND);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("NoticeException은 RuntimeException을 상속한다")
        void extendsRuntimeException() {
            // given
            NoticeException exception =
                    new NoticeException(NoticeErrorCode.NOTICE_FIELD_NOT_FOUND);

            // then
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }
    }
}
