package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.setof.config.SetofCommerceProperties;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofImageVariantSyncRequest;
import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import com.ryuqq.marketplace.application.imagevariantsync.port.out.client.ImageVariantSyncClient;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 이미지 Variant 동기화 어댑터.
 *
 * <p>이미지 변환 완료 후 Variant 정보를 세토프 Sync API로 동기화합니다. HTTP 호출은 {@link SetofCommerceApiClient}에 위임합니다.
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "base-url")
public class SetofCommerceImageVariantSyncAdapter implements ImageVariantSyncClient {

    private static final Logger log =
            LoggerFactory.getLogger(SetofCommerceImageVariantSyncAdapter.class);

    private final SetofCommerceApiClient apiClient;
    private final SetofCommerceProperties properties;

    public SetofCommerceImageVariantSyncAdapter(
            SetofCommerceApiClient apiClient, SetofCommerceProperties properties) {
        this.apiClient = apiClient;
        this.properties = properties;
    }

    @Override
    public void syncVariants(
            long sourceImageId, String sourceType, List<ImageVariantResult> variants) {
        SetofImageVariantSyncRequest request = toRequest(sourceImageId, sourceType, variants);

        log.info(
                "세토프 커머스 이미지 Variant 동기화 요청: sourceImageId={}, sourceType={}, variantCount={}",
                sourceImageId,
                sourceType,
                variants.size());

        apiClient.syncImageVariants(properties.getServiceToken(), request);

        log.info("세토프 커머스 이미지 Variant 동기화 성공: sourceImageId={}", sourceImageId);
    }

    private SetofImageVariantSyncRequest toRequest(
            long sourceImageId, String sourceType, List<ImageVariantResult> variants) {
        List<SetofImageVariantSyncRequest.VariantRequest> variantRequests =
                variants.stream()
                        .map(
                                v ->
                                        new SetofImageVariantSyncRequest.VariantRequest(
                                                v.variantType().name(),
                                                v.resultAssetId(),
                                                v.variantUrl(),
                                                v.width(),
                                                v.height()))
                        .toList();
        return new SetofImageVariantSyncRequest(sourceImageId, sourceType, variantRequests);
    }
}
