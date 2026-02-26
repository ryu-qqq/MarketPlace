package com.ryuqq.marketplace.adapter.out.persistence.selleradmin;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminJpaEntity;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.time.Instant;

/**
 * SellerAdminJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 SellerAdminJpaEntity 관련 객체들을 생성합니다.
 */
public final class SellerAdminJpaEntityFixtures {

    private SellerAdminJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_ID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_AUTH_USER_ID = "auth-user-123";
    public static final String DEFAULT_LOGIN_ID = "admin@test.com";
    public static final String DEFAULT_NAME = "홍길동";
    public static final String DEFAULT_PHONE_NUMBER = "010-1234-5678";

    // ===== Entity Fixtures =====

    /** 활성 상태의 셀러 관리자 Entity 생성. */
    public static SellerAdminJpaEntity activeEntity() {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_AUTH_USER_ID,
                DEFAULT_LOGIN_ID,
                DEFAULT_NAME,
                DEFAULT_PHONE_NUMBER,
                SellerAdminStatus.ACTIVE,
                now,
                now,
                null);
    }

    /** ID를 지정한 활성 상태 셀러 관리자 Entity 생성. loginId는 ID 기반으로 고유하게 생성. */
    public static SellerAdminJpaEntity activeEntity(String id) {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_AUTH_USER_ID,
                id + "@test.com", // ID 기반 고유 loginId
                DEFAULT_NAME,
                DEFAULT_PHONE_NUMBER,
                SellerAdminStatus.ACTIVE,
                now,
                now,
                null);
    }

    /** 셀러 ID를 지정한 활성 상태 셀러 관리자 Entity 생성. */
    public static SellerAdminJpaEntity activeEntityWithSellerId(Long sellerId) {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                DEFAULT_ID,
                sellerId,
                DEFAULT_AUTH_USER_ID,
                DEFAULT_LOGIN_ID,
                DEFAULT_NAME,
                DEFAULT_PHONE_NUMBER,
                SellerAdminStatus.ACTIVE,
                now,
                now,
                null);
    }

    /** 커스텀 로그인 ID를 가진 활성 상태 셀러 관리자 Entity 생성 (신규). */
    public static SellerAdminJpaEntity newActiveEntityWithLoginId(String id, String loginId) {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_AUTH_USER_ID,
                loginId,
                DEFAULT_NAME,
                DEFAULT_PHONE_NUMBER,
                SellerAdminStatus.ACTIVE,
                now,
                now,
                null);
    }

    /** 커스텀 이름과 로그인 ID를 가진 셀러 관리자 Entity 생성. */
    public static SellerAdminJpaEntity newEntityWithNameAndLoginId(
            String id, String name, String loginId) {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_AUTH_USER_ID,
                loginId,
                name,
                DEFAULT_PHONE_NUMBER,
                SellerAdminStatus.ACTIVE,
                now,
                now,
                null);
    }

    /** 승인 대기 상태 셀러 관리자 Entity 생성. */
    public static SellerAdminJpaEntity pendingApprovalEntity() {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f61",
                DEFAULT_SELLER_ID,
                null,
                "pending@test.com",
                "김대기",
                "010-9999-8888",
                SellerAdminStatus.PENDING_APPROVAL,
                now,
                now,
                null);
    }

    /** 승인 대기 상태 셀러 관리자 Entity 생성 (ID 지정). */
    public static SellerAdminJpaEntity pendingApprovalEntity(String id, String loginId) {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                null,
                loginId,
                "김대기",
                "010-9999-8888",
                SellerAdminStatus.PENDING_APPROVAL,
                now,
                now,
                null);
    }

    /** 거절 상태 셀러 관리자 Entity 생성. */
    public static SellerAdminJpaEntity rejectedEntity() {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f62",
                DEFAULT_SELLER_ID,
                null,
                "rejected@test.com",
                "박거절",
                "010-3333-4444",
                SellerAdminStatus.REJECTED,
                now,
                now,
                null);
    }

    /** 정지 상태 셀러 관리자 Entity 생성. */
    public static SellerAdminJpaEntity suspendedEntity() {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f63",
                DEFAULT_SELLER_ID,
                DEFAULT_AUTH_USER_ID,
                "suspended@test.com",
                "최정지",
                "010-5555-6666",
                SellerAdminStatus.SUSPENDED,
                now,
                now,
                null);
    }

    /** 비활성 상태 셀러 관리자 Entity 생성. */
    public static SellerAdminJpaEntity inactiveEntity() {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f64",
                DEFAULT_SELLER_ID,
                DEFAULT_AUTH_USER_ID,
                "inactive@test.com",
                "이비활성",
                "010-7777-8888",
                SellerAdminStatus.INACTIVE,
                now,
                now,
                null);
    }

    /** 삭제된 상태 셀러 관리자 Entity 생성. */
    public static SellerAdminJpaEntity deletedEntity() {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f65",
                DEFAULT_SELLER_ID,
                DEFAULT_AUTH_USER_ID,
                "deleted@test.com",
                "정삭제",
                "010-0000-0000",
                SellerAdminStatus.ACTIVE,
                now,
                now,
                now);
    }

    /** 삭제된 상태 셀러 관리자 Entity 생성 (ID 지정). */
    public static SellerAdminJpaEntity deletedEntity(String id, String loginId) {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_AUTH_USER_ID,
                loginId,
                "정삭제",
                "010-0000-0000",
                SellerAdminStatus.ACTIVE,
                now,
                now,
                now);
    }

    /** 핸드폰 번호가 없는 Entity 생성. */
    public static SellerAdminJpaEntity entityWithoutPhoneNumber() {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_AUTH_USER_ID,
                DEFAULT_LOGIN_ID,
                DEFAULT_NAME,
                null,
                SellerAdminStatus.ACTIVE,
                now,
                now,
                null);
    }

    /** 인증 사용자 ID가 없는 Entity 생성 (승인 전). */
    public static SellerAdminJpaEntity entityWithoutAuthUserId() {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                null,
                DEFAULT_LOGIN_ID,
                DEFAULT_NAME,
                DEFAULT_PHONE_NUMBER,
                SellerAdminStatus.PENDING_APPROVAL,
                now,
                now,
                null);
    }

    /** 전체 필드를 커스텀할 수 있는 Entity 생성. */
    public static SellerAdminJpaEntity customEntity(
            String id,
            Long sellerId,
            String authUserId,
            String loginId,
            String name,
            String phoneNumber,
            SellerAdminStatus status) {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                id, sellerId, authUserId, loginId, name, phoneNumber, status, now, now, null);
    }

    /** 특정 상태의 Entity 생성. */
    public static SellerAdminJpaEntity entityWithStatus(String id, SellerAdminStatus status) {
        Instant now = Instant.now();
        return SellerAdminJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                status == SellerAdminStatus.PENDING_APPROVAL ? null : DEFAULT_AUTH_USER_ID,
                "status-test-" + id + "@test.com",
                DEFAULT_NAME,
                DEFAULT_PHONE_NUMBER,
                status,
                now,
                now,
                null);
    }
}
