package com.ryuqq.marketplace.adapter.out.client.fileflow.mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * FileFlow Transform Mapper.
 *
 * <p>CDN URL과 S3 Key 간의 변환을 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class FileFlowTransformMapper {

    private final String cdnDomain;

    public FileFlowTransformMapper(@Value("${fileflow.cdn-domain:}") String cdnDomain) {
        this.cdnDomain = cdnDomain;
    }

    /**
     * CDN URL에서 S3 Key를 추출합니다.
     *
     * <p>CDN 도메인 prefix를 제거하여 순수한 S3 Key를 반환합니다.
     *
     * @param cdnUrl CDN URL (예: https://cdn.example.com/images/product/image.jpg)
     * @return S3 Key (예: images/product/image.jpg)
     */
    public String extractS3Key(String cdnUrl) {
        if (cdnUrl == null || cdnUrl.isBlank()) {
            return cdnUrl;
        }

        String httpsPrefix = "https://" + cdnDomain + "/";
        if (cdnUrl.startsWith(httpsPrefix)) {
            return cdnUrl.substring(httpsPrefix.length());
        }

        String httpPrefix = "http://" + cdnDomain + "/";
        if (cdnUrl.startsWith(httpPrefix)) {
            return cdnUrl.substring(httpPrefix.length());
        }

        return cdnUrl;
    }

    /**
     * S3 Key로 CDN URL을 빌드합니다.
     *
     * @param s3Key S3 Key (예: images/product/image.jpg)
     * @return CDN URL (예: https://cdn.example.com/images/product/image.jpg)
     */
    public String buildCdnUrl(String s3Key) {
        if (s3Key == null || s3Key.isBlank()) {
            return null;
        }
        return "https://" + cdnDomain + "/" + s3Key;
    }

    /**
     * S3 Key에서 파일명을 추출합니다.
     *
     * @param s3Key S3 Key (예: images/product/image.jpg)
     * @return 파일명 (예: image.jpg)
     */
    public String extractFileName(String s3Key) {
        if (s3Key == null || s3Key.isBlank()) {
            return s3Key;
        }
        int lastSlash = s3Key.lastIndexOf('/');
        return lastSlash >= 0 ? s3Key.substring(lastSlash + 1) : s3Key;
    }
}
