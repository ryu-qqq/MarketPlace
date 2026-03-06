package com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity.SellerSalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.mapper.SellerSalesChannelJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.repository.SellerSalesChannelQueryDslRepository;
import com.ryuqq.marketplace.application.sellersaleschannel.port.out.query.SellerSalesChannelQueryPort;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 셀러 판매채널 조회 어댑터. */
@Component
public class SellerSalesChannelQueryAdapter implements SellerSalesChannelQueryPort {

    private final SellerSalesChannelQueryDslRepository queryDslRepository;
    private final SellerSalesChannelJpaEntityMapper mapper;

    public SellerSalesChannelQueryAdapter(
            SellerSalesChannelQueryDslRepository queryDslRepository,
            SellerSalesChannelJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<SellerSalesChannel> findConnectedBySellerId(SellerId sellerId) {
        return queryDslRepository.findConnectedBySellerId(sellerId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<SellerSalesChannel> findConnectedBySellerIds(java.util.Set<SellerId> sellerIds) {
        java.util.Set<Long> rawIds =
                sellerIds.stream()
                        .map(SellerId::value)
                        .collect(java.util.stream.Collectors.toSet());
        return queryDslRepository.findConnectedBySellerIds(rawIds).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<SellerSalesChannel> findBySellerIdAndSalesChannelId(
            SellerId sellerId, SalesChannelId salesChannelId) {
        SellerSalesChannelJpaEntity entity =
                queryDslRepository.findBySellerIdAndSalesChannelId(
                        sellerId.value(), salesChannelId.value());
        return Optional.ofNullable(entity).map(mapper::toDomain);
    }

    @Override
    public List<SellerSalesChannel> findConnectedByChannelCode(String channelCode) {
        return queryDslRepository.findConnectedByChannelCode(channelCode).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
