package com.ryuqq.marketplace.application.outboundproductimage.internal;

import com.ryuqq.marketplace.application.outboundproductimage.dto.ResolvedExternalImage;
import com.ryuqq.marketplace.application.outboundproductimage.dto.ResolvedExternalImages;
import com.ryuqq.marketplace.application.outboundproductimage.manager.OutboundProductImageCommandManager;
import com.ryuqq.marketplace.application.outboundproductimage.manager.OutboundProductImageReadManager;
import com.ryuqq.marketplace.application.outboundproductimage.manager.SalesChannelImageClientManager;
import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import com.ryuqq.marketplace.domain.outboundproductimage.vo.OutboundProductImageDiff;
import com.ryuqq.marketplace.domain.outboundproductimage.vo.OutboundProductImages;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 아웃바운드 이미지 동기화 코디네이터.
 *
 * <p>캐시된 외부 이미지 URL과 현재 이미지를 diff 비교하여,
 * 새로 추가된 이미지만 외부 채널에 업로드하고, 삭제된 이미지는 soft delete 처리합니다.
 * 이미 업로드된 이미지는 캐시된 external URL을 재사용합니다.
 */
@Component
public class OutboundImageSyncCoordinator {

    private static final Logger log = LoggerFactory.getLogger(OutboundImageSyncCoordinator.class);

    private final OutboundProductImageReadManager readManager;
    private final OutboundProductImageCommandManager commandManager;
    private final SalesChannelImageClientManager imageClientManager;

    public OutboundImageSyncCoordinator(
            OutboundProductImageReadManager readManager,
            OutboundProductImageCommandManager commandManager,
            SalesChannelImageClientManager imageClientManager) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.imageClientManager = imageClientManager;
    }

    /**
     * 이미지 동기화를 수행하고 외부 채널 URL이 반영된 결과를 반환합니다.
     *
     * @param outboundProductId 아웃바운드 상품 ID
     * @param channelCode 판매채널 코드
     * @param currentImages 현재 상품 이미지 목록
     * @return 외부 채널 URL이 반영된 이미지 결과
     */
    @Transactional
    public ResolvedExternalImages syncImages(
            Long outboundProductId,
            String channelCode,
            List<ProductGroupImage> currentImages) {

        Instant now = Instant.now();

        // 1. 캐시된 OutboundProductImages 로드
        OutboundProductImages cached = readManager.findByOutboundProductId(outboundProductId);

        // 2. diff 계산
        OutboundProductImageDiff diff = cached.diff(currentImages, outboundProductId, now);

        // 3. 변경 없으면 캐시 그대로 반환
        if (diff.hasNoChanges()) {
            log.debug("이미지 변경 없음: outboundProductId={}", outboundProductId);
            return toResolvedImages(diff.retained());
        }

        log.info(
                "이미지 diff: outboundProductId={}, added={}, removed={}, retained={}",
                outboundProductId,
                diff.added().size(),
                diff.removed().size(),
                diff.retained().size());

        // 4. removed → soft delete persist
        if (!diff.removed().isEmpty()) {
            commandManager.persistAll(diff.removed());
        }

        // 5. added → 외부 채널에 업로드 → externalUrl 세팅
        if (!diff.added().isEmpty()) {
            uploadAndAssignExternalUrls(channelCode, diff.added());
            commandManager.persistAll(diff.added());
        }

        // 6. 전체 결과 반환 (retained + added)
        List<OutboundProductImage> allActive = new ArrayList<>(diff.retained());
        allActive.addAll(diff.added());
        return toResolvedImages(allActive);
    }

    private void uploadAndAssignExternalUrls(
            String channelCode, List<OutboundProductImage> addedImages) {

        List<String> originUrls = addedImages.stream()
                .map(OutboundProductImage::originUrl)
                .toList();

        List<String> externalUrls = imageClientManager.uploadImages(channelCode, originUrls);

        for (int i = 0; i < addedImages.size(); i++) {
            addedImages.get(i).assignExternalUrl(externalUrls.get(i));
        }
    }

    private ResolvedExternalImages toResolvedImages(List<OutboundProductImage> images) {
        List<ResolvedExternalImage> resolved = images.stream()
                .filter(OutboundProductImage::hasExternalUrl)
                .map(img -> new ResolvedExternalImage(
                        img.externalUrl(), img.imageType(), img.sortOrder()))
                .toList();
        return ResolvedExternalImages.of(resolved);
    }
}
