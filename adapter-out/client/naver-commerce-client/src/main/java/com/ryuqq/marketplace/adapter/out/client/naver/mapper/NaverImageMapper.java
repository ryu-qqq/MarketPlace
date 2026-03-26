package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.Images;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.Images.OptionalImage;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.Images.RepresentativeImage;
import com.ryuqq.marketplace.application.outboundproductimage.dto.ResolvedExternalImages;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import java.util.ArrayList;
import java.util.List;

/** 이미지 변환 매퍼. */
final class NaverImageMapper {

    private NaverImageMapper() {}

    static Images mapExternalImages(ResolvedExternalImages resolvedImages) {
        RepresentativeImage mainImage = null;
        List<OptionalImage> optionalImages = new ArrayList<>();

        String thumbnailUrl = resolvedImages.thumbnailUrl();
        if (thumbnailUrl != null) {
            mainImage = new RepresentativeImage(thumbnailUrl);
        }

        for (String detailUrl : resolvedImages.detailUrls()) {
            optionalImages.add(new OptionalImage(detailUrl));
        }

        return new Images(mainImage, optionalImages.isEmpty() ? null : optionalImages);
    }

    static Images mapImages(List<ProductGroupImageResult> groupImages) {
        RepresentativeImage mainImage = null;
        List<OptionalImage> optionalImages = new ArrayList<>();

        for (ProductGroupImageResult image : groupImages) {
            String url = resolveImageUrl(image);
            if ("THUMBNAIL".equals(image.imageType())) {
                mainImage = new RepresentativeImage(url);
            } else {
                optionalImages.add(new OptionalImage(url));
            }
        }

        if (mainImage == null && !groupImages.isEmpty()) {
            mainImage = new RepresentativeImage(resolveImageUrl(groupImages.get(0)));
        }

        return new Images(mainImage, optionalImages.isEmpty() ? null : optionalImages);
    }

    private static String resolveImageUrl(ProductGroupImageResult image) {
        String uploaded = image.uploadedUrl();
        return uploaded != null ? uploaded : image.originUrl();
    }
}
