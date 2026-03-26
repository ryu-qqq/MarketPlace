package com.ryuqq.marketplace.application.legacy.commoncode.manager;

import com.ryuqq.marketplace.application.legacy.commoncode.port.out.query.LegacyCommonCodeQueryPort;
import com.ryuqq.marketplace.domain.legacy.commoncode.aggregate.LegacyCommonCode;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 공통 코드 Read Manager. */
@Component
public class LegacyCommonCodeReadManager {

    private final LegacyCommonCodeQueryPort queryPort;

    public LegacyCommonCodeReadManager(LegacyCommonCodeQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<LegacyCommonCode> getByCodeGroupId(Long codeGroupId) {
        return queryPort.findByCodeGroupId(codeGroupId);
    }
}
