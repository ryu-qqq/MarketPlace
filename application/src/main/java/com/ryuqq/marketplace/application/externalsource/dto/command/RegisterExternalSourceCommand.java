package com.ryuqq.marketplace.application.externalsource.dto.command;

/** 외부 소스 등록 Command. */
public record RegisterExternalSourceCommand(
        String code, String name, String type, String description) {}
