package com.ryuqq.marketplace.application.legacy.qna.service;

import com.ryuqq.marketplace.application.brand.manager.BrandReadManager;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacyProductIdResolver;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;
import com.ryuqq.marketplace.application.legacy.qna.port.in.LegacyQnaDetailQueryUseCase;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.application.qna.port.in.query.GetQnaDetailUseCase;
import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 QnA 단건 조회 서비스.
 *
 * <p>market 스키마의 표준 GetQnaDetailUseCase를 호출하고, 결과를 레거시 응답 형태로 변환합니다.
 */
@Service
public class LegacyQnaDetailQueryService implements LegacyQnaDetailQueryUseCase {

    private final GetQnaDetailUseCase getQnaDetailUseCase;
    private final SellerReadManager sellerReadManager;
    private final ProductGroupReadManager productGroupReadManager;
    private final LegacyProductIdResolver productIdResolver;
    private final BrandReadManager brandReadManager;

    public LegacyQnaDetailQueryService(
            GetQnaDetailUseCase getQnaDetailUseCase,
            SellerReadManager sellerReadManager,
            ProductGroupReadManager productGroupReadManager,
            LegacyProductIdResolver productIdResolver,
            BrandReadManager brandReadManager) {
        this.getQnaDetailUseCase = getQnaDetailUseCase;
        this.sellerReadManager = sellerReadManager;
        this.productGroupReadManager = productGroupReadManager;
        this.productIdResolver = productIdResolver;
        this.brandReadManager = brandReadManager;
    }

    @Override
    @Transactional(readOnly = true)
    public LegacyQnaDetailResult execute(long qnaId) {
        QnaResult result = getQnaDetailUseCase.execute(qnaId);
        String sellerName = resolveSellerName(result.sellerId());

        String pgName = "";
        String mainImageUrl = "";
        Long brandId = 0L;
        String brandName = "";
        if (result.productGroupId() != 0) {
            try {
                long internalPgId = productIdResolver.resolveProductGroupId(result.productGroupId());
                ProductGroup pg = productGroupReadManager.getById(ProductGroupId.of(internalPgId));
                pgName = pg.productGroupName().value();
                brandId = pg.brandId().value();
                try {
                    Brand brand = brandReadManager.getById(pg.brandId());
                    brandName = brand.nameKo();
                } catch (Exception ignored) {
                }
            } catch (Exception e) {
                // 상품이 삭제됐을 수 있음
            }
        }

        return LegacyQnaFromMarketAssembler.toDetailResult(
                result, sellerName, pgName, mainImageUrl, brandId, brandName);
    }

    private String resolveSellerName(long sellerId) {
        try {
            return sellerReadManager.getById(SellerId.of(sellerId)).sellerName().value();
        } catch (Exception e) {
            return "";
        }
    }
}
