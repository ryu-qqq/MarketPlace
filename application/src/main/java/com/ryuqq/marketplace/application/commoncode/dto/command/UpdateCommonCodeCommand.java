package com.ryuqq.marketplace.application.commoncode.dto.command;

/** 공통 코드 수정 Command. */
public record UpdateCommonCodeCommand(Long id, String displayName, int displayOrder) {}
