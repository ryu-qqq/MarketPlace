package com.ryuqq.marketplace.adapter.in.rest.auth.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.in.rest.auth.AuthApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.auth.dto.response.MyInfoApiResponse;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("AuthQueryApiMapper 단위 테스트")
class AuthQueryApiMapperTest {

    private AuthQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AuthQueryApiMapper();
    }

    @Nested
    @DisplayName("extractToken() - Authorization 헤더에서 토큰 추출")
    class ExtractTokenTest {

        @Test
        @DisplayName("Bearer 토큰을 추출한다")
        void extractToken_BearerToken_ReturnsToken() {
            // given
            String authorization = "Bearer eyJhbGciOiJIUzI1NiJ9.token";

            // when
            String token = mapper.extractToken(authorization);

            // then
            assertThat(token).isEqualTo("eyJhbGciOiJIUzI1NiJ9.token");
        }

        @Test
        @DisplayName("null 헤더는 IllegalArgumentException을 발생시킨다")
        void extractToken_NullHeader_ThrowsIllegalArgumentException() {
            // when & then
            assertThatThrownBy(() -> mapper.extractToken(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid Authorization header");
        }

        @Test
        @DisplayName("Bearer 접두사가 없으면 IllegalArgumentException을 발생시킨다")
        void extractToken_NoBearerPrefix_ThrowsIllegalArgumentException() {
            // given
            String authorization = "Basic dXNlcjpwYXNz";

            // when & then
            assertThatThrownBy(() -> mapper.extractToken(authorization))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid Authorization header");
        }

        @Test
        @DisplayName("빈 문자열은 IllegalArgumentException을 발생시킨다")
        void extractToken_EmptyString_ThrowsIllegalArgumentException() {
            // when & then
            assertThatThrownBy(() -> mapper.extractToken(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid Authorization header");
        }
    }

    @Nested
    @DisplayName("toResponse(MyInfoResult) - 내 정보 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("MyInfoResult를 MyInfoApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            MyInfoResult result = AuthApiFixtures.myInfoResult();

            // when
            MyInfoApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.userId()).isEqualTo(AuthApiFixtures.DEFAULT_USER_ID);
            assertThat(response.email()).isEqualTo(AuthApiFixtures.DEFAULT_EMAIL);
            assertThat(response.name()).isEqualTo(AuthApiFixtures.DEFAULT_NAME);
            assertThat(response.tenantId()).isEqualTo(AuthApiFixtures.DEFAULT_TENANT_ID);
            assertThat(response.tenantName()).isEqualTo(AuthApiFixtures.DEFAULT_TENANT_NAME);
            assertThat(response.organizationId()).isEqualTo(AuthApiFixtures.DEFAULT_ORG_ID);
            assertThat(response.organizationName()).isEqualTo(AuthApiFixtures.DEFAULT_ORG_NAME);
        }

        @Test
        @DisplayName("역할 목록을 변환한다")
        void toResponse_ConvertsRoles_ReturnsRoleResponses() {
            // given
            MyInfoResult result = AuthApiFixtures.myInfoResult();

            // when
            MyInfoApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.roles()).hasSize(2);
            assertThat(response.roles().get(0).id()).isEqualTo("role-1");
            assertThat(response.roles().get(0).name()).isEqualTo("ADMIN");
            assertThat(response.roles().get(1).id()).isEqualTo("role-2");
            assertThat(response.roles().get(1).name()).isEqualTo("MANAGER");
        }

        @Test
        @DisplayName("권한 목록을 변환한다")
        void toResponse_ConvertsPermissions_ReturnsPermissions() {
            // given
            MyInfoResult result = AuthApiFixtures.myInfoResult();

            // when
            MyInfoApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.permissions()).containsExactly("READ", "WRITE", "DELETE");
        }

        @Test
        @DisplayName("역할이 null이면 빈 목록을 반환한다")
        void toResponse_NullRoles_ReturnsEmptyRoles() {
            // given
            MyInfoResult result = AuthApiFixtures.myInfoResultWithNullRoles();

            // when
            MyInfoApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.roles()).isEmpty();
        }

        @Test
        @DisplayName("역할이 빈 목록이면 빈 목록을 반환한다")
        void toResponse_EmptyRoles_ReturnsEmptyRoles() {
            // given
            MyInfoResult result = AuthApiFixtures.myInfoResultWithEmptyRoles();

            // when
            MyInfoApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.roles()).isEmpty();
            assertThat(response.permissions()).isEmpty();
        }
    }
}
