package com.ryuqq.marketplace.adapter.in.rest.productnotice.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.productnotice.exception.ProductNoticeNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("ProductNoticeErrorMapper 단위 테스트")
class ProductNoticeErrorMapperTest {

    private final ProductNoticeErrorMapper sut = new ProductNoticeErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("PRDNTC- 코드를 가진 ProductNoticeNotFoundException을 지원한다")
        void supports_ProductNoticeNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new ProductNoticeNotFoundException(1L);

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

        @Test
        @DisplayName("PRDNTC- 접두사가 없는 코드를 가진 예외는 지원하지 않는다")
        void supports_ExceptionWithoutPrdntcPrefix_ReturnsFalse() {
            // given
            DomainException ex =
                    new DomainException(
                            new com.ryuqq.marketplace.domain.common.exception.ErrorCode() {
                                @Override
                                public String getCode() {
                                    return "PRDGRP-001";
                                }

                                @Override
                                public int getHttpStatus() {
                                    return 404;
                                }

                                @Override
                                public String getMessage() {
                                    return "Product Group error";
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
        @DisplayName("ProductNoticeNotFoundException을 404 MappedError로 변환한다")
        void map_ProductNoticeNotFoundException_Returns404() {
            // given
            ProductNoticeNotFoundException ex = new ProductNoticeNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Product Notice Not Found");
            assertThat(result.type().toString()).startsWith("/errors/product-notice/");
            assertThat(result.type().toString()).contains("prdntc-001");
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_ProductNoticeNotFoundException_TypeUriContainsLowercaseCode() {
            // given
            ProductNoticeNotFoundException ex = new ProductNoticeNotFoundException(42L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString()).isEqualTo("/errors/product-notice/prdntc-001");
        }
    }
}
