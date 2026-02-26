package com.ryuqq.marketplace.adapter.in.rest.legacy.auth.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("LegacyAuthErrorMapper 단위 테스트")
class LegacyAuthErrorMapperTest {

    private final LegacyAuthErrorMapper sut = new LegacyAuthErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("AUTH_ 코드를 가진 예외를 지원한다")
        void supports_AuthCodeException_ReturnsTrue() {
            // given
            DomainException ex = createDomainException("AUTH_001", 401);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("AUTH_LOGIN_FAILED 코드를 가진 예외를 지원한다")
        void supports_AuthLoginFailedCodeException_ReturnsTrue() {
            // given
            DomainException ex = createDomainException("AUTH_LOGIN_FAILED", 401);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("SELLER_ 코드를 가진 예외는 지원하지 않는다")
        void supports_SellerCodeException_ReturnsFalse() {
            // given
            DomainException ex = createDomainException("SELLER_NOT_FOUND", 404);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("다른 도메인 예외는 지원하지 않는다")
        void supports_OtherDomainException_ReturnsFalse() {
            // given
            DomainException ex = createDomainException("PRDGRP-001", 404);

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
        @DisplayName("AUTH_ 예외를 올바른 HTTP 상태로 변환한다")
        void map_AuthException_ReturnsCorrectStatus() {
            // given
            DomainException ex = createDomainException("AUTH_001", 401);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("변환된 title이 'Legacy Authentication Error'이다")
        void map_AuthException_ReturnsCorrectTitle() {
            // given
            DomainException ex = createDomainException("AUTH_001", 401);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.title()).isEqualTo("Legacy Authentication Error");
        }

        @Test
        @DisplayName("에러 타입 URI에 '/errors/legacy-auth/' 접두사가 포함된다")
        void map_AuthException_TypeUriContainsPrefix() {
            // given
            DomainException ex = createDomainException("AUTH_001", 401);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString()).startsWith("/errors/legacy-auth/");
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_AuthException_TypeUriContainsLowercaseCode() {
            // given
            DomainException ex = createDomainException("AUTH_001", 401);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString()).isEqualTo("/errors/legacy-auth/auth_001");
        }

        @Test
        @DisplayName("400 상태 예외는 BAD_REQUEST로 변환된다")
        void map_AuthException400_ReturnsBadRequest() {
            // given
            DomainException ex = createDomainException("AUTH_INVALID_REQUEST", 400);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    private static DomainException createDomainException(String code, int httpStatus) {
        return new DomainException(
                new ErrorCode() {
                    @Override
                    public String getCode() {
                        return code;
                    }

                    @Override
                    public int getHttpStatus() {
                        return httpStatus;
                    }

                    @Override
                    public String getMessage() {
                        return "Test error message";
                    }
                }) {};
    }
}
