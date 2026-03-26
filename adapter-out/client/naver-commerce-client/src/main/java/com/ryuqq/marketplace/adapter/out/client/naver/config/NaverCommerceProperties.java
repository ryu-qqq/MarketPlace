package com.ryuqq.marketplace.adapter.out.client.naver.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Naver Commerce Client Properties.
 *
 * <p>naver-commerce.yml에서 설정을 읽어옵니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "naver-commerce")
public class NaverCommerceProperties {

    private String baseUrl = "https://api.commerce.naver.com/external";
    private String clientId;
    private String clientSecret;
    private boolean enabled = false;
    private Timeout timeout = new Timeout();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
