package com.ryuqq.marketplace.application.inboundproduct.service.query;

import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductDetailResult;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductDetailResult.OptionItem;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductDetailResult.ProductItem;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductReadManager;
import com.ryuqq.marketplace.application.inboundproduct.port.in.query.GetInboundProductDetailUseCase;
import com.ryuqq.marketplace.application.product.dto.response.ProductDetailResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.port.in.query.GetProductGroupUseCase;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인바운드 상품 상세 조회 서비스.
 *
 * <p>외부 식별자로 인바운드 상품을 조회하고, 변환 완료 상태면 내부 상품 목록까지 함께 반환합니다.
 */
@Service
public class GetInboundProductDetailService implements GetInboundProductDetailUseCase {

    private final InboundProductReadManager inboundProductReadManager;
    private final GetProductGroupUseCase getProductGroupUseCase;

    public GetInboundProductDetailService(
            InboundProductReadManager inboundProductReadManager,
            GetProductGroupUseCase getProductGroupUseCase) {
        this.inboundProductReadManager = inboundProductReadManager;
        this.getProductGroupUseCase = getProductGroupUseCase;
    }

    @Override
    @Transactional(readOnly = true)
    public InboundProductDetailResult execute(long inboundSourceId, String externalProductCode) {
        InboundProduct inbound =
                inboundProductReadManager.findByInboundSourceIdAndProductCodeOrThrow(
                        inboundSourceId, externalProductCode);

        String status = inbound.status().name();
        String extCode = inbound.externalProductCodeValue();

        if (!inbound.status().isConverted()) {
            return new InboundProductDetailResult(status, extCode, null, List.of());
        }

        Long productGroupId = inbound.internalProductGroupId();
        ProductGroupDetailCompositeResult composite =
                getProductGroupUseCase.execute(productGroupId);

        List<ProductItem> products = mapProducts(composite);
        return new InboundProductDetailResult(status, extCode, productGroupId, products);
    }

    private List<ProductItem> mapProducts(ProductGroupDetailCompositeResult composite) {
        if (composite.optionProductMatrix() == null
                || composite.optionProductMatrix().products() == null) {
            return List.of();
        }
        return composite.optionProductMatrix().products().stream()
                .map(this::toProductItem)
                .toList();
    }

    private ProductItem toProductItem(ProductDetailResult p) {
        List<OptionItem> options =
                p.options() != null
                        ? p.options().stream()
                                .map(o -> new OptionItem(o.optionGroupName(), o.optionValueName()))
                                .toList()
                        : List.of();
        return new ProductItem(
                p.id(),
                p.skuCode(),
                p.regularPrice(),
                p.currentPrice(),
                p.stockQuantity(),
                p.sortOrder(),
                options);
    }
}
