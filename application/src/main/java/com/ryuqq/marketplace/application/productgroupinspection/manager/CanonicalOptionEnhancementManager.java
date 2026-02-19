package com.ryuqq.marketplace.application.productgroupinspection.manager;

import com.ryuqq.marketplace.application.productgroupinspection.dto.response.CanonicalOptionEnhancementResult;
import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.CanonicalOptionEnhancementClient;
import org.springframework.stereotype.Component;

/** 캐노니컬 옵션 LLM 보강 Manager. */
@Component
public class CanonicalOptionEnhancementManager {

    private final CanonicalOptionEnhancementClient client;

    public CanonicalOptionEnhancementManager(CanonicalOptionEnhancementClient client) {
        this.client = client;
    }

    public CanonicalOptionEnhancementResult enhance(Long productGroupId) {
        return client.enhance(productGroupId);
    }
}
