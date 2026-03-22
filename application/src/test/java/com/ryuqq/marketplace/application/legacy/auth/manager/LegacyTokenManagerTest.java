package com.ryuqq.marketplace.application.legacy.auth.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.auth.LegacyAuthFixtures;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacyTokenResult;
import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacyTokenClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyTokenManager 단위 테스트")
class LegacyTokenManagerTest {

    @InjectMocks private LegacyTokenManager sut;

    @Mock private LegacyTokenClient tokenClient;

    @Nested
    @DisplayName("generateToken() - 토큰 발급")
    class GenerateTokenTest {

        @Test
        @DisplayName("이메일, 셀러ID, 역할로 토큰을 발급한다")
        void generateToken_ValidParams_ReturnsTokenResult() {
            // given
            String email = LegacyAuthFixtures.DEFAULT_EMAIL;
            long sellerId = LegacyAuthFixtures.DEFAULT_SELLER_ID;
            String roleType = LegacyAuthFixtures.DEFAULT_ROLE_TYPE;
            LegacyTokenResult expected = LegacyAuthFixtures.tokenResult();

            given(tokenClient.generateToken(email, sellerId, roleType)).willReturn(expected);

            // when
            LegacyTokenResult result = sut.generateToken(email, sellerId, roleType);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.accessToken()).isEqualTo(LegacyAuthFixtures.DEFAULT_ACCESS_TOKEN);
            assertThat(result.refreshToken()).isEqualTo(LegacyAuthFixtures.DEFAULT_REFRESH_TOKEN);
            assertThat(result.email()).isEqualTo(email);
            assertThat(result.expiresInSeconds()).isEqualTo(LegacyAuthFixtures.DEFAULT_EXPIRES_IN_SECONDS);
            then(tokenClient).should().generateToken(email, sellerId, roleType);
        }
    }

    @Nested
    @DisplayName("extractSubject() - subject 추출")
    class ExtractSubjectTest {

        @Test
        @DisplayName("토큰에서 subject(이메일)를 추출한다")
        void extractSubject_ValidToken_ReturnsEmail() {
            // given
            String token = LegacyAuthFixtures.DEFAULT_ACCESS_TOKEN;
            String expectedEmail = LegacyAuthFixtures.DEFAULT_EMAIL;

            given(tokenClient.extractSubject(token)).willReturn(expectedEmail);

            // when
            String result = sut.extractSubject(token);

            // then
            assertThat(result).isEqualTo(expectedEmail);
            then(tokenClient).should().extractSubject(token);
        }
    }

    @Nested
    @DisplayName("isValid() - 토큰 유효성 검증")
    class IsValidTest {

        @Test
        @DisplayName("유효한 토큰이면 true를 반환한다")
        void isValid_ValidToken_ReturnsTrue() {
            // given
            String token = LegacyAuthFixtures.DEFAULT_ACCESS_TOKEN;

            given(tokenClient.isValid(token)).willReturn(true);

            // when
            boolean result = sut.isValid(token);

            // then
            assertThat(result).isTrue();
            then(tokenClient).should().isValid(token);
        }

        @Test
        @DisplayName("유효하지 않은 토큰이면 false를 반환한다")
        void isValid_InvalidToken_ReturnsFalse() {
            // given
            String token = "invalid.token";

            given(tokenClient.isValid(token)).willReturn(false);

            // when
            boolean result = sut.isValid(token);

            // then
            assertThat(result).isFalse();
            then(tokenClient).should().isValid(token);
        }
    }

    @Nested
    @DisplayName("isExpired() - 토큰 만료 여부 확인")
    class IsExpiredTest {

        @Test
        @DisplayName("만료된 토큰이면 true를 반환한다")
        void isExpired_ExpiredToken_ReturnsTrue() {
            // given
            String token = "expired.token";

            given(tokenClient.isExpired(token)).willReturn(true);

            // when
            boolean result = sut.isExpired(token);

            // then
            assertThat(result).isTrue();
            then(tokenClient).should().isExpired(token);
        }

        @Test
        @DisplayName("만료되지 않은 토큰이면 false를 반환한다")
        void isExpired_NotExpiredToken_ReturnsFalse() {
            // given
            String token = LegacyAuthFixtures.DEFAULT_ACCESS_TOKEN;

            given(tokenClient.isExpired(token)).willReturn(false);

            // when
            boolean result = sut.isExpired(token);

            // then
            assertThat(result).isFalse();
            then(tokenClient).should().isExpired(token);
        }
    }

    @Nested
    @DisplayName("extractSellerId() - 셀러 ID 추출")
    class ExtractSellerIdTest {

        @Test
        @DisplayName("토큰에서 셀러 ID를 추출한다")
        void extractSellerId_ValidToken_ReturnsSellerId() {
            // given
            String token = LegacyAuthFixtures.DEFAULT_ACCESS_TOKEN;
            long expectedSellerId = LegacyAuthFixtures.DEFAULT_SELLER_ID;

            given(tokenClient.extractSellerId(token)).willReturn(expectedSellerId);

            // when
            long result = sut.extractSellerId(token);

            // then
            assertThat(result).isEqualTo(expectedSellerId);
            then(tokenClient).should().extractSellerId(token);
        }
    }

    @Nested
    @DisplayName("extractRole() - 역할 추출")
    class ExtractRoleTest {

        @Test
        @DisplayName("토큰에서 역할을 추출한다")
        void extractRole_ValidToken_ReturnsRole() {
            // given
            String token = LegacyAuthFixtures.DEFAULT_ACCESS_TOKEN;
            String expectedRole = LegacyAuthFixtures.DEFAULT_ROLE_TYPE;

            given(tokenClient.extractRole(token)).willReturn(expectedRole);

            // when
            String result = sut.extractRole(token);

            // then
            assertThat(result).isEqualTo(expectedRole);
            then(tokenClient).should().extractRole(token);
        }
    }
}
