package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import org.springframework.stereotype.Component;

/**
 * 레거시 Command Resolver.
 *
 * <p>placeholder(0L) productGroupId가 포함된 Command를 내부 ID로 교체한 새 Command를 생성합니다.
 */
@Component
public class LegacyCommandResolver {

    /** 이미지 수정 Command의 productGroupId를 내부 ID로 교체. */
    public UpdateProductGroupImagesCommand resolveImagesCommand(
            long internalProductGroupId, UpdateProductGroupImagesCommand command) {
        return new UpdateProductGroupImagesCommand(internalProductGroupId, command.images());
    }

    /** 상세설명 수정 Command의 productGroupId를 내부 ID로 교체. */
    public UpdateProductGroupDescriptionCommand resolveDescriptionCommand(
            long internalProductGroupId, UpdateProductGroupDescriptionCommand command) {
        return new UpdateProductGroupDescriptionCommand(internalProductGroupId, command.content());
    }
}
