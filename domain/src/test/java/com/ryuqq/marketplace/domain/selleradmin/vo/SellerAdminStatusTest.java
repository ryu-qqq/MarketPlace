package com.ryuqq.marketplace.domain.selleradmin.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@Tag("unit")
@DisplayName("SellerAdminStatus 단위 테스트")
class SellerAdminStatusTest {

    @Nested
    @DisplayName("canLogin() - 로그인 가능 여부")
    class CanLoginTest {

        @Test
        @DisplayName("ACTIVE 상태는 로그인 가능하다")
        void activeCanLogin() {
            assertThat(SellerAdminStatus.ACTIVE.canLogin()).isTrue();
        }

        @ParameterizedTest
        @EnumSource(
                value = SellerAdminStatus.class,
                names = {"PENDING_APPROVAL", "INACTIVE", "SUSPENDED", "REJECTED"})
        @DisplayName("ACTIVE가 아닌 상태는 로그인 불가하다")
        void nonActiveCannotLogin(SellerAdminStatus status) {
            assertThat(status.canLogin()).isFalse();
        }
    }

    @Nested
    @DisplayName("isPendingApproval() - 승인 대기 여부")
    class IsPendingApprovalTest {

        @Test
        @DisplayName("PENDING_APPROVAL 상태는 승인 대기 상태다")
        void pendingApprovalIsPending() {
            assertThat(SellerAdminStatus.PENDING_APPROVAL.isPendingApproval()).isTrue();
        }

        @ParameterizedTest
        @EnumSource(
                value = SellerAdminStatus.class,
                names = {"ACTIVE", "INACTIVE", "SUSPENDED", "REJECTED"})
        @DisplayName("PENDING_APPROVAL이 아닌 상태는 승인 대기가 아니다")
        void nonPendingIsNotPending(SellerAdminStatus status) {
            assertThat(status.isPendingApproval()).isFalse();
        }
    }

    @Nested
    @DisplayName("canApprove() - 승인 가능 여부")
    class CanApproveTest {

        @Test
        @DisplayName("PENDING_APPROVAL 상태에서만 승인 가능하다")
        void pendingApprovalCanApprove() {
            assertThat(SellerAdminStatus.PENDING_APPROVAL.canApprove()).isTrue();
        }

        @ParameterizedTest
        @EnumSource(
                value = SellerAdminStatus.class,
                names = {"ACTIVE", "INACTIVE", "SUSPENDED", "REJECTED"})
        @DisplayName("PENDING_APPROVAL이 아닌 상태는 승인 불가하다")
        void nonPendingCannotApprove(SellerAdminStatus status) {
            assertThat(status.canApprove()).isFalse();
        }
    }

    @Nested
    @DisplayName("canReject() - 거절 가능 여부")
    class CanRejectTest {

        @Test
        @DisplayName("PENDING_APPROVAL 상태에서만 거절 가능하다")
        void pendingApprovalCanReject() {
            assertThat(SellerAdminStatus.PENDING_APPROVAL.canReject()).isTrue();
        }

        @ParameterizedTest
        @EnumSource(
                value = SellerAdminStatus.class,
                names = {"ACTIVE", "INACTIVE", "SUSPENDED", "REJECTED"})
        @DisplayName("PENDING_APPROVAL이 아닌 상태는 거절 불가하다")
        void nonPendingCannotReject(SellerAdminStatus status) {
            assertThat(status.canReject()).isFalse();
        }
    }

    @Nested
    @DisplayName("description() - 설명 반환")
    class DescriptionTest {

        @Test
        @DisplayName("PENDING_APPROVAL의 설명은 '승인대기'다")
        void pendingApprovalDescription() {
            assertThat(SellerAdminStatus.PENDING_APPROVAL.description()).isEqualTo("승인대기");
        }

        @Test
        @DisplayName("ACTIVE의 설명은 '활성'이다")
        void activeDescription() {
            assertThat(SellerAdminStatus.ACTIVE.description()).isEqualTo("활성");
        }

        @Test
        @DisplayName("INACTIVE의 설명은 '비활성'이다")
        void inactiveDescription() {
            assertThat(SellerAdminStatus.INACTIVE.description()).isEqualTo("비활성");
        }

        @Test
        @DisplayName("SUSPENDED의 설명은 '정지'다")
        void suspendedDescription() {
            assertThat(SellerAdminStatus.SUSPENDED.description()).isEqualTo("정지");
        }

        @Test
        @DisplayName("REJECTED의 설명은 '거절'이다")
        void rejectedDescription() {
            assertThat(SellerAdminStatus.REJECTED.description()).isEqualTo("거절");
        }
    }

    @Test
    @DisplayName("SellerAdminStatus 값이 5개 존재한다")
    void hasExpectedValues() {
        assertThat(SellerAdminStatus.values()).hasSize(5);
    }
}
