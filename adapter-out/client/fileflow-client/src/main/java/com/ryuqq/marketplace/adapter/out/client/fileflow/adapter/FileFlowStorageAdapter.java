package com.ryuqq.marketplace.adapter.out.client.fileflow.adapter;

import com.ryuqq.fileflow.sdk.api.AssetApi;
import com.ryuqq.fileflow.sdk.api.DownloadTaskApi;
import com.ryuqq.fileflow.sdk.api.SingleUploadSessionApi;
import com.ryuqq.fileflow.sdk.exception.FileFlowBadRequestException;
import com.ryuqq.fileflow.sdk.exception.FileFlowException;
import com.ryuqq.fileflow.sdk.exception.FileFlowServerException;
import com.ryuqq.fileflow.sdk.model.asset.AssetResponse;
import com.ryuqq.fileflow.sdk.model.common.ApiResponse;
import com.ryuqq.fileflow.sdk.model.download.CreateDownloadTaskRequest;
import com.ryuqq.fileflow.sdk.model.download.DownloadTaskResponse;
import com.ryuqq.fileflow.sdk.model.session.CompleteSingleUploadSessionRequest;
import com.ryuqq.fileflow.sdk.model.session.CreateSingleUploadSessionRequest;
import com.ryuqq.fileflow.sdk.model.session.SingleUploadSessionResponse;
import com.ryuqq.marketplace.adapter.out.client.fileflow.config.FileFlowClientProperties;
import com.ryuqq.marketplace.adapter.out.client.fileflow.mapper.FileFlowStorageMapper;
import com.ryuqq.marketplace.application.common.dto.command.ExternalDownloadRequest;
import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.ExternalDownloadResponse;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.common.port.out.client.FileStorageClient;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * FileFlow 파일 스토리지 어댑터.
 *
 * <p>FileFlow SDK를 사용하여 파일 업로드, 다운로드, 삭제를 처리합니다.
 *
 * <p>다운로드 태스크는 Polling 방식으로 완료를 대기합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(prefix = "fileflow", name = "base-url")
@SuppressWarnings("PMD.ExcessiveImports")
public class FileFlowStorageAdapter implements FileStorageClient {

    private static final Logger log = LoggerFactory.getLogger(FileFlowStorageAdapter.class);
    private static final String SOURCE = "marketplace";
    private static final String COMPLETED = "COMPLETED";
    private static final String FAILED = "FAILED";
    private static final int MAX_POLL_ATTEMPTS = 30;
    private static final long POLL_INTERVAL_MS = 2000L;

    private final SingleUploadSessionApi singleUploadSessionApi;
    private final DownloadTaskApi downloadTaskApi;
    private final AssetApi assetApi;
    private final FileFlowStorageMapper mapper;
    private final FileFlowClientProperties properties;
    private final HttpClient httpClient;

