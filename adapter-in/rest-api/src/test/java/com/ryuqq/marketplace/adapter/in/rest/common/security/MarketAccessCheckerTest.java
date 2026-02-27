package com.ryuqq.marketplace.adapter.in.rest.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.ResolveLegacyProductGroupSellerIdUseCase;
import com.ryuqq.marketplace.application.seller.port.in.query.ResolveSellerIdByOrganizationUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.query.ResolveSellerIdBySellerAdminIdUseCase;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

@Tag("unit")
@DisplayName("MarketAccessChecker 단위 테스트")
class MarketAccessCheckerTest {

    private ResolveSellerIdByOrganizationUseCase resolveSellerIdUseCase;
    private ResolveLegacyProductGroupSellerIdUseCase resolveLegacyProductGroupSellerIdUseCase;
    private ResolveSellerIdBySellerAdminIdUseCase resolveSellerIdBySellerAdminIdUseCase;
    private MarketAccessChecker sut;

    @BeforeEach
    void setUp() {
        resolveSellerIdUseCase = mock(ResolveSellerIdByOrganizationUseCase.class);
        resolveLegacyProductGroupSellerIdUseCase =
                mock(ResolveLegacyProductGroupSellerIdUseCase.class);
        resolveSellerIdBySellerAdminIdUseCase = mock(ResolveSellerIdBySellerAdminIdUseCase.class);
        sut =
                new TestableMarketAccessChecker(
                        resolveSellerIdUseCase,
                        resolveLegacyProductGroupSellerIdUseCase,
                        resolveSellerIdBySellerAdminIdUseCase);
    }

    @Nested
    @DisplayName("isSellerOwnerOr() - 셀러 소유자 검증")
    class IsSellerOwnerOrTest {

        @Test
        @DisplayName("SUPER_ADMIN이면 true를 반환한다")
        void superAdmin_ReturnsTrue() {
            // given
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            true,
                            false,
                            "org1");

            // when
            boolean result = sut.isSellerOwnerOr(1L, "seller:write");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("organizationId로 조회한 sellerId가 일치하면 true를 반환한다")
        void ownerMatch_ReturnsTrue() {
            // given
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            false,
                            "org1");
            when(resolveSellerIdUseCase.execute("org1")).thenReturn(Optional.of(100L));

            // when
            boolean result = sut.isSellerOwnerOr(100L, "seller:write");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("소유자가 아니고 권한이 있으면 true를 반환한다")
        void notOwnerButHasPermission_ReturnsTrue() {
            // given
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            true,
                            "org1");
            when(resolveSellerIdUseCase.execute("org1")).thenReturn(Optional.of(200L));

            // when
            boolean result = sut.isSellerOwnerOr(100L, "seller:write");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("소유자가 아니고 권한도 없으면 false를 반환한다")
        void notOwnerNoPermission_ReturnsFalse() {
            // given
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            false,
                            "org1");
            when(resolveSellerIdUseCase.execute("org1")).thenReturn(Optional.of(200L));

            // when
            boolean result = sut.isSellerOwnerOr(100L, "seller:write");

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("organizationId가 null이면 권한으로 fallback한다")
        void nullOrganizationId_FallbackToPermission() {
            // given
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            true,
                            null);

            // when
            boolean result = sut.isSellerOwnerOr(100L, "seller:write");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("organizationId가 blank이면 권한으로 fallback한다")
        void blankOrganizationId_FallbackToPermission() {
            // given
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            false,
                            "  ");

            // when
            boolean result = sut.isSellerOwnerOr(100L, "seller:write");

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("sellerId 조회 결과가 empty이면 권한으로 fallback한다")
        void emptyResolvedSellerId_FallbackToPermission() {
            // given
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            true,
                            "org1");
            when(resolveSellerIdUseCase.execute("org1")).thenReturn(Optional.empty());

            // when
            boolean result = sut.isSellerOwnerOr(100L, "seller:write");

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("resolveCurrentSellerId() - 현재 셀러 ID 조회")
    class ResolveCurrentSellerIdTest {

