package com.ryuqq.marketplace.domain.sellersaleschannel.aggregate;

import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.id.SellerSalesChannelId;
import com.ryuqq.marketplace.domain.sellersaleschannel.vo.ConnectionStatus;
import java.time.Instant;

/**
 * 셀러 판매채널 Aggregate Root.
 *
 * <p>셀러와 외부 판매채널(네이버커머스, 세토프, 바이마, LF몰) 간 연동 정보를 관리합니다.
 *
 * <p>version 없음: 상태 전이가 빈번하지 않으며, 동시 수정 가능성 낮음.
 */
public class SellerSalesChannel {

    private final SellerSalesChannelId id;
    private final SellerId sellerId;
    private final SalesChannelId salesChannelId;
    private final String channelCode;
    private ConnectionStatus connectionStatus;
    private final String apiKey;
    private final String apiSecret;
    private final String accessToken;
    private final String vendorId;
    private final String displayName;
    private final long shopId;
    private final Instant createdAt;
    private Instant updatedAt;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private SellerSalesChannel(
            SellerSalesChannelId id,
            SellerId sellerId,
            SalesChannelId salesChannelId,
            String channelCode,
            ConnectionStatus connectionStatus,
            String apiKey,
            String apiSecret,
            String accessToken,
            String vendorId,
            String displayName,
            long shopId,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.salesChannelId = salesChannelId;
        this.channelCode = channelCode;
        this.connectionStatus = connectionStatus;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.accessToken = accessToken;
        this.vendorId = vendorId;
        this.displayName = displayName;
        this.shopId = shopId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 새 셀러 판매채널 생성.
     *
     * @param sellerId 셀러 ID
     * @param salesChannelId 판매채널 ID
     * @param channelCode 채널 코드
     * @param apiKey API Key
     * @param apiSecret API Secret
     * @param accessToken Access Token
     * @param vendorId 외부 벤더 ID
     * @param displayName 표시명
     * @param now 현재 시각
     * @return 새 SellerSalesChannel 인스턴스
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static SellerSalesChannel forNew(
            SellerId sellerId,
            SalesChannelId salesChannelId,
            String channelCode,
            String apiKey,
            String apiSecret,
            String accessToken,
            String vendorId,
            String displayName,
            long shopId,
            Instant now) {
        return new SellerSalesChannel(
                SellerSalesChannelId.forNew(),
                sellerId,
                salesChannelId,
                channelCode,
                ConnectionStatus.CONNECTED,
                apiKey,
                apiSecret,
                accessToken,
                vendorId,
                displayName,
                shopId,
                now,
                now);
    }

    /**
     * DB에서 재구성.
     *
     * @param id ID
     * @param sellerId 셀러 ID
     * @param salesChannelId 판매채널 ID
     * @param channelCode 채널 코드
     * @param connectionStatus 연동 상태
     * @param apiKey API Key
     * @param apiSecret API Secret
     * @param accessToken Access Token
     * @param vendorId 외부 벤더 ID
     * @param displayName 표시명
     * @param createdAt 생성일시
     * @param updatedAt 수정일시
     * @return 재구성된 SellerSalesChannel 인스턴스
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static SellerSalesChannel reconstitute(
            SellerSalesChannelId id,
            SellerId sellerId,
            SalesChannelId salesChannelId,
            String channelCode,
            ConnectionStatus connectionStatus,
            String apiKey,
            String apiSecret,
            String accessToken,
            String vendorId,
            String displayName,
            long shopId,
            Instant createdAt,
            Instant updatedAt) {
        return new SellerSalesChannel(
                id,
                sellerId,
                salesChannelId,
                channelCode,
                connectionStatus,
                apiKey,
                apiSecret,
                accessToken,
                vendorId,
                displayName,
                shopId,
                createdAt,
                updatedAt);
    }

    public boolean isNew() {
        return id.isNew();
    }

    /** 연동 활성화. */
    public void connect(Instant now) {
        this.connectionStatus = ConnectionStatus.CONNECTED;
        this.updatedAt = now;
    }

    /** 연동 해제. */
    public void disconnect(Instant now) {
        this.connectionStatus = ConnectionStatus.DISCONNECTED;
        this.updatedAt = now;
    }

    /** 연동 일시 중지. */
    public void suspend(Instant now) {
        this.connectionStatus = ConnectionStatus.SUSPENDED;
        this.updatedAt = now;
    }

    // Getters
    public SellerSalesChannelId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public SellerId sellerId() {
        return sellerId;
    }

    public Long sellerIdValue() {
        return sellerId.value();
    }

    public SalesChannelId salesChannelId() {
        return salesChannelId;
    }

    public Long salesChannelIdValue() {
        return salesChannelId.value();
    }

    public String channelCode() {
        return channelCode;
    }

    public ConnectionStatus connectionStatus() {
        return connectionStatus;
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

    public String displayName() {
        return displayName;
    }

    public long shopId() {
        return shopId;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public boolean isConnected() {
        return connectionStatus.isConnected();
    }
}
