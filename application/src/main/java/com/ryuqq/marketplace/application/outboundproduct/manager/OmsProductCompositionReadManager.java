package com.ryuqq.marketplace.application.outboundproduct.manager;

import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductListResult;
import com.ryuqq.marketplace.application.outboundproduct.port.out.query.OmsProductCompositionQueryPort;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** OMS 상품 목록 Composition 조회 ReadManager. */
@Component
public class OmsProductCompositionReadManager {

    private final OmsProductCompositionQueryPort compositionQueryPort;

    public OmsProductCompositionReadManager(OmsProductCompositionQueryPort compositionQueryPort) {
        this.compositionQueryPort = compositionQueryPort;
    }

    @Transactional(readOnly = true)
    public List<OmsProductListResult> findByCriteria(OmsProductSearchCriteria criteria) {
        return compositionQueryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(OmsProductSearchCriteria criteria) {
        return compositionQueryPort.countByCriteria(criteria);
    }
}
