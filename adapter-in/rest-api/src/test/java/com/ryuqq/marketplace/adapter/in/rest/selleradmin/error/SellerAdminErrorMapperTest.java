package com.ryuqq.marketplace.adapter.in.rest.selleradmin.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.seller.exception.SellerException;
import com.ryuqq.marketplace.domain.seller.exception.SellerNotFoundException;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("SellerAdminErrorMapper 단위 테스트")
class SellerAdminErrorMapperTest {

    private final SellerAdminErrorMapper sut = new SellerAdminErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("SellerException을 지원한다")
        void supports_SellerException_ReturnsTrue() {
            // given
            DomainException ex = new SellerNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("SellerAdminException은 지원하지 않는다")
        void supports_SellerAdminException_ReturnsFalse() {
            // given
            DomainException ex = new SellerAdminNotFoundException("test-id");

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isFalse();
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
        @DisplayName("SellerNotFoundException을 404 MappedError로 변환한다")
        void map_NotFound_Returns404() {
            // given
            SellerException ex = new SellerNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Seller Admin Error");
            assertThat(result.detail()).contains("1");
            assertThat(result.type().toString()).startsWith("/errors/seller-admin/");
        }
    }
}
