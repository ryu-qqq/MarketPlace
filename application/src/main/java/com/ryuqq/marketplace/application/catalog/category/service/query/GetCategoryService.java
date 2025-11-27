package com.ryuqq.marketplace.application.catalog.category.service.query;

import com.ryuqq.marketplace.application.catalog.category.assembler.CategoryAssembler;
import com.ryuqq.marketplace.application.catalog.category.dto.response.CategoryResponse;
import com.ryuqq.marketplace.application.catalog.category.port.in.query.GetCategoryUseCase;
import com.ryuqq.marketplace.application.catalog.category.port.out.query.CategoryQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * GetCategoryService - 단일 카테고리 조회 Query Service
 *
 * <p><strong>Zero-Tolerance 규칙</strong>:</p>
 * <ul>
 *   <li>No Lombok</li>
 *   <li>@Transactional(readOnly = true) 필수</li>
 *   <li>Transaction 내 외부 API 호출 금지</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
@Transactional(readOnly = true)
public class GetCategoryService implements GetCategoryUseCase {

    private final CategoryQueryPort queryPort;
    private final CategoryAssembler assembler;

    public GetCategoryService(CategoryQueryPort queryPort, CategoryAssembler assembler) {
        this.queryPort = queryPort;
        this.assembler = assembler;
    }

    @Override
    public Optional<CategoryResponse> getById(Long categoryId) {
        return queryPort.findById(categoryId)
            .map(assembler::toResponse);
    }

    @Override
    public Optional<CategoryResponse> getByCode(String code) {
        return queryPort.findByCode(code)
            .map(assembler::toResponse);
    }
}
