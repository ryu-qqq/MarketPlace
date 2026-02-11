package com.ryuqq.marketplace.application.categorypreset.dto.command;

import java.util.List;

/** 카테고리 프리셋 등록 커맨드 DTO. */
public record RegisterCategoryPresetCommand(
        Long shopId, String presetName, String categoryCode, List<Long> internalCategoryIds) {}
