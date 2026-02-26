package com.ryuqq.marketplace.application.commoncode.dto.command;

import java.util.List;

/** 공통 코드 활성화 상태 변경 Command. */
public record ChangeCommonCodeStatusCommand(List<Long> ids, boolean active) {}
