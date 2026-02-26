package com.ryuqq.marketplace.application.legacy.image.dto.command;

import java.util.List;

/**
 * 레거시 상품 이미지 수정 Command.
 *
 * @param productGroupId 세토프 상품그룹 PK
 * @param images 이미지 엔트리 목록 (순서대로 displayOrder 결정)
 */
public record LegacyUpdateImagesCommand(long productGroupId, List<ImageEntry> images) {

    public LegacyUpdateImagesCommand {
        images = List.copyOf(images);
    }

    /** 이미지 경량 엔트리. */
    public record ImageEntry(String imageType, String imageUrl, String originUrl) {}
}
