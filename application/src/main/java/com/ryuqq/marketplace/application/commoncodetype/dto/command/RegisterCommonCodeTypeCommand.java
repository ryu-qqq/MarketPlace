package com.ryuqq.marketplace.application.commoncodetype.dto.command;

/** 공통 코드 타입 등록 Command. */
public record RegisterCommonCodeTypeCommand(
        String code, String name, String description, int displayOrder) {}
