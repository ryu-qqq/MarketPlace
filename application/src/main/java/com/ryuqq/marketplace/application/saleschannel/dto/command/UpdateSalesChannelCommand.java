package com.ryuqq.marketplace.application.saleschannel.dto.command;

/** 판매채널 수정 커맨드 DTO. */
public record UpdateSalesChannelCommand(Long salesChannelId, String channelName, String status) {}
