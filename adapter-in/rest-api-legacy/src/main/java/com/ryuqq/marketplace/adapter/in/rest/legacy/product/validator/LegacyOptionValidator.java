package com.ryuqq.marketplace.adapter.in.rest.legacy.product.validator;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateOptionRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 레거시 옵션 Validation.
 *
 * <p>옵션 타입별 옵션 수 검증 + 2단 옵션 조합 검증을 담당합니다. 등록/수정 양쪽에서 사용됩니다.
 *
 * <p>규칙:
 * <ul>
 *   <li>SINGLE: options 비어있어야 함
 *   <li>OPTION_ONE: options 정확히 1개, optionName은 SIZE/COLOR/DEFAULT_ONE 중 하나
 *   <li>OPTION_TWO: options 정확히 2개, COLOR+SIZE 또는 DEFAULT_ONE+DEFAULT_TWO 조합만 허용
 * </ul>
 */
@Component
public class LegacyOptionValidator {

    private static final Set<String> VALID_SINGLE_OPTION_NAMES =
            Set.of("SIZE", "COLOR", "DEFAULT_ONE");

    /**
     * 옵션 타입 기반 검증 (등록 시 사용).
     *
     * @param optionType 레거시 옵션 타입 (SINGLE, OPTION_ONE, OPTION_TWO)
     * @param productOptions 옵션 목록
     */
    public void validateForRegister(
            String optionType, List<LegacyCreateOptionRequest> productOptions) {
        validateOptionSize(optionType, productOptions);
        if ("OPTION_TWO".equals(optionType)) {
            validateTwoStepCombination(productOptions);
        }
    }

    /**
     * 옵션 수정 시 검증 (optionType 없이, 옵션 수로 자동 판단).
     *
     * @param productOptions 옵션 목록
     */
    public void validateForUpdate(List<LegacyCreateOptionRequest> productOptions) {
        if (productOptions == null || productOptions.isEmpty()) return;

        int optionCount = productOptions.getFirst().options().size();

        for (LegacyCreateOptionRequest option : productOptions) {
            if (option.options().size() != optionCount) {
                throw new IllegalArgumentException(
                        "모든 SKU의 옵션 수가 동일해야 합니다. 첫 번째: %d, 불일치: %d"
                                .formatted(optionCount, option.options().size()));
            }
        }

        if (optionCount == 2) {
            validateTwoStepCombination(productOptions);
        }
    }

    private void validateOptionSize(
            String optionType, List<LegacyCreateOptionRequest> productOptions) {
        int expectedSize = switch (optionType) {
            case "SINGLE" -> 0;
            case "OPTION_ONE" -> 1;
            case "OPTION_TWO" -> 2;
            default -> throw new IllegalArgumentException("지원하지 않는 옵션 타입입니다: " + optionType);
        };

        for (LegacyCreateOptionRequest option : productOptions) {
            if (option.options().size() != expectedSize) {
                throw new IllegalArgumentException(
                        "옵션 타입 %s에 대한 옵션 항목 수가 올바르지 않습니다. 기대: %d, 실제: %d"
                                .formatted(optionType, expectedSize, option.options().size()));
            }
        }
    }

    private void validateTwoStepCombination(List<LegacyCreateOptionRequest> productOptions) {
        Set<String> allOptionNames = productOptions.stream()
                .flatMap(opt -> opt.options().stream())
                .map(LegacyCreateOptionRequest.OptionDetail::optionName)
                .collect(Collectors.toSet());

        boolean hasColorSize =
                allOptionNames.contains("COLOR") && allOptionNames.contains("SIZE");
        boolean hasDefaultOneTwo =
                allOptionNames.contains("DEFAULT_ONE") && allOptionNames.contains("DEFAULT_TWO");

        if (!hasColorSize && !hasDefaultOneTwo) {
            throw new IllegalArgumentException(
                    "2단 옵션은 COLOR+SIZE 조합 또는 DEFAULT_ONE+DEFAULT_TWO 조합만 허용됩니다. 현재: "
                            + allOptionNames);
        }
    }
}
