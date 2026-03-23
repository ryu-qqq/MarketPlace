package com.ryuqq.marketplace.adapter.in.rest.session.mapper;

import com.ryuqq.marketplace.adapter.in.rest.session.dto.command.LegacyImagePresignedApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.response.LegacyImagePresignedApiResponse;
import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.uploadsession.vo.UploadDirectory;
import java.net.URLConnection;
import java.util.Map;
import org.springframework.stereotype.Component;

/** 레거시 호환 이미지 Presigned URL API 변환 매퍼. */
@Component
public class LegacyImagePresignedApiMapper {

    private static final Map<String, UploadDirectory> IMAGE_PATH_MAPPING =
            Map.of(
                    "PRODUCT", UploadDirectory.PRODUCT_IMAGES,
                    "DESCRIPTION", UploadDirectory.DESCRIPTION,
                    "QNA", UploadDirectory.QNAS,
                    "CONTENT", UploadDirectory.CONTENTS,
                    "IMAGE_COMPONENT", UploadDirectory.CONTENTS,
                    "BANNER", UploadDirectory.CONTENTS);

    private static final long DEFAULT_FILE_SIZE = 10L * 1024 * 1024;
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    public PresignedUploadUrlRequest toCommand(LegacyImagePresignedApiRequest request) {
        UploadDirectory directory = resolveDirectory(request.imagePath());
        String contentType = guessContentType(request.fileName());
        long fileSize = request.fileSize() != null ? request.fileSize() : DEFAULT_FILE_SIZE;
        return PresignedUploadUrlRequest.of(directory, request.fileName(), contentType, fileSize);
    }

    public LegacyImagePresignedApiResponse toResponse(PresignedUrlResponse result) {
        return new LegacyImagePresignedApiResponse(
                result.sessionId(), result.presignedUrl(), result.fileKey());
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
