package com.ryuqq.marketplace.application.legacyproduct.port.in.command;

import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import java.util.List;

/**
 * 레거시 상품 수정 UseCase.
 *
 * <p>세토프 PK 기반의 10개 수정 오퍼레이션을 제공합니다.
 */
@SuppressWarnings("PMD.UseCaseTooManyMethods")
public interface LegacyProductCommandUseCase {

    /** PUT /{id} - 전체 수정 (updateStatus 플래그 기반 선택적 수정). */
    void updateFull(long setofProductGroupId, ProductGroupUpdateBundle bundle);

    /** PUT /{id}/notice - 고시정보 수정. */
    void updateNotice(long setofProductGroupId, UpdateProductNoticeCommand command);

    /** PUT /{id}/images - 이미지 수정. */
    void updateImages(long setofProductGroupId, UpdateProductGroupImagesCommand command);

    /** PUT /{id}/detailDescription - 상세설명 수정. */
    void updateDescription(long setofProductGroupId, UpdateProductGroupDescriptionCommand command);

    /** PUT /{id}/option - 옵션+상품 수정. */
    void updateOptions(
            long setofProductGroupId,
            UpdateSellerOptionGroupsCommand optionCmd,
            List<ProductDiffUpdateEntry> productEntries,
            List<UpdateProductsCommand.OptionGroupData> optionGroupData);

    /** PATCH /{id}/price - 가격 수정 (그룹 하위 전체 상품). */
    void updatePrice(long setofProductGroupId, int regularPrice, int currentPrice);

    /** PATCH /{id}/display-yn - 진열 상태 토글. */
    void updateDisplayStatus(long setofProductGroupId, String displayYn);

    /** PATCH /{id}/out-stock - 품절 처리. */
    void markOutOfStock(long setofProductGroupId);

    /** PATCH /{id}/stock - 개별 상품 재고 수정. */
    void updateStock(List<UpdateProductStockCommand> commands);
}
