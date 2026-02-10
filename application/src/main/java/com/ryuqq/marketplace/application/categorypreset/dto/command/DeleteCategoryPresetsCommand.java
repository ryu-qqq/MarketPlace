package com.ryuqq.marketplace.application.categorypreset.dto.command;

import java.util.List;

/** 카테고리 프리셋 벌크 삭제 커맨드 DTO. */
public record DeleteCategoryPresetsCommand(List<Long> ids) {}
