package com.ryuqq.marketplace.domain.claimhistory.vo;

/** 클레임 이력 액터 Value Object. 이력을 생성한 주체 정보를 담습니다. */
public record Actor(ActorType actorType, String actorId, String actorName) {

    public Actor {
        if (actorType == null) {
            throw new IllegalArgumentException("actorType은 필수입니다");
        }
        if (actorId == null || actorId.isBlank()) {
            throw new IllegalArgumentException("actorId는 필수입니다");
        }
    }

    public static Actor system() {
        return new Actor(ActorType.SYSTEM, "system", "시스템");
    }

    public static Actor admin(String actorId, String actorName) {
        return new Actor(ActorType.ADMIN, actorId, actorName);
    }

    public static Actor seller(String actorId, String actorName) {
        return new Actor(ActorType.SELLER, actorId, actorName);
    }

    public static Actor customer(String actorId, String actorName) {
        return new Actor(ActorType.CUSTOMER, actorId, actorName);
    }
}
