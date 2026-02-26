package com.ryuqq.marketplace.adapter.in.rest.auth.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("AuthErrorMapper 단위 테스트")
class AuthErrorMapperTest {

    private final AuthErrorMapper sut = new AuthErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("현재 어떤 예외도 지원하지 않는다")
        void supports_AnyDomainException_ReturnsFalse() {
            // given
            DomainException ex =
                    new DomainException(
                            new com.ryuqq.marketplace.domain.common.exception.ErrorCode() {
                                @Override
                                public String getCode() {
                                    return "AUTH-001";
                                }

                                @Override
                                public int getHttpStatus() {
                                    return 401;
                                }

                                @Override
                                public String getMessage() {
                                    return "Authentication failed";
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
        @DisplayName("DomainException을 MappedError로 변환한다")
        void map_DomainException_ReturnsMappedError() {
            // given
            DomainException ex =
                    new DomainException(
                            new com.ryuqq.marketplace.domain.common.exception.ErrorCode() {
                                @Override
                                public String getCode() {
                                    return "AUTH-001";
                                }

                                @Override
                                public int getHttpStatus() {
                                    return 401;
                                }

                                @Override
                                public String getMessage() {
                                    return "Authentication failed";
                                }
                            }) {};

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(result.title()).isEqualTo("Authentication Error");
            assertThat(result.type().toString()).startsWith("/errors/auth/");
        }
    }
}
