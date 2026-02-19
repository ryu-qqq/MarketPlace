package com.ryuqq.marketplace.application.productgroupdescription.internal;

import com.ryuqq.marketplace.application.common.manager.FileStorageManager;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionCommandManager;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.vo.CdnPath;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Description CDN 퍼블리시 Coordinator.
 *
 * <p>HTML 이미지 URL 치환 → CDN 업로드(외부 호출) → 도메인 상태 변경 + 영속화를 조율합니다. 외부 HTTP 호출을 포함하므로 메서드
 * 자체에 @Transactional을 걸지 않고, 도메인 상태 변경 + 영속화는 {@link ProductGroupDescriptionCommandManager}에 위임합니다.
 */
@Component
public class DescriptionPublishCoordinator {

    private static final Logger log = LoggerFactory.getLogger(DescriptionPublishCoordinator.class);
    private static final String CATEGORY = "description";

    private final FileStorageManager fileStorageManager;
    private final ProductGroupDescriptionCommandManager descriptionCommandManager;

    public DescriptionPublishCoordinator(
            FileStorageManager fileStorageManager,
            DescriptionCommandFacade descriptionCommandFacade,
            ProductGroupDescriptionCommandManager descriptionCommandManager) {
        this.fileStorageManager = fileStorageManager;
        this.descriptionCommandManager = descriptionCommandManager;
    }

    /**
     * 단건 Description CDN 퍼블리시.
     *
     * @param description PUBLISH_READY 상태의 도메인 객체
     * @return 퍼블리시 성공 여부
     */
    public boolean publish(ProductGroupDescription description) {
        try {
            DescriptionHtml publishedHtml = description.buildPublishableHtml();

            String cdnUrl =
                    fileStorageManager.uploadHtmlContent(
                            publishedHtml.value(), CATEGORY, description.publishFilename());

            description.publish(CdnPath.of(cdnUrl));

            descriptionCommandManager.persist(description);

            return true;
        } catch (Exception e) {
            log.error("Description CDN 퍼블리시 실패: descriptionId={}", description.idValue(), e);
            return false;
        }
    }
}
