package com.ryuqq.marketplace.adapter.out.persistence.brand.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandAliasJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brand.mapper.BrandJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.brand.repository.BrandAliasJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.brand.repository.BrandJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.brand.repository.BrandQueryDslRepository;
import com.ryuqq.marketplace.application.brand.dto.query.BrandSearchQuery;
import com.ryuqq.marketplace.application.brand.port.out.query.BrandQueryPort;
import com.ryuqq.marketplace.application.common.dto.response.PageResponse;
import com.ryuqq.marketplace.domain.brand.aggregate.brand.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Brand Query Adapter
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지 - Plain Java class</li>
 *   <li>Long FK 전략</li>
 *   <li>생성자 주입</li>
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
@Component
public class BrandQueryAdapter implements BrandQueryPort {

    private final BrandJpaRepository brandRepository;
    private final BrandAliasJpaRepository aliasRepository;
    private final BrandQueryDslRepository queryDslRepository;
    private final BrandJpaEntityMapper mapper;

    public BrandQueryAdapter(
        BrandJpaRepository brandRepository,
        BrandAliasJpaRepository aliasRepository,
        BrandQueryDslRepository queryDslRepository,
        BrandJpaEntityMapper mapper
    ) {
        this.brandRepository = brandRepository;
        this.aliasRepository = aliasRepository;
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Brand> findById(Long brandId) {
        return brandRepository.findById(brandId)
            .map(entity -> {
                List<BrandAliasJpaEntity> aliases = aliasRepository.findByBrandId(brandId);
                return mapper.toDomain(entity, aliases);
            });
    }

    @Override
    public Optional<Brand> findByCode(String code) {
        return brandRepository.findByCode(code)
            .map(entity -> {
                List<BrandAliasJpaEntity> aliases = aliasRepository.findByBrandId(entity.getId());
                return mapper.toDomain(entity, aliases);
            });
    }

    @Override
    public PageResponse<Brand> search(BrandSearchQuery query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BrandJpaEntity> brandPage = queryDslRepository.search(query, pageable);

        List<Brand> brands = brandPage.getContent().stream()
            .map(mapper::toDomainWithoutAliases)
            .toList();

        return PageResponse.of(
            brands,
            brandPage.getNumber(),
            brandPage.getSize(),
            brandPage.getTotalElements(),
            brandPage.getTotalPages(),
            brandPage.isFirst(),
            brandPage.isLast()
        );
    }

    @Override
    public List<Brand> findByIds(List<Long> brandIds) {
        return brandRepository.findAllById(brandIds).stream()
            .map(mapper::toDomainWithoutAliases)
            .toList();
    }

    @Override
    public List<Brand> findAll(BrandSearchQuery query) {
        return queryDslRepository.search(query, Pageable.unpaged()).getContent().stream()
            .map(mapper::toDomainWithoutAliases)
            .toList();
    }
}
