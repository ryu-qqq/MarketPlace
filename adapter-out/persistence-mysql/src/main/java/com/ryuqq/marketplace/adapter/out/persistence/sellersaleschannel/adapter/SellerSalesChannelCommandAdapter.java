package com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity.SellerSalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.mapper.SellerSalesChannelJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.repository.SellerSalesChannelJpaRepository;
import com.ryuqq.marketplace.application.sellersaleschannel.port.out.command.SellerSalesChannelCommandPort;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import org.springframework.stereotype.Component;

/** 셀러 판매채널 커맨드 어댑터. */
@Component
public class SellerSalesChannelCommandAdapter implements SellerSalesChannelCommandPort {

    private final SellerSalesChannelJpaRepository repository;
    private final SellerSalesChannelJpaEntityMapper mapper;

    public SellerSalesChannelCommandAdapter(
            SellerSalesChannelJpaRepository repository, SellerSalesChannelJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(SellerSalesChannel sellerSalesChannel) {
        SellerSalesChannelJpaEntity entity = mapper.toEntity(sellerSalesChannel);
        SellerSalesChannelJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