    public FileFlowStorageAdapter(
            SingleUploadSessionApi singleUploadSessionApi,
            DownloadTaskApi downloadTaskApi,
            AssetApi assetApi,
            FileFlowStorageMapper mapper,
            FileFlowClientProperties properties) {
        this.singleUploadSessionApi = singleUploadSessionApi;
        this.downloadTaskApi = downloadTaskApi;
        this.assetApi = assetApi;
        this.mapper = mapper;
        this.properties = properties;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public PresignedUrlResponse generateUploadUrl(PresignedUploadUrlRequest request) {
        try {
            CreateSingleUploadSessionRequest sdkRequest =
                    mapper.toCreateSingleUploadSessionRequest(request);
            ApiResponse<SingleUploadSessionResponse> response =
                    singleUploadSessionApi.create(sdkRequest);
            return mapper.toPresignedUrlResponse(response.data());
        } catch (FileFlowBadRequestException e) {
            throw new IllegalArgumentException(
                    "FileFlow upload URL 생성 실패 (잘못된 요청): " + e.getErrorMessage(), e);
        } catch (FileFlowException e) {
            throw new RuntimeException("FileFlow upload URL 생성 실패: " + e.getErrorMessage(), e);
        }
    }

    @Override
    public void completeUploadSession(String sessionId, long fileSize, String etag) {
        try {
            singleUploadSessionApi.complete(
                    sessionId, new CompleteSingleUploadSessionRequest(fileSize, etag));
        } catch (FileFlowBadRequestException e) {
            throw new IllegalArgumentException(
                    "FileFlow 업로드 세션 완료 실패 (잘못된 요청): " + e.getErrorMessage(), e);
        } catch (FileFlowException e) {
            throw new RuntimeException("FileFlow 업로드 세션 완료 실패: " + e.getErrorMessage(), e);
        }
    }

    @Override
    public String generateDownloadUrl(String fileAssetId, int expirationMinutes) {
        try {
            ApiResponse<AssetResponse> response = assetApi.get(fileAssetId);
            AssetResponse asset = response.data();
            return "https://" + properties.cdnDomain() + "/" + asset.s3Key();
        } catch (FileFlowException e) {
            throw new RuntimeException(
                    "FileFlow download URL 생성 실패: assetId="
                            + fileAssetId
                            + ", error="
                            + e.getErrorMessage(),
                    e);
        }
    }

    @Override
    public void deleteFile(String fileAssetId) {
        try {
            assetApi.delete(fileAssetId, SOURCE);
        } catch (FileFlowException e) {
            log.warn(
                    "FileFlow 파일 삭제 실패: assetId={}, error={}", fileAssetId, e.getErrorMessage(), e);
        }
    }

    @Override
    public void deleteFiles(List<String> fileAssetIds) {
        for (String fileAssetId : fileAssetIds) {
            deleteFile(fileAssetId);
        }
    }

    @Override
    public ExternalDownloadResponse downloadFromExternalUrl(ExternalDownloadRequest request) {
        try {
            CreateDownloadTaskRequest sdkRequest = mapper.toCreateDownloadTaskRequest(request);
            ApiResponse<DownloadTaskResponse> createResponse = downloadTaskApi.create(sdkRequest);
            String taskId = createResponse.data().downloadTaskId();

            DownloadTaskResponse completedTask = pollUntilCompleted(taskId);

            if (FAILED.equals(completedTask.status())) {
                return ExternalDownloadResponse.failure(
                        request.sourceUrl(), completedTask.lastError());
            }

            return mapper.toExternalDownloadResponse(completedTask, request.sourceUrl());

        } catch (FileFlowBadRequestException e) {
            return ExternalDownloadResponse.failure(
                    request.sourceUrl(), "잘못된 요청: " + e.getErrorMessage());
        } catch (FileFlowServerException e) {
            return ExternalDownloadResponse.failure(
                    request.sourceUrl(), "서버 오류: " + e.getErrorMessage());
        } catch (FileFlowException e) {
            return ExternalDownloadResponse.failure(
                    request.sourceUrl(), "FileFlow 오류: " + e.getErrorMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ExternalDownloadResponse.failure(request.sourceUrl(), "다운로드 태스크 대기 중 인터럽트 발생");
        }
    }

    @Override
    public List<ExternalDownloadResponse> downloadFromExternalUrls(
            List<ExternalDownloadRequest> requests) {
        List<CompletableFuture<ExternalDownloadResponse>> futures =
                new ArrayList<>(requests.size());
        for (ExternalDownloadRequest request : requests) {
            futures.add(CompletableFuture.supplyAsync(() -> downloadFromExternalUrl(request)));
        }
        return futures.stream().map(CompletableFuture::join).toList();
    }

    @Override
    public String uploadHtmlContent(String htmlContent, String category, String filename) {
        try {
            byte[] contentBytes = htmlContent.getBytes(StandardCharsets.UTF_8);
            long fileSize = contentBytes.length;

            CreateSingleUploadSessionRequest request =
                    new CreateSingleUploadSessionRequest(
                            filename, "text/html", "PUBLIC", category, SOURCE);
            ApiResponse<SingleUploadSessionResponse> response =
                    singleUploadSessionApi.create(request);
            SingleUploadSessionResponse session = response.data();

            HttpRequest putRequest =
                    HttpRequest.newBuilder()
                            .uri(URI.create(session.presignedUrl()))
                            .header("Content-Type", "text/html")
                            .PUT(HttpRequest.BodyPublishers.ofByteArray(contentBytes))
                            .build();
            HttpResponse<String> putResponse =
                    httpClient.send(putRequest, HttpResponse.BodyHandlers.ofString());

            if (putResponse.statusCode() < 200 || putResponse.statusCode() >= 300) {
                throw new RuntimeException(
                        "S3 presigned URL PUT 실패: statusCode="
                                + putResponse.statusCode()
                                + ", body="
                                + putResponse.body());
            }

            String etag = putResponse.headers().firstValue("ETag").orElse(null);

            singleUploadSessionApi.complete(
                    session.sessionId(), new CompleteSingleUploadSessionRequest(fileSize, etag));

            return "https://" + properties.cdnDomain() + "/" + session.s3Key();
        } catch (FileFlowBadRequestException e) {
            throw new IllegalArgumentException(
                    "FileFlow HTML 업로드 실패 (잘못된 요청): " + e.getErrorMessage(), e);
        } catch (FileFlowException e) {
            throw new RuntimeException("FileFlow HTML 업로드 실패: " + e.getErrorMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("FileFlow HTML 업로드 중 인터럽트 발생", e);
        } catch (Exception e) {
            throw new RuntimeException("FileFlow HTML 업로드 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private DownloadTaskResponse pollUntilCompleted(String taskId) throws InterruptedException {
        for (int attempt = 0; attempt < MAX_POLL_ATTEMPTS; attempt++) {
            ApiResponse<DownloadTaskResponse> response = downloadTaskApi.get(taskId);
            DownloadTaskResponse task = response.data();

            if (COMPLETED.equals(task.status()) || FAILED.equals(task.status())) {
                return task;
            }

            Thread.sleep(POLL_INTERVAL_MS);
        }

        throw new RuntimeException(
                "FileFlow 다운로드 태스크 타임아웃: taskId=" + taskId + ", maxAttempts=" + MAX_POLL_ATTEMPTS);
    }
}
