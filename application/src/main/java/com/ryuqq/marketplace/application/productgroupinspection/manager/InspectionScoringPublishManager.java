package com.ryuqq.marketplace.application.productgroupinspection.manager;

import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.InspectionScoringPublishClient;
import org.springframework.stereotype.Component;

/** Scoring 큐 메시지 발행 매니저. */
@Component
public class InspectionScoringPublishManager {

    private final InspectionScoringPublishClient publishClient;

    public InspectionScoringPublishManager(InspectionScoringPublishClient publishClient) {
        this.publishClient = publishClient;
    }

    public String publish(String messageBody) {
        return publishClient.publish(messageBody);
    }
}
