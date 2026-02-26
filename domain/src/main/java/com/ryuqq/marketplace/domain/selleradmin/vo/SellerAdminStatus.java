package com.ryuqq.marketplace.domain.selleradmin.vo;

/**
 * 셀러 관리자 상태.
 *
 * <p>셀러 관리자 계정의 활성화 상태를 나타냅니다.
 */
public enum SellerAdminStatus {

    /** 승인 대기. 가입 신청 후 승인 대기 중인 상태. */
    PENDING_APPROVAL("승인대기"),

    /** 활성 상태. 로그인 및 모든 기능 사용 가능. */
    ACTIVE("활성"),

    /** 비활성 상태. 로그인 불가. */
    INACTIVE("비활성"),

    /** 정지 상태. 규정 위반 등으로 계정 정지. */
    SUSPENDED("정지"),

    /** 거절 상태. 가입 신청이 거절됨. */
    REJECTED("거절");

    private final String description;

    SellerAdminStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    /** 로그인 가능 여부 확인. */
    public boolean canLogin() {
        return this == ACTIVE;
    }

    /** 승인 대기 상태 여부. */
    public boolean isPendingApproval() {
        return this == PENDING_APPROVAL;
    }

    /** 승인 가능 여부. 승인 대기 상태에서만 승인 가능. */
    public boolean canApprove() {
        return this == PENDING_APPROVAL;
    }

    /** 거절 가능 여부. 승인 대기 상태에서만 거절 가능. */
    public boolean canReject() {
        return this == PENDING_APPROVAL;
    }
}
