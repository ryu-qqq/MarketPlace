package com.ryuqq.marketplace.application.outboundproduct.internal;

import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyProductIdMappingReadManager;
import com.ryuqq.marketplace.application.outboundproduct.dto.vo.ExternalProductEntry;
import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductCommandManager;
import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.application.outboundproduct.port.out.client.SalesChannelProductSearchClient;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 외부 채널 상품 초기화 코디네이터.
 *
 * <p>외부 채널에 이미 등록된 상품을 조회하여 outbound_products 테이블에 매핑을 초기화합니다. sellerManagementCode →
 * legacy_product_id_mappings → internal_product_group_id 경로로 매핑합니다.
 */
@Component
public class ExternalProductInitializer {

    private static final Logger log = LoggerFactory.getLogger(ExternalProductInitializer.class);

    private final LegacyProductIdMappingReadManager legacyMappingReadManager;
    private final OutboundProductReadManager outboundProductReadManager;
    private final OutboundProductCommandManager outboundProductCommandManager;

    public ExternalProductInitializer(
            LegacyProductIdMappingReadManager legacyMappingReadManager,
            OutboundProductReadManager outboundProductReadManager,
            OutboundProductCommandManager outboundProductCommandManager) {
        this.legacyMappingReadManager = legacyMappingReadManager;
        this.outboundProductReadManager = outboundProductReadManager;
        this.outboundProductCommandManager = outboundProductCommandManager;
    }

    /**
     * 외부 채널 상품을 조회하여 outbound_products에 매핑을 초기화합니다.
     *
     * @param searchClient 외부 채널 상품 조회 클라이언트
     * @param salesChannelId 판매채널 ID
     * @return 초기화 결과
     */
    public InitResult initialize(
            SalesChannelProductSearchClient searchClient, long salesChannelId, long shopId) {
        log.info(
                "외부 상품 초기화 시작: channel={}, salesChannelId={}",
                searchClient.channelCode(),
                salesChannelId);

        // 1. 외부 채널에서 전체 상품 조회
        List<ExternalProductEntry> externalProducts = searchClient.fetchAllProducts();
        log.info("외부 채널 상품 조회 완료: {}건", externalProducts.size());

        // 2. sellerManagementCode → legacyProductGroupId로 사용하여 레거시 매핑 조회
        Set<Long> legacyGroupIds =
                externalProducts.stream()
                        .filter(e -> e.sellerManagementCode() != null)
                        .map(e -> parseLong(e.sellerManagementCode()))
                        .filter(id -> id != null)
                        .collect(Collectors.toSet());

        log.info("레거시 매핑 조회 대상: {}건", legacyGroupIds.size());

        List<LegacyProductIdMapping> legacyMappings =
                legacyMappingReadManager.findByLegacyProductGroupIds(legacyGroupIds);

        // legacyProductGroupId → internalProductGroupId 맵 (그룹 레벨이므로 distinct)
        Map<Long, Long> legacyToInternalGroupMap =
                legacyMappings.stream()
                        .collect(
                                Collectors.toMap(
                                        LegacyProductIdMapping::legacyProductGroupId,
                                        LegacyProductIdMapping::internalProductGroupId,
                                        (a, b) -> a));

        log.info("레거시 → 내부 그룹 매핑: {}건", legacyToInternalGroupMap.size());

        // 3. 이미 존재하는 outbound_products 확인 (중복 방지)
        Set<Long> internalGroupIds = Set.copyOf(legacyToInternalGroupMap.values());
        List<OutboundProduct> existingProducts =
                outboundProductReadManager.findByProductGroupIds(List.copyOf(internalGroupIds));

        Set<Long> alreadyMappedGroupIds =
                existingProducts.stream()
                        .filter(op -> op.salesChannelIdValue() == salesChannelId)
                        .map(OutboundProduct::productGroupIdValue)
                        .collect(Collectors.toSet());

        log.info("이미 매핑된 상품그룹: {}건", alreadyMappedGroupIds.size());

        // 4. 신규 OutboundProduct 생성
        Instant now = Instant.now();
        List<OutboundProduct> newProducts = new ArrayList<>();
        int skippedNoMapping = 0;
        int skippedAlreadyExists = 0;

        for (ExternalProductEntry entry : externalProducts) {
            Long legacyGroupId = parseLong(entry.sellerManagementCode());
            if (legacyGroupId == null) {
                skippedNoMapping++;
                continue;
            }

            Long internalGroupId = legacyToInternalGroupMap.get(legacyGroupId);
            if (internalGroupId == null) {
                skippedNoMapping++;
                log.debug(
                        "레거시 매핑 없음: sellerManagementCode={}, externalProductId={}",
                        entry.sellerManagementCode(),
                        entry.externalProductId());
                continue;
            }

            if (alreadyMappedGroupIds.contains(internalGroupId)) {
                skippedAlreadyExists++;
                continue;
            }

            // 중복 저장 방지 (같은 배치 내)
            alreadyMappedGroupIds.add(internalGroupId);

            newProducts.add(
                    OutboundProduct.forNewWithExternalId(
                            ProductGroupId.of(internalGroupId),
                            SalesChannelId.of(salesChannelId),
                            shopId,
                            entry.externalProductId(),
                            now));
        }

        // 5. 저장
        List<Long> savedIds = List.of();
        if (!newProducts.isEmpty()) {
            savedIds = outboundProductCommandManager.persistAll(newProducts);
        }

        InitResult result =
                new InitResult(
                        externalProducts.size(),
                        savedIds.size(),
                        skippedNoMapping,
                        skippedAlreadyExists);

        log.info(
                "외부 상품 초기화 완료: 전체={}, 신규저장={}, 매핑없음={}, 이미존재={}",
                result.totalExternal(),
                result.saved(),
                result.skippedNoMapping(),
                result.skippedAlreadyExists());

        return result;
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 초기화 결과. */
    public record InitResult(
            int totalExternal, int saved, int skippedNoMapping, int skippedAlreadyExists) {}
}
