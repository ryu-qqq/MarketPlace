package com.ryuqq.marketplace.application.channeloptionmapping.dto.command;

/**
 * 채널 옵션 매핑 등록 Command.
 *
 * @param salesChannelId 판매채널 ID
 * @param canonicalOptionValueId 캐노니컬 옵션값 ID
 * @param externalOptionCode 외부몰 옵션 코드
 */
public record RegisterChannelOptionMappingCommand(
        long salesChannelId, long canonicalOptionValueId, String externalOptionCode) {}
