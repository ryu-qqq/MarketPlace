package com.ryuqq.marketplace.domain.saleschannel.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelErrorCode Enum 단위 테스트")
class SalesChannelErrorCodeTest {

    @Nested
    @DisplayName("에러 코드 정의 테스트")
    class ErrorCodeDefinitionTest {
        @Test
        @DisplayName("SALES_CHANNEL_NOT_FOUND 코드는 올바른 값을 갖는다")
        void salesChannelNotFoundHasCorrectValues() {
            // given
            SalesChannelErrorCode errorCode = SalesChannelErrorCode.SALES_CHANNEL_NOT_FOUND;

            // when & then
            assertThat(errorCode.getCode()).isEqualTo("SCH-001");
            assertThat(errorCode.getHttpStatus()).isEqualTo(404);
            assertThat(errorCode.getMessage()).isEqualTo("판매채널을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("SALES_CHANNEL_NAME_DUPLICATE 코드는 올바른 값을 갖는다")
        void salesChannelNameDuplicateHasCorrectValues() {
            // given
            SalesChannelErrorCode errorCode = SalesChannelErrorCode.SALES_CHANNEL_NAME_DUPLICATE;

            // when & then
            assertThat(errorCode.getCode()).isEqualTo("SCH-002");
            assertThat(errorCode.getHttpStatus()).isEqualTo(409);
            assertThat(errorCode.getMessage()).isEqualTo("이미 존재하는 판매채널명입니다");
        }
    }

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {
        @Test
        @DisplayName("모든 에러 코드는 getCode()를 구현한다")
        void allErrorCodesImplementGetCode() {
            for (SalesChannelErrorCode errorCode : SalesChannelErrorCode.values()) {
                assertThat(errorCode.getCode()).isNotNull();
                assertThat(errorCode.getCode()).startsWith("SCH-");
            }
        }

        @Test
        @DisplayName("모든 에러 코드는 getHttpStatus()를 구현한다")
        void allErrorCodesImplementGetHttpStatus() {
            for (SalesChannelErrorCode errorCode : SalesChannelErrorCode.values()) {
                assertThat(errorCode.getHttpStatus()).isGreaterThan(0);
                assertThat(errorCode.getHttpStatus()).isLessThan(600);
            }
        }

        @Test
        @DisplayName("모든 에러 코드는 getMessage()를 구현한다")
        void allErrorCodesImplementGetMessage() {
            for (SalesChannelErrorCode errorCode : SalesChannelErrorCode.values()) {
                assertThat(errorCode.getMessage()).isNotNull();
                assertThat(errorCode.getMessage()).isNotBlank();
            }
        }
    }
}
