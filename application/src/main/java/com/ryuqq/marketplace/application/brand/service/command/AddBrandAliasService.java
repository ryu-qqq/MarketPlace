package com.ryuqq.marketplace.application.brand.service.command;

import com.ryuqq.marketplace.application.brand.assembler.BrandAssembler;
import com.ryuqq.marketplace.application.brand.dto.command.AddBrandAliasCommand;
import com.ryuqq.marketplace.application.brand.dto.response.BrandAliasResponse;
import com.ryuqq.marketplace.application.brand.port.in.command.AddBrandAliasUseCase;
import com.ryuqq.marketplace.application.brand.port.out.command.BrandPersistencePort;
import com.ryuqq.marketplace.application.brand.port.out.query.BrandQueryPort;
import com.ryuqq.marketplace.domain.brand.aggregate.brand.Brand;
import com.ryuqq.marketplace.domain.brand.aggregate.brand.BrandAlias;
import com.ryuqq.marketplace.domain.brand.vo.AliasName;
import com.ryuqq.marketplace.domain.brand.vo.AliasSource;
import com.ryuqq.marketplace.domain.brand.vo.AliasSourceType;
import com.ryuqq.marketplace.domain.brand.vo.AliasStatus;
import com.ryuqq.marketplace.domain.brand.vo.Confidence;
import com.ryuqq.marketplace.domain.brand.exception.BrandNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AddBrandAliasService - 브랜드 별칭 추가 Command Service
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
public class AddBrandAliasService implements AddBrandAliasUseCase {
    
    private final BrandQueryPort queryPort;
    private final BrandPersistencePort persistencePort;
    private final BrandAssembler assembler;
    
    public AddBrandAliasService(
        BrandQueryPort queryPort,
        BrandPersistencePort persistencePort,
        BrandAssembler assembler
    ) {
        this.queryPort = queryPort;
        this.persistencePort = persistencePort;
        this.assembler = assembler;
    }
    
    @Override
    @Transactional
    public BrandAliasResponse execute(AddBrandAliasCommand command) {
        // 브랜드 조회
        Brand brand = queryPort.findById(command.brandId())
            .orElseThrow(() -> new BrandNotFoundException(command.brandId()));
        
        // 별칭 추가 (Aggregate Root를 통해서만)
        BrandAlias alias = brand.addAlias(
            AliasName.of(command.aliasName()),
            AliasSource.of(
                AliasSourceType.valueOf(command.sourceType()),
                command.sellerId(),
                command.mallCode()
            ),
            Confidence.of(command.confidence()),
            AliasStatus.AUTO_SUGGESTED
        );
        
        // 저장
        persistencePort.persist(brand);
        
        return assembler.toAliasResponse(alias);
    }
}
