package com.ryuqq.marketplace.adapter.out.client.sqs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AWS SQS Client Properties.
 *
 * <p>sqs-client.yml에서 설정을 읽어옵니다.
 */
@ConfigurationProperties(prefix = "sqs")
public class SqsClientProperties {

    private String region = "ap-northeast-2";
    private String endpoint;
    private Queues queues = new Queues();

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Queues getQueues() {
        return queues;
    }

    public void setQueues(Queues queues) {
        this.queues = queues;
    }

    public static class Queues {

        // Intelligence pipeline queues
        private String intelligenceOrchestration;
        private String intelligenceDescriptionAnalysis;
        private String intelligenceOptionAnalysis;
        private String intelligenceNoticeAnalysis;
        private String intelligenceAggregation;

        // OutboundSync queue
        private String outboundSync;

        // Claim outbox queues
        private String cancelOutbox;
        private String refundOutbox;
        private String exchangeOutbox;

        public String getIntelligenceOrchestration() {
            return intelligenceOrchestration;
        }

        public void setIntelligenceOrchestration(String intelligenceOrchestration) {
            this.intelligenceOrchestration = intelligenceOrchestration;
        }

        public String getIntelligenceDescriptionAnalysis() {
            return intelligenceDescriptionAnalysis;
        }

        public void setIntelligenceDescriptionAnalysis(String intelligenceDescriptionAnalysis) {
            this.intelligenceDescriptionAnalysis = intelligenceDescriptionAnalysis;
        }

        public String getIntelligenceOptionAnalysis() {
            return intelligenceOptionAnalysis;
        }

        public void setIntelligenceOptionAnalysis(String intelligenceOptionAnalysis) {
            this.intelligenceOptionAnalysis = intelligenceOptionAnalysis;
        }

        public String getIntelligenceNoticeAnalysis() {
            return intelligenceNoticeAnalysis;
        }

        public void setIntelligenceNoticeAnalysis(String intelligenceNoticeAnalysis) {
            this.intelligenceNoticeAnalysis = intelligenceNoticeAnalysis;
        }

        public String getIntelligenceAggregation() {
            return intelligenceAggregation;
        }

        public void setIntelligenceAggregation(String intelligenceAggregation) {
            this.intelligenceAggregation = intelligenceAggregation;
        }

        public String getOutboundSync() {
            return outboundSync;
        }

        public void setOutboundSync(String outboundSync) {
            this.outboundSync = outboundSync;
        }

        public String getCancelOutbox() {
            return cancelOutbox;
        }

        public void setCancelOutbox(String cancelOutbox) {
            this.cancelOutbox = cancelOutbox;
        }

        public String getRefundOutbox() {
            return refundOutbox;
        }

        public void setRefundOutbox(String refundOutbox) {
            this.refundOutbox = refundOutbox;
        }

        public String getExchangeOutbox() {
            return exchangeOutbox;
        }

        public void setExchangeOutbox(String exchangeOutbox) {
            this.exchangeOutbox = exchangeOutbox;
        }
    }
}
