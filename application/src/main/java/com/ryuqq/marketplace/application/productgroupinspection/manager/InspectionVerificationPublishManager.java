package com.ryuqq.marketplace.application.productgroupinspection.manager;

import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.InspectionVerificationPublishClient;
import org.springframework.stereotype.Component;

/** Verification 큐 메시지 발행 매니저. */
@Component
public class InspectionVerificationPublishManager {

    private final InspectionVerificationPublishClient publishClient;

    public InspectionVerificationPublishManager(InspectionVerificationPublishClient publishClient) {
        this.publishClient = publishClient;
    }

    public String publish(String messageBody) {
        return publishClient.publish(messageBody);
    }
}
