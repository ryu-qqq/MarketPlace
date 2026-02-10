package com.ryuqq.marketplace.application.brandpreset.dto.command;

import java.util.List;

/** 브랜드 프리셋 벌크 삭제 커맨드 DTO. */
public record DeleteBrandPresetsCommand(List<Long> ids) {}
