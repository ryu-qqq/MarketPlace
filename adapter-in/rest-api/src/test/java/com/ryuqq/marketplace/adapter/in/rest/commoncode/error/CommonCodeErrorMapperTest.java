package com.ryuqq.marketplace.adapter.in.rest.commoncode.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.commoncode.exception.CommonCodeDuplicateException;
import com.ryuqq.marketplace.domain.commoncode.exception.CommonCodeException;
import com.ryuqq.marketplace.domain.commoncode.exception.CommonCodeNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("CommonCodeErrorMapper 단위 테스트")
class CommonCodeErrorMapperTest {

    private final CommonCodeErrorMapper sut = new CommonCodeErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("CommonCodeException을 지원한다")
        void supports_CommonCodeException_ReturnsTrue() {
            // given
            DomainException ex = new CommonCodeNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("CommonCodeDuplicateException을 지원한다")
        void supports_CommonCodeDuplicateException_ReturnsTrue() {
            // given
            DomainException ex = new CommonCodeDuplicateException("TYPE", "CODE");

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("다른 도메인 예외는 지원하지 않는다")
        void supports_OtherDomainException_ReturnsFalse() {
            // given
            DomainException ex =
                    new DomainException(
                            new com.ryuqq.marketplace.domain.common.exception.ErrorCode() {
                                @Override
                                public String getCode() {
                                    return "OTHER-001";
                                }

                                @Override
                                public int getHttpStatus() {
                                    return 400;
                                }

                                @Override
                                public String getMessage() {
                                    return "Other error";
                                }
                            }) {};

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("map() - 예외를 MappedError로 변환")
    class MapTest {

        @Test
        @DisplayName("CommonCodeNotFoundException을 404 MappedError로 변환한다")
        void map_NotFound_Returns404() {
            // given
            CommonCodeException ex = new CommonCodeNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Common Code Error");
            assertThat(result.detail()).contains("1");
            assertThat(result.type().toString()).startsWith("/errors/common-code/");
        }

        @Test
        @DisplayName("CommonCodeDuplicateException을 409 MappedError로 변환한다")
        void map_Duplicate_Returns409() {
            // given
            CommonCodeException ex = new CommonCodeDuplicateException("TYPE", "CODE");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Common Code Error");
            assertThat(result.type().toString()).contains("/errors/common-code/");
        }
    }
}
