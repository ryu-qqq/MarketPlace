package com.ryuqq.marketplace.application.productgroup.dto.bundle;

import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.RegisterProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import java.time.Instant;
import java.util.List;

/**
 * 상품 그룹 등록 번들.
 *
 * <p>ProductGroup + per-package 등록 Command를 포함하는 immutable record. per-package Command는
 * productGroupId 없이 생성되며, bindAll / bindProductCommand 메서드로 실제 ID를 바인딩합니다.
 *
 * <p>Product Command는 allOptionValueIds가 SellerOption persist 이후에 확정되므로 bindAll이 아닌 별도의 {@link
 * #bindProductCommand}를 사용합니다.
 *
 * <p>검수 Outbox는 {@link #bindAll}에서 다른 Command와 함께 생성됩니다.
 */
public record ProductGroupRegistrationBundle(
        ProductGroup productGroup,
        RegisterProductGroupImagesCommand imageCommand,
        RegisterSellerOptionGroupsCommand optionGroupCommand,
        RegisterProductGroupDescriptionCommand descriptionCommand,
        RegisterProductNoticeCommand noticeCommand,
        RegisterProductsCommand productCommand,
        Instant createdAt) {

    /** per-package Command에 productGroupId를 바인딩한 결과 (Product 제외). 검수 Outbox도 포함. */
    public record BoundCommands(
            RegisterProductGroupImagesCommand imageCommand,
            RegisterSellerOptionGroupsCommand optionGroupCommand,
            RegisterProductGroupDescriptionCommand descriptionCommand,
            RegisterProductNoticeCommand noticeCommand,
            ProductGroupInspectionOutbox inspectionOutbox) {}

    /** Image, Option, Description, Notice Command + 검수 Outbox에 productGroupId를 한 번에 바인딩합니다. */
    public BoundCommands bindAll(long productGroupId) {
        return new BoundCommands(
                new RegisterProductGroupImagesCommand(productGroupId, imageCommand.images()),
                new RegisterSellerOptionGroupsCommand(
                        productGroupId,
                        optionGroupCommand.optionType(),
                        optionGroupCommand.optionGroups()),
                new RegisterProductGroupDescriptionCommand(
                        productGroupId, descriptionCommand.content()),
                new RegisterProductNoticeCommand(
                        productGroupId, noticeCommand.noticeCategoryId(), noticeCommand.entries()),
                ProductGroupInspectionOutbox.forNew(productGroupId, createdAt));
    }

    /**
     * Product Command에 productGroupId와 allOptionValueIds를 바인딩합니다.
     *
     * <p>allOptionValueIds는 SellerOption persist 이후에 확정되므로 bindAll과 별도로 바인딩합니다.
     *
     * @param productGroupId 확정된 상품 그룹 ID
     * @param allOptionValueIds persist 후 확정된 모든 SellerOptionValueId (플랫 리스트)
     * @return productGroupId + allOptionValueIds가 바인딩된 RegisterProductsCommand
     */
    public RegisterProductsCommand bindProductCommand(
            long productGroupId, List<Long> allOptionValueIds) {
        return new RegisterProductsCommand(
                productGroupId, productCommand.products(), allOptionValueIds);
    }
}
