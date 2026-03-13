package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductListCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductMainImageDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductPriceStockDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductSyncInfoDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.mapper.OmsProductCompositionMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository.OmsProductCompositionQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository.OmsProductEnrichmentQueryDslRepository;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductListResult;
import com.ryuqq.marketplace.application.outboundproduct.port.out.query.OmsProductCompositionQueryPort;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchCriteria;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * OMS 상품 목록 Composition 조회 어댑터.
 *
 * <p>2-pass 전략: 1) outbound_products 기준 base composite 조회 → 2) 이미지/가격재고/연동상태 enrichment.
 */
@Component
public class OmsProductCompositionQueryAdapter implements OmsProductCompositionQueryPort {

    private final OmsProductCompositionQueryDslRepository compositionRepository;
    private final OmsProductEnrichmentQueryDslRepository enrichmentRepository;
    private final OmsProductCompositionMapper mapper;

    public OmsProductCompositionQueryAdapter(
            OmsProductCompositionQueryDslRepository compositionRepository,
            OmsProductEnrichmentQueryDslRepository enrichmentRepository,
            OmsProductCompositionMapper mapper) {
        this.compositionRepository = compositionRepository;
        this.enrichmentRepository = enrichmentRepository;
        this.mapper = mapper;
    }

    @Override
    public List<OmsProductListResult> findByCriteria(OmsProductSearchCriteria criteria) {
        List<OmsProductListCompositeDto> composites =
                compositionRepository.findByCriteria(criteria);
        if (composites.isEmpty()) {
            return List.of();
        }

        List<Long> pgIds =
                composites.stream()
                        .map(OmsProductListCompositeDto::productGroupId)
                        .distinct()
                        .toList();

        Map<Long, OmsProductMainImageDto> imageMap = enrichmentRepository.fetchMainImages(pgIds);
        Map<Long, OmsProductPriceStockDto> priceStockMap =
                enrichmentRepository.fetchPriceStock(pgIds);
        Map<String, OmsProductSyncInfoDto> syncInfoMap =
                enrichmentRepository.fetchLatestSyncInfo(pgIds);

        return mapper.toResults(composites, imageMap, priceStockMap, syncInfoMap);
    }

    @Override
    public long countByCriteria(OmsProductSearchCriteria criteria) {
        return compositionRepository.countByCriteria(criteria);
    }
}
