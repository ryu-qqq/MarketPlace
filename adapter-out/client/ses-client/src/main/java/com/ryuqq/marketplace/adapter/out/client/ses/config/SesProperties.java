package com.ryuqq.marketplace.adapter.out.client.ses.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AWS SES Client Properties.
 *
 * <p>ses.yml에서 설정을 읽어옵니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "ses")
public class SesProperties {

    private String region = "ap-northeast-2";
    private String senderEmail;
    private String signUpBaseUrl;
    private Map<String, String> templates = new HashMap<>();

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSignUpBaseUrl() {
        return signUpBaseUrl;
    }

    public void setSignUpBaseUrl(String signUpBaseUrl) {
        this.signUpBaseUrl = signUpBaseUrl;
    }

    public Map<String, String> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, String> templates) {
        this.templates = templates;
    }
}
