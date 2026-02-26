package com.ryuqq.marketplace.domain.selleradmin.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import com.ryuqq.marketplace.domain.selleradmin.vo.AdminName;
import com.ryuqq.marketplace.domain.selleradmin.vo.LoginId;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAdmin Aggregate 테스트")
class SellerAdminTest {

    private static final String TEST_ADMIN_ID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";

    @Nested
    @DisplayName("forNew() - 신규 관리자 생성")
    class ForNewTest {

        @Test
        @DisplayName("SellerId와 함께 새 관리자를 생성한다")
        void createNewAdminWithSellerId() {
            // given
            SellerAdminId id = SellerAdminId.forNew(TEST_ADMIN_ID);
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            String authUserId = "auth-123";
            LoginId loginId = LoginId.of("admin@test.com");
            AdminName name = AdminName.of("홍길동");
            PhoneNumber phoneNumber = CommonVoFixtures.defaultPhoneNumber();
            Instant now = CommonVoFixtures.now();

            // when
            SellerAdmin admin =
                    SellerAdmin.forNew(id, sellerId, authUserId, loginId, name, phoneNumber, now);

            // then
            assertThat(admin.sellerId()).isEqualTo(sellerId);
            assertThat(admin.authUserId()).isEqualTo(authUserId);
            assertThat(admin.loginId()).isEqualTo(loginId);
            assertThat(admin.name()).isEqualTo(name);
            assertThat(admin.phoneNumber()).isEqualTo(phoneNumber);
            assertThat(admin.status()).isEqualTo(SellerAdminStatus.ACTIVE);
            assertThat(admin.isActive()).isTrue();
            assertThat(admin.canLogin()).isTrue();
        }