        @Test
        @DisplayName("sellerId가 존재하면 반환한다")
        void exists_ReturnsSellerId() {
            // given
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            false,
                            "org1");
            when(resolveSellerIdUseCase.execute("org1")).thenReturn(Optional.of(42L));

            // when
            long result = sut.resolveCurrentSellerId();

            // then
            assertThat(result).isEqualTo(42L);
        }

        @Test
        @DisplayName("sellerId가 없으면 AccessDeniedException을 발생시킨다")
        void notExists_ThrowsAccessDenied() {
            // given
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            false,
                            "org1");
            when(resolveSellerIdUseCase.execute("org1")).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.resolveCurrentSellerId())
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("canManageSeller() / canManageProduct()")
    class PermissionCheckTest {

        @Test
        @DisplayName("seller:write 권한이 있으면 canManageSeller는 true를 반환한다")
        void canManageSeller_WithPermission_ReturnsTrue() {
            // given
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            true,
                            "org1");

            // when & then
            assertThat(sut.canManageSeller()).isTrue();
        }

        @Test
        @DisplayName("product:write 권한이 있으면 canManageProduct는 true를 반환한다")
        void canManageProduct_WithPermission_ReturnsTrue() {
            // given
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            true,
                            "org1");

            // when & then
            assertThat(sut.canManageProduct()).isTrue();
        }

        @Test
        @DisplayName("권한이 없으면 canManageSeller는 false를 반환한다")
        void canManageSeller_WithoutPermission_ReturnsFalse() {
            // given
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            false,
                            "org1");

            // when & then
            assertThat(sut.canManageSeller()).isFalse();
        }
    }

    @Nested
    @DisplayName("isSellerAdminOwnerOrSuperAdmin() - 셀러 관리자 소속 검증 (단건)")
    class IsSellerAdminOwnerOrSuperAdminTest {

        @Test
        @DisplayName("SUPER_ADMIN이면 true를 반환한다")
        void superAdmin_ReturnsTrue() {
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            true,
                            false,
                            "org1");

            assertThat(sut.isSellerAdminOwnerOrSuperAdmin("admin-1")).isTrue();
        }

        @Test
        @DisplayName("같은 셀러 소속이면 true를 반환한다")
        void sameSeller_ReturnsTrue() {
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            false,
                            "org1");
            when(resolveSellerIdUseCase.execute("org1")).thenReturn(Optional.of(100L));
            when(resolveSellerIdBySellerAdminIdUseCase.execute("admin-1"))
                    .thenReturn(Optional.of(100L));

            assertThat(sut.isSellerAdminOwnerOrSuperAdmin("admin-1")).isTrue();
        }

        @Test
        @DisplayName("다른 셀러 소속이면 false를 반환한다")
        void differentSeller_ReturnsFalse() {
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            false,
                            "org1");
            when(resolveSellerIdUseCase.execute("org1")).thenReturn(Optional.of(100L));
            when(resolveSellerIdBySellerAdminIdUseCase.execute("admin-1"))
                    .thenReturn(Optional.of(200L));

            assertThat(sut.isSellerAdminOwnerOrSuperAdmin("admin-1")).isFalse();
        }

        @Test
        @DisplayName("organizationId가 없으면 false를 반환한다")
        void noOrganization_ReturnsFalse() {
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            false,
                            null);

            assertThat(sut.isSellerAdminOwnerOrSuperAdmin("admin-1")).isFalse();
        }
    }

    @Nested
    @DisplayName("isSellerAdminBulkOwnerOrSuperAdmin() - 셀러 관리자 소속 검증 (일괄)")
    class IsSellerAdminBulkOwnerOrSuperAdminTest {

        @Test
        @DisplayName("SUPER_ADMIN이면 true를 반환한다")
        void superAdmin_ReturnsTrue() {
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            true,
                            false,
                            "org1");

            assertThat(sut.isSellerAdminBulkOwnerOrSuperAdmin(List.of("a1", "a2"))).isTrue();
        }

        @Test
        @DisplayName("모두 같은 셀러 소속이면 true를 반환한다")
        void allSameSeller_ReturnsTrue() {
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            false,
                            "org1");
            when(resolveSellerIdUseCase.execute("org1")).thenReturn(Optional.of(100L));
            when(resolveSellerIdBySellerAdminIdUseCase.resolveIfAllSameSeller(List.of("a1", "a2")))
                    .thenReturn(Optional.of(100L));

            assertThat(sut.isSellerAdminBulkOwnerOrSuperAdmin(List.of("a1", "a2"))).isTrue();
        }

        @Test
        @DisplayName("서로 다른 셀러 소속이면 false를 반환한다")
        void mixedSellers_ReturnsFalse() {
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            false,
                            "org1");
            when(resolveSellerIdUseCase.execute("org1")).thenReturn(Optional.of(100L));
            when(resolveSellerIdBySellerAdminIdUseCase.resolveIfAllSameSeller(List.of("a1", "a2")))
                    .thenReturn(Optional.empty());

            assertThat(sut.isSellerAdminBulkOwnerOrSuperAdmin(List.of("a1", "a2"))).isFalse();
        }

        @Test
        @DisplayName("대상 셀러와 내 셀러가 다르면 false를 반환한다")
        void differentFromMySeller_ReturnsFalse() {
            sut =
                    new TestableMarketAccessChecker(
                            resolveSellerIdUseCase,
                            resolveLegacyProductGroupSellerIdUseCase,
                            resolveSellerIdBySellerAdminIdUseCase,
                            false,
                            false,
                            "org1");
            when(resolveSellerIdUseCase.execute("org1")).thenReturn(Optional.of(100L));
            when(resolveSellerIdBySellerAdminIdUseCase.resolveIfAllSameSeller(List.of("a1", "a2")))
                    .thenReturn(Optional.of(200L));

            assertThat(sut.isSellerAdminBulkOwnerOrSuperAdmin(List.of("a1", "a2"))).isFalse();
        }
    }

    /**
     * BaseAccessChecker의 protected 메서드를 오버라이드하여 테스트 가능하게 만든 서브클래스. Spring Security 컨텍스트 없이 단위 테스트를
     * 수행한다.
     */
    private static class TestableMarketAccessChecker extends MarketAccessChecker {

        private final boolean isSuperAdmin;
        private final boolean hasAnyPermission;
        private final String organizationId;

        TestableMarketAccessChecker(
                ResolveSellerIdByOrganizationUseCase resolveSellerIdUseCase,
                ResolveLegacyProductGroupSellerIdUseCase resolveLegacyProductGroupSellerIdUseCase,
                ResolveSellerIdBySellerAdminIdUseCase resolveSellerIdBySellerAdminIdUseCase) {
            this(
                    resolveSellerIdUseCase,
                    resolveLegacyProductGroupSellerIdUseCase,
                    resolveSellerIdBySellerAdminIdUseCase,
                    false,
                    false,
                    null);
        }

        TestableMarketAccessChecker(
                ResolveSellerIdByOrganizationUseCase resolveSellerIdUseCase,
                ResolveLegacyProductGroupSellerIdUseCase resolveLegacyProductGroupSellerIdUseCase,
                ResolveSellerIdBySellerAdminIdUseCase resolveSellerIdBySellerAdminIdUseCase,
                boolean isSuperAdmin,
                boolean hasAnyPermission,
                String organizationId) {
            super(
                    resolveSellerIdUseCase,
                    resolveLegacyProductGroupSellerIdUseCase,
                    resolveSellerIdBySellerAdminIdUseCase);
            this.isSuperAdmin = isSuperAdmin;
            this.hasAnyPermission = hasAnyPermission;
            this.organizationId = organizationId;
        }

        @Override
        public boolean superAdmin() {
            return isSuperAdmin;
        }

        @Override
        public boolean hasPermission(String permission) {
            return hasAnyPermission;
        }

        @Override
        public String getCurrentOrganizationId() {
            return organizationId;
        }
    }
}
