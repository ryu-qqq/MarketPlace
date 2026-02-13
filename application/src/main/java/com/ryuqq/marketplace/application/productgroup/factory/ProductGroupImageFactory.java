package com.ryuqq.marketplace.application.productgroup.factory;

import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupImages;
import java.util.List;
import org.springframework.stereotype.Component;

/** ProductGroupImages 생성 서브 팩토리. */
@Component
public class ProductGroupImageFactory {

    /** intermediate ImageData 리스트로부터 ProductGroupImages 생성. */
    public ProductGroupImages create(ProductGroupId productGroupId, List<ImageData> imageDataList) {
        List<ProductGroupImage> images =
                imageDataList.stream()
                        .map(
                                data ->
                                        ProductGroupImage.forNew(
                                                productGroupId,
                                                ImageUrl.of(data.originUrl()),
                                                ImageType.valueOf(data.imageType()),
                                                data.sortOrder()))
                        .toList();
        return ProductGroupImages.of(images);
    }

    public record ImageData(String imageType, String originUrl, int sortOrder) {}
}
