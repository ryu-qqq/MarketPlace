package com.ryuqq.marketplace.application.outboundproductimage.port.out.query;

import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import java.util.List;

/** OutboundProductImage 조회 포트. */
public interface OutboundProductImageQueryPort {

    List<OutboundProductImage> findActiveByOutboundProductId(Long outboundProductId);
}
