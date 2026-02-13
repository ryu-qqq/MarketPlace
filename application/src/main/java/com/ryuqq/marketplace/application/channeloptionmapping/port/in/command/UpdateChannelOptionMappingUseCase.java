package com.ryuqq.marketplace.application.channeloptionmapping.port.in.command;

import com.ryuqq.marketplace.application.channeloptionmapping.dto.command.UpdateChannelOptionMappingCommand;

/** 채널 옵션 매핑 수정 UseCase. */
public interface UpdateChannelOptionMappingUseCase {

    void execute(UpdateChannelOptionMappingCommand command);
}
