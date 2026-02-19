package com.ryuqq.marketplace.adapter.out.persistence.selleraddress.adapter;

import com.querydsl.core.BooleanBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.condition.SellerAddressConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.mapper.SellerAddressJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.repository.SellerAddressQueryDslRepository;
import com.ryuqq.marketplace.application.selleraddress.port.out.query.SellerAddressQueryPort;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
import com.ryuqq.marketplace.domain.selleraddress.query.SellerAddressSearchCriteria;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * SellerAddressQueryAdapter - 셀러 주소 조회 어댑터.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 */
@Component
public class SellerAddressQueryAdapter implements SellerAddressQueryPort {

    private final SellerAddressQueryDslRepository queryDslRepository;
    private final SellerAddressJpaEntityMapper mapper;
    private final SellerAddressConditionBuilder conditionBuilder;

    public SellerAddressQueryAdapter(
            SellerAddressQueryDslRepository queryDslRepository,
            SellerAddressJpaEntityMapper mapper,
            SellerAddressConditionBuilder conditionBuilder) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
        this.conditionBuilder = conditionBuilder;
    }

    @Override
    public Optional<SellerAddress> findById(SellerAddressId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<SellerAddress> findAllBySellerId(SellerId sellerId) {
        return queryDslRepository.findAllBySellerId(sellerId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<SellerAddress> findDefaultBySellerId(
            SellerId sellerId, AddressType addressType) {
        return queryDslRepository
                .findDefaultAddress(sellerId.value(), addressType.name())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsBySellerId(SellerId sellerId) {
        return queryDslRepository.existsBySellerId(sellerId.value());
    }

    @Override
    public boolean existsBySellerIdAndAddressTypeAndAddressName(
            SellerId sellerId, AddressType addressType, String addressName) {
        return queryDslRepository.existsBySellerIdAndAddressTypeAndAddressName(
                sellerId.value(), addressType.name(), addressName);
    }

    @Override
    public List<SellerAddress> search(SellerAddressSearchCriteria criteria) {
        BooleanBuilder conditions = buildConditions(criteria);
        return queryDslRepository.search(conditions, criteria.offset(), criteria.size()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long count(SellerAddressSearchCriteria criteria) {
        BooleanBuilder conditions = buildConditions(criteria);
        return queryDslRepository.count(conditions);
    }

    private BooleanBuilder buildConditions(SellerAddressSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(conditionBuilder.sellerIdIn(criteria.sellerIdValues()));
        if (criteria.hasAddressTypesFilter()) {
            builder.and(conditionBuilder.addressTypeIn(criteria.addressTypeNames()));
        }
        if (criteria.hasDefaultFilter()) {
            builder.and(conditionBuilder.defaultAddressEq(criteria.defaultAddress()));
        }
        if (criteria.hasSearchCondition()) {
            builder.and(
                    conditionBuilder.searchCondition(
                            criteria.searchField(), criteria.searchWord()));
        }
        return builder;
    }
}
