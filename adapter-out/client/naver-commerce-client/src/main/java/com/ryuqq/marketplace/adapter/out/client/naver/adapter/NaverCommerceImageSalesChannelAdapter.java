package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.application.outboundproductimage.port.out.client.SalesChannelImageClient;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 이미지 업로드 SalesChannelImageClient 구현.
 *
 * <p>기존 NaverCommerceImageClientAdapter를 래핑하여 SalesChannelImageClient 포트를 구현합니다.
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceImageSalesChannelAdapter implements SalesChannelImageClient {

    private static final String NAVER_CHANNEL_CODE = "NAVER";

    private final NaverCommerceImageClientAdapter imageClientAdapter;

    public NaverCommerceImageSalesChannelAdapter(
            NaverCommerceImageClientAdapter imageClientAdapter) {
        this.imageClientAdapter = imageClientAdapter;
    }

    @Override
    public String channelCode() {
        return NAVER_CHANNEL_CODE;
    }

    @Override
    public List<String> uploadImages(List<String> imageUrls) {
        return imageClientAdapter.uploadFromUrls(imageUrls);
    }
}
