package com.ryuqq.marketplace.domain.channeloptionmapping.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ChannelOptionMappingNotFoundException 테스트")
class ChannelOptionMappingNotFoundExceptionTest {

    @Nested
    @DisplayName("예외 생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("채널 옵션 매핑 ID로 예외를 생성한다")
        void createExceptionWithChannelOptionMappingId() {
            // given
            Long channelOptionMappingId = 123L;

            // when
            ChannelOptionMappingNotFoundException exception =
                    new ChannelOptionMappingNotFoundException(channelOptionMappingId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(ChannelOptionMappingErrorCode.CHANNEL_OPTION_MAPPING_NOT_FOUND);
            assertThat(exception.getMessage()).contains("채널 옵션 매핑을 찾을 수 없습니다");
            assertThat(exception.getMessage()).contains("123");
        }

        @Test
        @DisplayName("예외 메타데이터에 channelOptionMappingId가 포함된다")
        void exceptionMetadataContainsChannelOptionMappingId() {
            // given
            Long channelOptionMappingId = 456L;

            // when
            ChannelOptionMappingNotFoundException exception =
                    new ChannelOptionMappingNotFoundException(channelOptionMappingId);

            // then
            assertThat(exception.args()).containsEntry("channelOptionMappingId",
                    channelOptionMappingId);
        }
    }

    @Nested
    @DisplayName("ErrorCode 검증 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("올바른 ErrorCode를 가진다")
        void hasCorrectErrorCode() {
            // when
            ChannelOptionMappingNotFoundException exception =
                    new ChannelOptionMappingNotFoundException(1L);

            // then
            assertThat(exception.getErrorCode().getCode()).isEqualTo("CHOPT-001");
            assertThat(exception.getErrorCode().getHttpStatus()).isEqualTo(404);
        }
    }
}
