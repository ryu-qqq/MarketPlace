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

    private QueueConsumer inspectionScoring = new QueueConsumer();
    private QueueConsumer inspectionEnhancement = new QueueConsumer();
    private QueueConsumer inspectionVerification = new QueueConsumer();

    public QueueConsumer getInspectionScoring() {
        return inspectionScoring;
    }

    public void setInspectionScoring(QueueConsumer inspectionScoring) {
        this.inspectionScoring = inspectionScoring;
    }

    public QueueConsumer getInspectionEnhancement() {
        return inspectionEnhancement;
    }

    public void setInspectionEnhancement(QueueConsumer inspectionEnhancement) {
        this.inspectionEnhancement = inspectionEnhancement;
    }

    public QueueConsumer getInspectionVerification() {
        return inspectionVerification;
    }

    public void setInspectionVerification(QueueConsumer inspectionVerification) {
        this.inspectionVerification = inspectionVerification;
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
