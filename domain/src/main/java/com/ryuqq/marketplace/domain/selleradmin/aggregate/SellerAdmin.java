package com.ryuqq.marketplace.domain.selleradmin.aggregate;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.event.SellerAdminApprovedEvent;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import com.ryuqq.marketplace.domain.selleradmin.vo.AdminName;
import com.ryuqq.marketplace.domain.selleradmin.vo.LoginId;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 셀러 관리자 Aggregate.
 *
 * <p>셀러의 상품/주문을 관리하는 관리자 계정입니다. 한 셀러에 여러 관리자가 있을 수 있습니다.
 *
 * <p>인증/인가는 외부 인증 서버에서 관리하며, 이 Entity는 authUserId를 통해 인증 서버와 매핑됩니다. 비밀번호, 권한 등은 인증 서버에서 관리합니다.
 */
public class SellerAdmin {

    private final SellerAdminId id;
    private SellerId sellerId;
    private String authUserId;
    private LoginId loginId;
    private AdminName name;
    private PhoneNumber phoneNumber;
    private SellerAdminStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    // 도메인 이벤트
    private final List<DomainEvent> events = new ArrayList<>();

    private SellerAdmin(
            SellerAdminId id,
            SellerId sellerId,
            String authUserId,
            LoginId loginId,
            AdminName name,
            PhoneNumber phoneNumber,
            SellerAdminStatus status,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.authUserId = authUserId;
        this.loginId = loginId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    /**
     * 새 셀러 관리자 생성.
     *
     * @param id 셀러 관리자 ID (외부에서 생성)
     * @param sellerId 셀러 ID
     * @param authUserId 인증 서버 사용자 ID
     * @param loginId 로그인 ID
     * @param name 관리자 이름
     * @param phoneNumber 핸드폰 번호
     * @param now 현재 시각
     * @return 새 SellerAdmin 인스턴스
     */
    public static SellerAdmin forNew(
            SellerAdminId id,
            SellerId sellerId,
            String authUserId,
            LoginId loginId,
            AdminName name,
            PhoneNumber phoneNumber,
            Instant now) {
        return new SellerAdmin(
                id,
                sellerId,
                authUserId,
                loginId,
                name,
                phoneNumber,
                SellerAdminStatus.ACTIVE,
                now,
                now,
                null);
    }

    /**
     * SellerId 없이 새 셀러 관리자 생성 (나중에 할당).
     *
     * @param id 셀러 관리자 ID (외부에서 생성)
     * @param authUserId 인증 서버 사용자 ID
     * @param loginId 로그인 ID
     * @param name 관리자 이름
     * @param phoneNumber 핸드폰 번호
     * @param now 현재 시각
     * @return 새 SellerAdmin 인스턴스
     */
    public static SellerAdmin forNew(
            SellerAdminId id,
            String authUserId,
            LoginId loginId,
            AdminName name,
            PhoneNumber phoneNumber,
            Instant now) {
        return new SellerAdmin(
                id,
                null,
                authUserId,
                loginId,
                name,
                phoneNumber,
                SellerAdminStatus.ACTIVE,
                now,
                now,
                null);
    }

    /**
     * 가입 신청용 셀러 관리자 생성 (승인 대기 상태).
     *
     * <p>승인 전까지는 authUserId가 없습니다. 승인 시 인증 서버에 등록 후 authUserId를 설정합니다.
     *
     * @param id 셀러 관리자 ID (외부에서 생성)
     * @param sellerId 셀러 ID
     * @param loginId 로그인 ID
     * @param name 관리자 이름
     * @param phoneNumber 핸드폰 번호
     * @param now 현재 시각
     * @return 승인 대기 상태의 SellerAdmin 인스턴스
     */
    public static SellerAdmin forApplication(
            SellerAdminId id,
            SellerId sellerId,
            LoginId loginId,
            AdminName name,
            PhoneNumber phoneNumber,
            Instant now) {
        return new SellerAdmin(
                id,
                sellerId,
                null,
                loginId,
                name,
                phoneNumber,
                SellerAdminStatus.PENDING_APPROVAL,
                now,
                now,
                null);
    }

    /**
     * DB에서 재구성.
     *
     * @param id 관리자 ID
     * @param sellerId 셀러 ID
     * @param authUserId 인증 서버 사용자 ID
     * @param loginId 로그인 ID
     * @param name 관리자 이름
     * @param phoneNumber 핸드폰 번호
     * @param status 상태
     * @param createdAt 생성일시
     * @param updatedAt 수정일시
     * @param deletedAt 삭제일시
     * @return 재구성된 SellerAdmin 인스턴스
     */
    public static SellerAdmin reconstitute(
            SellerAdminId id,
            SellerId sellerId,
            String authUserId,
            LoginId loginId,
            AdminName name,
            PhoneNumber phoneNumber,
            SellerAdminStatus status,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new SellerAdmin(
                id,
                sellerId,
                authUserId,
                loginId,
                name,
                phoneNumber,
                status,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public boolean isNew() {
        return false;
    }

    protected void registerEvent(DomainEvent event) {
        this.events.add(event);
    }

    public List<DomainEvent> pollEvents() {
        List<DomainEvent> polled = new ArrayList<>(this.events);
        this.events.clear();
        return Collections.unmodifiableList(polled);
    }

    public boolean isActive() {
        return status == SellerAdminStatus.ACTIVE && deletedAt == null;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean canLogin() {
        return status.canLogin() && deletedAt == null;
    }

    /** 승인 대기 상태 여부. */
    public boolean isPendingApproval() {
        return status.isPendingApproval();
    }

    /** 거절 상태 여부. */
    public boolean isRejected() {
        return status == SellerAdminStatus.REJECTED;
    }

    /**
     * 가입 신청 승인 (Outbox 기반 비동기 처리용).
     *
     * <p>인증 서버 연동은 Outbox를 통해 비동기로 처리되며, authUserId는 나중에 설정됩니다.
     *
     * @param now 승인 시각
     * @throws IllegalStateException 승인 대기 상태가 아닌 경우
     */
    public void approve(Instant now) {
        if (!status.canApprove()) {
            throw new IllegalStateException("승인할 수 없는 상태입니다. 현재 상태: " + status);
        }
        this.status = SellerAdminStatus.ACTIVE;
        this.updatedAt = now;

        registerEvent(SellerAdminApprovedEvent.of(this.id, this.sellerId, now));
    }

    /**
     * 가입 신청 승인 (동기 처리용).
     *
     * <p>인증 서버에 회원 등록 후 반환된 authUserId와 함께 호출합니다.
     *
     * @param authUserId 인증 서버에서 발급받은 사용자 ID
     * @param now 승인 시각
     * @throws IllegalStateException 승인 대기 상태가 아닌 경우
     */
    public void approveWithAuthUserId(String authUserId, Instant now) {
        if (!status.canApprove()) {
            throw new IllegalStateException("승인할 수 없는 상태입니다. 현재 상태: " + status);
        }
        this.authUserId = authUserId;
        this.status = SellerAdminStatus.ACTIVE;
        this.updatedAt = now;

        registerEvent(SellerAdminApprovedEvent.of(this.id, this.sellerId, now));
    }

    /**
     * 가입 신청 거절.
     *
     * @param now 거절 시각
     * @throws IllegalStateException 거절 가능한 상태가 아닌 경우
     */
    public void reject(Instant now) {
        if (!status.canReject()) {
            throw new IllegalStateException("거절할 수 없는 상태입니다. 현재 상태: " + status);
        }
        this.status = SellerAdminStatus.REJECTED;
        this.updatedAt = now;
    }

    /** SellerId 설정. */
    public void assignSellerId(SellerId sellerId) {
        this.sellerId = sellerId;
    }

    /** 기본 정보 수정. */
    public void updateInfo(AdminName name, PhoneNumber phoneNumber, Instant now) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.updatedAt = now;
    }

    /** 인증 서버 사용자 ID 변경. */
    public void updateAuthUserId(String authUserId, Instant now) {
        this.authUserId = authUserId;
        this.updatedAt = now;
    }

    /** 상태 변경. */
    public void changeStatus(SellerAdminStatus status, Instant now) {
        this.status = status;
        this.updatedAt = now;
    }

    /** 활성화. */
    public void activate(Instant now) {
        this.status = SellerAdminStatus.ACTIVE;
        this.updatedAt = now;
    }

    /** 비활성화. */
    public void deactivate(Instant now) {
        this.status = SellerAdminStatus.INACTIVE;
        this.updatedAt = now;
    }

    /** 정지. */
    public void suspend(Instant now) {
        this.status = SellerAdminStatus.SUSPENDED;
        this.updatedAt = now;
    }

    /**
     * 비밀번호 초기화 가능 여부 검증.
     *
     * @throws IllegalStateException ACTIVE 상태가 아니거나 인증 서버에 미등록인 경우
     */
    public void validatePasswordResetEligibility() {
        if (!isActive()) {
            throw new IllegalStateException("비밀번호 초기화는 ACTIVE 상태에서만 가능합니다. 현재 상태: " + status);
        }
        if (authUserId == null) {
            throw new IllegalStateException("인증 서버에 등록되지 않은 관리자입니다. sellerAdminId: " + id.value());
        }
    }

    /** 비밀번호 변경 가능 여부 검증 (ACTIVE, authUserId 존재). */
    public void validatePasswordChangeEligibility() {
        if (!isActive()) {
            throw new IllegalStateException("비밀번호 변경은 ACTIVE 상태에서만 가능합니다. 현재 상태: " + status);
        }
        if (authUserId == null) {
            throw new IllegalStateException("인증 서버에 등록되지 않은 관리자입니다. sellerAdminId: " + id.value());
        }
    }

    /** 소프트 삭제. */
    public void delete(Instant now) {
        this.deletedAt = now;
        this.updatedAt = now;
    }

    // Getters
    public SellerAdminId id() {
        return id;
    }

    public String idValue() {
        return id.value();
    }

    public SellerId sellerId() {
        return sellerId;
    }

    public Long sellerIdValue() {
        return sellerId != null ? sellerId.value() : null;
    }

    public String authUserId() {
        return authUserId;
    }

    public LoginId loginId() {
        return loginId;
    }

    public String loginIdValue() {
        return loginId != null ? loginId.value() : null;
    }

    public AdminName name() {
        return name;
    }

    public String nameValue() {
        return name != null ? name.value() : null;
    }

    public PhoneNumber phoneNumber() {
        return phoneNumber;
    }

    public String phoneNumberValue() {
        return phoneNumber != null ? phoneNumber.value() : null;
    }

    public SellerAdminStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Instant deletedAt() {
        return deletedAt;
    }
}
