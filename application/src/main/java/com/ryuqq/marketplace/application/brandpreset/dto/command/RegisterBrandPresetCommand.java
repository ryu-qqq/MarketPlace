package com.ryuqq.marketplace.application.brandpreset.dto.command;

/** 브랜드 프리셋 등록 커맨드 DTO. */
public record RegisterBrandPresetCommand(
        Long shopId, Long salesChannelBrandId, String presetName) {}
