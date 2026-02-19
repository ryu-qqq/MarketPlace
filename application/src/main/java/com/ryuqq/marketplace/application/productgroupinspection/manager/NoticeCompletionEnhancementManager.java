package com.ryuqq.marketplace.application.productgroupinspection.manager;

import com.ryuqq.marketplace.application.productgroupinspection.dto.response.NoticeCompletionEnhancementResult;
import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.NoticeCompletionEnhancementClient;
import org.springframework.stereotype.Component;

/** 고시정보 LLM 보완 Manager. */
@Component
public class NoticeCompletionEnhancementManager {

    private final NoticeCompletionEnhancementClient client;

    public NoticeCompletionEnhancementManager(NoticeCompletionEnhancementClient client) {
        this.client = client;
    }

    public NoticeCompletionEnhancementResult enhance(Long productGroupId) {
        return client.enhance(productGroupId);
    }
}
