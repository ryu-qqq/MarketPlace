package com.ryuqq.marketplace.application.saleschannelcategory.dto.command;

/** 외부 채널 카테고리 등록 커맨드 DTO. */
public record RegisterSalesChannelCategoryCommand(
        Long salesChannelId,
        String externalCategoryCode,
        String externalCategoryName,
        Long parentId,
        int depth,
        String path,
        int sortOrder,
        boolean leaf,
        String displayPath) {}
