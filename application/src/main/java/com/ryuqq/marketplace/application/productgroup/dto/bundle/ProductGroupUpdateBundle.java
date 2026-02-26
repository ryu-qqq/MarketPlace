package com.ryuqq.marketplace.application.productgroup.dto.bundle;

import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import java.util.List;

/**
 * 상품 그룹 수정 번들.
 *
 * <p>ProductGroup 기본 정보 UpdateData + 각 도메인별 수정 Command를 포함하는 immutable record.
 */
public record ProductGroupUpdateBundle(
        ProductGroupUpdateData basicInfoUpdateData,
        UpdateProductGroupImagesCommand imageCommand,
        UpdateSellerOptionGroupsCommand optionGroupCommand,
        UpdateProductGroupDescriptionCommand descriptionCommand,
        UpdateProductNoticeCommand noticeCommand,
        List<ProductDiffUpdateEntry> productEntries) {}
