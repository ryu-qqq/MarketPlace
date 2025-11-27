package com.ryuqq.marketplace.application.brand.service.command;

import com.ryuqq.marketplace.application.brand.dto.command.RemoveBrandAliasCommand;
import com.ryuqq.marketplace.application.brand.port.in.command.RemoveBrandAliasUseCase;
import com.ryuqq.marketplace.application.brand.port.out.command.BrandPersistencePort;
import com.ryuqq.marketplace.application.brand.port.out.query.BrandQueryPort;
import com.ryuqq.marketplace.domain.brand.aggregate.brand.Brand;
import com.ryuqq.marketplace.domain.brand.vo.BrandAliasId;
import com.ryuqq.marketplace.domain.brand.exception.BrandNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RemoveBrandAliasService - 브랜드 별칭 제거 Command Service
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지</li>
 *   <li>@Transactional 경계: Command Service에만 적용</li>
 *   <li>Transaction 내 외부 API 호출 금지</li>
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
@Service
public class RemoveBrandAliasService implements RemoveBrandAliasUseCase {
    
    private final BrandQueryPort queryPort;
    private final BrandPersistencePort persistencePort;
    
    public RemoveBrandAliasService(
        BrandQueryPort queryPort,
        BrandPersistencePort persistencePort
    ) {
        this.queryPort = queryPort;
        this.persistencePort = persistencePort;
    }
    
    @Override
    @Transactional
    public void execute(RemoveBrandAliasCommand command) {
        // 브랜드 조회
        Brand brand = queryPort.findById(command.brandId())
            .orElseThrow(() -> new BrandNotFoundException(command.brandId()));
        
        // 별칭 제거
        BrandAliasId aliasId = BrandAliasId.of(command.aliasId());
        brand.removeAlias(aliasId);
        
        // 저장
        persistencePort.persist(brand);
    }
}
