package com.ryuqq.marketplace.domain.brand.aggregate.brand;

import com.ryuqq.marketplace.domain.brand.event.BrandAliasAddedEvent;
import com.ryuqq.marketplace.domain.brand.event.BrandAliasConfirmedEvent;
import com.ryuqq.marketplace.domain.brand.event.BrandCreatedEvent;
import com.ryuqq.marketplace.domain.brand.event.BrandStatusChangedEvent;
import com.ryuqq.marketplace.domain.brand.event.BrandUpdatedEvent;
import com.ryuqq.marketplace.domain.brand.vo.AliasName;
import com.ryuqq.marketplace.domain.brand.vo.AliasSource;
import com.ryuqq.marketplace.domain.brand.vo.AliasStatus;
import com.ryuqq.marketplace.domain.brand.vo.BrandAliasId;
import com.ryuqq.marketplace.domain.brand.vo.BrandCode;
import com.ryuqq.marketplace.domain.brand.vo.BrandId;
import com.ryuqq.marketplace.domain.brand.vo.BrandMeta;
import com.ryuqq.marketplace.domain.brand.vo.BrandName;
import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;
import com.ryuqq.marketplace.domain.brand.vo.CanonicalName;
import com.ryuqq.marketplace.domain.brand.vo.Confidence;
import com.ryuqq.marketplace.domain.brand.vo.Country;
import com.ryuqq.marketplace.domain.brand.vo.DataQuality;
import com.ryuqq.marketplace.domain.brand.vo.Department;
import com.ryuqq.marketplace.domain.brand.exception.BrandAliasDuplicateException;
import com.ryuqq.marketplace.domain.brand.exception.BrandAliasNotFoundException;
import com.ryuqq.marketplace.domain.brand.exception.BrandBlockedException;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Brand Aggregate Root
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지 - Plain Java class</li>
 *   <li>외부 의존성 금지 - import java.* 만 허용</li>
 *   <li>JPA 어노테이션 금지</li>
 *   <li>Law of Demeter 준수 - Getter 체이닝 금지</li>
 *   <li>Tell Don't Ask 패턴</li>
 *   <li>불변 컬렉션 반환 - Collections.unmodifiableList()</li>
 * </ul>
 *
 * <p><strong>책임</strong>:</p>
 * <ul>
 *   <li>브랜드 정보 관리</li>
 *   <li>브랜드 상태 관리</li>
 *   <li>브랜드 별칭(BrandAlias) 관리 (Aggregate Root를 통해서만 조작)</li>
 *   <li>도메인 이벤트 발행</li>
 *   <li>데이터 품질 관리</li>
 * </ul>
 *
 * <p><strong>Aggregate 경계</strong>:</p>
 * <ul>
 *   <li>모든 BrandAlias 조작은 Brand를 통해서만 수행</li>
 *   <li>중복 alias 검증: brand_id + normalized_alias + mall_code + seller_id</li>
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
public class Brand {
    private final BrandId id;
    private final BrandCode code;
    private final CanonicalName canonicalName;
    private BrandName name;
    private Country country;
    private Department department;
    private boolean isLuxury;
    private BrandStatus status;
    private BrandMeta meta;
    private DataQuality dataQuality;
    private final List<BrandAlias> aliases;
    private long version;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Private 생성자 - 신규 생성용
     */
    private Brand(
        BrandCode code,
        CanonicalName canonicalName,
        BrandName name,
        Country country,
        Department department,
        boolean isLuxury,
        BrandStatus status,
        BrandMeta meta,
        DataQuality dataQuality
    ) {
        this.id = BrandId.forNew();
        this.code = code;
        this.canonicalName = canonicalName;
        this.name = name;
        this.country = country;
        this.department = department;
        this.isLuxury = isLuxury;
        this.status = status;
        this.meta = meta;
        this.dataQuality = dataQuality;
        this.aliases = new ArrayList<>();
        this.version = 0L;
    }

