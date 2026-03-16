package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupDetailResponse;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceProductMapper;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 세토프 전체 수정 실행기.
 *
 * <p>PUT /api/v2/admin/product-groups/{productGroupId}로 모든 필드를 전체 교체합니다. changedAreas가 비어있거나 변경 영역이
 * 임계값 이상일 때 사용됩니다.
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "base-url")
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "Spring-managed bean injection")
public class SetofFullProductUpdateExecutor implements SetofProductUpdateExecutor {

    private static final Logger log = LoggerFactory.getLogger(SetofFullProductUpdateExecutor.class);

    private final RestClient restClient;
    private final SetofCommerceProductMapper mapper;

    public SetofFullProductUpdateExecutor(
            RestClient setofCommerceRestClient, SetofCommerceProductMapper mapper) {
        this.restClient = setofCommerceRestClient;
        this.mapper = mapper;
    }

    /** changedAreas가 비어있으면(전체 수정) 항상 지원합니다. Provider에서 폴백으로도 사용됩니다. */
    @Override
    public boolean supports(Set<ChangedArea> changedAreas) {
        return changedAreas == null || changedAreas.isEmpty();
    }

    @Override
    public void execute(
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel,
            Set<ChangedArea> changedAreas,
            SetofProductGroupDetailResponse existingProduct) {

        SetofProductGroupUpdateRequest request =
                mapper.toUpdateRequest(
                        bundle, externalCategoryId, externalBrandId, existingProduct);

        log.info(
                "세토프 전체 수정 실행: externalProductId={}, productGroupId={}",
                externalProductId,
                bundle.group().idValue());

        restClient
                .put()
                .uri("/api/v2/admin/product-groups/{productGroupId}", externalProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        log.info("세토프 전체 수정 성공: externalProductId={}", externalProductId);
    }
}
