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

        private String inspectionScoring;
        private String inspectionEnhancement;
        private String inspectionVerification;
        private String inspectionDlq;

        public String getInspectionScoring() {
            return inspectionScoring;
        }

        public void setInspectionScoring(String inspectionScoring) {
            this.inspectionScoring = inspectionScoring;
        }

        public String getInspectionEnhancement() {
            return inspectionEnhancement;
        }

        public void setInspectionEnhancement(String inspectionEnhancement) {
            this.inspectionEnhancement = inspectionEnhancement;
        }

        public String getInspectionVerification() {
            return inspectionVerification;
        }

        public void setInspectionVerification(String inspectionVerification) {
            this.inspectionVerification = inspectionVerification;
        }

        public String getInspectionDlq() {
            return inspectionDlq;
        }

        public void setInspectionDlq(String inspectionDlq) {
            this.inspectionDlq = inspectionDlq;
        }
    }
}
