package com.ryuqq.marketplace.application.channeloptionmapping.assembler;

import com.ryuqq.marketplace.application.channeloptionmapping.dto.response.ChannelOptionMappingPageResult;
import com.ryuqq.marketplace.application.channeloptionmapping.dto.response.ChannelOptionMappingResult;
import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import java.util.List;
import org.springframework.stereotype.Component;

/** ChannelOptionMapping Assembler. */
@Component
public class ChannelOptionMappingAssembler {

    public ChannelOptionMappingResult toResult(ChannelOptionMapping mapping) {
        return ChannelOptionMappingResult.from(mapping);
    }

    public List<ChannelOptionMappingResult> toResults(List<ChannelOptionMapping> mappings) {
        return mappings.stream().map(this::toResult).toList();
    }

    public ChannelOptionMappingPageResult toPageResult(
            List<ChannelOptionMapping> mappings, int page, int size, long totalElements) {
        List<ChannelOptionMappingResult> results = toResults(mappings);
        return ChannelOptionMappingPageResult.of(results, page, size, totalElements);
    }
}
