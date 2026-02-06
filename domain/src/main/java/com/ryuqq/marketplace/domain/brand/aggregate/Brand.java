package com.ryuqq.marketplace.domain.brand.aggregate;

import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.brand.vo.BrandCode;
import com.ryuqq.marketplace.domain.brand.vo.BrandName;
import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;
import com.ryuqq.marketplace.domain.brand.vo.LogoUrl;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import java.time.Instant;

/** 브랜드 Aggregate Root. */
public class Brand {

    private final BrandId id;
    private final BrandCode code;
    private BrandName brandName;
    private BrandStatus status;
    private LogoUrl logoUrl;
    private DeletionStatus deletionStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    private Brand(
            BrandId id,
            BrandCode code,
            BrandName brandName,
            BrandStatus status,
            LogoUrl logoUrl,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.brandName = brandName;
        this.status = status;
        this.logoUrl = logoUrl;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Brand forNew(BrandCode code, BrandName brandName, LogoUrl logoUrl, Instant now) {
        return new Brand(
                BrandId.forNew(),
                code,
                brandName,
                BrandStatus.ACTIVE,
                logoUrl,
                DeletionStatus.active(),
                now,
                now);
    }

    public static Brand reconstitute(
            BrandId id,
            BrandCode code,
            BrandName brandName,
            BrandStatus status,
            LogoUrl logoUrl,
            Instant deletedAt,
            Instant createdAt,
            Instant updatedAt) {
        DeletionStatus deletion =
                deletedAt != null ? DeletionStatus.deletedAt(deletedAt) : DeletionStatus.active();
        return new Brand(id, code, brandName, status, logoUrl, deletion, createdAt, updatedAt);
    }

    public boolean isNew() {
        return id.isNew();
    }

    public void activate(Instant now) {
        this.status = BrandStatus.ACTIVE;
        this.updatedAt = now;
    }

    public void deactivate(Instant now) {
        this.status = BrandStatus.INACTIVE;
        this.updatedAt = now;
    }

    /**
     * 브랜드 삭제 (Soft Delete).
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 브랜드 복원.
     *
     * @param now 복원 시각
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    // Getters
    public BrandId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public BrandCode code() {
        return code;
    }

    public String codeValue() {
        return code.value();
    }

    public BrandName brandName() {
        return brandName;
    }

    public String nameKo() {
        return brandName.nameKo();
    }

    public String nameEn() {
        return brandName.nameEn();
    }

    public String shortName() {
        return brandName.shortName();
    }

    public BrandStatus status() {
        return status;
    }

    public boolean isActive() {
        return status.isActive();
    }

    public LogoUrl logoUrl() {
        return logoUrl;
    }

    public String logoUrlValue() {
        return logoUrl != null ? logoUrl.value() : null;
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }

    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    public Instant deletedAt() {
        return deletionStatus.deletedAt();
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
