package com.ryuqq.marketplace.adapter.out.persistence.brand.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandAliasJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brand.mapper.BrandJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.brand.repository.BrandAliasJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.brand.repository.BrandJpaRepository;
import com.ryuqq.marketplace.application.brand.port.out.command.BrandPersistencePort;
import com.ryuqq.marketplace.domain.brand.aggregate.brand.Brand;
import com.ryuqq.marketplace.domain.brand.aggregate.brand.BrandAlias;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Brand Command Adapter
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
public class BrandCommandAdapter implements BrandPersistencePort {

    private final BrandJpaRepository brandRepository;
    private final BrandAliasJpaRepository aliasRepository;
    private final BrandJpaEntityMapper mapper;

    public BrandCommandAdapter(
        BrandJpaRepository brandRepository,
        BrandAliasJpaRepository aliasRepository,
        BrandJpaEntityMapper mapper
    ) {
        this.brandRepository = brandRepository;
        this.aliasRepository = aliasRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Brand persist(Brand brand) {
        // Brand 저장
        BrandJpaEntity brandEntity = mapper.toEntity(brand);
        BrandJpaEntity savedBrand = brandRepository.save(brandEntity);

        // 기존 Alias 삭제 후 새로 저장 (UPSERT 방식)
        aliasRepository.deleteByBrandId(savedBrand.getId());

        List<BrandAliasJpaEntity> aliasEntities = brand.aliases().stream()
            .map(alias -> BrandAliasJpaEntity.from(
                BrandAlias.reconstitute(
                    alias.id(),
                    savedBrand.getId(), // 저장된 Brand ID로 설정
                    alias.aliasName(),
                    alias.source(),
                    alias.confidence(),
                    alias.status()
                )
            ))
            .toList();

        List<BrandAliasJpaEntity> savedAliases = aliasRepository.saveAll(aliasEntities);

        // 저장된 Entity를 Domain으로 재구성
        return mapper.toDomain(savedBrand, savedAliases);
    }

    @Override
    public boolean existsByCode(String code) {
        return brandRepository.existsByCode(code);
    }

    @Override
    public boolean existsByCanonicalName(String canonicalName) {
        return brandRepository.existsByCanonicalName(canonicalName);
    }
}
