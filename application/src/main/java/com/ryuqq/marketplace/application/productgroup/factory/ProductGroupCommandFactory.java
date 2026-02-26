package com.ryuqq.marketplace.application.productgroup.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupBasicInfoCommand;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import org.springframework.stereotype.Component;

/**
 * ProductGroup Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider는 Factory에서만 사용합니다.
 *
 * <p>번들 생성은 {@link ProductGroupBundleFactory}를 Service에서 직접 사용합니다.
 */
@Component
public class ProductGroupCommandFactory {

    private final TimeProvider timeProvider;

    public ProductGroupCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 기본 정보 수정 UpdateData 생성. */
    public ProductGroupUpdateData createUpdateData(
            UpdateProductGroupBasicInfoCommand command, OptionType optionType) {
        return ProductGroupUpdateData.of(
                ProductGroupId.of(command.productGroupId()),
                ProductGroupName.of(command.productGroupName()),
                BrandId.of(command.brandId()),
                CategoryId.of(command.categoryId()),
                ShippingPolicyId.of(command.shippingPolicyId()),
                RefundPolicyId.of(command.refundPolicyId()),
                optionType,
                timeProvider.now());
    }
}
