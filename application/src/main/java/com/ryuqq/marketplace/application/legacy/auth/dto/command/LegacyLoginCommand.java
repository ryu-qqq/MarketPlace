package com.ryuqq.marketplace.application.legacy.auth.dto.command;

/** 레거시 로그인 Command. */
public record LegacyLoginCommand(String identifier, String password) {}
