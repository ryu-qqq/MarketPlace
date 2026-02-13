package com.ryuqq.marketplace.application.channeloptionmapping.port.in.command;

import com.ryuqq.marketplace.application.channeloptionmapping.dto.command.RegisterChannelOptionMappingCommand;

/** 채널 옵션 매핑 등록 UseCase. */
public interface RegisterChannelOptionMappingUseCase {

    Long execute(RegisterChannelOptionMappingCommand command);
}
