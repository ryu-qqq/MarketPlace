package com.ryuqq.marketplace.application.productgroupdescription.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ProductGroupDescription Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider는 Factory에서만 사용합니다.
 */
@Component
public class ProductGroupDescriptionCommandFactory {

    private final TimeProvider timeProvider;

    public ProductGroupDescriptionCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * 상세 설명을 생성하거나 기존 설명을 업데이트합니다.
     *
     * @param command 수정 Command
     * @param existingOpt 기존 상세 설명 (Optional)
     * @return 생성 또는 수정된 ProductGroupDescription
     */
    public ProductGroupDescription createOrUpdateDescription(
            UpdateProductGroupDescriptionCommand command,
            Optional<ProductGroupDescription> existingOpt) {

        DescriptionHtml content = DescriptionHtml.of(command.content());

        if (existingOpt.isPresent()) {
            ProductGroupDescription existing = existingOpt.get();
            existing.updateContent(content);
            return existing;
        } else {
            return ProductGroupDescription.forNew(
                    ProductGroupId.of(command.productGroupId()), content);
        }
    }
}
