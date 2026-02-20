package com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity.SellerSalesChannelJpaEntity;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import com.ryuqq.marketplace.domain.sellersaleschannel.id.SellerSalesChannelId;
import com.ryuqq.marketplace.domain.sellersaleschannel.vo.ConnectionStatus;
import org.springframework.stereotype.Component;

/** SellerSalesChannel Domain ↔ JPA Entity 변환 매퍼. */
@Component
public class SellerSalesChannelJpaEntityMapper {

    public SellerSalesChannelJpaEntity toEntity(SellerSalesChannel domain) {
        return SellerSalesChannelJpaEntity.create(
                domain.idValue(),
                domain.sellerIdValue(),
                domain.salesChannelIdValue(),
                domain.channelCode(),
                toEntityStatus(domain.connectionStatus()),
                domain.apiKey(),
                domain.apiSecret(),
                domain.accessToken(),
                domain.vendorId(),
                domain.displayName(),
                domain.createdAt(),
                domain.updatedAt());
    }

    public SellerSalesChannel toDomain(SellerSalesChannelJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException(
                    "SellerSalesChannelJpaEntity.id가 null입니다. DB에서 복원된 엔티티에 ID가 없을 수 없습니다.");
        }
        SellerSalesChannelId id = SellerSalesChannelId.of(entity.getId());
        return SellerSalesChannel.reconstitute(
                id,
                SellerId.of(entity.getSellerId()),
                SalesChannelId.of(entity.getSalesChannelId()),
                entity.getChannelCode(),
                toDomainStatus(entity.getConnectionStatus()),
                entity.getApiKey(),
                entity.getApiSecret(),
                entity.getAccessToken(),
                entity.getVendorId(),
                entity.getDisplayName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private SellerSalesChannelJpaEntity.ConnectionStatus toEntityStatus(ConnectionStatus status) {
        return switch (status) {
            case CONNECTED -> SellerSalesChannelJpaEntity.ConnectionStatus.CONNECTED;
            case DISCONNECTED -> SellerSalesChannelJpaEntity.ConnectionStatus.DISCONNECTED;
            case SUSPENDED -> SellerSalesChannelJpaEntity.ConnectionStatus.SUSPENDED;
        };
    }

    private ConnectionStatus toDomainStatus(SellerSalesChannelJpaEntity.ConnectionStatus status) {
        return switch (status) {
            case CONNECTED -> ConnectionStatus.CONNECTED;
            case DISCONNECTED -> ConnectionStatus.DISCONNECTED;
            case SUSPENDED -> ConnectionStatus.SUSPENDED;
        };
    }
}
