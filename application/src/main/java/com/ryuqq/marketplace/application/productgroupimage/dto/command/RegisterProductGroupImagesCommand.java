package com.ryuqq.marketplace.application.productgroupimage.dto.command;

import java.util.List;

/**
 * RegisterProductGroupImagesCommand - 상품 그룹 이미지 등록 Command.
 *
 * <p>APP-CMD-001: Command Record 패턴
 *
 * <p>APP-CMD-002: Primitive 타입 사용 (DTO 레이어)
 */
public record RegisterProductGroupImagesCommand(long productGroupId, List<ImageCommand> images) {

    public record ImageCommand(String imageType, String originUrl, int sortOrder) {}
}
