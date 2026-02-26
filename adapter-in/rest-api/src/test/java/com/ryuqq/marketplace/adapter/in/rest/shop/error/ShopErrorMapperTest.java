package com.ryuqq.marketplace.adapter.in.rest.shop.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.shop.exception.ShopAccountIdDuplicateException;
import com.ryuqq.marketplace.domain.shop.exception.ShopException;
import com.ryuqq.marketplace.domain.shop.exception.ShopNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("ShopErrorMapper 단위 테스트")
class ShopErrorMapperTest {

    private final ShopErrorMapper sut = new ShopErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("ShopNotFoundException을 지원한다")
        void supports_ShopNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new ShopNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("ShopAccountIdDuplicateException을 지원한다")
        void supports_ShopAccountIdDuplicateException_ReturnsTrue() {
            // given
            DomainException ex = new ShopAccountIdDuplicateException("test-account");

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
        @DisplayName("ShopNotFoundException(SHP-001)을 404 MappedError로 변환하고 제목이 올바르다")
        void map_ShopNotFoundException_Returns404WithCorrectTitle() {
            // given
            ShopException ex = new ShopNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("외부몰을 찾을 수 없음");
            assertThat(result.type().toString()).startsWith("/errors/shop/");
            assertThat(result.type().toString()).contains("shp-001");
        }

        @Test
        @DisplayName("ShopAccountIdDuplicateException(SHP-003)을 409 MappedError로 변환하고 제목이 올바르다")
        void map_ShopAccountIdDuplicateException_Returns409WithCorrectTitle() {
            // given
            ShopException ex = new ShopAccountIdDuplicateException("test-account");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("계정 ID 중복");
            assertThat(result.type().toString()).startsWith("/errors/shop/");
            assertThat(result.type().toString()).contains("shp-003");
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_ShopNotFoundException_TypeUriContainsLowercaseCode() {
            // given
            ShopException ex = new ShopNotFoundException(42L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString()).isEqualTo("/errors/shop/shp-001");
        }
    }
}
