package com.ryuqq.marketplace.domain.brand.aggregate.brand;

import com.ryuqq.marketplace.domain.brand.vo.AliasName;
import com.ryuqq.marketplace.domain.brand.vo.AliasSource;
import com.ryuqq.marketplace.domain.brand.vo.AliasStatus;
import com.ryuqq.marketplace.domain.brand.vo.BrandAliasId;
import com.ryuqq.marketplace.domain.brand.vo.Confidence;

import java.util.Objects;

/**
 * BrandAlias 내부 Entity
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지 - Plain Java class</li>
 *   <li>외부 의존성 금지 - import java.* 만 허용</li>
 *   <li>JPA 어노테이션 금지</li>
 *   <li>Law of Demeter 준수 - Getter 체이닝 금지</li>
 *   <li>Tell Don't Ask 패턴</li>
 * </ul>
 *
 * <p><strong>책임</strong>:</p>
 * <ul>
 *   <li>브랜드 별칭 정보 관리</li>
 *   <li>별칭 상태 관리 (AUTO_SUGGESTED, PENDING_REVIEW, CONFIRMED, REJECTED)</li>
 *   <li>별칭 신뢰도 관리</li>
 *   <li>별칭 매칭 검증</li>
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
public class BrandAlias {
    private final BrandAliasId id;
    private final Long brandId;  // Long FK 전략
    private final AliasName aliasName;
    private final AliasSource source;
    private Confidence confidence;
    private AliasStatus status;

    /**
     * 생성용 Private Constructor (신규)
     *
     * @param brandId 브랜드 ID (Long FK)
     * @param aliasName 별칭명
     * @param source 별칭 출처
     * @param confidence 신뢰도
     * @param status 별칭 상태
     */
    private BrandAlias(Long brandId, AliasName aliasName, AliasSource source, Confidence confidence, AliasStatus status) {
        this.id = BrandAliasId.forNew();
        this.brandId = brandId;
        this.aliasName = aliasName;
        this.source = source;
        this.confidence = confidence;
        this.status = status;
    }

    /**
     * 재구성용 Private Constructor (영속성에서 복원)
     *
     * @param id 별칭 ID
     * @param brandId 브랜드 ID (Long FK)
     * @param aliasName 별칭명
     * @param source 별칭 출처
     * @param confidence 신뢰도
     * @param status 별칭 상태
     */
    private BrandAlias(BrandAliasId id, Long brandId, AliasName aliasName, AliasSource source, Confidence confidence, AliasStatus status) {
        this.id = id;
        this.brandId = brandId;
        this.aliasName = aliasName;
        this.source = source;
        this.confidence = confidence;
        this.status = status;
    }

    /**
     * 팩토리 메서드 - 신규 생성
     *
     * @param brandId 브랜드 ID
     * @param aliasName 별칭명
     * @param source 별칭 출처
     * @param confidence 신뢰도
     * @param status 별칭 상태
     * @return BrandAlias
     */
    public static BrandAlias create(Long brandId, AliasName aliasName, AliasSource source, Confidence confidence, AliasStatus status) {
        return new BrandAlias(brandId, aliasName, source, confidence, status);
    }

    /**
     * 팩토리 메서드 - 재구성 (영속성에서 복원)
     *
     * @param id 별칭 ID
     * @param brandId 브랜드 ID
     * @param aliasName 별칭명
     * @param source 별칭 출처
     * @param confidence 신뢰도
     * @param status 별칭 상태
     * @return BrandAlias
     */
    public static BrandAlias reconstitute(BrandAliasId id, Long brandId, AliasName aliasName, AliasSource source, Confidence confidence, AliasStatus status) {
        return new BrandAlias(id, brandId, aliasName, source, confidence, status);
    }

    // ===== 도메인 행위 =====

    /**
     * 별칭 확정
     *
     * <p>검수 후 확정된 별칭으로 상태 변경
     */
    public void confirm() {
        this.status = AliasStatus.CONFIRMED;
    }

    /**
     * 별칭 거부
     *
     * <p>검수 후 거부된 별칭으로 상태 변경
     */
    public void reject() {
        this.status = AliasStatus.REJECTED;
    }

    /**
     * 신뢰도 업데이트
     *
     * @param newConfidence 새로운 신뢰도
     */
    public void updateConfidence(Confidence newConfidence) {
        this.confidence = newConfidence;
    }

    // ===== 쿼리 메서드 (Law of Demeter 준수) =====

    /**
     * 확정 상태 여부 확인
     *
     * @return CONFIRMED 상태이면 true
     */
    public boolean isConfirmed() {
        return status == AliasStatus.CONFIRMED;
    }

    /**
     * 거부 상태 여부 확인
     *
     * @return REJECTED 상태이면 true
     */
    public boolean isRejected() {
        return status == AliasStatus.REJECTED;
    }

    /**
     * 활성 상태 여부 확인
     *
     * @return 활성 상태이면 true
     */
    public boolean isActive() {
        return status.isActive();
    }

    /**
     * 정규화된 별칭 반환
     *
     * @return 정규화된 별칭
     */
    public String normalizedAlias() {
        return aliasName.normalized();
    }

    /**
     * 원본 별칭 반환
     *
     * @return 원본 별칭
     */
    public String originalAlias() {
        return aliasName.original();
    }

    /**
     * 소스 타입 반환
     *
     * @return 소스 타입명
     */
    public String sourceType() {
        return source.sourceType().name();
    }

    /**
     * 셀러 ID 반환
     *
     * @return 셀러 ID
     */
    public Long sellerId() {
        return source.sellerId();
    }

    /**
     * 몰 코드 반환
     *
     * @return 몰 코드
     */
    public String mallCode() {
        return source.mallCode();
    }

    /**
     * 신뢰도 값 반환
     *
     * @return 신뢰도 (0.0 ~ 1.0)
     */
    public double confidenceValue() {
        return confidence.value();
    }

    /**
     * 동일 scope 체크 (중복 검증용)
     *
     * <p><strong>중복 조건</strong>: normalizedAlias + mallCode + sellerId 조합</p>
     *
     * @param normalizedAlias 정규화된 별칭
     * @param mallCode 몰 코드
     * @param sellerId 셀러 ID
     * @return 동일한 scope이면 true
     */
    public boolean matchesScope(String normalizedAlias, String mallCode, Long sellerId) {
        return this.normalizedAlias().equals(normalizedAlias)
            && this.mallCode().equals(mallCode)
            && Objects.equals(this.sellerId(), sellerId);
    }

    // ===== Getters =====

    /**
     * 별칭 ID 반환
     *
     * @return BrandAliasId
     */
    public BrandAliasId id() {
        return id;
    }

    /**
     * 브랜드 ID 반환 (Long FK)
     *
     * @return 브랜드 ID
     */
    public Long brandId() {
        return brandId;
    }

    /**
     * 신뢰도 VO 반환
     *
     * @return Confidence
     */
    public Confidence confidence() {
        return confidence;
    }

    /**
     * 별칭 상태 반환
     *
     * @return AliasStatus
     */
    public AliasStatus status() {
        return status;
    }

    /**
     * 별칭명 VO 반환
     *
     * @return AliasName
     */
    public AliasName aliasName() {
        return aliasName;
    }

    /**
     * 별칭 출처 VO 반환
     *
     * @return AliasSource
     */
    public AliasSource source() {
        return source;
    }
}
