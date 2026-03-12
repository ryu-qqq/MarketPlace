package com.ryuqq.marketplace.application.outboundproductimage.port.out.client;

import java.util.List;

/**
 * 채널별 이미지 업로드 포트.
 *
 * <p>외부 채널(네이버 등)에 이미지를 업로드하고 채널 CDN URL을 반환합니다.
 */
public interface SalesChannelImageClient {

    /** 이 클라이언트가 담당하는 판매채널 코드. */
    String channelCode();

    /**
     * 이미지 URL 목록을 외부 채널에 업로드합니다.
     *
     * @param imageUrls 원본 이미지 URL 목록
     * @return 외부 채널 CDN URL 목록 (입력 순서와 동일)
     */
    List<String> uploadImages(List<String> imageUrls);
}
