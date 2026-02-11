package com.ryuqq.marketplace.domain.channeloptionmapping.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ChannelOptionMappingErrorCode 테스트")
class ChannelOptionMappingErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            assertThat(ChannelOptionMappingErrorCode.CHANNEL_OPTION_MAPPING_NOT_FOUND)
                    .isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("채널 옵션 매핑 에러 코드 테스트")
    class ChannelOptionMappingErrorCodesTest {

        @Test
        @DisplayName("CHANNEL_OPTION_MAPPING_NOT_FOUND 에러 코드를 검증한다")
        void channelOptionMappingNotFound() {
            assertThat(ChannelOptionMappingErrorCode.CHANNEL_OPTION_MAPPING_NOT_FOUND.getCode())
                    .isEqualTo("CHOPT-001");
            assertThat(
                            ChannelOptionMappingErrorCode.CHANNEL_OPTION_MAPPING_NOT_FOUND
                                    .getHttpStatus())
                    .isEqualTo(404);
            assertThat(ChannelOptionMappingErrorCode.CHANNEL_OPTION_MAPPING_NOT_FOUND.getMessage())
                    .isEqualTo("채널 옵션 매핑을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("CHANNEL_OPTION_MAPPING_DUPLICATE 에러 코드를 검증한다")
        void channelOptionMappingDuplicate() {
            assertThat(ChannelOptionMappingErrorCode.CHANNEL_OPTION_MAPPING_DUPLICATE.getCode())
                    .isEqualTo("CHOPT-002");
            assertThat(
                            ChannelOptionMappingErrorCode.CHANNEL_OPTION_MAPPING_DUPLICATE
                                    .getHttpStatus())
                    .isEqualTo(409);
            assertThat(ChannelOptionMappingErrorCode.CHANNEL_OPTION_MAPPING_DUPLICATE.getMessage())
                    .isEqualTo("이미 존재하는 채널 옵션 매핑입니다");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            assertThat(ChannelOptionMappingErrorCode.values())
                    .containsExactly(
                            ChannelOptionMappingErrorCode.CHANNEL_OPTION_MAPPING_NOT_FOUND,
                            ChannelOptionMappingErrorCode.CHANNEL_OPTION_MAPPING_DUPLICATE);
        }
    }
}
