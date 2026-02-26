package com.ryuqq.marketplace.adapter.out.persistence.selleraddress.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.SellerAddressJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.mapper.SellerAddressJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.repository.SellerAddressJpaRepository;
import com.ryuqq.marketplace.application.selleraddress.port.out.command.SellerAddressCommandPort;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerAddressCommandAdapter - 셀러 주소 명령 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 */
@Component
public class SellerAddressCommandAdapter implements SellerAddressCommandPort {

    private final SellerAddressJpaRepository repository;
    private final SellerAddressJpaEntityMapper mapper;

    public SellerAddressCommandAdapter(
            SellerAddressJpaRepository repository, SellerAddressJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(SellerAddress address) {
        SellerAddressJpaEntity entity = mapper.toEntity(address);
        SellerAddressJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public void persistAll(List<SellerAddress> addresses) {
        List<SellerAddressJpaEntity> entities = addresses.stream().map(mapper::toEntity).toList();
        repository.saveAll(entities);
    }
}
