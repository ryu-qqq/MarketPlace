package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofImageVariantSyncRequest;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import com.ryuqq.marketplace.application.imagevariantsync.port.out.client.ImageVariantSyncClient;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 세토프 커머스 이미지 Variant 동기화 어댑터.
 *
 * <p>이미지 변환 완료 후 Variant 정보를 세토프 Sync API로 동기화합니다.
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "base-url")
public class SetofCommerceImageVariantSyncAdapter implements ImageVariantSyncClient {

    private static final Logger log =
            LoggerFactory.getLogger(SetofCommerceImageVariantSyncAdapter.class);

    private final RestClient restClient;
    private final CircuitBreaker circuitBreaker;

    public SetofCommerceImageVariantSyncAdapter(
            RestClient setofCommerceRestClient, CircuitBreaker setofCommerceCircuitBreaker) {
        this.restClient = setofCommerceRestClient;
        this.circuitBreaker = setofCommerceCircuitBreaker;
    }

    @Override
    public void syncVariants(
            long sourceImageId, String sourceType, List<ImageVariantResult> variants) {
        SetofImageVariantSyncRequest request = toRequest(sourceImageId, sourceType, variants);

        try {
            circuitBreaker.executeRunnable(
                    () -> {
                        log.info(
                                "세토프 커머스 이미지 Variant 동기화 요청: sourceImageId={}, sourceType={},"
                                        + " variantCount={}",
                                sourceImageId,
                                sourceType,
                                variants.size());

                        restClient
                                .put()
                                .uri("/api/v2/admin/image-variants/sync")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity();

                        log.info("세토프 커머스 이미지 Variant 동기화 성공: sourceImageId={}", sourceImageId);
                    });
        } catch (CallNotPermittedException e) {
            throw new ExternalServiceUnavailableException(
                    "세토프 커머스 서비스 일시 중단 (Circuit Breaker OPEN)", e);
        }
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
