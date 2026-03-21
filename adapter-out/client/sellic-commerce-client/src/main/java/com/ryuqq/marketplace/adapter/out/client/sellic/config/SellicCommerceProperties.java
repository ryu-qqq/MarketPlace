package com.ryuqq.marketplace.adapter.out.client.sellic.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Sellic Commerce Client Properties.
 *
 * <p>sellic-commerce.yml에서 설정을 읽어옵니다.
 */
@ConfigurationProperties(prefix = "sellic-commerce")
public class SellicCommerceProperties {

    private String baseUrl;
    private String customerId;
    private String apiKey;
    private Timeout timeout = new Timeout();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Timeout getTimeout() {
        return timeout;
    }

    public void setTimeout(Timeout timeout) {
        this.timeout = timeout;
    }

    @SuppressWarnings("PMD.DataClass")
    public static class Timeout {
        private Duration connect = Duration.ofSeconds(5);
        private Duration read = Duration.ofSeconds(30);

        public Duration getConnect() {
            return connect;
        }

        public void setConnect(Duration connect) {
            this.connect = connect;
        }

        public Duration getRead() {
            return read;
        }

        public void setRead(Duration read) {
            this.read = read;
        }
    }
}
