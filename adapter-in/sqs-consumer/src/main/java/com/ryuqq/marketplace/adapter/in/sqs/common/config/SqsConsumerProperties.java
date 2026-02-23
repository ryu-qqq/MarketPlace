package com.ryuqq.marketplace.adapter.in.sqs.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SQS Consumer 설정 프로퍼티.
 *
 * <p>각 큐별 활성화/비활성화 제어를 위한 설정입니다. 메시지 폴링, 동시성, visibility timeout 등은 Spring Cloud AWS SQS 기본 설정 또는
 * {@code @SqsListener} 어노테이션으로 관리됩니다.
 */
@ConfigurationProperties(prefix = "sqs.consumer")
public class SqsConsumerProperties {

    // Intelligence pipeline consumers
    private QueueConsumer intelligenceOrchestration = new QueueConsumer();
    private QueueConsumer intelligenceDescriptionAnalysis = new QueueConsumer();
    private QueueConsumer intelligenceOptionAnalysis = new QueueConsumer();
    private QueueConsumer intelligenceNoticeAnalysis = new QueueConsumer();
    private QueueConsumer intelligenceAggregation = new QueueConsumer();

    public QueueConsumer getIntelligenceOrchestration() {
        return intelligenceOrchestration;
    }

    public void setIntelligenceOrchestration(QueueConsumer intelligenceOrchestration) {
        this.intelligenceOrchestration = intelligenceOrchestration;
    }

    public QueueConsumer getIntelligenceDescriptionAnalysis() {
        return intelligenceDescriptionAnalysis;
    }

    public void setIntelligenceDescriptionAnalysis(QueueConsumer intelligenceDescriptionAnalysis) {
        this.intelligenceDescriptionAnalysis = intelligenceDescriptionAnalysis;
    }

    public QueueConsumer getIntelligenceOptionAnalysis() {
        return intelligenceOptionAnalysis;
    }

    public void setIntelligenceOptionAnalysis(QueueConsumer intelligenceOptionAnalysis) {
        this.intelligenceOptionAnalysis = intelligenceOptionAnalysis;
    }

    public QueueConsumer getIntelligenceNoticeAnalysis() {
        return intelligenceNoticeAnalysis;
    }

    public void setIntelligenceNoticeAnalysis(QueueConsumer intelligenceNoticeAnalysis) {
        this.intelligenceNoticeAnalysis = intelligenceNoticeAnalysis;
    }

    public QueueConsumer getIntelligenceAggregation() {
        return intelligenceAggregation;
    }

    public void setIntelligenceAggregation(QueueConsumer intelligenceAggregation) {
        this.intelligenceAggregation = intelligenceAggregation;
    }

    public static class QueueConsumer {

        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
