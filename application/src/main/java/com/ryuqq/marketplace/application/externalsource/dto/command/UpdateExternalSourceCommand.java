package com.ryuqq.marketplace.application.externalsource.dto.command;

/** 외부 소스 수정 Command. */
public record UpdateExternalSourceCommand(
        long externalSourceId, String name, String status, String description) {}
