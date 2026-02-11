package com.ryuqq.marketplace.application.categorypreset.dto.command;

import java.util.List;

/** 카테고리 프리셋 수정 커맨드 DTO. */
public record UpdateCategoryPresetCommand(
        Long categoryPresetId,
        String presetName,
        String categoryCode,
        List<Long> internalCategoryIds) {}