    /**
     * Private 생성자 - 재구성용
     */
    private Brand(
        BrandId id,
        BrandCode code,
        CanonicalName canonicalName,
        BrandName name,
        Country country,
        Department department,
        boolean isLuxury,
        BrandStatus status,
        BrandMeta meta,
        DataQuality dataQuality,
        List<BrandAlias> aliases,
        long version
    ) {
        this.id = id;
        this.code = code;
        this.canonicalName = canonicalName;
        this.name = name;
        this.country = country;
        this.department = department;
        this.isLuxury = isLuxury;
        this.status = status;
        this.meta = meta;
        this.dataQuality = dataQuality;
        this.aliases = new ArrayList<>(aliases);
        this.version = version;
    }

    /**
     * 팩토리 메서드 - 신규 브랜드 생성
     *
     * @param code 브랜드 코드
     * @param canonicalName 정규 이름
     * @param name 브랜드 이름
     * @param country 국가
     * @param department 부문
     * @param isLuxury 럭셔리 여부
     * @return Brand
     */
    public static Brand create(
        BrandCode code,
        CanonicalName canonicalName,
        BrandName name,
        Country country,
        Department department,
        boolean isLuxury
    ) {
        Brand brand = new Brand(
            code,
            canonicalName,
            name,
            country,
            department,
            isLuxury,
            BrandStatus.ACTIVE,
            BrandMeta.empty(),
            DataQuality.unknown()
        );

        brand.registerEvent(BrandCreatedEvent.of(null, code.value(), canonicalName.value()));

        return brand;
    }

    /**
     * 팩토리 메서드 - 재구성 (영속성에서 복원)
     *
     * @param id 브랜드 ID
     * @param code 브랜드 코드
     * @param canonicalName 정규 이름
     * @param name 브랜드 이름
     * @param country 국가
     * @param department 부문
     * @param isLuxury 럭셔리 여부
     * @param status 브랜드 상태
     * @param meta 메타 정보
     * @param dataQuality 데이터 품질
     * @param aliases 별칭 목록
     * @param version 버전
     * @return Brand
     */
    public static Brand reconstitute(
        BrandId id,
        BrandCode code,
        CanonicalName canonicalName,
        BrandName name,
        Country country,
        Department department,
        boolean isLuxury,
        BrandStatus status,
        BrandMeta meta,
        DataQuality dataQuality,
        List<BrandAlias> aliases,
        long version
    ) {
        return new Brand(
            id,
            code,
            canonicalName,
            name,
            country,
            department,
            isLuxury,
            status,
            meta,
            dataQuality,
            aliases,
            version
        );
    }

    // ===== 도메인 행위 =====

    /**
     * 상품 매핑 가능 여부 (Tell Don't Ask)
     *
     * <p>외부에서 status를 직접 검사하지 않고, 도메인 객체에 위임</p>
     *
     * @return 사용 가능한 상태이면 true
     */
    public boolean canMapProduct() {
        return status.isUsable();
    }

    /**
     * 상품 매핑 검증 (Tell Don't Ask)
     *
     * <p>차단된 브랜드인 경우 예외 발생</p>
     *
     * @throws BrandBlockedException 차단된 브랜드인 경우
     */
    public void validateProductMapping() {
        if (!canMapProduct()) {
            throw new BrandBlockedException(id != null ? id.value() : null);
        }
    }

    /**
     * 브랜드 정보 수정
     *
     * @param newName 새로운 이름
     * @param newCountry 새로운 국가
     * @param newDepartment 새로운 부문
     * @param newIsLuxury 새로운 럭셔리 여부
     */
    public void update(BrandName newName, Country newCountry, Department newDepartment, boolean newIsLuxury) {
        this.name = newName;
        this.country = newCountry;
        this.department = newDepartment;
        this.isLuxury = newIsLuxury;
        registerEvent(BrandUpdatedEvent.of(id != null ? id.value() : null));
    }

    /**
     * 메타 정보 업데이트
     *
     * @param newMeta 새로운 메타 정보
     */
    public void updateMeta(BrandMeta newMeta) {
        this.meta = newMeta;
        registerEvent(BrandUpdatedEvent.of(id != null ? id.value() : null));
    }

