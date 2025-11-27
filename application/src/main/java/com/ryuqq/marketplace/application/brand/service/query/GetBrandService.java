package com.ryuqq.marketplace.application.brand.service.query;

import com.ryuqq.marketplace.application.brand.assembler.BrandAssembler;
import com.ryuqq.marketplace.application.brand.dto.response.BrandDetailResponse;
import com.ryuqq.marketplace.application.brand.port.in.query.GetBrandUseCase;
import com.ryuqq.marketplace.application.brand.port.out.query.BrandQueryPort;
import com.ryuqq.marketplace.domain.brand.exception.BrandNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * GetBrandService - 브랜드 조회 Query Service
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지</li>
 *   <li>@Transactional(readOnly = true): Query Service에만 적용</li>
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
@Service
public class GetBrandService implements GetBrandUseCase {
    
    private final BrandQueryPort queryPort;
    private final BrandAssembler assembler;
    
    public GetBrandService(BrandQueryPort queryPort, BrandAssembler assembler) {
        this.queryPort = queryPort;
        this.assembler = assembler;
    }
    
    @Override
    @Transactional(readOnly = true)
    public BrandDetailResponse getById(Long brandId) {
        return queryPort.findById(brandId)
            .map(assembler::toDetailResponse)
            .orElseThrow(() -> new BrandNotFoundException(brandId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public BrandDetailResponse getByCode(String code) {
        return queryPort.findByCode(code)
            .map(assembler::toDetailResponse)
            .orElseThrow(() -> new BrandNotFoundException(code));
    }
}
