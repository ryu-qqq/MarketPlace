package com.ryuqq.marketplace.adapter.out.client.fileflow.adapter;

import com.ryuqq.marketplace.adapter.out.client.fileflow.config.FileFlowClientProperties;
import com.ryuqq.marketplace.application.common.port.out.InternalImageUrlChecker;
import java.net.URI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * FileFlow CDN 도메인 기반 내부 URL 판별 어댑터.
 *
 * <p>fileflow.cdn-domain 설정값과 URL의 호스트를 비교하여
 * 내부 스토리지 URL인지 판별합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(prefix = "fileflow", name = "cdn-domain")
public class FileFlowInternalImageUrlChecker implements InternalImageUrlChecker {

    private final String cdnDomain;

    public FileFlowInternalImageUrlChecker(FileFlowClientProperties properties) {
        this.cdnDomain = properties.cdnDomain();
    }

    @Override
    public boolean isInternal(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        try {
            String host = URI.create(url).getHost();
            return cdnDomain.equals(host);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
