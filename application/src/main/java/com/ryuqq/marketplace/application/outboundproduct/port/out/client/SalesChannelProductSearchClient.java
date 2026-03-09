package com.ryuqq.marketplace.application.outboundproduct.port.out.client;

import com.ryuqq.marketplace.application.outboundproduct.dto.vo.ExternalProductEntry;
import java.util.List;

/**
 * 외부 판매채널의 등록 상품 조회 포트.
 *
 * <p>채널별 어댑터가 구현하여 해당 채널에 등록된 전체 상품 목록을 반환합니다.
 */
public interface SalesChannelProductSearchClient {

    /** 이 클라이언트가 담당하는 판매채널 코드. */
    String channelCode();

    /**
     * 외부 채널에 등록된 전체 상품을 조회합니다.
     *
     * @return 외부 상품 목록
     */
    List<ExternalProductEntry> fetchAllProducts();
}
