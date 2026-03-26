package com.ryuqq.marketplace.application.legacy.shared.factory;

import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyRegisterProductGroupCommand;
import com.ryuqq.marketplace.application.legacy.shared.dto.bundle.LegacyProductRegistrationBundle;
import com.ryuqq.marketplace.application.legacy.shared.dto.bundle.LegacyProductRegistrationBundle.ImageEntry;
import com.ryuqq.marketplace.application.legacy.shared.dto.bundle.LegacyProductRegistrationBundle.OptionEntry;
import com.ryuqq.marketplace.application.legacy.shared.dto.bundle.LegacyProductRegistrationBundle.SkuEntry;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * LegacyRegisterProductGroupCommand → LegacyProductRegistrationBundle 변환 팩토리.
 *
 * <p>Command를 경량 엔트리로 분해하여 번들로 조립합니다. 도메인 객체 생성은 Coordinator에서 처리됩니다.
 */
@Component
public class LegacyProductBundleFactory {

    public LegacyProductRegistrationBundle create(LegacyRegisterProductGroupCommand command) {
        return new LegacyProductRegistrationBundle(
                command.sellerId(),
                command.brandId(),
                command.categoryId(),
                command.productGroupName(),
                command.optionType(),
                command.regularPrice(),
                command.currentPrice(),
                command.notice(),
                command.delivery(),
                command.detailDescription(),
                toImageEntries(command),
                toSkuEntries(command));
    }

    private List<ImageEntry> toImageEntries(LegacyRegisterProductGroupCommand command) {
        return command.images().stream()
                .map(img -> new ImageEntry(img.imageType(), img.originUrl()))
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
