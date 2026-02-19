package com.ryuqq.marketplace.adapter.out.client.fileflow.adapter;

import com.ryuqq.fileflow.sdk.api.AssetApi;
import com.ryuqq.fileflow.sdk.api.TransformRequestApi;
import com.ryuqq.fileflow.sdk.exception.FileFlowBadRequestException;
import com.ryuqq.fileflow.sdk.exception.FileFlowException;
import com.ryuqq.fileflow.sdk.model.asset.AssetMetadataResponse;
import com.ryuqq.fileflow.sdk.model.asset.AssetResponse;
import com.ryuqq.fileflow.sdk.model.asset.RegisterAssetRequest;
import com.ryuqq.fileflow.sdk.model.common.ApiResponse;
import com.ryuqq.fileflow.sdk.model.transform.CreateTransformRequestRequest;
import com.ryuqq.fileflow.sdk.model.transform.TransformRequestResponse;
import com.ryuqq.marketplace.adapter.out.client.fileflow.mapper.FileFlowTransformMapper;
import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;
import com.ryuqq.marketplace.application.imagetransform.port.out.client.ImageTransformClient;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * FileFlow 이미지 변환 어댑터.
 *
 * <p>FileFlow SDK의 TransformRequestApi와 AssetApi를 사용하여 이미지 변환 요청 생성 및 상태 조회를 처리합니다.
 *
 * <p><strong>변환 요청 흐름</strong>:
 *
 * <ol>
 *   <li>CDN URL에서 S3 Key를 추출합니다.
 *   <li>AssetApi.register()를 통해 기존 S3 파일을 Asset으로 등록합니다.
 *   <li>TransformRequestApi.create()를 통해 이미지 변환을 요청합니다.
 *   <li>변환 완료 시 결과 Asset의 CDN URL을 빌드하여 반환합니다.
 * </ol>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(prefix = "fileflow", name = "base-url")
public class FileFlowTransformAdapter implements ImageTransformClient {

    private static final Logger log = LoggerFactory.getLogger(FileFlowTransformAdapter.class);
    private static final String SOURCE = "marketplace";
    private static final String ACCESS_TYPE_PUBLIC = "PUBLIC";
    private static final String PURPOSE_PRODUCT_IMAGE = "PRODUCT_IMAGE";
    private static final String CONTENT_TYPE_WEBP = "image/webp";
    private static final String COMPLETED = "COMPLETED";
    private static final String FAILED = "FAILED";

    private final TransformRequestApi transformRequestApi;
    private final AssetApi assetApi;
    private final FileFlowTransformMapper mapper;
    private final String cdnDomain;

    public FileFlowTransformAdapter(
            TransformRequestApi transformRequestApi,
            AssetApi assetApi,
            FileFlowTransformMapper mapper,
            @Value("${fileflow.cdn-domain:}") String cdnDomain) {
        this.transformRequestApi = transformRequestApi;
        this.assetApi = assetApi;
        this.mapper = mapper;
        this.cdnDomain = cdnDomain;
    }

    @Override
    public ImageTransformResponse createTransformRequest(
            String uploadedUrl, ImageVariantType variantType) {
        try {
            String s3Key = mapper.extractS3Key(uploadedUrl);
            String fileName = mapper.extractFileName(s3Key);

            ApiResponse<AssetResponse> registerResponse =
                    assetApi.register(
                            new RegisterAssetRequest(
                                    s3Key,
                                    cdnDomain,
                                    ACCESS_TYPE_PUBLIC,
                                    fileName,
                                    CONTENT_TYPE_WEBP,
                                    PURPOSE_PRODUCT_IMAGE,
                                    SOURCE));
            String assetId = registerResponse.data().assetId();

            ApiResponse<TransformRequestResponse> transformResponse =
                    transformRequestApi.create(
                            new CreateTransformRequestRequest(
                                    assetId,
                                    variantType.transformType(),
                                    variantType.width(),
                                    variantType.height(),
                                    variantType.quality(),
                                    variantType.targetFormat().toUpperCase()));
            String transformRequestId = transformResponse.data().transformRequestId();

            log.info(
                    "이미지 변환 요청 생성 완료: uploadedUrl={}, variantType={}, assetId={},"
                            + " transformRequestId={}",
                    uploadedUrl,
                    variantType,
                    assetId,
                    transformRequestId);

            return ImageTransformResponse.pending(transformRequestId);

        } catch (FileFlowBadRequestException e) {
            throw new IllegalArgumentException(
                    "FileFlow 이미지 변환 요청 실패 (잘못된 요청): " + e.getErrorMessage(), e);
        } catch (FileFlowException e) {
            throw new RuntimeException("FileFlow 이미지 변환 요청 실패: " + e.getErrorMessage(), e);
        }
    }

    @Override
    public ImageTransformResponse getTransformRequest(String transformRequestId) {
        try {
            ApiResponse<TransformRequestResponse> response =
                    transformRequestApi.get(transformRequestId);
            TransformRequestResponse result = response.data();

            if (COMPLETED.equals(result.status())) {
                return handleCompleted(transformRequestId, result);
            }

            if (FAILED.equals(result.status())) {
                log.warn(
                        "이미지 변환 실패: transformRequestId={}, error={}",
                        transformRequestId,
                        result.lastError());
                return ImageTransformResponse.failed(transformRequestId);
            }

            return ImageTransformResponse.processing(transformRequestId);

        } catch (FileFlowException e) {
            throw new RuntimeException(
                    "FileFlow 이미지 변환 상태 조회 실패: transformRequestId="
                            + transformRequestId
                            + ", error="
                            + e.getErrorMessage(),
                    e);
        }
    }

    private ImageTransformResponse handleCompleted(
            String transformRequestId, TransformRequestResponse result) {
        String resultAssetId = result.resultAssetId();

        ApiResponse<AssetResponse> assetResponse = assetApi.get(resultAssetId);
        AssetResponse asset = assetResponse.data();
        String resultCdnUrl = mapper.buildCdnUrl(asset.s3Key());

        ApiResponse<AssetMetadataResponse> metadataResponse = assetApi.getMetadata(resultAssetId);
        AssetMetadataResponse metadata = metadataResponse.data();

        log.info(
                "이미지 변환 완료: transformRequestId={}, resultAssetId={}, resultCdnUrl={}, width={},"
                        + " height={}",
                transformRequestId,
                resultAssetId,
                resultCdnUrl,
                metadata.width(),
                metadata.height());

        return ImageTransformResponse.completed(
                transformRequestId,
                resultAssetId,
                resultCdnUrl,
                metadata.width(),
                metadata.height());
    }
}
