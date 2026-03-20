package com.ryuqq.marketplace.application.legacy.shared.factory;

import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyRegisterProductGroupCommand;
import com.ryuqq.marketplace.application.legacy.shared.dto.bundle.LegacyProductRegistrationBundle;
import com.ryuqq.marketplace.application.legacy.shared.dto.bundle.LegacyProductRegistrationBundle.ImageEntry;
import com.ryuqq.marketplace.application.legacy.shared.dto.bundle.LegacyProductRegistrationBundle.OptionEntry;
import com.ryuqq.marketplace.application.legacy.shared.dto.bundle.LegacyProductRegistrationBundle.SkuEntry;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ManagementType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.Origin;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ProductCondition;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * LegacyRegisterProductGroupCommand → LegacyProductRegistrationBundle 변환 팩토리.
 *
 * <p>Command를 도메인 객체와 경량 엔트리로 분해하여 번들로 조립합니다. productGroupId가 필요한 이미지/SKU는 경량 엔트리로 생성되며, 코디네이터에서 ID
 * 바인딩 후 도메인 객체로 변환됩니다.
 */
@Component
public class LegacyProductBundleFactory {

    public LegacyProductRegistrationBundle create(LegacyRegisterProductGroupCommand command) {
        return new LegacyProductRegistrationBundle(
                toProductGroup(command), toImageEntries(command), toSkuEntries(command));
    }

    private LegacyProductGroup toProductGroup(LegacyRegisterProductGroupCommand command) {
        return LegacyProductGroup.forNew(
                command.productGroupName(),
                command.sellerId(),
                command.brandId(),
                command.categoryId(),
                OptionType.valueOf(command.optionType()),
                ManagementType.valueOf(command.managementType()),
                command.regularPrice(),
                command.currentPrice(),
                command.soldOutYn(),
                command.displayYn(),
                ProductCondition.valueOf(command.productCondition()),
                Origin.valueOf(command.origin()),
                command.styleCode());
    }

    private List<ImageEntry> toImageEntries(LegacyRegisterProductGroupCommand command) {
        return command.images().stream()
                .map(img -> new ImageEntry(img.imageType(), img.imageUrl(), img.originUrl()))
                .toList();
    }

    private List<SkuEntry> toSkuEntries(LegacyRegisterProductGroupCommand command) {
        return command.options().stream()
                .map(
                        opt -> {
                            List<OptionEntry> optionEntries =
                                    opt.optionDetails().stream()
                                            .map(
                                                    detail ->
                                                            new OptionEntry(
                                                                    detail.optionName(),
                                                                    detail.optionValue(),
                                                                    opt.additionalPrice()))
                                            .toList();
                            return new SkuEntry(
                                    command.soldOutYn(),
                                    command.displayYn(),
                                    opt.quantity(),
                                    optionEntries);
                        })
                .toList();
    }
}
