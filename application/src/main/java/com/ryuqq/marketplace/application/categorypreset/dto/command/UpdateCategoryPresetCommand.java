package com.ryuqq.marketplace.application.categorypreset.dto.command;

/** 카테고리 프리셋 수정 커맨드 DTO. */
public record UpdateCategoryPresetCommand(
        Long categoryPresetId, String presetName, String categoryCode) {}
