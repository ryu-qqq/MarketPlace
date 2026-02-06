package com.ryuqq.marketplace.adapter.in.rest.sellerapplication.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.sellerapplication.exception.SellerApplicationException;
import com.ryuqq.marketplace.domain.sellerapplication.exception.SellerApplicationNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("SellerApplicationErrorMapper 단위 테스트")
class SellerApplicationErrorMapperTest {

    private final SellerApplicationErrorMapper sut = new SellerApplicationErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("SellerApplicationException을 지원한다")
        void supports_SellerApplicationException_ReturnsTrue() {
            // given
            DomainException ex = new SellerApplicationNotFoundException(1L);

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
        @DisplayName("SellerApplicationNotFoundException을 404 MappedError로 변환한다")
        void map_NotFound_Returns404() {
            // given
            SellerApplicationException ex = new SellerApplicationNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Seller Application Error");
            assertThat(result.detail()).contains("1");
            assertThat(result.type().toString()).startsWith("/errors/seller-application/");
        }
    }
}
