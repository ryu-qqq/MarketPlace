package com.ryuqq.marketplace.application.productgroup.dto.bundle;

import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Instant;
import java.util.List;

/**
 * 상품 그룹 등록 번들.
 *
 * <p>ProductGroup + per-package 등록 데이터를 포함하는 immutable record. 각 per-package 데이터는 Register Command의
 * inner record를 직접 사용하여 중간 변환 없이 Coordinator에서 Command로 조립됩니다.
 *
 * @param productGroup 등록할 ProductGroup 도메인 객체
 * @param images 이미지 등록 데이터
 * @param optionType 옵션 타입 (NONE, SINGLE, COMBINATION)
 * @param optionGroups 옵션 그룹 등록 데이터
 * @param descriptionContent 상세 설명 HTML
 * @param noticeCategoryId 고시정보 카테고리 ID (0이면 고시정보 없음)
 * @param noticeEntries 고시정보 엔트리 목록
 * @param products 상품(SKU) 등록 데이터
 * @param createdAt 생성 시각
 */
@SuppressFBWarnings(
        value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
        justification = "Domain objects are immutable by design")
public record ProductGroupRegistrationBundle(
        ProductGroup productGroup,
        List<RegisterProductGroupImagesCommand.ImageCommand> images,
        String optionType,
        List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> optionGroups,
        String descriptionContent,
        long noticeCategoryId,
        List<RegisterProductNoticeCommand.NoticeEntryCommand> noticeEntries,
        List<RegisterProductsCommand.ProductData> products,
        Instant createdAt) {

    public ProductGroupRegistrationBundle {
        images = List.copyOf(images);
        optionGroups = List.copyOf(optionGroups);
        noticeEntries = List.copyOf(noticeEntries);
        products = List.copyOf(products);
    }
}
