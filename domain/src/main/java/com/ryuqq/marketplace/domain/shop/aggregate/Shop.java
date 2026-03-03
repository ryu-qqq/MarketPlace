package com.ryuqq.marketplace.domain.shop.aggregate;

import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.vo.AccountId;
import com.ryuqq.marketplace.domain.shop.vo.ShopCredentials;
import com.ryuqq.marketplace.domain.shop.vo.ShopName;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import java.time.Instant;

/** Shop Aggregate Root. */
public class Shop {

    private final ShopId id;
    private final Long salesChannelId;
    private ShopName shopName;
    private AccountId accountId;
    private ShopStatus status;
    private DeletionStatus deletionStatus;
    private final String channelCode;
    private final String apiKey;
    private final String apiSecret;
    private final String accessToken;
    private final String vendorId;
    private final Instant createdAt;
    private Instant updatedAt;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private Shop(
            ShopId id,
            Long salesChannelId,
            ShopName shopName,
            AccountId accountId,
            ShopStatus status,
            DeletionStatus deletionStatus,
            String channelCode,
            String apiKey,
            String apiSecret,
            String accessToken,
            String vendorId,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.salesChannelId = salesChannelId;
        this.shopName = shopName;
        this.accountId = accountId;
        this.status = status;
        this.deletionStatus = deletionStatus;
        this.channelCode = channelCode;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.accessToken = accessToken;
        this.vendorId = vendorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 Shop 생성 팩토리. */
    public static Shop forNew(Long salesChannelId, String shopName, String accountId, Instant now) {
        return new Shop(
                ShopId.forNew(),
                salesChannelId,
                ShopName.of(shopName),
                AccountId.of(accountId),
                ShopStatus.ACTIVE,
                DeletionStatus.active(),
                null,
                null,
                null,
                null,
                null,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static Shop reconstitute(
            ShopId id,
            Long salesChannelId,
            String shopName,
            String accountId,
            ShopStatus status,
            Instant deletedAt,
            String channelCode,
            String apiKey,
            String apiSecret,
            String accessToken,
            String vendorId,
            Instant createdAt,
            Instant updatedAt) {
        return new Shop(
                id,
                salesChannelId,
                ShopName.of(shopName),
                AccountId.of(accountId),
                status,
                DeletionStatus.reconstitute(deletedAt != null, deletedAt),
                channelCode,
                apiKey,
                apiSecret,
                accessToken,
                vendorId,
                createdAt,
                updatedAt);
    }

    /** Shop 정보 수정. */
    public void update(ShopUpdateData updateData, Instant now) {
        this.shopName = ShopName.of(updateData.shopName());
        this.accountId = AccountId.of(updateData.accountId());
        this.status = updateData.status();
        this.updatedAt = now;
    }

    /** 활성화. */
    public void activate(Instant now) {
        this.status = ShopStatus.ACTIVE;
        this.updatedAt = now;
    }

    /** 비활성화. */
    public void deactivate(Instant now) {
        this.status = ShopStatus.INACTIVE;
        this.updatedAt = now;
    }

    /** 삭제 (소프트 삭제). */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    public ShopId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long salesChannelId() {
        return salesChannelId;
    }

    public String shopName() {
        return shopName.value();
    }

    public String accountId() {
        return accountId.value();
    }

    public ShopStatus status() {
        return status;
    }

    public boolean isActive() {
        return status.isActive();
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

    public String channelCode() {
        return channelCode;
    }

    public String apiKey() {
        return apiKey;
    }

    public String apiSecret() {
        return apiSecret;
    }

    public String accessToken() {
        return accessToken;
    }

    public String vendorId() {
        return vendorId;
    }

    /** Shop의 API 자격증명을 ShopCredentials VO로 반환. */
    public ShopCredentials toCredentials() {
        return ShopCredentials.of(channelCode, apiKey, apiSecret, accessToken, vendorId);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
