package com.ryuqq.marketplace.application.productgroupinspection.manager;

import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.InspectionEnhancementPublishClient;
import org.springframework.stereotype.Component;

/** Enhancement 큐 메시지 발행 매니저. */
@Component
public class InspectionEnhancementPublishManager {

    private final InspectionEnhancementPublishClient publishClient;

    public InspectionEnhancementPublishManager(InspectionEnhancementPublishClient publishClient) {
        this.publishClient = publishClient;
    }

    public String publish(String messageBody) {
        return publishClient.publish(messageBody);
    }
}
