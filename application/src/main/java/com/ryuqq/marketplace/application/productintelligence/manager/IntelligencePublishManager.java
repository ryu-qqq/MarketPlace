package com.ryuqq.marketplace.application.productintelligence.manager;

import com.ryuqq.marketplace.application.productintelligence.port.out.client.AggregationPublishClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.DescriptionAnalysisPublishClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.NoticeAnalysisPublishClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.OptionAnalysisPublishClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Intelligence Pipeline SQS 발행 매니저.
 *
 * <p>Orchestrator에서 3개 Analyzer 큐로 동시 발행하고, 마지막 Analyzer 완료 시 Aggregation 큐로 발행합니다.
 */
@Component
@ConditionalOnProperty(name = "intelligence.pipeline.enabled", havingValue = "true")
public class IntelligencePublishManager {

    private static final Logger log = LoggerFactory.getLogger(IntelligencePublishManager.class);

    private final DescriptionAnalysisPublishClient descriptionPublishClient;
    private final OptionAnalysisPublishClient optionPublishClient;
    private final NoticeAnalysisPublishClient noticePublishClient;
    private final AggregationPublishClient aggregationPublishClient;

    public IntelligencePublishManager(
            DescriptionAnalysisPublishClient descriptionPublishClient,
            OptionAnalysisPublishClient optionPublishClient,
            NoticeAnalysisPublishClient noticePublishClient,
            AggregationPublishClient aggregationPublishClient) {
        this.descriptionPublishClient = descriptionPublishClient;
        this.optionPublishClient = optionPublishClient;
        this.noticePublishClient = noticePublishClient;
        this.aggregationPublishClient = aggregationPublishClient;
    }

    /** 3개 Analyzer 큐에 동시 발행. */
    public void publishToAllAnalyzers(Long profileId, Long productGroupId) {
        String messageBody = createMessageBody(profileId, productGroupId);

        descriptionPublishClient.publish(messageBody);
        optionPublishClient.publish(messageBody);
        noticePublishClient.publish(messageBody);

        log.info("3개 Analyzer 큐 발행 완료: profileId={}, productGroupId={}", profileId, productGroupId);
    }

    /** Aggregation 큐에 발행. */
    public void publishToAggregation(Long profileId, Long productGroupId) {
        String messageBody = createMessageBody(profileId, productGroupId);
        aggregationPublishClient.publish(messageBody);

        log.info("Aggregation 큐 발행: profileId={}, productGroupId={}", profileId, productGroupId);
    }

    private String createMessageBody(Long profileId, Long productGroupId) {
        return "{\"profileId\":" + profileId + ",\"productGroupId\":" + productGroupId + "}";
    }
}
