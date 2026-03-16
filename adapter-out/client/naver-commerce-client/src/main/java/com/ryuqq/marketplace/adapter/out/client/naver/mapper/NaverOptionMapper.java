package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductDetailResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OptionInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OptionInfo.OptionCombination;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OptionInfo.OptionCombinationGroupNames;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OptionInfo.OptionCustom;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionInputType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 옵션 정보 변환 매퍼.
 *
 * <p>등록 시에는 순차 ID를 부여하고, 수정 시에는 기존 네이버 combination ID를 매칭하여 유지합니다.
 *
 * <p>inputType에 따라 PREDEFINED 그룹은 optionCombinations로, FREE_INPUT 그룹은 optionCustom으로 분리하여 매핑합니다.
 */
final class NaverOptionMapper {

    private static final String OPTION_SORT_CREATE = "CREATE";

    private NaverOptionMapper() {}

    /** 등록용: ID 없이 전송 (네이버가 자동 부여). */
    static OptionInfo mapOptionInfo(ProductGroup group, List<Product> products) {
        List<SellerOptionGroup> optionGroups = group.sellerOptionGroups();
        if (optionGroups.isEmpty()) {
            return null;
        }

        boolean soldOut = group.status().isSoldout();
        List<SellerOptionGroup> combinationGroups = resolveCombinationGroups(optionGroups);
        List<SellerOptionGroup> customGroups = resolveCustomGroups(optionGroups);

        OptionCombinationGroupNames groupNames = combinationGroups.isEmpty() ? null : buildGroupNames(combinationGroups);
        List<OptionCombination> combinations = combinationGroups.isEmpty() ? List.of() : buildCombinations(combinationGroups, products, null, soldOut);
        List<OptionCustom> optionCustom = customGroups.isEmpty() ? List.of() : buildOptionCustom(customGroups);

        String sortType = combinationGroups.isEmpty() ? null : OPTION_SORT_CREATE;
        return new OptionInfo(sortType, groupNames, combinations, optionCustom);
    }

    /**
     * 수정용: 기존 네이버 옵션 combination ID를 매칭하여 유지.
     *
     * <p>매칭 우선순위:
     *
     * <ol>
     *   <li>sellerManagerCode(SKU 코드) 일치
     *   <li>optionName 조합 일치
     * </ol>
     *
     * 매칭되지 않는 신규 옵션은 ID 없이(null) 전송하여 네이버가 새로 부여하도록 합니다. 내부 Product에 없는 기존 네이버 옵션은 요청에서 제외되어 자동
     * 삭제됩니다.
     */
    static OptionInfo mapOptionInfoForUpdate(
            ProductGroup group,
            List<Product> products,
            NaverProductDetailResponse existingProduct) {

        List<SellerOptionGroup> optionGroups = group.sellerOptionGroups();
        if (optionGroups.isEmpty()) {
            return null;
        }

        boolean soldOut = group.status().isSoldout();
        List<SellerOptionGroup> combinationGroups = resolveCombinationGroups(optionGroups);
        List<SellerOptionGroup> customGroups = resolveCustomGroups(optionGroups);

        OptionCombinationGroupNames groupNames = combinationGroups.isEmpty() ? null : buildGroupNames(combinationGroups);
        List<OptionCombination> combinations = combinationGroups.isEmpty()
                ? List.of()
                : buildCombinationsForUpdate(combinationGroups, products, existingProduct, soldOut);
        List<OptionCustom> optionCustom = customGroups.isEmpty() ? List.of() : buildOptionCustomForUpdate(customGroups, existingProduct);

        String sortType = combinationGroups.isEmpty() ? null : OPTION_SORT_CREATE;
        return new OptionInfo(sortType, groupNames, combinations, optionCustom);
    }

    /**
     * optionCombinations로 보낼 그룹을 결정합니다.
     *
     * <p>PREDEFINED 그룹 + FREE_INPUT이면서 옵션값 2개 이상인 그룹 (선택형으로 전송).
     */
    private static List<SellerOptionGroup> resolveCombinationGroups(List<SellerOptionGroup> optionGroups) {
        return optionGroups.stream()
                .filter(g -> g.inputType() == OptionInputType.PREDEFINED
                        || (g.inputType() == OptionInputType.FREE_INPUT && g.optionValueCount() >= 2))
                .toList();
    }

