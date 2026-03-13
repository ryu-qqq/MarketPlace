package com.ryuqq.marketplace.application.outboundproductimage.port.out.command;

import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import java.util.List;

/** OutboundProductImage 저장/삭제 포트. */
public interface OutboundProductImageCommandPort {

    Long persist(OutboundProductImage image);

    List<Long> persistAll(List<OutboundProductImage> images);
}
