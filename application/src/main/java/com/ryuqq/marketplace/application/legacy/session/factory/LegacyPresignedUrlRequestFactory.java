package com.ryuqq.marketplace.application.legacy.session.factory;

import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.legacy.session.dto.command.LegacyGetPresignedUrlCommand;
import com.ryuqq.marketplace.application.uploadsession.vo.UploadDirectory;
import java.net.URLConnection;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 레거시 커맨드 → PresignedUploadUrlRequest 변환 Factory.
 *
 * <p>세토프 어드민의 ImagePath를 MarketPlace의 UploadDirectory로 매핑하고, 파일명으로 contentType을 추론합니다.
 */
@Component
public class LegacyPresignedUrlRequestFactory {

    private static final Map<String, UploadDirectory> IMAGE_PATH_MAPPING =
            Map.of(
                    "PRODUCT", UploadDirectory.PRODUCT_IMAGES,
                    "DESCRIPTION", UploadDirectory.DESCRIPTION,
                    "QNA", UploadDirectory.QNAS,
                    "CONTENT", UploadDirectory.CONTENTS,
                    "IMAGE_COMPONENT", UploadDirectory.CONTENTS,
                    "BANNER", UploadDirectory.CONTENTS);

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    public PresignedUploadUrlRequest create(LegacyGetPresignedUrlCommand command) {
        UploadDirectory directory = resolveDirectory(command.imagePath());
        String contentType = guessContentType(command.fileName());
        return PresignedUploadUrlRequest.of(
                directory, command.fileName(), contentType, command.fileSizeOrDefault());
    }

    private UploadDirectory resolveDirectory(String imagePath) {
        if (imagePath == null) {
            return UploadDirectory.PRODUCT_IMAGES;
        }
        UploadDirectory mapped = IMAGE_PATH_MAPPING.get(imagePath);
        return mapped != null ? mapped : UploadDirectory.PRODUCT_IMAGES;
    }

    private String guessContentType(String fileName) {
        if (fileName == null) {
            return DEFAULT_CONTENT_TYPE;
        }
        String guessed = URLConnection.guessContentTypeFromName(fileName);
        return guessed != null ? guessed : DEFAULT_CONTENT_TYPE;
    }
}
