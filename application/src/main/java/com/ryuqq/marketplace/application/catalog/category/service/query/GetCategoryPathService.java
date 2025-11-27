package com.ryuqq.marketplace.application.catalog.category.service.query;

import com.ryuqq.marketplace.application.catalog.category.assembler.CategoryAssembler;
import com.ryuqq.marketplace.application.catalog.category.dto.response.CategoryPathResponse;
import com.ryuqq.marketplace.application.catalog.category.port.in.query.GetCategoryPathUseCase;
import com.ryuqq.marketplace.application.catalog.category.port.out.query.CategoryQueryPort;
import com.ryuqq.marketplace.domain.catalog.category.aggregate.Category;
import com.ryuqq.marketplace.domain.catalog.category.exception.CategoryNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * GetCategoryPathService - 카테고리 경로 조회 Query Service (breadcrumb)
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
public class GetCategoryPathService implements GetCategoryPathUseCase {

    private final CategoryQueryPort queryPort;
    private final CategoryAssembler assembler;

    public GetCategoryPathService(CategoryQueryPort queryPort, CategoryAssembler assembler) {
        this.queryPort = queryPort;
        this.assembler = assembler;
    }

    @Override
    public CategoryPathResponse getPath(Long categoryId) {
        // 카테고리 존재 확인
        queryPort.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        // 조상 카테고리 조회 (루트 → 현재)
        List<Category> ancestors = queryPort.findAncestors(categoryId);

        return assembler.toPathResponse(categoryId, ancestors);
    }
}
