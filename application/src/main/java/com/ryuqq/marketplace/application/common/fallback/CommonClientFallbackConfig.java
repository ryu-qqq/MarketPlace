package com.ryuqq.marketplace.application.common.fallback;

import com.ryuqq.marketplace.application.common.dto.command.ExternalDownloadRequest;
import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.ExternalDownloadResponse;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.common.port.out.client.FileStorageClient;
import java.time.Clock;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 공통 인프라 빈 폴백 설정.
 *
 * <p>FileFlow 어댑터 또는 bootstrap-web-api의 CoreConfiguration이 클래스패스에 없는 경우 NoOp/기본 구현체를 제공합니다. 실제 빈이
 * 존재하면 이 폴백은 무시됩니다.
 */
@Configuration
public class CommonClientFallbackConfig {

    @Bean
    @ConditionalOnMissingBean
    Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean("batchExecutor")
    @ConditionalOnMissingBean(name = "batchExecutor")
    ExecutorService batchExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    @ConditionalOnMissingBean
    FileStorageClient noOpFileStorageClient() {
        return new FileStorageClient() {
            @Override
            public PresignedUrlResponse generateUploadUrl(PresignedUploadUrlRequest request) {
                throw new UnsupportedOperationException("FileStorageClient not available");
            }

            @Override
            public String generateDownloadUrl(String fileAssetId, int expirationMinutes) {
                throw new UnsupportedOperationException("FileStorageClient not available");
            }

            @Override
            public void deleteFile(String fileAssetId) {
                throw new UnsupportedOperationException("FileStorageClient not available");
            }

            @Override
            public void deleteFiles(List<String> fileAssetIds) {
                throw new UnsupportedOperationException("FileStorageClient not available");
            }

            @Override
            public ExternalDownloadResponse downloadFromExternalUrl(
                    ExternalDownloadRequest request) {
                throw new UnsupportedOperationException("FileStorageClient not available");
            }

            @Override
            public List<ExternalDownloadResponse> downloadFromExternalUrls(
                    List<ExternalDownloadRequest> requests) {
                throw new UnsupportedOperationException("FileStorageClient not available");
            }

            @Override
            public void completeUploadSession(String sessionId, long fileSize, String etag) {
                throw new UnsupportedOperationException("FileStorageClient not available");
            }

            @Override
            public String uploadHtmlContent(String htmlContent, String category, String filename) {
                throw new UnsupportedOperationException("FileStorageClient not available");
            }
        };
    }
}
