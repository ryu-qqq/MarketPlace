package com.ryuqq.marketplace.application.brand.service.command;

import com.ryuqq.marketplace.application.brand.assembler.BrandAssembler;
import com.ryuqq.marketplace.application.brand.dto.command.CreateBrandCommand;
import com.ryuqq.marketplace.application.brand.dto.response.BrandResponse;
import com.ryuqq.marketplace.application.brand.port.in.command.CreateBrandUseCase;
import com.ryuqq.marketplace.application.brand.port.out.command.BrandPersistencePort;
import com.ryuqq.marketplace.domain.brand.aggregate.brand.Brand;
import com.ryuqq.marketplace.domain.brand.exception.BrandCodeDuplicateException;
import com.ryuqq.marketplace.domain.brand.exception.CanonicalNameDuplicateException;
import com.ryuqq.marketplace.domain.brand.vo.BrandCode;
import com.ryuqq.marketplace.domain.brand.vo.BrandMeta;
import com.ryuqq.marketplace.domain.brand.vo.BrandName;
import com.ryuqq.marketplace.domain.brand.vo.CanonicalName;
import com.ryuqq.marketplace.domain.brand.vo.Country;
import com.ryuqq.marketplace.domain.brand.vo.Department;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CreateBrandService - 브랜드 생성 Command Service
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
public class CreateBrandService implements CreateBrandUseCase {
    
    private final BrandPersistencePort persistencePort;
    private final BrandAssembler assembler;
    
    public CreateBrandService(BrandPersistencePort persistencePort, BrandAssembler assembler) {
        this.persistencePort = persistencePort;
        this.assembler = assembler;
    }
    
    @Override
    @Transactional
    public BrandResponse execute(CreateBrandCommand command) {
        // 유니크 검증
        if (persistencePort.existsByCode(command.code())) {
            throw new BrandCodeDuplicateException(command.code());
        }
        if (persistencePort.existsByCanonicalName(command.canonicalName())) {
            throw new CanonicalNameDuplicateException(command.canonicalName());
        }
        
        // 도메인 객체 생성
        Brand brand = Brand.create(
            BrandCode.of(command.code()),
            CanonicalName.of(command.canonicalName()),
            BrandName.of(command.nameKo(), command.nameEn(), command.shortName()),
            command.country() != null ? Country.of(command.country()) : null,
            command.department() != null ? Department.fromString(command.department()) : Department.FASHION,
            command.isLuxury()
        );
        
        // 메타 정보 설정
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
    
    private boolean hasMetaInfo(CreateBrandCommand command) {
        return (command.officialWebsite() != null && !command.officialWebsite().isBlank())
            || (command.logoUrl() != null && !command.logoUrl().isBlank())
            || (command.description() != null && !command.description().isBlank());
    }
}
