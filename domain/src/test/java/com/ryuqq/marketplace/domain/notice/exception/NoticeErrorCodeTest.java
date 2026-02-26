package com.ryuqq.marketplace.domain.notice.exception;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NoticeErrorCode 테스트")
class NoticeErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            // then
            assertThat(NoticeErrorCode.NOTICE_CATEGORY_NOT_FOUND).isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("고시정보 관련 에러 코드 테스트")
    class NoticeErrorCodesTest {

        @Test
        @DisplayName("NOTICE_CATEGORY_NOT_FOUND 에러 코드를 검증한다")
        void noticeCategoryNotFound() {
            // then
            assertThat(NoticeErrorCode.NOTICE_CATEGORY_NOT_FOUND.getCode()).isEqualTo("NOTICE-001");
            assertThat(NoticeErrorCode.NOTICE_CATEGORY_NOT_FOUND.getHttpStatus()).isEqualTo(404);
            assertThat(NoticeErrorCode.NOTICE_CATEGORY_NOT_FOUND.getMessage())
                    .isEqualTo("고시정보 카테고리를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("NOTICE_FIELD_NOT_FOUND 에러 코드를 검증한다")
        void noticeFieldNotFound() {
            // then
            assertThat(NoticeErrorCode.NOTICE_FIELD_NOT_FOUND.getCode()).isEqualTo("NOTICE-002");
            assertThat(NoticeErrorCode.NOTICE_FIELD_NOT_FOUND.getHttpStatus()).isEqualTo(404);
            assertThat(NoticeErrorCode.NOTICE_FIELD_NOT_FOUND.getMessage())
                    .isEqualTo("고시정보 필드를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(NoticeErrorCode.values())
                    .containsExactly(
                            NoticeErrorCode.NOTICE_CATEGORY_NOT_FOUND,
                            NoticeErrorCode.NOTICE_FIELD_NOT_FOUND,
                            NoticeErrorCode.NOTICE_INVALID_FIELD,
                            NoticeErrorCode.NOTICE_REQUIRED_FIELD_MISSING);
        }

        @Test
        @DisplayName("에러 코드 개수가 정확하다")
        void correctNumberOfValues() {
            // then
            assertThat(NoticeErrorCode.values()).hasSize(4);
        }
    }
}
