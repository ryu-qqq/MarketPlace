package com.ryuqq.marketplace.application.legacy.qna.service;

import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;
import com.ryuqq.marketplace.application.legacy.qna.port.in.LegacyQnaDetailQueryUseCase;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.application.qna.port.in.query.GetQnaDetailUseCase;
import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
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

    public LegacyQnaDetailQueryService(
            GetQnaDetailUseCase getQnaDetailUseCase,
            SellerReadManager sellerReadManager,
            ProductGroupReadManager productGroupReadManager) {
        this.getQnaDetailUseCase = getQnaDetailUseCase;
        this.sellerReadManager = sellerReadManager;
        this.productGroupReadManager = productGroupReadManager;
    }

    @Override
    @Transactional(readOnly = true)
    public LegacyQnaDetailResult execute(long qnaId) {
        QnaResult result = getQnaDetailUseCase.execute(qnaId);
        String sellerName = resolveSellerName(result.sellerId());

        String pgName = "";
        Long brandId = 0L;
        if (result.productGroupId() != 0) {
            try {
                ProductGroup pg =
                        productGroupReadManager.getById(ProductGroupId.of(result.productGroupId()));
                pgName = pg.productGroupName().value();
                brandId = pg.brandId().value();
            } catch (Exception e) {
                // 상품이 삭제됐을 수 있음
            }
        }

        return LegacyQnaFromMarketAssembler.toDetailResult(
                result, sellerName, pgName, "", brandId, "");
    }

    private String resolveSellerName(long sellerId) {
        try {
            return sellerReadManager.getById(SellerId.of(sellerId)).sellerName().value();
        } catch (Exception e) {
            return "";
        }
    }
}
