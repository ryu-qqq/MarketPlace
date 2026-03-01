package com.ryuqq.marketplace.adapter.out.client.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring AI Client Properties.
 *
 * <p>spring-ai-client.yml에서 설정을 읽어옵니다.
 */
@ConfigurationProperties(prefix = "spring-ai")
public class SpringAiClientProperties {

    private String model = "gpt-4.1";
    private double temperature = 0.3;
    private int maxTokens = 4096;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
}
