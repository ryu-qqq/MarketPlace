package com.ryuqq.marketplace.application.saleschannel.assembler;

import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelPageResult;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelResult;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import java.util.List;
import org.springframework.stereotype.Component;

/** SalesChannel Assembler. */
@Component
public class SalesChannelAssembler {

    public SalesChannelResult toResult(SalesChannel salesChannel) {
        return SalesChannelResult.from(salesChannel);
    }

    public List<SalesChannelResult> toResults(List<SalesChannel> salesChannels) {
        return salesChannels.stream().map(this::toResult).toList();
    }

    public SalesChannelPageResult toPageResult(
            List<SalesChannel> salesChannels, int page, int size, long totalElements) {
        List<SalesChannelResult> results = toResults(salesChannels);
        return SalesChannelPageResult.of(results, page, size, totalElements);
    }
}
