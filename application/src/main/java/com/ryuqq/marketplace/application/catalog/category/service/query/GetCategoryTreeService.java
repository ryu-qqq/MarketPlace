package com.ryuqq.marketplace.application.catalog.category.service.query;

import com.ryuqq.marketplace.application.catalog.category.assembler.CategoryAssembler;
import com.ryuqq.marketplace.application.catalog.category.dto.query.CategoryTreeQuery;
import com.ryuqq.marketplace.application.catalog.category.dto.response.CategoryTreeResponse;
import com.ryuqq.marketplace.application.catalog.category.port.in.query.GetCategoryTreeUseCase;
import com.ryuqq.marketplace.application.catalog.category.port.out.query.CategoryQueryPort;
import com.ryuqq.marketplace.domain.catalog.category.aggregate.Category;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * GetCategoryTreeService - 카테고리 트리 조회 Query Service
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
public class GetCategoryTreeService implements GetCategoryTreeUseCase {

    private final CategoryQueryPort queryPort;
    private final CategoryAssembler assembler;

    public GetCategoryTreeService(CategoryQueryPort queryPort, CategoryAssembler assembler) {
        this.queryPort = queryPort;
        this.assembler = assembler;
    }

    @Override
    public CategoryTreeResponse getTree(CategoryTreeQuery query) {
        List<Category> categories;

        if (query.includeInactive()) {
            // Admin용: 전체 조회
            categories = queryPort.findAll();
        } else {
            // Public용: 활성 + 노출 카테고리만
            categories = queryPort.findAllActiveVisible();
        }

        // 필터링 (department, productGroup)
        if (query.department() != null || query.productGroup() != null) {
            categories = categories.stream()
                .filter(c -> matchesFilter(c, query))
                .toList();
        }

        return assembler.toTreeResponse(categories);
    }

    private boolean matchesFilter(Category category, CategoryTreeQuery query) {
        if (query.department() != null
            && !query.department().equals(category.department().name())) {
            return false;
        }
        if (query.productGroup() != null
            && !query.productGroup().equals(category.productGroup().name())) {
            return false;
        }
        return true;
    }
}
