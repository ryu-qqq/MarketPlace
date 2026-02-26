package com.ryuqq.marketplace.application.legacy.product.internal;

import com.ryuqq.marketplace.application.legacy.product.facade.LegacyOptionRegistrationFacade;
import com.ryuqq.marketplace.application.legacy.shared.dto.bundle.LegacyProductRegistrationBundle.OptionEntry;
import com.ryuqq.marketplace.application.legacy.shared.dto.bundle.LegacyProductRegistrationBundle.SkuEntry;
import com.ryuqq.marketplace.domain.legacy.optiondetail.aggregate.LegacyOptionDetail;
import com.ryuqq.marketplace.domain.legacy.optiongroup.aggregate.LegacyOptionGroup;
import com.ryuqq.marketplace.domain.legacy.optiongroup.id.LegacyOptionGroupId;
import com.ryuqq.marketplace.domain.legacy.optiongroup.vo.LegacyOptionName;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * 레거시 옵션 Resolver.
 *
 * <p>배치 내 중복 옵션그룹/옵션상세를 한 번만 INSERT하고 ID를 매핑합니다. 레거시 DB의 옵션 테이블은 글로벌 유니크가 아니므로 기존 레코드 조회 없이 이번 등록
 * 단위에서만 중복을 제거합니다.
 *
 * <p>등록/업데이트 코디네이터에서 공통 사용.
 */
@Component
public class LegacyOptionResolver {

    private final LegacyOptionRegistrationFacade optionFacade;

    public LegacyOptionResolver(LegacyOptionRegistrationFacade optionFacade) {
        this.optionFacade = optionFacade;
    }

    public ResolvedOptions resolve(List<SkuEntry> skus) {
        Map<String, Long> optionGroupIdMap = resolveOptionGroups(skus);
        Map<OptionDetailKey, Long> optionDetailIdMap = resolveOptionDetails(skus, optionGroupIdMap);
        return new ResolvedOptions(optionGroupIdMap, optionDetailIdMap);
    }

    private Map<String, Long> resolveOptionGroups(List<SkuEntry> skus) {
        Set<String> uniqueNames = new LinkedHashSet<>();
        for (SkuEntry sku : skus) {
            for (OptionEntry entry : sku.optionEntries()) {
                uniqueNames.add(entry.optionName());
            }
        }

        Map<String, Long> map = new LinkedHashMap<>();
        for (String optionName : uniqueNames) {
            Long groupId =
                    optionFacade.persistOptionGroup(
                            LegacyOptionGroup.forNew(LegacyOptionName.valueOf(optionName)));
            map.put(optionName, groupId);
        }
        return map;
    }

    private Map<OptionDetailKey, Long> resolveOptionDetails(
            List<SkuEntry> skus, Map<String, Long> optionGroupIdMap) {
        Map<OptionDetailKey, Long> map = new LinkedHashMap<>();
        for (SkuEntry sku : skus) {
            for (OptionEntry entry : sku.optionEntries()) {
                Long groupId = optionGroupIdMap.get(entry.optionName());
                OptionDetailKey key = new OptionDetailKey(groupId, entry.optionValue());
                if (!map.containsKey(key)) {
                    Long detailId =
                            optionFacade.persistOptionDetail(
                                    LegacyOptionDetail.forNew(
                                            LegacyOptionGroupId.of(groupId), entry.optionValue()));
                    map.put(key, detailId);
                }
            }
        }
        return map;
    }

    public record ResolvedOptions(
            Map<String, Long> optionGroupIdMap, Map<OptionDetailKey, Long> optionDetailIdMap) {

        public ResolvedOptions(
                Map<String, Long> optionGroupIdMap, Map<OptionDetailKey, Long> optionDetailIdMap) {
            this.optionGroupIdMap = Map.copyOf(optionGroupIdMap);
            this.optionDetailIdMap = Map.copyOf(optionDetailIdMap);
        }

        public Long optionGroupId(String optionName) {
            return optionGroupIdMap.get(optionName);
        }

        public Long optionDetailId(Long optionGroupId, String optionValue) {
            return optionDetailIdMap.get(new OptionDetailKey(optionGroupId, optionValue));
        }
    }

    public record OptionDetailKey(Long optionGroupId, String optionValue) {}
}
