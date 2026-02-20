package com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.mapper.SellerSalesChannelJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.repository.SellerSalesChannelQueryDslRepository;
import com.ryuqq.marketplace.application.sellersaleschannel.port.out.query.SellerSalesChannelQueryPort;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.util.List;
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
}
