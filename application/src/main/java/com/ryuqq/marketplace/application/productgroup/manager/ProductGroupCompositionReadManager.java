package com.ryuqq.marketplace.application.productgroup.manager;

import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupEnrichmentResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupExcelBaseBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListCompositeResult;
import com.ryuqq.marketplace.application.productgroup.port.out.query.ProductGroupCompositionQueryPort;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupNotFoundException;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProductGroup Composition Read Manager.
 *
 * <p>크로스 도메인 JOIN을 통한 성능 최적화된 조회와 배치 enrichment 쿼리를 담당합니다.
 */
@Component
public class ProductGroupCompositionReadManager {

    private final ProductGroupCompositionQueryPort compositionQueryPort;

    public ProductGroupCompositionReadManager(
            ProductGroupCompositionQueryPort compositionQueryPort) {
        this.compositionQueryPort = compositionQueryPort;
    }

    @Transactional(readOnly = true)
    public ProductGroupListCompositeResult getCompositeById(Long productGroupId) {
        return compositionQueryPort
                .findCompositeById(productGroupId)
                .orElseThrow(() -> new ProductGroupNotFoundException(productGroupId));
    }

    /** 상세용 Composition 조회 (정책 포함). */
    @Transactional(readOnly = true)
    public ProductGroupDetailCompositeQueryResult getDetailCompositeById(Long productGroupId) {
        return compositionQueryPort
                .findDetailCompositeById(productGroupId)
                .orElseThrow(() -> new ProductGroupNotFoundException(productGroupId));
    }

    @Transactional(readOnly = true)
    public List<ProductGroupListCompositeResult> findCompositeByCriteria(
            ProductGroupSearchCriteria criteria) {
        return compositionQueryPort.findCompositeByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(ProductGroupSearchCriteria criteria) {
        return compositionQueryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public List<ProductGroupEnrichmentResult> findEnrichments(List<Long> productGroupIds) {
        if (productGroupIds.isEmpty()) {
            return List.of();
        }
        return compositionQueryPort.findEnrichmentsByProductGroupIds(productGroupIds);
    }

    /** 엑셀용 통합 Composite 조회 (base + 가격 enrichment + description cdnUrl). */
    @Transactional(readOnly = true)
    public ProductGroupExcelBaseBundle findExcelBaseBundleByCriteria(
            ProductGroupSearchCriteria criteria) {
        return compositionQueryPort.findExcelBaseBundleByCriteria(criteria);
    }

    /** 상품 + 옵션 매핑(이름 해석 포함) 배치 조회. */
    @Transactional(readOnly = true)
    public Map<Long, List<ProductResult>> findProductsWithOptionNamesByProductGroupIds(
            List<Long> productGroupIds) {
        if (productGroupIds == null || productGroupIds.isEmpty()) {
            return Map.of();
        }
        return compositionQueryPort.findProductsWithOptionNamesByProductGroupIds(productGroupIds);
    }
}