    /**
     * 상태 변경
     *
     * @param newStatus 새로운 상태
     */
    public void changeStatus(BrandStatus newStatus) {
        BrandStatus oldStatus = this.status;
        this.status = newStatus;
        registerEvent(BrandStatusChangedEvent.of(
            id != null ? id.value() : null,
            oldStatus.name(),
            newStatus.name()
        ));
    }

    /**
     * 데이터 품질 업데이트
     *
     * @param newQuality 새로운 품질 정보
     */
    public void updateDataQuality(DataQuality newQuality) {
        this.dataQuality = newQuality;
    }

    // ===== Alias 관리 (Aggregate Root를 통해서만 조작) =====

    /**
     * 별칭 추가
     *
     * <p><strong>중복 검증</strong>: brand_id + normalized_alias + mall_code + seller_id</p>
     *
     * @param aliasName 별칭명
     * @param source 별칭 출처
     * @param confidence 신뢰도
     * @param aliasStatus 별칭 상태
     * @return 추가된 BrandAlias
     * @throws BrandAliasDuplicateException 중복된 별칭인 경우
     */
    public BrandAlias addAlias(AliasName aliasName, AliasSource source, Confidence confidence, AliasStatus aliasStatus) {
        // 중복 검증 (REJECTED 상태는 제외)
        boolean duplicate = aliases.stream()
            .filter(a -> !a.isRejected())
            .anyMatch(a -> a.matchesScope(aliasName.normalized(), source.mallCode(), source.sellerId()));

        if (duplicate) {
            throw new BrandAliasDuplicateException(
                id != null ? id.value() : null,
                aliasName.normalized(),
                source.mallCode() + ":" + source.sellerId()
            );
        }

        BrandAlias alias = BrandAlias.create(id != null ? id.value() : null, aliasName, source, confidence, aliasStatus);
        aliases.add(alias);

        registerEvent(BrandAliasAddedEvent.of(
            id != null ? id.value() : null,
            alias.id() != null ? alias.id().value() : null,
            aliasName.original(),
            aliasName.normalized(),
            source.sourceType().name()
        ));

        return alias;
    }

    /**
     * 별칭 확정
     *
     * @param aliasId 별칭 ID
     * @throws BrandAliasNotFoundException 별칭이 존재하지 않는 경우
     */
    public void confirmAlias(BrandAliasId aliasId) {
        BrandAlias alias = findAliasById(aliasId);
        alias.confirm();
        registerEvent(BrandAliasConfirmedEvent.of(
            id != null ? id.value() : null,
            aliasId.value(),
            alias.normalizedAlias()
        ));
    }

    /**
     * 별칭 거부
     *
     * @param aliasId 별칭 ID
     * @throws BrandAliasNotFoundException 별칭이 존재하지 않는 경우
     */
    public void rejectAlias(BrandAliasId aliasId) {
        BrandAlias alias = findAliasById(aliasId);
        alias.reject();
    }

    /**
     * 별칭 신뢰도 업데이트
     *
     * @param aliasId 별칭 ID
     * @param newConfidence 새로운 신뢰도
     * @throws BrandAliasNotFoundException 별칭이 존재하지 않는 경우
     */
    public void updateAliasConfidence(BrandAliasId aliasId, Confidence newConfidence) {
        BrandAlias alias = findAliasById(aliasId);
        alias.updateConfidence(newConfidence);
    }

    /**
     * 별칭 제거
     *
     * @param aliasId 별칭 ID
     * @throws BrandAliasNotFoundException 별칭이 존재하지 않는 경우
     */
    public void removeAlias(BrandAliasId aliasId) {
        BrandAlias alias = findAliasById(aliasId);
        aliases.remove(alias);
    }

