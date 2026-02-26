package com.ryuqq.marketplace.adapter.out.persistence.productgroup.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.ProductGroupCompositionQueryDslRepository;
import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupEnrichmentResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupExcelBaseBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListCompositeResult;
import com.ryuqq.marketplace.application.productgroup.port.out.query.ProductGroupCompositionQueryPort;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** ProductGroup Composition Query Adapter. 크로스 도메인 JOIN 기반 목록/상세 조회. */
@Component
public class ProductGroupCompositionQueryAdapter implements ProductGroupCompositionQueryPort {

    private final ProductGroupCompositionQueryDslRepository compositionRepository;

    public ProductGroupCompositionQueryAdapter(
            ProductGroupCompositionQueryDslRepository compositionRepository) {
        this.compositionRepository = compositionRepository;
    }

    @Override
    public Optional<ProductGroupListCompositeResult> findCompositeById(Long productGroupId) {
        return compositionRepository.findCompositeById(productGroupId);
    }

    @Override
    public List<ProductGroupListCompositeResult> findCompositeByCriteria(
            ProductGroupSearchCriteria criteria) {
        return compositionRepository.findCompositeByCriteria(criteria);
    }

    @Override
    public long countByCriteria(ProductGroupSearchCriteria criteria) {
        return compositionRepository.countByCriteria(criteria);
    }

    @Override
    public List<ProductGroupEnrichmentResult> findEnrichmentsByProductGroupIds(
            List<Long> productGroupIds) {
        return compositionRepository.findEnrichmentsByProductGroupIds(productGroupIds);
    }

    @Override
    public Optional<ProductGroupDetailCompositeQueryResult> findDetailCompositeById(
            Long productGroupId) {
        return compositionRepository.findDetailCompositeById(productGroupId);
    }

    @Override
    public ProductGroupExcelBaseBundle findExcelBaseBundleByCriteria(
            ProductGroupSearchCriteria criteria) {
        return compositionRepository.findExcelBaseBundleByCriteria(criteria);
    }

    @Override
    public Map<Long, List<ProductResult>> findProductsWithOptionNamesByProductGroupIds(
            List<Long> productGroupIds) {
        return compositionRepository.findProductsWithOptionNamesByProductGroupIds(productGroupIds);
    }
}
