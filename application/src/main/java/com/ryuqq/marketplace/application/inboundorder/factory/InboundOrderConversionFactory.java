package com.ryuqq.marketplace.application.inboundorder.factory;

import com.ryuqq.marketplace.application.brand.manager.BrandReadManager;
import com.ryuqq.marketplace.application.order.dto.command.CreateOrderCommand;
import com.ryuqq.marketplace.application.order.dto.command.CreateOrderItemCommand;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrderItem;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundOrder → CreateOrderCommand 변환 팩토리. */
@Component
public class InboundOrderConversionFactory {

    private final ShopReadManager shopReadManager;
    private final BrandReadManager brandReadManager;
    private final SellerReadManager sellerReadManager;
    private final ProductGroupReadManager productGroupReadManager;

    public InboundOrderConversionFactory(
            ShopReadManager shopReadManager,
            BrandReadManager brandReadManager,
            SellerReadManager sellerReadManager,
            ProductGroupReadManager productGroupReadManager) {
        this.shopReadManager = shopReadManager;
        this.brandReadManager = brandReadManager;
        this.sellerReadManager = sellerReadManager;
        this.productGroupReadManager = productGroupReadManager;
    }

    public CreateOrderCommand toCreateOrderCommand(InboundOrder inbound) {
        Shop shop = shopReadManager.getById(ShopId.of(inbound.shopId()));

        List<CreateOrderItemCommand> items =
                inbound.items().stream().map(this::toCreateOrderItemCommand).toList();

        return new CreateOrderCommand(
                inbound.salesChannelId(),
                inbound.shopId(),
                shop.channelCode(),
                shop.shopName(),
                inbound.externalOrderNo(),
                inbound.externalOrderedAt(),
                inbound.buyerName(),
                inbound.buyerEmail(),
                inbound.buyerPhone(),
                inbound.paymentMethod(),
                inbound.totalPaymentAmount(),
                inbound.paidAt(),
                items,
                "inbound-order-system");
    }

    private CreateOrderItemCommand toCreateOrderItemCommand(InboundOrderItem item) {
        String brandName = resolveBrandName(item.resolvedBrandId());
        String sellerName = resolveSellerName(item.resolvedSellerId());
        String mainImageUrl = resolveMainImageUrl(item.resolvedProductGroupId());

        return new CreateOrderItemCommand(
                item.resolvedProductGroupId(),
                item.resolvedProductId(),
                item.resolvedSellerId(),
                item.resolvedBrandId(),
                item.resolvedSkuCode(),
                item.resolvedProductGroupName(),
                brandName,
                sellerName,
                mainImageUrl,
                item.externalProductId(),
                item.externalOptionId(),
                item.externalProductName(),
                item.externalOptionName(),
                item.externalImageUrl(),
                item.unitPrice(),
                item.quantity(),
                item.totalAmount(),
                item.discountAmount(),
                item.sellerBurdenDiscountAmount(),
                item.paymentAmount(),
                item.receiverName(),
                item.receiverPhone(),
                item.receiverZipCode(),
                item.receiverAddress(),
                item.receiverAddressDetail(),
                item.deliveryRequest());
    }

    private String resolveBrandName(Long brandId) {
        if (brandId == null) {
            return null;
        }
        Brand brand = brandReadManager.getById(BrandId.of(brandId));
        return brand.nameKo();
    }

    private String resolveSellerName(Long sellerId) {
        if (sellerId == null) {
            return null;
        }
        Seller seller = sellerReadManager.getById(SellerId.of(sellerId));
        return seller.sellerNameValue();
    }

    private String resolveMainImageUrl(Long productGroupId) {
        if (productGroupId == null) {
            return null;
        }
        ProductGroup productGroup =
                productGroupReadManager.getById(ProductGroupId.of(productGroupId));
        List<ProductGroupImage> images = productGroup.images();
        if (images.isEmpty()) {
            return null;
        }
        return images.getFirst().originUrlValue();
    }
}
