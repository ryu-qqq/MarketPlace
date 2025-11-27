package com.ryuqq.marketplace.adapter.in.rest.brand.mapper;

import com.ryuqq.marketplace.adapter.in.rest.brand.dto.command.AddBrandAliasApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.command.ChangeBrandStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.command.CreateBrandApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.command.UpdateAliasApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.command.UpdateBrandApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.AliasMatchApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandAliasApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandSimpleApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.query.BrandSearchApiRequest;
import com.ryuqq.marketplace.application.brand.dto.command.AddBrandAliasCommand;
import com.ryuqq.marketplace.application.brand.dto.command.ChangeBrandStatusCommand;
import com.ryuqq.marketplace.application.brand.dto.command.CreateBrandCommand;
import com.ryuqq.marketplace.application.brand.dto.command.UpdateAliasConfidenceCommand;
import com.ryuqq.marketplace.application.brand.dto.command.UpdateBrandCommand;
import com.ryuqq.marketplace.application.brand.dto.response.AliasMatchResponse;
import com.ryuqq.marketplace.application.brand.dto.response.BrandAliasResponse;
import com.ryuqq.marketplace.application.brand.dto.response.BrandDetailResponse;
import com.ryuqq.marketplace.application.brand.dto.response.BrandResponse;
import com.ryuqq.marketplace.application.brand.dto.response.BrandSimpleResponse;
import com.ryuqq.marketplace.application.brand.dto.query.BrandSearchQuery;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Brand API Mapper
 *
 * <p>API Request DTO를 Application Command로 변환하고,
 * Application Response를 API Response로 변환합니다.
 */
@Component
public class BrandApiMapper {

    // ========== Request → Command ==========

    public CreateBrandCommand toCreateCommand(CreateBrandApiRequest request) {
        return new CreateBrandCommand(
            request.code(),
            request.canonicalName(),
            request.nameKo(),
            request.nameEn(),
            request.shortName(),
            request.country(),
            request.department(),
            request.isLuxury() != null ? request.isLuxury() : false,
            request.officialWebsite(),
            request.logoUrl(),
            request.description()
        );
    }

    public UpdateBrandCommand toUpdateCommand(Long brandId, UpdateBrandApiRequest request) {
        return new UpdateBrandCommand(
            brandId,
            request.nameKo(),
            request.nameEn(),
            request.shortName(),
            request.country(),
            request.department(),
            request.isLuxury() != null ? request.isLuxury() : false,
            request.officialWebsite(),
            request.logoUrl(),
            request.description()
        );
    }

    public ChangeBrandStatusCommand toChangeStatusCommand(Long brandId, ChangeBrandStatusApiRequest request) {
        return new ChangeBrandStatusCommand(
            brandId,
            request.status()
        );
    }

    public AddBrandAliasCommand toAddAliasCommand(Long brandId, AddBrandAliasApiRequest request) {
        return new AddBrandAliasCommand(
            brandId,
            request.aliasName(),
            request.sourceType(),
            request.sellerId(),
            request.mallCode() != null ? request.mallCode() : "GLOBAL",
            request.confidence() != null ? request.confidence().doubleValue() : 1.0
        );
    }

    public UpdateAliasConfidenceCommand toUpdateAliasConfidenceCommand(
            Long brandId, Long aliasId, UpdateAliasApiRequest request) {
        return new UpdateAliasConfidenceCommand(
            brandId,
            aliasId,
            request.confidence() != null ? request.confidence().doubleValue() : null
        );
    }

    // ========== Query Mapping ==========

    public BrandSearchQuery toSearchQuery(BrandSearchApiRequest request) {
        return new BrandSearchQuery(
            request.keyword(),
            request.status(),
            request.isLuxury(),
            request.department(),
            null // country - not in API request
        );
    }

    // ========== Response Mapping ==========

    public BrandApiResponse toApiResponse(BrandResponse response) {
        return new BrandApiResponse(
            response.brandId(),
            response.code(),
            response.canonicalName(),
            response.nameKo(),
            response.nameEn(),
            response.shortName(),
            response.countryCode(),
            response.department(),
            response.isLuxury(),
            response.status(),
            response.logoUrl()
        );
    }

    public BrandDetailApiResponse toDetailApiResponse(BrandDetailResponse response) {
        return new BrandDetailApiResponse(
            response.brandId(),
            response.code(),
            response.canonicalName(),
            response.nameKo(),
            response.nameEn(),
            response.shortName(),
            response.countryCode(),
            response.department(),
            response.isLuxury(),
            response.status(),
            response.officialWebsite(),
            response.logoUrl(),
            response.description(),
            response.dataQualityLevel(),
            (int) response.dataQualityScore(),
            response.aliases().stream()
                .map(this::toAliasApiResponse)
                .toList()
        );
    }

    public BrandSimpleApiResponse toSimpleApiResponse(BrandSimpleResponse response) {
        return new BrandSimpleApiResponse(
            response.brandId(),
            response.code(),
            response.nameKo(),
            response.nameEn()
        );
    }

    public BrandAliasApiResponse toAliasApiResponse(BrandAliasResponse response) {
        return new BrandAliasApiResponse(
            response.aliasId(),
            response.brandId(),
            response.originalAlias(),
            response.normalizedAlias(),
            response.sourceType(),
            response.sellerId(),
            response.mallCode(),
            BigDecimal.valueOf(response.confidenceValue()),
            response.status()
        );
    }

    public AliasMatchApiResponse toAliasMatchApiResponse(String searchedAlias, AliasMatchResponse response) {
        return new AliasMatchApiResponse(
            searchedAlias,
            normalizeAlias(searchedAlias),
            response.matches().stream()
                .map(m -> new AliasMatchApiResponse.MatchCandidate(
                    m.brandId(),
                    m.brandCode(),
                    m.canonicalName(),
                    m.nameKo(),
                    BigDecimal.valueOf(m.confidence())
                ))
                .toList()
        );
    }

    public List<BrandApiResponse> toApiResponseList(List<BrandResponse> responses) {
        return responses.stream()
            .map(this::toApiResponse)
            .toList();
    }

    public List<BrandSimpleApiResponse> toSimpleApiResponseList(List<BrandSimpleResponse> responses) {
        return responses.stream()
            .map(this::toSimpleApiResponse)
            .toList();
    }

    public List<BrandAliasApiResponse> toAliasApiResponseList(List<BrandAliasResponse> responses) {
        return responses.stream()
            .map(this::toAliasApiResponse)
            .toList();
    }

    // ========== Helper Methods ==========

    private String normalizeAlias(String alias) {
        if (alias == null) {
            return null;
        }
        return alias.toLowerCase().replaceAll("[^a-z0-9가-힣]", "");
    }
}
