package com.ryuqq.marketplace.application.externalsource.manager;

import com.ryuqq.marketplace.application.externalsource.dto.query.ExternalSourceSearchParams;
import com.ryuqq.marketplace.application.externalsource.port.out.query.ExternalSourceQueryPort;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.exception.ExternalSourceNotFoundException;
import com.ryuqq.marketplace.domain.externalsource.id.ExternalSourceId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ExternalSource Read Manager. */
@Component
public class ExternalSourceReadManager {

    private final ExternalSourceQueryPort queryPort;

    public ExternalSourceReadManager(ExternalSourceQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public ExternalSource getById(long id) {
        ExternalSourceId externalSourceId = ExternalSourceId.of(id);
        return queryPort
                .findById(externalSourceId)
                .orElseThrow(() -> new ExternalSourceNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public ExternalSource findByCode(String code) {
        return queryPort
                .findByCode(code)
                .orElseThrow(() -> new ExternalSourceNotFoundException(code));
    }

    @Transactional(readOnly = true)
    public List<ExternalSource> findByCriteria(ExternalSourceSearchParams params) {
        return queryPort.findByCriteria(params);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(ExternalSourceSearchParams params) {
        return queryPort.countByCriteria(params);
    }
}
