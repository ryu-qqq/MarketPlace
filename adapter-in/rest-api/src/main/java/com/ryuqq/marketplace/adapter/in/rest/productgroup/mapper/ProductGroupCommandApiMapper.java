package com.ryuqq.marketplace.adapter.in.rest.productgroup.mapper;

import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.ChangeProductGroupStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.RegisterProductGroupApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.UpdateProductGroupBasicInfoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.UpdateProductGroupDescriptionApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.UpdateProductGroupFullApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.UpdateProductGroupImagesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.UpdateProductNoticeApiRequest;
import com.ryuqq.marketplace.application.productgroup.dto.command.ChangeProductGroupStatusCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupBasicInfoCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ProductGroupCommandApiMapper - 상품 그룹 Command API 변환 매퍼.
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-002: 양방향 변환 지원.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * <p>CQRS 분리: Command 전용 Mapper (QueryApiMapper와 분리).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ProductGroupCommandApiMapper {

    /**
     * RegisterProductGroupApiRequest -> RegisterProductGroupCommand 변환.
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public RegisterProductGroupCommand toCommand(RegisterProductGroupApiRequest request) {
        return new RegisterProductGroupCommand(
                request.sellerId(),
                request.brandId(),
                request.categoryId(),
                request.shippingPolicyId(),
                request.refundPolicyId(),
                request.productGroupName(),
                request.optionType(),
                request.images() != null
                        ? request.images().stream()
                                .map(
                                        img ->
                                                new RegisterProductGroupCommand.ImageCommand(
                                                        img.imageType(),
                                                        img.originUrl(),
                                                        img.sortOrder()))
                                .toList()
                        : null,
                request.optionGroups() != null
                        ? request.optionGroups().stream()
                                .map(
                                        group ->
                                                new RegisterProductGroupCommand.OptionGroupCommand(
                                                        group.optionGroupName(),
                                                        group.canonicalOptionGroupId(),
                                                        group.optionValues().stream()
                                                                .map(
                                                                        value ->
                                                                                new RegisterProductGroupCommand
                                                                                        .OptionValueCommand(
                                                                                        value
                                                                                                .optionValueName(),
                                                                                        value
                                                                                                .canonicalOptionValueId(),
                                                                                        value
                                                                                                .sortOrder()))
                                                                .toList()))
                                .toList()
                        : null,
                request.products() != null
                        ? request.products().stream()
                                .map(
                                        product ->
                                                new RegisterProductGroupCommand.ProductCommand(
                                                        product.skuCode(),
                                                        product.regularPrice(),
                                                        product.currentPrice(),
                                                        product.salePrice(),
                                                        product.discountRate(),
                                                        product.stockQuantity(),
                                                        product.sortOrder(),
                                                        product.optionIndices()))
                                .toList()
                        : null,
                request.description() != null
                        ? new RegisterProductGroupCommand.DescriptionCommand(
                                request.description().content(), List.of())
                        : null,
                request.notice() != null
                        ? new RegisterProductGroupCommand.NoticeCommand(
                                request.notice().noticeCategoryId(),
                                request.notice().entries().stream()
                                        .map(
                                                entry ->
                                                        new RegisterProductGroupCommand
                                                                .NoticeEntryCommand(
                                                                entry.noticeFieldId(),
                                                                entry.fieldValue()))
                                        .toList())
                        : null);
    }

    /**
     * UpdateProductGroupFullApiRequest -> UpdateProductGroupFullCommand 변환.
     *
     * @param productGroupId 상품 그룹 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateProductGroupFullCommand toCommand(
            Long productGroupId, UpdateProductGroupFullApiRequest request) {
        return new UpdateProductGroupFullCommand(
                productGroupId,
                request.productGroupName(),
                request.brandId(),
                request.categoryId(),
                request.shippingPolicyId(),
                request.refundPolicyId(),
                request.images() != null
                        ? request.images().stream()
                                .map(
                                        img ->
                                                new UpdateProductGroupFullCommand.ImageCommand(
                                                        img.imageType(),
                                                        img.originUrl(),
                                                        img.sortOrder()))
                                .toList()
                        : null,
                request.optionGroups() != null
                        ? request.optionGroups().stream()
                                .map(
                                        group ->
                                                new UpdateProductGroupFullCommand
                                                        .OptionGroupCommand(
                                                        group.optionGroupName(),
                                                        group.canonicalOptionGroupId(),
                                                        group.optionValues().stream()
                                                                .map(
                                                                        value ->
                                                                                new UpdateProductGroupFullCommand
                                                                                        .OptionValueCommand(
                                                                                        value
                                                                                                .optionValueName(),
                                                                                        value
                                                                                                .canonicalOptionValueId(),
                                                                                        value
                                                                                                .sortOrder()))
                                                                .toList()))
                                .toList()
                        : null,
                request.products() != null
                        ? request.products().stream()
                                .map(
                                        product ->
                                                new UpdateProductGroupFullCommand.ProductCommand(
                                                        product.skuCode(),
                                                        product.regularPrice(),
                                                        product.currentPrice(),
                                                        product.salePrice(),
                                                        product.discountRate(),
                                                        product.stockQuantity(),
                                                        product.sortOrder(),
                                                        product.optionIndices()))
                                .toList()
                        : null,
                request.description() != null
                        ? new UpdateProductGroupFullCommand.DescriptionCommand(
                                request.description().content(), List.of())
                        : null,
                request.notice() != null
                        ? new UpdateProductGroupFullCommand.NoticeCommand(
                                request.notice().noticeCategoryId(),
                                request.notice().entries().stream()
                                        .map(
                                                entry ->
                                                        new UpdateProductGroupFullCommand
                                                                .NoticeEntryCommand(
                                                                entry.noticeFieldId(),
                                                                entry.fieldValue()))
                                        .toList())
                        : null);
    }

    /**
     * UpdateProductGroupBasicInfoApiRequest -> UpdateProductGroupBasicInfoCommand 변환.
     *
     * @param productGroupId 상품 그룹 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateProductGroupBasicInfoCommand toCommand(
            Long productGroupId, UpdateProductGroupBasicInfoApiRequest request) {
        return new UpdateProductGroupBasicInfoCommand(
                productGroupId,
                request.productGroupName(),
                request.brandId(),
                request.categoryId(),
                request.shippingPolicyId(),
                request.refundPolicyId());
    }

    /**
     * UpdateProductGroupImagesApiRequest -> UpdateProductGroupImagesCommand 변환.
     *
     * @param productGroupId 상품 그룹 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateProductGroupImagesCommand toCommand(
            Long productGroupId, UpdateProductGroupImagesApiRequest request) {
        return new UpdateProductGroupImagesCommand(
                productGroupId,
                request.images().stream()
                        .map(
                                img ->
                                        new UpdateProductGroupImagesCommand.ImageCommand(
                                                img.imageType(), img.originUrl(), img.sortOrder()))
                        .toList());
    }

    /**
     * UpdateProductGroupDescriptionApiRequest -> UpdateProductGroupDescriptionCommand 변환.
     *
     * @param productGroupId 상품 그룹 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateProductGroupDescriptionCommand toCommand(
            Long productGroupId, UpdateProductGroupDescriptionApiRequest request) {
        return new UpdateProductGroupDescriptionCommand(productGroupId, request.content());
    }

    /**
     * UpdateProductNoticeApiRequest -> UpdateProductNoticeCommand 변환.
     *
     * @param productGroupId 상품 그룹 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateProductNoticeCommand toCommand(
            Long productGroupId, UpdateProductNoticeApiRequest request) {
        return new UpdateProductNoticeCommand(
                productGroupId,
                request.noticeCategoryId(),
                request.entries().stream()
                        .map(
                                entry ->
                                        new UpdateProductNoticeCommand.NoticeEntryCommand(
                                                entry.noticeFieldId(), entry.fieldValue()))
                        .toList());
    }

    /**
     * ChangeProductGroupStatusApiRequest -> ChangeProductGroupStatusCommand 변환.
     *
     * @param productGroupId 상품 그룹 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public ChangeProductGroupStatusCommand toCommand(
            Long productGroupId, ChangeProductGroupStatusApiRequest request) {
        return new ChangeProductGroupStatusCommand(productGroupId, request.targetStatus());
    }
}
