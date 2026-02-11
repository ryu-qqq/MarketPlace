package com.ryuqq.marketplace.domain.channeloptionmapping.id;

/** ChannelOptionMapping ID Value Object. */
public record ChannelOptionMappingId(Long value) {

    public static ChannelOptionMappingId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ChannelOptionMappingId 값은 null일 수 없습니다");
        }
        return new ChannelOptionMappingId(value);
    }

    public static ChannelOptionMappingId forNew() {
        return new ChannelOptionMappingId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
