package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.mapper.OutboundProductJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository.OutboundProductJpaRepository;
import com.ryuqq.marketplace.application.outboundproduct.port.out.query.OutboundProductQueryPort;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundproduct.vo.OutboundProductStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class OutboundProductQueryAdapter implements OutboundProductQueryPort {

    private final OutboundProductJpaRepository repository;
    private final OutboundProductJpaEntityMapper mapper;

    public OutboundProductQueryAdapter(
            OutboundProductJpaRepository repository, OutboundProductJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public boolean existsByProductGroupIdAndSalesChannelId(
            Long productGroupId, Long salesChannelId) {
        return repository.existsByProductGroupIdAndSalesChannelId(productGroupId, salesChannelId);
    }

    @Override
    public Optional<OutboundProduct> findByProductGroupIdAndSalesChannelId(
            Long productGroupId, Long salesChannelId) {
        return repository
                .findByProductGroupIdAndSalesChannelId(productGroupId, salesChannelId)
                .map(mapper::toDomain);
    }

    @Override
    public List<OutboundProduct> findRegisteredByProductGroupId(Long productGroupId) {
        return repository
                .findByProductGroupIdAndStatus(
                        productGroupId, OutboundProductStatus.REGISTERED.name())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<OutboundProduct> findByExternalProductIdAndSalesChannelId(
            String externalProductId, long salesChannelId) {
        return repository
                .findByExternalProductIdAndSalesChannelId(externalProductId, salesChannelId)
                .map(mapper::toDomain);
    }

    @Override
    public List<OutboundProduct> findDeregisteredByProductGroupId(Long productGroupId) {
        return repository
                .findByProductGroupIdAndStatus(
                        productGroupId, OutboundProductStatus.DEREGISTERED.name())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
