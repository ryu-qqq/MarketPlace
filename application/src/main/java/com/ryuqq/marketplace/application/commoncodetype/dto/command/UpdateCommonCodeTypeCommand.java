package com.ryuqq.marketplace.application.commoncodetype.dto.command;

/** 공통 코드 타입 수정 Command. */
public record UpdateCommonCodeTypeCommand(
        Long id, String name, String description, int displayOrder) {}
