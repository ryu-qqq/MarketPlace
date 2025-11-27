package com.ryuqq.marketplace.application.catalog.category.service.query;

import com.ryuqq.marketplace.application.catalog.category.assembler.CategoryAssembler;
import com.ryuqq.marketplace.application.catalog.category.dto.query.CategorySearchQuery;
import com.ryuqq.marketplace.application.catalog.category.dto.response.CategoryResponse;
import com.ryuqq.marketplace.application.catalog.category.port.in.query.SearchCategoryUseCase;
import com.ryuqq.marketplace.application.catalog.category.port.out.query.CategoryQueryPort;
import com.ryuqq.marketplace.domain.catalog.category.aggregate.Category;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SearchCategoryService - 카테고리 검색 Query Service
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
public class SearchCategoryService implements SearchCategoryUseCase {

    private final CategoryQueryPort queryPort;
    private final CategoryAssembler assembler;

    public SearchCategoryService(CategoryQueryPort queryPort, CategoryAssembler assembler) {
        this.queryPort = queryPort;
        this.assembler = assembler;
    }

    @Override
    public List<CategoryResponse> search(String keyword) {
        List<Category> categories = queryPort.search(keyword);
        return assembler.toResponseList(categories);
    }

    @Override
    public List<CategoryResponse> searchLeaves(CategorySearchQuery query) {
        List<Category> categories = queryPort.findListableLeaves(query);
        return assembler.toResponseList(categories);
    }

    @Override
    public List<CategoryResponse> findUpdatedSince(LocalDateTime since) {
        List<Category> categories = queryPort.findUpdatedSince(since);
        return assembler.toResponseList(categories);
    }
}