    /**
     * 별칭 ID로 찾기 (내부 헬퍼 메서드)
     *
     * @param aliasId 별칭 ID
     * @return BrandAlias
     * @throws BrandAliasNotFoundException 별칭이 존재하지 않는 경우
     */
    private BrandAlias findAliasById(BrandAliasId aliasId) {
        return aliases.stream()
            .filter(a -> a.id() != null && a.id().equals(aliasId))
            .findFirst()
            .orElseThrow(() -> new BrandAliasNotFoundException(id != null ? id.value() : null, aliasId.value()));
    }

    // ===== Getters (Law of Demeter 준수) =====

    /**
     * 브랜드 ID 반환
     *
     * @return BrandId
     */
    public BrandId id() {
        return id;
    }

    /**
     * 브랜드 코드 반환
     *
     * @return BrandCode
     */
    public BrandCode code() {
        return code;
    }

    /**
     * 정규 이름 반환
     *
     * @return CanonicalName
     */
    public CanonicalName canonicalName() {
        return canonicalName;
    }

    /**
     * 브랜드 상태 반환
     *
     * @return BrandStatus
     */
    public BrandStatus status() {
        return status;
    }

    /**
     * 버전 반환
     *
     * @return version
     */
    public long version() {
        return version;
    }

    // ===== 이름 관련 (Law of Demeter - 직접 값 반환) =====

    /**
     * 한글명 반환 (Law of Demeter 준수)
     *
     * @return 한글명
     */
    public String nameKo() {
        return name.nameKo();
    }

    /**
     * 영문명 반환 (Law of Demeter 준수)
     *
     * @return 영문명
     */
    public String nameEn() {
        return name.nameEn();
    }

    /**
     * 단축명 반환 (Law of Demeter 준수)
     *
     * @return 단축명
     */
    public String shortName() {
        return name.shortName();
    }

    /**
     * 표시용 이름 반환 (우선순위: ko > en)
     *
     * @return 표시용 이름
     */
    public String displayName() {
        return name.displayName();
    }

    // ===== 국가/부문 =====

    /**
     * 국가 반환
     *
     * @return Country
     */
    public Country country() {
        return country;
    }

    /**
     * 부문 반환
     *
     * @return Department
     */
    public Department department() {
        return department;
    }

    /**
     * 럭셔리 여부 반환
     *
     * @return 럭셔리 여부
     */
    public boolean isLuxury() {
        return isLuxury;
    }

    // ===== 메타 (Law of Demeter - 직접 값 반환) =====

    /**
     * 공식 웹사이트 반환 (Law of Demeter 준수)
     *
     * @return 공식 웹사이트
     */
    public String officialWebsite() {
        return meta.officialWebsite();
    }

    /**
     * 로고 URL 반환 (Law of Demeter 준수)
     *
     * @return 로고 URL
     */
    public String logoUrl() {
        return meta.logoUrl();
    }

    /**
     * 설명 반환 (Law of Demeter 준수)
     *
     * @return 설명
     */
    public String description() {
        return meta.description();
    }

    // ===== 데이터 품질 =====

    /**
     * 데이터 품질 반환
     *
     * @return DataQuality
     */
    public DataQuality dataQuality() {
        return dataQuality;
    }

    // ===== Alias 컬렉션 (불변) =====

    /**
     * 별칭 목록 반환 (불변 컬렉션)
     *
     * @return 불변 별칭 목록
     */
    public List<BrandAlias> aliases() {
        return Collections.unmodifiableList(aliases);
    }

    /**
     * 별칭 개수 반환
     *
     * @return 별칭 개수
     */
    public int aliasCount() {
        return aliases.size();
    }

    // ===== 도메인 이벤트 =====

    /**
     * 도메인 이벤트 목록 반환 (불변 컬렉션)
     *
     * @return 불변 이벤트 목록
     */
    public List<DomainEvent> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * 도메인 이벤트 초기화
     *
     * <p>이벤트 발행 후 호출하여 이벤트 목록을 비움</p>
     */
    public void clearDomainEvents() {
        domainEvents.clear();
    }

    /**
     * 도메인 이벤트 등록 (내부 헬퍼 메서드)
     *
     * @param event 등록할 이벤트
     */
    private void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }
}
