package com.ryuqq.marketplace.application.brandpreset.dto.command;

/** 브랜드 프리셋 수정 커맨드 DTO. */
public record UpdateBrandPresetCommand(
        Long brandPresetId, String presetName, Long salesChannelBrandId) {}
