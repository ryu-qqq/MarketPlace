package com.ryuqq.marketplace.domain.seller.aggregate;

import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.seller.exception.SellerAlreadyActiveException;
import com.ryuqq.marketplace.domain.seller.exception.SellerAlreadyInactiveException;
import com.ryuqq.marketplace.domain.seller.exception.SellerInactiveException;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.Description;
import com.ryuqq.marketplace.domain.seller.vo.DisplayName;
import com.ryuqq.marketplace.domain.seller.vo.LogoUrl;
import com.ryuqq.marketplace.domain.seller.vo.SellerName;
import java.time.Instant;

/** 셀러 Aggregate Root. */
public class Seller {

    private final SellerId id;
    private SellerName sellerName;
    private DisplayName displayName;
    private LogoUrl logoUrl;
    private Description description;
    private boolean active;
    private DeletionStatus deletionStatus;
    private String authTenantId;
    private String authOrganizationId;
    private final Instant createdAt;
    private Instant updatedAt;

    private Seller(
            SellerId id,
            SellerName sellerName,
            DisplayName displayName,
            LogoUrl logoUrl,
            Description description,
            boolean active,
            DeletionStatus deletionStatus,
            String authTenantId,
            String authOrganizationId,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.sellerName = sellerName;
        this.displayName = displayName;
        this.logoUrl = logoUrl;
        this.description = description;
        this.active = active;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.authTenantId = authTenantId;
        this.authOrganizationId = authOrganizationId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Seller forNew(
            SellerName sellerName,
            DisplayName displayName,
            LogoUrl logoUrl,
            Description description,
            Instant now) {
        return new Seller(
                SellerId.forNew(),
                sellerName,
                displayName,
                logoUrl,
                description,
                true,
                DeletionStatus.active(),
                null,
                null,
                now,
                now);
    }

    public static Seller reconstitute(
            SellerId id,
            SellerName sellerName,
            DisplayName displayName,
            LogoUrl logoUrl,
            Description description,
            boolean active,
            Instant deletedAt,
            String authTenantId,
            String authOrganizationId,
            Instant createdAt,
            Instant updatedAt) {
        DeletionStatus status =
                deletedAt != null ? DeletionStatus.deletedAt(deletedAt) : DeletionStatus.active();
        return new Seller(
                id,
                sellerName,
                displayName,
                logoUrl,
                description,
                active,
                status,
                authTenantId,
                authOrganizationId,
                createdAt,
                updatedAt);
    }

    public boolean isNew() {
        return id.isNew();
    }

    /**
     * 셀러 정보 수정.
     *
     * @param updateData 수정 데이터
     * @param now 현재 시간
     */
    public void update(SellerUpdateData updateData, Instant now) {
        this.sellerName = updateData.sellerName();
        this.displayName = updateData.displayName();
        this.logoUrl = updateData.logoUrl();
        this.description = updateData.description();
        this.updatedAt = now;
    }

    public void activate(Instant now) {
        if (this.active) {
            throw new SellerAlreadyActiveException();
        }
        this.active = true;
        this.updatedAt = now;
    }

    public void deactivate(Instant now) {
        if (!this.active) {
            throw new SellerAlreadyInactiveException();
        }
        this.active = false;
        this.updatedAt = now;
    }

    /**
     * 셀러 삭제 (Soft Delete).
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 셀러 복원.
     *
     * @param now 복원 시각
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    public void ensureActive() {
        if (!this.active) {
            throw new SellerInactiveException();
        }
    }

    /**
     * 인증 서버 정보 업데이트.
     *
     * <p>인증 서버에서 테넌트/조직 생성 후 반환된 ID를 저장합니다.
     *
     * @param authTenantId 인증 서버 테넌트 ID
     * @param authOrganizationId 인증 서버 조직 ID
     * @param now 수정 시각
     */
    public void updateAuthInfo(String authTenantId, String authOrganizationId, Instant now) {
        this.authTenantId = authTenantId;
        this.authOrganizationId = authOrganizationId;
        this.updatedAt = now;
    }

    /** 인증 서버 연동 완료 여부. */
    public boolean hasAuthInfo() {
        return authTenantId != null && authOrganizationId != null;
    }

    // VO Getters
    public SellerId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public SellerName sellerName() {
        return sellerName;
    }

    public String sellerNameValue() {
        return sellerName.value();
    }

    public DisplayName displayName() {
        return displayName;
    }

    public String displayNameValue() {
        return displayName.value();
    }

    public LogoUrl logoUrl() {
        return logoUrl;
    }

    public String logoUrlValue() {
        return logoUrl != null ? logoUrl.value() : null;
    }

    public Description description() {
        return description;
    }

    public String descriptionValue() {
        return description != null ? description.value() : null;
    }

    public boolean isActive() {
        return active;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }

    /** 삭제 여부 확인 */
    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    /**
     * 삭제 시각 반환.
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 삭제 시각 (활성 상태인 경우 null)
     */
    public Instant deletedAt() {
        return deletionStatus.deletedAt();
    }

    public String authTenantId() {
        return authTenantId;
    }

    public String authOrganizationId() {
        return authOrganizationId;
    }
}
