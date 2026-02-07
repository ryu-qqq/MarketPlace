package com.ryuqq.marketplace.application.commoncodetype.dto.command;

import java.util.List;

/** 공통 코드 타입 활성화 상태 변경 Command. */
public record ChangeActiveStatusCommand(List<Long> ids, boolean active) {}