    /**
     * optionCustom으로 보낼 그룹을 결정합니다.
     *
     * <p>모든 FREE_INPUT 그룹은 optionCustom으로도 보냅니다.
     * 옵션값이 여러 개인 경우 combination + custom 양쪽 모두 전송되어,
     * 구매자가 선택형 값을 고르면서 동시에 자유입력란도 사용할 수 있습니다.
     */
    private static List<SellerOptionGroup> resolveCustomGroups(List<SellerOptionGroup> optionGroups) {
        return optionGroups.stream()
                .filter(g -> g.inputType() == OptionInputType.FREE_INPUT)
                .toList();
    }

    // === PREDEFINED 옵션 (optionCombinations) ===

    private static List<OptionCombination> buildCombinations(
            List<SellerOptionGroup> predefinedGroups,
            List<Product> products,
            Map<String, Long> skuToNaverId,
            boolean soldOut) {

        Map<Long, SellerOptionValue> optionValueMap = buildOptionValueMap(predefinedGroups);
        int basePrice = resolveBasePrice(products);
        List<OptionCombination> combinations = new ArrayList<>();

        for (Product product : products) {
            List<String> optionNames = resolveOptionNames(product, predefinedGroups, optionValueMap);
            if (optionNames.isEmpty()) {
                continue;
            }
            Long naverId = skuToNaverId != null
                    ? matchNaverIdBySkuOrOptionKey(product.skuCodeValue(), optionNames, skuToNaverId, Map.of())
                    : null;
            combinations.add(buildCombination(naverId, optionNames, product, basePrice, soldOut));
        }

        return combinations;
    }

    private static List<OptionCombination> buildCombinationsForUpdate(
            List<SellerOptionGroup> predefinedGroups,
            List<Product> products,
            NaverProductDetailResponse existingProduct,
            boolean soldOut) {

        Map<Long, SellerOptionValue> optionValueMap = buildOptionValueMap(predefinedGroups);
        int basePrice = resolveBasePrice(products);

        List<NaverProductDetailResponse.OptionCombination> existingCombinations =
                resolveExistingCombinations(existingProduct);

        Map<String, Long> skuToNaverId = new HashMap<>();
        Map<String, Long> optionKeyToNaverId = new HashMap<>();

        for (NaverProductDetailResponse.OptionCombination ec : existingCombinations) {
            if (ec.sellerManagerCode() != null && !ec.sellerManagerCode().isBlank()) {
                skuToNaverId.put(ec.sellerManagerCode(), ec.id());
            }
            String key = buildOptionKey(ec.optionName1(), ec.optionName2(), ec.optionName3());
            optionKeyToNaverId.put(key, ec.id());
        }

        List<OptionCombination> combinations = new ArrayList<>();

        for (Product product : products) {
            List<String> optionNames = resolveOptionNames(product, predefinedGroups, optionValueMap);
            if (optionNames.isEmpty()) {
                continue;
            }
            Long naverId = matchNaverIdBySkuOrOptionKey(
                    product.skuCodeValue(), optionNames, skuToNaverId, optionKeyToNaverId);
            combinations.add(buildCombination(naverId, optionNames, product, basePrice, soldOut));
        }

        return combinations;
    }

    // === FREE_INPUT 옵션 (optionCustom) ===

    /** 등록용: groupName 전송. 옵션값 여러개인 경우 combination과 이름 중복 방지를 위해 접미사 추가. */
    private static List<OptionCustom> buildOptionCustom(List<SellerOptionGroup> freeInputGroups) {
        return freeInputGroups.stream()
                .map(g -> new OptionCustom(null, resolveCustomGroupName(g), true))
                .toList();
    }

    /** 수정용: 기존 네이버 optionCustom ID 매칭. */
    private static List<OptionCustom> buildOptionCustomForUpdate(
            List<SellerOptionGroup> freeInputGroups,
            NaverProductDetailResponse existingProduct) {

        Map<String, Long> existingCustomByName = resolveExistingOptionCustom(existingProduct);

        return freeInputGroups.stream()
                .map(g -> {
                    String customName = resolveCustomGroupName(g);
                    Long existingId = existingCustomByName.get(customName);
                    return new OptionCustom(existingId, customName, true);
                })
                .toList();
    }

    /** 기존 네이버 optionCustom에서 groupName → id 매핑 추출. */
    private static Map<String, Long> resolveExistingOptionCustom(
            NaverProductDetailResponse existingProduct) {
        if (existingProduct == null
                || existingProduct.originProduct() == null
                || existingProduct.originProduct().detailAttribute() == null
                || existingProduct.originProduct().detailAttribute().optionInfo() == null
                || existingProduct.originProduct().detailAttribute().optionInfo().optionCustom() == null) {
            return Map.of();
        }

        Map<String, Long> map = new HashMap<>();
        for (NaverProductDetailResponse.OptionCustom oc :
                existingProduct.originProduct().detailAttribute().optionInfo().optionCustom()) {
            if (oc.groupName() != null) {
                map.put(oc.groupName(), oc.id());
            }
        }
        return map;
    }

