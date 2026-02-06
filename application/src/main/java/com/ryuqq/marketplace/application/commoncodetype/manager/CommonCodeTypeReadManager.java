package com.ryuqq.marketplace.application.commoncodetype.manager;

import com.ryuqq.marketplace.application.commoncodetype.port.out.query.CommonCodeTypeQueryPort;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.exception.CommonCodeTypeNotFoundException;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 공통 코드 타입 Read Manager. */
@Component
public class CommonCodeTypeReadManager {

    private final CommonCodeTypeQueryPort queryPort;

    public CommonCodeTypeReadManager(CommonCodeTypeQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public CommonCodeType getById(CommonCodeTypeId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new CommonCodeTypeNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<CommonCodeType> getByIds(List<CommonCodeTypeId> ids) {
        return queryPort.findByIds(ids);
    }

    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return queryPort.existsByCode(code);
    }

    @Transactional(readOnly = true)
    public boolean existsByDisplayOrderExcludingId(int displayOrder, Long excludeId) {
        return queryPort.existsByDisplayOrderExcludingId(displayOrder, excludeId);
    }

    @Transactional(readOnly = true)
    public List<CommonCodeType> findByCriteria(CommonCodeTypeSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(CommonCodeTypeSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }
}
