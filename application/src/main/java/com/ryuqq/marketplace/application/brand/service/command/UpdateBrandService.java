package com.ryuqq.marketplace.application.brand.service.command;

import com.ryuqq.marketplace.application.brand.assembler.BrandAssembler;
import com.ryuqq.marketplace.application.brand.dto.command.UpdateBrandCommand;
import com.ryuqq.marketplace.application.brand.dto.response.BrandResponse;
import com.ryuqq.marketplace.application.brand.port.in.command.UpdateBrandUseCase;
import com.ryuqq.marketplace.application.brand.port.out.command.BrandPersistencePort;
import com.ryuqq.marketplace.application.brand.port.out.query.BrandQueryPort;
import com.ryuqq.marketplace.domain.brand.aggregate.brand.Brand;
import com.ryuqq.marketplace.domain.brand.exception.BrandNotFoundException;
import com.ryuqq.marketplace.domain.brand.vo.BrandMeta;
import com.ryuqq.marketplace.domain.brand.vo.BrandName;
import com.ryuqq.marketplace.domain.brand.vo.Country;
import com.ryuqq.marketplace.domain.brand.vo.Department;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UpdateBrandService - 브랜드 수정 Command Service
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
public class UpdateBrandService implements UpdateBrandUseCase {
    
    private final BrandQueryPort queryPort;
    private final BrandPersistencePort persistencePort;
    private final BrandAssembler assembler;
    
    public UpdateBrandService(
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
    public BrandResponse execute(UpdateBrandCommand command) {
        // 브랜드 조회
        Brand brand = queryPort.findById(command.brandId())
            .orElseThrow(() -> new BrandNotFoundException(command.brandId()));
        
        // 브랜드 정보 수정
        brand.update(
            BrandName.of(command.nameKo(), command.nameEn(), command.shortName()),
            command.country() != null ? Country.of(command.country()) : null,
            command.department() != null ? Department.fromString(command.department()) : brand.department(),
            command.isLuxury()
        );
        
        // 메타 정보 수정
        if (hasMetaInfo(command)) {
            brand.updateMeta(BrandMeta.of(
                command.officialWebsite(),
                command.logoUrl(),
                command.description()
            ));
        }
        
        // 저장
        Brand saved = persistencePort.persist(brand);
        
        return assembler.toResponse(saved);
    }
    
    private boolean hasMetaInfo(UpdateBrandCommand command) {
        return (command.officialWebsite() != null && !command.officialWebsite().isBlank())
            || (command.logoUrl() != null && !command.logoUrl().isBlank())
            || (command.description() != null && !command.description().isBlank());
    }
}
