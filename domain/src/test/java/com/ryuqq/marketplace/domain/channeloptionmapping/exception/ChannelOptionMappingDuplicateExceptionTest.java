package com.ryuqq.marketplace.domain.channeloptionmapping.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ChannelOptionMappingDuplicateException 테스트")
class ChannelOptionMappingDuplicateExceptionTest {

    @Nested
    @DisplayName("예외 생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("판매채널 ID와 캐노니컬 옵션 값 ID로 예외를 생성한다")
        void createExceptionWithSalesChannelIdAndCanonicalOptionValueId() {
            // given
            Long salesChannelId = 1L;
            Long canonicalOptionValueId = 100L;

            // when
            ChannelOptionMappingDuplicateException exception =
                    new ChannelOptionMappingDuplicateException(salesChannelId,
                            canonicalOptionValueId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(ChannelOptionMappingErrorCode.CHANNEL_OPTION_MAPPING_DUPLICATE);
            assertThat(exception.getMessage()).contains("salesChannelId=1");
            assertThat(exception.getMessage()).contains("canonicalOptionValueId=100");
            assertThat(exception.getMessage()).contains("조합의 매핑이 이미 존재합니다");
        }

        @Test
        @DisplayName("예외 메타데이터에 salesChannelId와 canonicalOptionValueId가 포함된다")
        void exceptionMetadataContainsBothIds() {
            // given
            Long salesChannelId = 2L;
            Long canonicalOptionValueId = 200L;

            // when
            ChannelOptionMappingDuplicateException exception =
                    new ChannelOptionMappingDuplicateException(salesChannelId,
                            canonicalOptionValueId);

            // then
            assertThat(exception.args()).containsEntry("salesChannelId", salesChannelId);
            assertThat(exception.args()).containsEntry("canonicalOptionValueId",
                    canonicalOptionValueId);
        }
    }

    @Nested
    @DisplayName("ErrorCode 검증 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("올바른 ErrorCode를 가진다")
        void hasCorrectErrorCode() {
            // when
            ChannelOptionMappingDuplicateException exception =
                    new ChannelOptionMappingDuplicateException(1L, 100L);

            // then
            assertThat(exception.getErrorCode().getCode()).isEqualTo("CHOPT-002");
            assertThat(exception.getErrorCode().getHttpStatus()).isEqualTo(409);
        }
    }
}
