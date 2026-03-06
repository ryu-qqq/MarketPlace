package com.ryuqq.marketplace.adapter.out.persistence.categorymapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.repository.CategoryMappingQueryDslRepository;
import com.ryuqq.marketplace.application.categorymapping.port.out.query.CategoryMappingQueryPort;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 카테고리 매핑 역조회 어댑터. */
@Component
public class CategoryMappingQueryAdapter implements CategoryMappingQueryPort {

    private final CategoryMappingQueryDslRepository queryDslRepository;

    public CategoryMappingQueryAdapter(CategoryMappingQueryDslRepository queryDslRepository) {
        this.queryDslRepository = queryDslRepository;
    }

    @Override
    public Optional<Long> findSalesChannelCategoryId(Long salesChannelId, Long internalCategoryId) {
        return queryDslRepository.findSalesChannelCategoryId(salesChannelId, internalCategoryId);
    }
}