    /**
     * optionCustom groupName 결정.
     *
     * <p>옵션값이 2개 이상이면 combination에도 같은 이름이 들어가므로,
     * 네이버 중복 방지를 위해 "(자유입력)" 접미사를 추가합니다.
     */
    private static String resolveCustomGroupName(SellerOptionGroup group) {
        if (group.optionValueCount() >= 2) {
            return group.optionGroupNameValue() + "(자유입력)";
        }
        return group.optionGroupNameValue();
    }

    // === 공통 유틸 ===

    private static Long matchNaverIdBySkuOrOptionKey(
            String skuCode,
            List<String> optionNames,
            Map<String, Long> skuToNaverId,
            Map<String, Long> optionKeyToNaverId) {

        if (skuCode != null && !skuCode.isBlank()) {
            Long id = skuToNaverId.get(skuCode);
            if (id != null) {
                return id;
            }
        }

        String key =
                buildOptionKey(
                        optionNames.size() > 0 ? optionNames.get(0) : null,
                        optionNames.size() > 1 ? optionNames.get(1) : null,
                        optionNames.size() > 2 ? optionNames.get(2) : null);
        return optionKeyToNaverId.get(key);
    }

    private static String buildOptionKey(String name1, String name2, String name3) {
        return (name1 != null ? name1 : "")
                + "|"
                + (name2 != null ? name2 : "")
                + "|"
                + (name3 != null ? name3 : "");
    }

    private static List<NaverProductDetailResponse.OptionCombination> resolveExistingCombinations(
            NaverProductDetailResponse existingProduct) {
        if (existingProduct == null
                || existingProduct.originProduct() == null
                || existingProduct.originProduct().detailAttribute() == null
                || existingProduct.originProduct().detailAttribute().optionInfo() == null
                || existingProduct
                                .originProduct()
                                .detailAttribute()
                                .optionInfo()
                                .optionCombinations()
                        == null) {
            return List.of();
        }
        return existingProduct.originProduct().detailAttribute().optionInfo().optionCombinations();
    }

    /**
     * 옵션 조합을 생성합니다.
     *
     * <p>price는 대표가격(salePrice) 대비 차액입니다. 네이버 API에서 옵션 price는 절대 가격이 아닌 추가/할인 금액입니다.
     */
    private static OptionCombination buildCombination(
            Long id, List<String> optionNames, Product product, int basePrice, boolean soldOut) {
        int priceDiff = product.currentPriceValue() - basePrice;
        int stock = soldOut ? 0 : product.stockQuantity();
        return new OptionCombination(
                id,
                optionNames.size() > 0 ? optionNames.get(0) : null,
                optionNames.size() > 1 ? optionNames.get(1) : null,
                optionNames.size() > 2 ? optionNames.get(2) : null,
                stock,
                priceDiff,
                product.skuCodeValue(),
                !soldOut && product.stockQuantity() > 0);
    }

    private static int resolveBasePrice(List<Product> products) {
        return products.stream().mapToInt(Product::currentPriceValue).min().orElse(0);
    }

    private static OptionCombinationGroupNames buildGroupNames(
            List<SellerOptionGroup> optionGroups) {
        List<String> names =
                optionGroups.stream().map(SellerOptionGroup::optionGroupNameValue).toList();
        return OptionCombinationGroupNames.of(names);
    }

    private static Map<Long, SellerOptionValue> buildOptionValueMap(
            List<SellerOptionGroup> optionGroups) {
        Map<Long, SellerOptionValue> map = new HashMap<>();
        for (SellerOptionGroup group : optionGroups) {
            for (SellerOptionValue value : group.optionValues()) {
                map.put(value.idValue(), value);
            }
        }
        return map;
    }

    private static List<String> resolveOptionNames(
            Product product,
            List<SellerOptionGroup> optionGroups,
            Map<Long, SellerOptionValue> optionValueMap) {

        Map<Long, String> mappingByGroupId = new HashMap<>();
        for (ProductOptionMapping mapping : product.optionMappings()) {
            SellerOptionValue value = optionValueMap.get(mapping.sellerOptionValueIdValue());
            if (value != null) {
                mappingByGroupId.put(
                        value.sellerOptionGroupIdValue(), value.optionValueNameValue());
            }
        }

        List<String> names = new ArrayList<>();
        for (SellerOptionGroup group : optionGroups) {
            String name = mappingByGroupId.get(group.idValue());
            if (name != null) {
                names.add(name);
            }
        }
        return names;
    }
}
