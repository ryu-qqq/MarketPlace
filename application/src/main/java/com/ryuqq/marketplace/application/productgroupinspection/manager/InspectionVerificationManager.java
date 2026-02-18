package com.ryuqq.marketplace.application.productgroupinspection.manager;

import com.ryuqq.marketplace.application.productgroupinspection.dto.response.InspectionVerificationResult;
import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.InspectionVerificationClient;
import org.springframework.stereotype.Component;

/** LLM 최종 검증 Manager. */
@Component
public class InspectionVerificationManager {

    private final InspectionVerificationClient client;

    public InspectionVerificationManager(InspectionVerificationClient client) {
        this.client = client;
    }

    public InspectionVerificationResult verify(Long productGroupId) {
        return client.verify(productGroupId);
    }
}
