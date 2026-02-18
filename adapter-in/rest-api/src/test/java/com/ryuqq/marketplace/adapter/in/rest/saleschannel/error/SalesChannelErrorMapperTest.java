package com.ryuqq.marketplace.adapter.in.rest.saleschannel.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.saleschannel.exception.SalesChannelException;
import com.ryuqq.marketplace.domain.saleschannel.exception.SalesChannelNameDuplicateException;
import com.ryuqq.marketplace.domain.saleschannel.exception.SalesChannelNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("SalesChannelErrorMapper 단위 테스트")
class SalesChannelErrorMapperTest {

    private final SalesChannelErrorMapper sut = new SalesChannelErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("SalesChannelNotFoundException을 지원한다")
        void supports_SalesChannelNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new SalesChannelNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("SalesChannelNameDuplicateException을 지원한다")
        void supports_SalesChannelNameDuplicateException_ReturnsTrue() {
            // given
            DomainException ex = new SalesChannelNameDuplicateException("쿠팡");

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
        @DisplayName("SalesChannelNotFoundException을 404 MappedError로 변환한다")
        void map_SalesChannelNotFound_Returns404() {
            // given
            SalesChannelException ex = new SalesChannelNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Sales Channel Error");
            assertThat(result.detail()).contains("1");
            assertThat(result.type().toString()).startsWith("/errors/sales-channel/");
        }

        @Test
        @DisplayName("SalesChannelNameDuplicateException을 409 MappedError로 변환한다")
        void map_SalesChannelNameDuplicate_Returns409() {
            // given
            SalesChannelException ex = new SalesChannelNameDuplicateException("쿠팡");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Sales Channel Error");
            assertThat(result.type().toString()).contains("/errors/sales-channel/");
        }

        @Test
        @DisplayName("error type URI가 /errors/sales-channel/ 접두사를 가진다")
        void map_AnyException_TypeUriStartsWithPrefix() {
            // given
            SalesChannelException ex = new SalesChannelNotFoundException();

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString()).startsWith("/errors/sales-channel/");
        }
    }
}
