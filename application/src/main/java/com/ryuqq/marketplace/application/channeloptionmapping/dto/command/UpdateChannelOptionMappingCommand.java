package com.ryuqq.marketplace.application.channeloptionmapping.dto.command;

/**
 * 채널 옵션 매핑 수정 Command.
 *
 * @param channelOptionMappingId 매핑 ID
 * @param externalOptionCode 변경할 외부몰 옵션 코드
 */
public record UpdateChannelOptionMappingCommand(
        long channelOptionMappingId, String externalOptionCode) {}