        @Test
        @DisplayName("SellerId 없이 새 관리자를 생성한다")
        void createNewAdminWithoutSellerId() {
            // given
            SellerAdminId id = SellerAdminId.forNew(TEST_ADMIN_ID);
            String authUserId = "auth-123";
            LoginId loginId = LoginId.of("admin@test.com");
            AdminName name = AdminName.of("홍길동");
            PhoneNumber phoneNumber = CommonVoFixtures.defaultPhoneNumber();
            Instant now = CommonVoFixtures.now();

            // when
            SellerAdmin admin = SellerAdmin.forNew(id, authUserId, loginId, name, phoneNumber, now);

            // then
            assertThat(admin.sellerId()).isNull();
            assertThat(admin.sellerIdValue()).isNull();
            assertThat(admin.status()).isEqualTo(SellerAdminStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("forApplication() - 가입 신청 관리자 생성")
    class ForApplicationTest {

        @Test
        @DisplayName("승인 대기 상태의 관리자를 생성한다")
        void createApplicationAdmin() {
            // given
            SellerAdminId id = SellerAdminId.forNew(TEST_ADMIN_ID);
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            LoginId loginId = LoginId.of("pending@test.com");
            AdminName name = AdminName.of("김대기");
            PhoneNumber phoneNumber = CommonVoFixtures.phoneNumber("010-9999-8888");
            Instant now = CommonVoFixtures.now();

            // when
            SellerAdmin admin =
                    SellerAdmin.forApplication(id, sellerId, loginId, name, phoneNumber, now);

            // then
            assertThat(admin.sellerId()).isEqualTo(sellerId);
            assertThat(admin.authUserId()).isNull();
            assertThat(admin.loginId()).isEqualTo(loginId);
            assertThat(admin.status()).isEqualTo(SellerAdminStatus.PENDING_APPROVAL);
            assertThat(admin.isPendingApproval()).isTrue();
            assertThat(admin.canLogin()).isFalse();
            assertThat(admin.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("approve() - 가입 승인")
    class ApproveTest {

        @Test
        @DisplayName("승인 대기 상태의 관리자를 승인한다")
        void approvePendingAdmin() {
            // given
            SellerAdmin admin = SellerFixtures.pendingApprovalSellerAdmin();
            String authUserId = "auth-new-123";
            Instant now = CommonVoFixtures.now();

            // when
            admin.approveWithAuthUserId(authUserId, now);

            // then
            assertThat(admin.status()).isEqualTo(SellerAdminStatus.ACTIVE);
            assertThat(admin.authUserId()).isEqualTo(authUserId);
            assertThat(admin.updatedAt()).isEqualTo(now);
            assertThat(admin.isActive()).isTrue();
            assertThat(admin.canLogin()).isTrue();
        }

        @Test
        @DisplayName("이미 활성 상태인 관리자를 승인하면 예외가 발생한다")
        void approveActiveAdmin_ThrowsException() {
            // given
            SellerAdmin admin = SellerFixtures.activeSellerAdmin();

            // when & then
            assertThatThrownBy(
                            () -> admin.approveWithAuthUserId("auth-123", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("승인할 수 없는 상태");
        }

        @Test
        @DisplayName("거절된 관리자를 승인하면 예외가 발생한다")
        void approveRejectedAdmin_ThrowsException() {
            // given
            SellerAdmin admin = SellerFixtures.rejectedSellerAdmin();

            // when & then
            assertThatThrownBy(
                            () -> admin.approveWithAuthUserId("auth-123", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("승인할 수 없는 상태");
        }
    }

    @Nested
    @DisplayName("reject() - 가입 거절")
    class RejectTest {

        @Test
        @DisplayName("승인 대기 상태의 관리자를 거절한다")
        void rejectPendingAdmin() {
            // given
            SellerAdmin admin = SellerFixtures.pendingApprovalSellerAdmin();
            Instant now = CommonVoFixtures.now();

            // when
            admin.reject(now);

            // then
            assertThat(admin.status()).isEqualTo(SellerAdminStatus.REJECTED);
            assertThat(admin.updatedAt()).isEqualTo(now);
            assertThat(admin.isRejected()).isTrue();
            assertThat(admin.canLogin()).isFalse();
        }

        @Test
        @DisplayName("이미 활성 상태인 관리자를 거절하면 예외가 발생한다")
        void rejectActiveAdmin_ThrowsException() {
            // given
            SellerAdmin admin = SellerFixtures.activeSellerAdmin();

            // when & then
            assertThatThrownBy(() -> admin.reject(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("거절할 수 없는 상태");
        }
    }

    @Nested
    @DisplayName("assignSellerId() - SellerId 할당")
    class AssignSellerIdTest {

        @Test
        @DisplayName("SellerId를 할당한다")
        void assignSellerId() {
            // given
            SellerAdmin admin = SellerFixtures.newSellerAdminWithoutSellerId();
            SellerId sellerId = SellerId.of(100L);

            // when
            admin.assignSellerId(sellerId);

            // then
            assertThat(admin.sellerId()).isEqualTo(sellerId);
            assertThat(admin.sellerIdValue()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("상태 변경 메서드")
    class StatusChangeTest {

        @Test
        @DisplayName("관리자를 비활성화한다")
        void deactivate() {
            // given
            SellerAdmin admin = SellerFixtures.activeSellerAdmin();
            Instant now = CommonVoFixtures.now();

            // when
            admin.deactivate(now);

            // then
            assertThat(admin.status()).isEqualTo(SellerAdminStatus.INACTIVE);
            assertThat(admin.canLogin()).isFalse();
        }

        @Test
        @DisplayName("관리자를 정지한다")
        void suspend() {
            // given
            SellerAdmin admin = SellerFixtures.activeSellerAdmin();
            Instant now = CommonVoFixtures.now();

            // when
            admin.suspend(now);

            // then
            assertThat(admin.status()).isEqualTo(SellerAdminStatus.SUSPENDED);
            assertThat(admin.canLogin()).isFalse();
        }

        @Test
        @DisplayName("관리자를 활성화한다")
        void activate() {
            // given
            SellerAdmin admin = SellerFixtures.suspendedSellerAdmin();
            Instant now = CommonVoFixtures.now();

            // when
            admin.activate(now);

            // then
            assertThat(admin.status()).isEqualTo(SellerAdminStatus.ACTIVE);
            assertThat(admin.canLogin()).isTrue();
        }

        @Test
        @DisplayName("관리자를 삭제한다")
        void delete() {
            // given
            SellerAdmin admin = SellerFixtures.activeSellerAdmin();
            Instant now = CommonVoFixtures.now();

            // when
            admin.delete(now);

            // then
            assertThat(admin.isDeleted()).isTrue();
            assertThat(admin.deletedAt()).isEqualTo(now);
            assertThat(admin.canLogin()).isFalse();
        }
    }

    @Nested
    @DisplayName("updateInfo() - 기본 정보 수정")
    class UpdateInfoTest {

        @Test
        @DisplayName("관리자 정보를 수정한다")
        void updateInfo() {
            // given
            SellerAdmin admin = SellerFixtures.activeSellerAdmin();
            AdminName newName = AdminName.of("김수정");
            PhoneNumber newPhone = PhoneNumber.of("010-0000-0000");
            Instant now = CommonVoFixtures.now();

            // when
            admin.updateInfo(newName, newPhone, now);

            // then
            assertThat(admin.name()).isEqualTo(newName);
            assertThat(admin.phoneNumber()).isEqualTo(newPhone);
            assertThat(admin.updatedAt()).isEqualTo(now);
        }
    }
}
